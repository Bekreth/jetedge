package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.math.BigInteger;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for BigInteger data fields.
 */
public class BigIntegerLimiter extends SimpleAbstractLimiter<BigInteger> {

    private final Integer bits;
    private final Integer offset;

    /**
     * Default BigIntegerLimiter is 20 bits with 0 offset. 0 - 1048576
     */
    public BigIntegerLimiter() {
        this(20, 0);
    }

    /**
     * Provide the number of bits that should be in the random output. e.g. 10 -> 1024, 20 -> 1048576
     * @param bits
     */
    public BigIntegerLimiter(Integer bits) {
        this(bits, 0);
    }

    /**
     * Provide an integer offset to the randomly created BigInt.
     * @param bits
     * @param offset
     */
    public BigIntegerLimiter(Integer bits, Integer offset) {
        this.bits = bits;
        this.offset = offset;
    }

    @Override
    public Supplier<BigInteger> generateSupplier(Random random) {
        return () -> {
            return new BigInteger(bits, random).add(BigInteger.valueOf(offset));
        };
    }
}
