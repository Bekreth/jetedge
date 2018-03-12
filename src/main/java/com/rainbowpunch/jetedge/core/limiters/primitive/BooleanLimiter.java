package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Boolean data fields.
 */
public class BooleanLimiter extends SimpleAbstractLimiter<Boolean> {

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