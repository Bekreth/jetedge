package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Limiter for integers
 */
public class IntegerLimiter extends SimpleAbstractLimiter<Integer> {

    private final Integer range;
    private final Integer offset;

    /**
     * This will configure the IntegerLimiter to output any random int.
     */
    public IntegerLimiter() {
        this(null, 0);
    }

    /**
     * This will configure the IntegerLimiter to output values between 0 and range (exclusive)
     * @param range
     *          The upper limit on output values
     */
    public IntegerLimiter(Integer range) {
        this(range, 0);
    }


    /**
     * This will configure the IntegerLimiter to output values between the provided offset (inclusive) and
     *      range (exclusive)
     * @param range
     *          The upper limit on output values
     * @param offset
     *          The lower limit on output values
     */
    public IntegerLimiter(Integer range, Integer offset) {
        if (range != null && range < 0) {
            throw new LimiterConstructionException("Cannot create IntegerLimiter with a negative range");
        }
        this.range = range;
        this.offset = offset;
    }

    @Override
    public Supplier<Integer> generateSupplier(Random random) {
        return range == null
                ? () -> (Integer) random.nextInt()
                : () -> (Integer) (random.nextInt(range) + offset);
    }

}
