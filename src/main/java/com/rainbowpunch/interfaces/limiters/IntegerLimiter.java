package com.rainbowpunch.interfaces.limiters;

import com.rainbowpunch.interfaces.limiters.parameters.NumberType;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Limiter for integers
 */
public class IntegerLimiter implements Limiter<Integer> {

    private Integer range;
    private Integer offset;
    private NumberType integerType;

    public IntegerLimiter() {
        this(null, null, null);
    }

    public IntegerLimiter(Integer range) {
        this(range, null, null);
    }

    public IntegerLimiter(Integer range, Integer offset) {
        this(range, offset, null);
    }

    public IntegerLimiter(Integer range, Integer offset, NumberType type) {
        this.range = range;
        this.offset = offset;
        this.integerType = type == null ? NumberType.MIXED : type;
    }

    @Override
    public Supplier<Integer> generateSupplier(Random random) {
        Supplier<Integer> supplier = null;
        if (range == null) {
            supplier = () -> {
                Integer returnValue = random.nextInt();
                if ((integerType.equals(NumberType.POSITIVE) && returnValue < 0)
                        || (integerType.equals(NumberType.NEGATIVE) && returnValue > 0)) {
                    returnValue *= -1;
                }
                return returnValue;
            };
        } else {
            supplier = () -> {
                Integer returnValue = random.nextInt(range * 2) - range;
                if ((integerType.equals(NumberType.POSITIVE) && returnValue < 0)
                        || (integerType.equals(NumberType.NEGATIVE) && returnValue > 0)) {
                    returnValue *= -1;
                }
                offset = offset == null ? 0 : offset;
                return returnValue + offset;
            };
        }
        return supplier;
    }

}
