package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.math.BigDecimal;
import java.util.Random;
import java.util.function.Supplier;

public class BigDecimalLimiter implements Limiter<BigDecimal> {


    @Override
    public Supplier<BigDecimal> generateSupplier(Random random) {
        return () -> new BigDecimal(random.nextInt());
    }
}
