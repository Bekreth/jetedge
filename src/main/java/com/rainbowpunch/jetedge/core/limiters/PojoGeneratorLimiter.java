package com.rainbowpunch.jetedge.core.limiters;

import com.rainbowpunch.jetedge.spi.PojoGenerator;

import java.util.Random;
import java.util.function.Supplier;

public class PojoGeneratorLimiter<T> implements Limiter<T> {

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
