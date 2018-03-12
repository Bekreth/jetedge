package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Double data fields.
 */
public class DoubleLimiter extends SimpleAbstractLimiter<Double> {
    // TODO: 3/9/18 This needs some love
    @Override
    public Supplier<Double> generateSupplier(Random random) {
        return random::nextDouble;
    }
}