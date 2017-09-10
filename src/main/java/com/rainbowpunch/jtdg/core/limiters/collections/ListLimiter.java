package com.rainbowpunch.jtdg.core.limiters.collections;

import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class ListLimiter implements Limiter<List<Object>> {

    private int numberOfElements;
    private Limiter limiter;

    public static ListLimiter createListLimiter(Limiter<?> limiter) {
        return new ListLimiter(limiter);
    }

    public ListLimiter(Limiter limiter) {
        this(limiter, 5);
    }

    public ListLimiter(Limiter limiter, int numberOfElements) {
        this.numberOfElements=  numberOfElements;
        this.limiter = limiter;
    }

    @Override
    public Supplier<List<Object>> generateSupplier(Random random) {
        return () -> {
            try {
                return IntStream.range(0, numberOfElements)
                        .mapToObj(i -> limiter.generateSupplier(random).get())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
