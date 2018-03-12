package com.rainbowpunch.jetedge.core;

import com.rainbowpunch.jetedge.core.limiters.DefaultLimiters;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.RequiresDefaultLimiter;
import com.rainbowpunch.jetedge.core.limiters.special.CorrelationLimiter;
import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;
import com.rainbowpunch.jetedge.spi.PojoGeneratorBuilder;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Looks through all the consumers that have been configured for a POJO and creates appropriate data to fill them.  The data generated is what has
 *      been specified by the PojoGeneratorBuilder.
 */
public class FieldDataGenerator<T> {

    /**
     * Populates a PojoAttribute with appropriate data.
     * @param attributes
     *          The PojoAttribute to be populated.
     */
    public static <T> void populateSuppliers(PojoAttributes<T> attributes) {
        FuturesContainer futuresContainer = attributes.getFuturesContainer();

        Random random = new Random(attributes.getRandomSeed());
        Map<String, Limiter<?>> limiters = attributes.getLimiters();

        attributes.fieldSetterStream()
                .map(entry -> {
                    ClassAttributes classAttributes = entry.getValue().getClassAttributes();
                    String entryName = classAttributes.getFieldNameOfClass();
                    Class clazz = entry.getValue().getClassAttributes().getClazz();

                    Limiter limiter = attributes.getAllFieldLimiterMap().get(clazz);
                    limiter = limiters.getOrDefault(entryName, limiter);

                    if (limiter == null || requiresPopulation(limiter)) {
                        Limiter defaultLimiter = DefaultLimiters.getDefaultLimiter(classAttributes, attributes);

                        if (limiter == null) {
                            limiter = defaultLimiter;
                        } else {
                            limiter = ((RequiresDefaultLimiter) limiter).reconcile(defaultLimiter);
                        }
                    }
                    CompletableFuture future = futuresContainer.getCompletableFuture(entryName);
                    return new StreamContainer(limiter, future, entry.getValue(), entryName);
                })
                .forEach(container -> {
                    if (container.getLimiter() instanceof CorrelationLimiter) {
                        CorrelationLimiter limiter = (CorrelationLimiter) container.getLimiter();
                        String dependency = limiter.getDependencyName();
                        limiter.supplyFuture(attributes.getFuturesContainer().getCompletableFuture(dependency));
                    }
                    container.completeFuture(random);
                });
    }

    private static <T> int alphabetical(Map.Entry<String, FieldSetter<T, ?>> entry1, Map.Entry<String, FieldSetter<T, ?>> entry2) {
        return String.CASE_INSENSITIVE_ORDER.compare(entry1.getKey(), entry2.getKey());
    }

    private static boolean requiresPopulation(Limiter limiter) {
        if (RequiresDefaultLimiter.class.isAssignableFrom(limiter.getClass())) {
            RequiresDefaultLimiter innerLimiter = (RequiresDefaultLimiter) limiter;
            return !innerLimiter.hasLimiter();
        } else {
            return false;
        }
    }

    private static class StreamContainer {
        private Limiter limiter;
        private CompletableFuture<Tuple<Limiter<?>, Random>> future;
        private FieldSetter fieldSetter;
        private String name;

        public StreamContainer(Limiter limiter, CompletableFuture future, FieldSetter fieldSetter, String name) {
            this.limiter = limiter;
            this.future = future;
            this.fieldSetter = fieldSetter;
            this.name = name;
        }

        public void completeFuture(Random random) {
            PojoGeneratorBuilder.getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Random clonedRandom = RandomCloner.cloneRandom(random);
                        fieldSetter.setSupplier(limiter.generateSupplier(clonedRandom));
                        limiter.generateFuture(future, clonedRandom);
                    } catch (Exception e) {
                        throw new RuntimeException("Something is amiss");
                    }
                }
            });
        }

        public Limiter getLimiter() {
            return limiter;
        }

        public void setLimiter(Limiter limiter) {
            this.limiter = limiter;
        }

        public FieldSetter getFieldSetter() {
            return fieldSetter;
        }

        public void setFieldSetter(FieldSetter fieldSetter) {
            this.fieldSetter = fieldSetter;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
