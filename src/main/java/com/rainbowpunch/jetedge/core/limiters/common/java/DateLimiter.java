package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Date;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Date data fields.
 */
public class DateLimiter implements Limiter<Date> {

    @Override
    public Supplier<Date> generateSupplier(Random random) {
        return () -> {
            return new Date();
        };
    }
}
