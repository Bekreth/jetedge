package com.rainbowpunch.jetedge.core;

import com.rainbowpunch.jetedge.core.limiters.DefaultLimiters;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.RequiresDefaultLimiter;
import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;

/**
 * Looks through all the consumers that have been configured for a POJO and creates appropriate data to fill them.  The data generated is what has
 *      been specified by the PojoGeneratorBuilder.
 */
public class FieldDataGenerator<T> {

    /**
     * Populates a PojoAttribute with appropriate date.
     * @param attributes
     *          The PojoAttribute to be populated.
     */
    public static <T> void populateSuppliers(PojoAttributes<T> attributes) {
        Random random = new Random(attributes.getRandomSeed());
        Map<String, Limiter<?>> limiters = attributes.getLimiters();

        attributes.fieldSetterStream()
                .sorted(FieldDataGenerator::alphabetical)
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
                    return new StreamContainer(limiter, entry.getValue(), entryName);
                })
                .forEach(container -> container.getFieldSetter().setSupplier(container.getLimiter().generateSupplier(random)));
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
        private FieldSetter fieldSetter;
        private String name;

        public StreamContainer(Limiter limiter, FieldSetter fieldSetter, String name) {
            this.limiter = limiter;
            this.fieldSetter = fieldSetter;
            this.name = name;
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

    private static int comparator(StreamContainer container1, StreamContainer container2) {
        return 1;
    }
}
