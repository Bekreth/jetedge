package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Short data fields.
 */
public class ShortLimiter extends SimpleAbstractLimiter<Short> {

    private Short range;
    private Short offset;

    /**
     * This will configure the ShortLimiter to output any random int.
     */
    public ShortLimiter() {
        this(null, (short) 0);
    }

    /**
     * This will configure the ShortLimiter to output values between 0 and range (exclusive)
     * @param range
     *          The upper limit on output values
     */
    public ShortLimiter(Short range) {
        this(range, (short) 0);
    }

    /**
     * This will configure the ShortLimiter to output values between the provided offset (inclusive) and
     *      range (exclusive)
     * @param range
     *          The upper limit on output values
     * @param offset
     *          The lower limit on output values
     */
    public ShortLimiter(Short range, Short offset) {
        if (range != null && range < 0) {
            throw new LimiterConstructionException("Cannot create ShortLimiter with a negative range");
        }
        this.range = range;
        this.offset = offset;
    }


    @Override
    public Supplier<Short> generateSupplier(Random random) {
        return range == null
                ? () -> (short) random.nextInt()
                : () -> (short) (random.nextInt(range) + offset);
    }
}
