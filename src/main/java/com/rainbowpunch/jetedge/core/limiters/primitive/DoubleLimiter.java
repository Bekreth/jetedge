package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Double data fields.
 */
public class DoubleLimiter implements Limiter<Double> {
    @Override
    public Supplier<Double> generateSupplier(Random random) {
        return random::nextDouble;
    }
}