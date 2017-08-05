package com.rainbowpunch.interfaces.limiters;

import com.rainbowpunch.interfaces.PojoGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Maps classes to limiters
 */
public enum DefaultLimiters {
    SHORT(null, short.class, Short.class),
    INT(new IntegerLimiter(), int.class, Integer.class),
    FLOAT(null),
    DOUBLE(null),
    LONG(null),
    STRING(new StringLimiter(), String.class),
    CHAR(null),
    ARRAY(null);

    private List<Class<?>> clazz;
    private Limiter<?> limiter;

    DefaultLimiters(Limiter<?> limiter, Class<?>... clazz) {
        this.limiter = limiter;
        this.clazz = Arrays.asList(clazz);
    }

    public static Limiter<?> getSimpleLimiter(Class clazz) {
        Limiter limiter = null;

        for (DefaultLimiters simpleLimiters : DefaultLimiters.values()) {
            if (simpleLimiters.clazz.contains(clazz)) {
                limiter = simpleLimiters.limiter;
                break;
            }
        }
        if (limiter == null) {
            limiter = new LocalLimiter(clazz);
        }

        return limiter;
    }

    private static class LocalLimiter implements Limiter {

        private PojoGenerator generator;

        public LocalLimiter(Class clazz) {
            this.generator = new PojoGenerator(clazz).analyzePojo();
        }

        @Override
        public Supplier generateSupplier(Random random) {
            return () -> {
                return generator.generatePojo();
            };
        }
    }
}
