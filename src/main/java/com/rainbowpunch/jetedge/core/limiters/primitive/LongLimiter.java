package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Long data fields.
 */
public class LongLimiter implements Limiter<Long> {
    @Override
    public Supplier<Long> generateSupplier(Random random) {
        return random::nextLong;
    }
}