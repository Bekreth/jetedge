package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Byte data fields.
 */
public class ByteLimiter implements Limiter<Byte> {
    @Override
    public Supplier<Byte> generateSupplier(Random random) {
        return () -> {
            byte[] bytes = new byte[1];
            random.nextBytes(bytes);
            return bytes[0];
        };
    }
}
