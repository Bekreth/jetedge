package com.rainbowpunch.jetedge.core.limiters;

import com.rainbowpunch.jetedge.spi.pojo.PojoGenerator;

import java.util.Random;
import java.util.function.Supplier;

/**
 * This limiter is a wrapper for existing PojoGenerators so that they can be used as part of a large Limiter.
 * @param <T>
 */
public class PojoGeneratorLimiter<T> extends SimpleAbstractLimiter<T> {

    private PojoGenerator<T> generator;

    public PojoGeneratorLimiter(PojoGenerator<T> generator) {
        this.generator = generator;
    }

    @Override
    public Supplier<T> generateSupplier(Random random) {
        return () -> {
            return generator.generatePojo();
        };
    }
}
