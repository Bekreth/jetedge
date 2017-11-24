package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 *
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
