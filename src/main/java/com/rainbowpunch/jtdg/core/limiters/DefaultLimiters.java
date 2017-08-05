package com.rainbowpunch.jtdg.core.limiters;

import com.rainbowpunch.jtdg.api.PojoGenerator;
import com.rainbowpunch.jtdg.core.PojoAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Maps classes to limiters
 */
public enum DefaultLimiters {
    //SHORT(null, short.class, Short.class),
    INT(new IntegerLimiter(), int.class, Integer.class),
    //FLOAT(null),
    //DOUBLE(null),
    //LONG(null),
    STRING(new StringLimiter(), String.class);
    //CHAR(null),
    //ARRAY(null);

    private List<Class<?>> clazz;
    private Limiter<?> limiter;

    DefaultLimiters(Limiter<?> limiter, Class<?>... clazz) {
        this.limiter = limiter;
        this.clazz = Arrays.asList(clazz);
    }

    private static DefaultLimiters defaultLimiters(Class clazz) {
        DefaultLimiters defaultLimiters = null;
        for (DefaultLimiters simpleLimiters : DefaultLimiters.values()) {
            if (simpleLimiters.clazz.contains(clazz)) {
                defaultLimiters = simpleLimiters;
                break;
            }
        }
        return defaultLimiters;
    }

    public static boolean isPrimativeData(Class clazz) {
        return defaultLimiters(clazz) != null;
    }

    public static Limiter<?> getSimpleLimiter(Class clazz, PojoAttributes attributes) {
        DefaultLimiters defaultLimiters = defaultLimiters(clazz);
        Limiter limiter = defaultLimiters == null ? new LocalLimiter(clazz, attributes) : defaultLimiters.limiter;
        return limiter;
    }

    private static class LocalLimiter<T extends Object> implements Limiter<T> {

        private PojoGenerator<T> generator;

        public LocalLimiter(Class<T> clazz, PojoAttributes<T> parentAttributes) {
            this.generator = new PojoGenerator<>(clazz);
            parentAttributes.getLimiters()
                    .entrySet()
                    .stream()
                    .filter(entry -> !entry.getKey().equals(parentAttributes.getPojoClazz()))
                    .flatMap(this::flattenLimiterMap)
                    .forEach(entry -> {
                        generator.andLimitField(entry.getKey(), entry.getValue());
                    });
            generator.analyzePojo();
        }

        private Stream<Map.Entry<String, Limiter<?>>> flattenLimiterMap(Map.Entry<Class, Map<String, Limiter<?>>> entry) {
            return entry.getValue()
                    .entrySet()
                    .stream();
        }

        @Override
        public Supplier<T> generateSupplier(Random random) {
            return () -> {
                return generator.generatePojo();
            };
        }
    }
}
