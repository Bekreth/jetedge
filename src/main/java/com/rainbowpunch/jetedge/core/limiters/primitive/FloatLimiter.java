package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Float data fields.
 */
public class FloatLimiter extends SimpleAbstractLimiter<Float> {
    // TODO: 3/9/18 This needs some love.
    @Override
    public Supplier<Float> generateSupplier(Random random) {
        return random::nextFloat;
    }
}