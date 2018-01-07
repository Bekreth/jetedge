package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Float data fields.
 */
public class FloatLimiter implements Limiter<Float> {
    @Override
    public Supplier<Float> generateSupplier(Random random) {
        return random::nextFloat;
    }
}