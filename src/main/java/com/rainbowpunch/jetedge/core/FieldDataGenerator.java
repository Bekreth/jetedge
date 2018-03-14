package com.rainbowpunch.jetedge.core;

import com.rainbowpunch.jetedge.core.limiters.DefaultLimiters;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.RequiresDefaultLimiter;
import com.rainbowpunch.jetedge.core.limiters.special.CorrelationLimiter;
import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
                    return new StreamContainer(futuresContainer.addFutureListener(), limiter, future, entry.getValue(), entryName);
                })
                .forEach(container -> {
                    if (container.getLimiter() instanceof CorrelationLimiter) {
                        CorrelationLimiter<?> limiter = (CorrelationLimiter) container.getLimiter();

                        limiter.getFieldDependencies()
                                .forEach(dependency -> {
                                    String lowerCase = dependency.toLowerCase();
                                    limiter.supplyFuture(dependency, attributes.getFuturesContainer().getCompletableFuture(lowerCase));
                                });
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
        private Consumer<CompletableFuture<?>> futureConsumer;
        private Limiter limiter;
        private CompletableFuture<Tuple<Limiter<?>, Random>> future;
        private FieldSetter fieldSetter;
        private String name;

        public StreamContainer(Consumer<CompletableFuture<?>> futureConsumer, Limiter limiter, CompletableFuture future,
                               FieldSetter fieldSetter, String name) {
            this.futureConsumer = futureConsumer;
            this.limiter = limiter;
            this.future = future;
            this.fieldSetter = fieldSetter;
            this.name = name;
        }

        public void completeFuture(Random random) {
            random.nextInt(); // changes the random value from the previous limiter
            Random clonedRandom = RandomCloner.cloneRandom(random);
            limiter.generateFuture(future, clonedRandom);
            futureConsumer.accept(future.thenRunAsync(new Runnable() {
                @Override
                public void run() {
                    fieldSetter.setSupplier(limiter.generateSupplier(clonedRandom));
                }
            }));
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
