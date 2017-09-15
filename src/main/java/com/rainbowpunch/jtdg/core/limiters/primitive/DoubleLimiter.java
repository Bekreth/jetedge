package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

public class DoubleLimiter implements Limiter<Double> {
    @Override
    public Supplier<Double> generateSupplier(Random random) {
        return random::nextDouble;
    }
}