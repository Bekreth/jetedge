package com.rainbowpunch.jtdg.core.limiters.collections;

import com.rainbowpunch.jtdg.core.limiters.Limiter;
import com.rainbowpunch.jtdg.core.limiters.RequiresDefaultLimiter;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class ListLimiter implements Limiter<List<Object>>, RequiresDefaultLimiter<ListLimiter> {

    private final int range;
    private final int offset;
    private Limiter limiter;

    public static ListLimiter createListLimiter(Limiter<?> limiter) {
        return new ListLimiter(limiter);
    }

    public ListLimiter(Limiter limiter) {
        this(limiter, 2, 5);
    }

    public ListLimiter(Limiter limiter, int range, int offset) {
        this.range = range;
        this.offset = offset;
        this.limiter = limiter;
    }

    public ListLimiter(int range, int offset) {
        this.range = range;
        this.offset = offset;
    }

    public Limiter getLimiter() {
        return limiter;
    }

    @Override
    public boolean hasLimiter() {
        return limiter != null;
    }

    @Override
    public ListLimiter reconcile(ListLimiter baseLimiter) {
        return new ListLimiter(baseLimiter.getLimiter(), this.range, this.offset);
    }

    @Override
    public Supplier<List<Object>> generateSupplier(Random random) {
        return () -> {
            try {
                int count = range == 0 ? offset : random.nextInt(range) + offset;
                return IntStream.range(0, count)
                        .mapToObj(i -> limiter.generateSupplier(random).get())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                throw new RuntimeException("Error creating list limiter", e);
            }
        };
    }
}
