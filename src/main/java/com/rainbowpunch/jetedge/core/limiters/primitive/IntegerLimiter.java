package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Limiter for integers
 */
public class IntegerLimiter implements Limiter<Integer> {

    private final Integer range;
    private final Integer offset;

    public IntegerLimiter() {
        this(null, 0);
    }

    public IntegerLimiter(Integer range) {
        this(range, 0);
    }

    public IntegerLimiter(Integer range, Integer offset) {
        this.range = range;
        this.offset = offset;
    }

    @Override
    public Supplier<Integer> generateSupplier(Random random) {
        return range == null ?
             () -> (Integer) random.nextInt() :
             () -> (Integer) (random.nextInt(range) + offset) + offset;
    }

}
