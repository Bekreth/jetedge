package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.LongStream;

/**
 * A limiter for Long data fields.
 * origin is inclusive left bound which is 0L by default
 * bound is exclusive right bound which is Long.MAX_VALUE by default
 */
public class LongLimiter implements Limiter<Long> {

    private final Long origin;
    private final Long bound;

    public LongLimiter() {
        this.origin = 0L;
        this.bound = Long.MAX_VALUE;
    }

    public LongLimiter(Long bound) {
        this.origin = 0L;
        this.bound = bound;
    }

    public LongLimiter(Long origin, Long bound) {
        this.origin = origin;
        this.bound = bound;
    }

    /**
     * This code comes from javadoc for Random.longs()
     * Refer to Random.longs() javadoc
     *
     * @param random
     * @return Randomly generated long value between origin & bound
     */
    @Override
    public Supplier<Long> generateSupplier(Random random) {
        return () -> random.longs(origin, bound).findFirst().getAsLong();
    }
}