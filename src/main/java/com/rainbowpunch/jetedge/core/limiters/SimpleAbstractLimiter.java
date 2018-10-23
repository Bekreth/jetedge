package com.rainbowpunch.jetedge.core.limiters;

import com.rainbowpunch.jetedge.core.Tuple;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * This class provides a basic wrapper on how futures of a limiter should be generated.  All user defined Limiters
 *      should extend this class unless there is a REALLY good reason not to.
 * @param <T>
 */
public abstract class SimpleAbstractLimiter<T> implements Limiter<T> {

    @Override
    public void generateFuture(CompletableFuture<Tuple<Limiter<T>, Random>> future, Random random) {
        Tuple<Limiter<T>, Random> tupleSupplier = new Tuple<>(this, random);
        future.complete(tupleSupplier);
    }
}
