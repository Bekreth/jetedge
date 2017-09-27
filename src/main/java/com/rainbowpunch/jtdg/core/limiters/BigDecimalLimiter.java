package com.rainbowpunch.jtdg.core.limiters;

import java.math.BigDecimal;
import java.util.Random;
import java.util.function.Supplier;

public class BigDecimalLimiter implements Limiter<BigDecimal> {


    @Override
    public Supplier<BigDecimal> generateSupplier(Random random) {
        return () -> {
            return new BigDecimal(random.nextInt());

        };
    }
}
