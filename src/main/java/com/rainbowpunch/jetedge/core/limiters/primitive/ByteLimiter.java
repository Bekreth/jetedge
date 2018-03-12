package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Byte data fields.
 */
public class ByteLimiter extends SimpleAbstractLimiter<Byte> {
    // TODO: 3/9/18 This needs to be bolstered
    @Override
    public Supplier<Byte> generateSupplier(Random random) {
        return () -> {
            byte[] bytes = new byte[1];
            random.nextBytes(bytes);
            return bytes[0];
        };
    }
}
