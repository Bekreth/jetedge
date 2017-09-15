package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Limiter for integers
 */
public class IntegerLimiter implements Limiter<Integer> {

    private Integer range;
    private Integer offset;

    public IntegerLimiter() {
        this(null, null);
    }

    public IntegerLimiter(Integer range) {
        this(range, null);
    }

    public IntegerLimiter(Integer range, Integer offset) {
        this.range = range;
        this.offset = offset;
    }

    @Override
    public Supplier<Integer> generateSupplier(Random random) {
        Supplier<Integer> supplier = null;
        if (range == null) {
            supplier = () -> {
                Integer returnValue = random.nextInt();
                return returnValue;
            };
        } else {
            supplier = () -> {
                Integer returnValue = random.nextInt(range) + offset;
                return returnValue + offset;
            };
        }
        return supplier;
    }

}
