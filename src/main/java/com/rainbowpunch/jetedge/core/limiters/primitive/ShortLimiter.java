package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Short data fields.
 */
public class ShortLimiter extends SimpleAbstractLimiter<Short> {
    // TODO: 3/9/18 This needs to be bolstered
    @Override
    public Supplier<Short> generateSupplier(Random random) {
        return () -> (short) random.nextInt();
    }
}
