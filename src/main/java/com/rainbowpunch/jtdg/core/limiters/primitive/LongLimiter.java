package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

public class LongLimiter implements Limiter<Long> {
    @Override
    public Supplier<Long> generateSupplier(Random random) {
        return random::nextLong;
    }
}