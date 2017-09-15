package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 *
 */
public class ShortLimiter implements Limiter<Short> {
    @Override
    public Supplier<Short> generateSupplier(Random random) {
        return () ->{
            return (short) random.nextInt();
        };
    }
}
