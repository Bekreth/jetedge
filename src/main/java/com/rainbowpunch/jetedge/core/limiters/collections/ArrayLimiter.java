package com.rainbowpunch.jetedge.core.limiters.collections;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.exception.PojoConstructionException;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.RequiresDefaultLimiter;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.lang.reflect.Array;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for arrays
 */
public class ArrayLimiter extends SimpleAbstractLimiter<Object[]> implements RequiresDefaultLimiter<ArrayLimiter> {

    private static final int DEFAULT_RANGE = 2;
    private static final int DEFAULT_OFFSET = 5;

    private final int range;
    private final int offset;
    private Limiter limiter;

    public static ArrayLimiter createArrayLimiter(Limiter<?> limiter) {
        return new ArrayLimiter(limiter);
    }

    public ArrayLimiter(Limiter limiter) {
        this(limiter, DEFAULT_RANGE, DEFAULT_OFFSET);
    }

    public ArrayLimiter(int range, int offset) {
        this(null, range, offset);
    }

    public ArrayLimiter(Limiter limiter, int range, int offset) {
        this.range = range;
        this.offset = offset;
        this.limiter = limiter;
        validate();
    }

    public Limiter getLimiter() {
        return limiter;
    }

    private void validate() {
        if (range < 0 || offset < 0) {
            throw new LimiterConstructionException("Error creating ArrayLimiter : "
                    + "Offset and Range cannot be less than 0");
        }
    }

    @Override
    public boolean hasLimiter() {
        return limiter != null;
    }

    @Override
    public ArrayLimiter reconcile(ArrayLimiter baseLimiter) {
        return new ArrayLimiter(baseLimiter.getLimiter(), this.range, this.offset);
    }

    @Override
    public Supplier<Object[]> generateSupplier(Random random) {
        if (this.limiter == null) {
            throw new LimiterConstructionException("Error creating ArrayLimiter : Missing internal Limiter");
        }
        return () -> {
            try {
                int count = range == 0 ? offset : random.nextInt(range) + offset;
                Object[] objectArray = (Object[]) Array.newInstance(limiter.generateSupplier(random).get().getClass(),
                        count);
                for (int i = 0; i < count; i++) {
                    objectArray[i] = limiter.generateSupplier(random).get();
                }
                return objectArray;
            } catch (Exception e) {
                throw new PojoConstructionException("Failed to create object for ArrayLimiter: ", e);
            }
        };
    }
}
