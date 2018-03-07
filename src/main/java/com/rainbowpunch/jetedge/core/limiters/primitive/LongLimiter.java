package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.LongStream;

/**
 * A limiter for Long data fields.
 * <p>
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
        return () -> {
            long r = random.nextLong();
            long n = bound - origin, m = n - 1;
            if ((n & m) == 0L)  // power of two
                r = (r & m) + origin;
            else if (n > 0L) {  // reject over-represented candidates
                for (long u = r >>> 1;            // ensure nonnegative
                     u + m - (r = u % n) < 0L;    // rejection check
                     u = random.nextLong() >>> 1) // retry
                    ;
                r += origin;
            } else {              // range not representable as long
                while (r < origin || r >= bound)
                    r = random.nextLong();
            }
            return r;
        };
    }
}