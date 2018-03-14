package com.rainbowpunch.jetedge.core.limiters.special;

import com.rainbowpunch.jetedge.core.RandomCloner;
import com.rainbowpunch.jetedge.core.Tuple;
import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.exception.ValueGenerationException;
import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The CorrelationLimiter allows users to define how a given field should be limited based on the values of other fields.  As an example,
 *      a POJO may exist with an effective date and expiration date where the expiration date needs to be 30-35 days afterwards.
 *      In psuedo-code
 *
 *      <code>
 *          new CorrelationLimiter((random, dateLimiter) -> {
 *              Date date = dateLimiter.get()
 *              Date incrementDate = random.generateARandomDate();
 *              return date + incrementDate;
 *          }, "dateLimiterField");
 *
 *      </code>
 *
 *      If a CorrelationLimiter need a to listen for multiple other limiters, then the BiFunction needs to take in a Map.
 *
 *      <code>
 *          new CorrelationLimiter((random, limiters) -> {
 *              int intValue = (Integer) limiters.get("intLimiterField").get();
 *              String stringValue = (String) limiters.get("stringLimiterField").get();
 *              return stringValue + intValue;
 *          }, Arrays.asList("intLimiterField", "stringLimiterField"));
 *      </code>
 *
 * @param <T>
 */
public class CorrelationLimiter<T> implements Limiter<T> {

    private BiFunction<Random, Map<String, Supplier<?>>, T> correlatorMap;
    private BiFunction<Random, Supplier<?>, T> correlator;
    private Collection<String> fieldDependencies;
    private Map<String, CompletableFuture<Tuple<Limiter<?>, Random>>> listenerObjects;
    private Map<String, Tuple<Limiter<?>, Random>> limiterTuples;

    /**
     * Creates a limiter that listens for the output of several other limiters
     * @param correlatorMap
     *          A BiFunction that takes in a Random and a Map of String, Supplier for all of the limiters this is listening too.
     * @param fieldDependencies
     *          A collection of the string names that this object is listening too.
     */
    public CorrelationLimiter(BiFunction<Random, Map<String, Supplier<?>>, T> correlatorMap, Collection<String> fieldDependencies) {
        if (correlatorMap == null || fieldDependencies == null || fieldDependencies.size() == 0) {
            throw new LimiterConstructionException("Null and empty values can't be provided to the CorrelationLimiter");
        }
        this.correlatorMap = correlatorMap;
        this.fieldDependencies = fieldDependencies;
        this.listenerObjects = new HashMap<>();
    }

    /**
     * Creates a limiter that listens for the output of another listener
     * @param correlator
     *          A BiFunction that takes in a random number and the supplier of the listen this CorrelationLimiter is listening too.
     * @param fieldDependency
     *          The dot-delimited path that this object is listening too.
     */
    public CorrelationLimiter(BiFunction<Random, Supplier<?>, T> correlator, String fieldDependency) {
        if (correlator == null || fieldDependency == null || fieldDependency.isEmpty()) {
            throw new LimiterConstructionException("Null and empty values can't be provided to the CorrelationLimiter");
        }
        this.correlator = correlator;
        Set<String> dependencies = new HashSet<>();
        dependencies.add(fieldDependency);
        fieldDependencies = dependencies;
        this.listenerObjects = new HashMap<>();
    }

    /**
     * Registers a limiter to listen to.
     * @param name
     * @param providerFuture
     */
    public void supplyFuture(String name, CompletableFuture<Tuple<Limiter<?>, Random>> providerFuture) {
        if (name == null || name.isEmpty() || providerFuture == null) {
            throw new LimiterConstructionException("Null and empty values can't be provided to the CorrelationLimiter");
        }
        this.listenerObjects.put(name, providerFuture);
    }

    public Collection<String> getFieldDependencies() {
        return fieldDependencies;
    }

    @Override
    public Supplier<T> generateSupplier(Random random) {
        Map<String, Supplier<?>> supplierMap = limiterTuples.entrySet().stream()
                .map(entry -> {
                    Tuple<Limiter<?>, Random> tuple = entry.getValue();
                    Random limiterRandomClone = RandomCloner.cloneRandom(tuple.getU());
                    Supplier<?> dependentLimiterValue = tuple.getT().generateSupplier(limiterRandomClone);
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), dependentLimiterValue);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return supplierMap.size() == 1
                ? () -> correlator.apply(random, supplierMap.values().iterator().next())
                : () -> correlatorMap.apply(random, supplierMap);
    }

    @Override
    public void generateFuture(CompletableFuture<Tuple<Limiter<T>, Random>> future, Random random) {
        try {
            CorrelationLimiter<T> myself = this;
            List<CompletableFuture> futures = new ArrayList<>(listenerObjects.values());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                    .thenRunAsync(new Runnable() {
                        @Override
                        public void run() {
                            limiterTuples = listenerObjects.entrySet().stream()
                                    .map(entry -> {
                                        try {
                                            return new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().get());
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                            future.complete(new Tuple<>(myself, random));
                        }
                    });

        } catch (Exception e) {
            throw new ValueGenerationException("Unable to attain value of correlated data.", e);
        }
    }
}
