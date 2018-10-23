package com.rainbowpunch.jetedge.core.limiters;

import com.rainbowpunch.jetedge.core.Tuple;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * An interface for specifying how data of type <code>T</code> should be generated.
 */
public interface Limiter<T> {

    /**
     * This method should return a supplier that creates data based off of the provided Random number generator.
     * @param random Random value generator to be used.
     */
    Supplier<T> generateSupplier(Random random);

    /**
     * This method should provide the Supplier from generateSupplier into the provided CompletableFuture.
     * @param future
     * @param random
     */
    void generateFuture(CompletableFuture<Tuple<Limiter<T>, Random>> future, Random random);
}
