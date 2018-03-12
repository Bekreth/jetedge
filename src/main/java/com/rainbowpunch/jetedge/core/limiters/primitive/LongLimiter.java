package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Long data fields.
 * origin is inclusive left bound which is 0L by default
 * bound is exclusive right bound which is Long.MAX_VALUE by default
 */
public class LongLimiter extends SimpleAbstractLimiter<Long> {

    private final Long origin;
    private final Long bound;

    /**
     * Default constructor will generate random long between 0L(inclusive) and Long.MAX_VALUE(exclusive)
     */
    public LongLimiter() {
        this(0L, Long.MAX_VALUE);
    }

    /**
     * Constructor with Long as argument will generate random long between 0L(inclusive) and bound(exclusive)
     *
     * @param bound exclusive right bound
     */
    public LongLimiter(Long bound) {
        this(0L, bound);
    }

    /**
     * Constructor with two Long as arguments will generate random long between origin(inclusive) and bound(exclusive)
     *
     * @param origin inclusive left bound
     * @param bound exclusive right bound
     */
    public LongLimiter(Long origin, Long bound) {
        if (bound < origin) {
            throw new LimiterConstructionException("Origin can't be greater than the bound!");
        }
        this.origin = origin;
        this.bound = bound;
    }

    @Override
    public Supplier<Long> generateSupplier(Random random) {
        return () -> random.longs(origin, bound).findFirst().getAsLong();
    }
}