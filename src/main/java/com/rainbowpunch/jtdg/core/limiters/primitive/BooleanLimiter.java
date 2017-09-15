package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

public class BooleanLimiter implements Limiter<Boolean> {

    private Boolean expectedBoolean;

    public BooleanLimiter() {

    }

    public BooleanLimiter(boolean expectedBoolean) {
        this.expectedBoolean = expectedBoolean;
    }

    @Override
    public Supplier<Boolean> generateSupplier(Random random) {
        return expectedBoolean == null ? random::nextBoolean : this::getExpectedBoolean;
    }

    private Boolean getExpectedBoolean() {
        return expectedBoolean;
    }
}