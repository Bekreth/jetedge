package com.rainbowpunch.jtdg.core.limiters;

import java.util.Random;
import java.util.function.Supplier;

/**
 *
 */
public class NestedLimiter<T> implements Limiter<T> {

    private final Class clazz;
    private final Limiter<T> limiter;

    public NestedLimiter(Class clazz, Limiter<T> limiter) {
        this.clazz = clazz;
        this.limiter = limiter;
    }

    @Override
    public Supplier<T> generateSupplier(Random random) {
        return () -> limiter.generateSupplier(random).get();
    }

    public Class getClazz() {
        return clazz;
    }

    public Limiter<?> getLimiter() {
        return limiter;
    }
}
