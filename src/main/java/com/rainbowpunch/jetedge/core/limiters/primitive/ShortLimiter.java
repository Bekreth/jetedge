package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Short data fields.
 */
public class ShortLimiter implements Limiter<Short> {
    @Override
    public Supplier<Short> generateSupplier(Random random) {
        return () -> (short) random.nextInt();
    }
}
