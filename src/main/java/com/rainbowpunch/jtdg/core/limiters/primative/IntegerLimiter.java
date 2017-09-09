package com.rainbowpunch.jtdg.core.limiters.primative;

import com.rainbowpunch.jtdg.core.limiters.Limiter;
import com.rainbowpunch.jtdg.core.limiters.parameters.NumberSign;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Limiter for integers
 */
public class IntegerLimiter implements Limiter<Integer> {

    private Integer range;
    private Integer offset;
    private NumberSign integerType;

    public IntegerLimiter() {
        this(null, null, null);
    }

    public IntegerLimiter(Integer range) {
        this(range, null, null);
    }

    public IntegerLimiter(Integer range, Integer offset) {
        this(range, offset, null);
    }

    public IntegerLimiter(Integer range, Integer offset, NumberSign type) {
        this.range = range;
        this.offset = offset;
        this.integerType = type == null ? NumberSign.MIXED : type;
    }

    @Override
    public Supplier<Integer> generateSupplier(Random random) {
        Supplier<Integer> supplier = null;
        if (range == null) {
            supplier = () -> {
                Integer returnValue = random.nextInt();
                if ((integerType.equals(NumberSign.POSITIVE) && returnValue < 0)
                        || (integerType.equals(NumberSign.NEGATIVE) && returnValue > 0)) {
                    returnValue *= -1;
                }
                return returnValue;
            };
        } else {
            supplier = () -> {
                Integer returnValue = random.nextInt(range * 2) - range;
                if ((integerType.equals(NumberSign.POSITIVE) && returnValue < 0)
                        || (integerType.equals(NumberSign.NEGATIVE) && returnValue > 0)) {
                    returnValue *= -1;
                }
                offset = offset == null ? 0 : offset;
                return returnValue + offset;
            };
        }
        return supplier;
    }

}
