package com.rainbowpunch.jetedge.core.limiters.special;

import com.rainbowpunch.jetedge.core.RandomCloner;
import com.rainbowpunch.jetedge.core.Tuple;
import com.rainbowpunch.jetedge.core.exception.ValueGenerationException;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.spi.PojoGeneratorBuilder;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CorrelationLimiter<U, T> implements Limiter<T> {
    private BiFunction<Random, Supplier<U>, T> correlator;
    private String fieldDependencies;
    private CompletableFuture<Tuple<Limiter<U>, Random>> listenerObject;

    public CorrelationLimiter(BiFunction<Random, Supplier<U>, T> correlator, String fieldDependencies) {
        this.correlator = correlator;
        this.fieldDependencies = fieldDependencies;
        this.listenerObject = new CompletableFuture<>();
    }

    public void supplyFuture(CompletableFuture<Tuple<Limiter<U>, Random>> providerFuture) {
        this.listenerObject = providerFuture;
    }

    public String getDependencyName() {
        return fieldDependencies;
    }

    @Override
    public Supplier<T> generateSupplier(Random random) {
        try {
            Tuple<Limiter<U>, Random> limiterTuple = listenerObject.get();
            Random limiterRandomClone = RandomCloner.cloneRandom(limiterTuple.getU());

            Supplier<U> dependentLimiterValue = limiterTuple.getT().generateSupplier(limiterRandomClone);
            return () -> correlator.apply(random, dependentLimiterValue);
        } catch (Exception e) {
            throw new ValueGenerationException("Unable to attain value of correlated data.", e);
        }
    }

    @Override
    public void generateFuture(CompletableFuture<Tuple<Limiter<T>, Random>> future, Random random) {
        CorrelationLimiter<U, T> myself = this;
        PojoGeneratorBuilder.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                future.complete(new Tuple<>(myself, random));
            }
        });
    }
}
