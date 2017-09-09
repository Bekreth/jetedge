package com.rainbowpunch.jtdg.core.limiters.primative;

import com.rainbowpunch.jtdg.core.limiters.Limiter;
import com.rainbowpunch.jtdg.core.limiters.parameters.NumberSign;

import java.util.Random;
import java.util.function.Supplier;

/**
 *
 */
public class ShortLimiter implements Limiter<Short> {

    private NumberSign type;

    public ShortLimiter() {
        this(NumberSign.MIXED);
    }

    public ShortLimiter(NumberSign type) {
        this.type = type;
    }

    @Override
    public Supplier<Short> generateSupplier(Random random) {
        return () -> {
            long randomlong = random.nextLong();

            if ((type.equals(NumberSign.POSITIVE) && randomlong < 0)
                    || type.equals(NumberSign.NEGATIVE) && randomlong > 0) {
                randomlong *= -1;
            }
            return (short) randomlong;
        };
    }

}
