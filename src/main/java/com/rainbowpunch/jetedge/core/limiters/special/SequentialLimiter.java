package com.rainbowpunch.jetedge.core.limiters.special;

import com.rainbowpunch.jetedge.core.exception.PojoConstructionException;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A limiter that increments through a collection of values and outputs them in order.
 * @param <T> The output type of this Limiter
 */
public class SequentialLimiter<T> extends SimpleAbstractLimiter<T> {

    private List<T> seedData = new ArrayList<>();
    private Function<T, T> cloningFunction;
    private boolean loopInfinitely;

    /**
     * @param cloningFunction How to clone element from this pipe.
     * @param loopInfinitely Should this be loop back to the beginning when the limiter hits the end of the Queue.
     * @param tStream The collection of element to use sequentially.
     */
    public SequentialLimiter(Function<T, T> cloningFunction, boolean loopInfinitely, Stream<T> tStream) {
        this.loopInfinitely = loopInfinitely;
        this.cloningFunction = cloningFunction;
        seedData = tStream.collect(Collectors.toList());
    }

    /**
     * @param cloningFunction How to clone element from this pipe.
     * @param loopInfinitely Should this be loop back to the beginning when the limiter hits the end of the Queue.
     * @param seedData The collection of element to use sequentially.
     */
    public SequentialLimiter(Function<T, T> cloningFunction, boolean loopInfinitely, Collection<T> seedData) {
        this(cloningFunction, loopInfinitely, seedData.stream().sequential());
    }

    /**
     * @param cloningFunction How to clone element from this pipe.
     * @param loopInfinitely Should this be loop back to the beginning when the limiter hits the end of the Queue.
     * @param tObjects The collection of element to use sequentially.
     */
    public SequentialLimiter(Function<T, T> cloningFunction, boolean loopInfinitely, T... tObjects) {
        this(cloningFunction, loopInfinitely, Arrays.stream(tObjects).sequential());
    }

    @Override
    public Supplier<T> generateSupplier(Random random) {
        Queue<T> cloneQueue = new ArrayDeque<>();
        for (T datum : seedData) {
            cloneQueue.add(cloningFunction.apply(datum));
        }

        return () -> {
            try {
                T outputObject = cloneQueue.remove();
                if (loopInfinitely) cloneQueue.add(outputObject);
                return outputObject;
            } catch (NoSuchElementException e) {
                throw new PojoConstructionException("Object queue has run empty.");
            }
        };
    }
}
