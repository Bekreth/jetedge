package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.math.BigInteger;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for BigInteger data fields.
 */
public class BigIntegerLimiter<T> implements Limiter<BigInteger> {

    @Override
    public Supplier<BigInteger> generateSupplier(Random random) {
        return () ->{
            return new BigInteger("0");
        };
    }
}
