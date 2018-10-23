package com.rainbowpunch.jetedge.core;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This is a container for all of the CompleteableFutures to be generated. One instance of this will be created per
 *      PojoGeneratorBuilder
 */
public class FuturesContainer {

    private Map<String, CompletableFuture<Tuple<Limiter<?>, Random>>> completableFutureMap;
    private List<CompletableFuture<?>> setterFutures;

    public FuturesContainer() {
        completableFutureMap = new ConcurrentHashMap<>();
        setterFutures = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Retrieves a CompleteableFuture for the given string if it exists, or creates it if it doesn't.
     * @param limiterName
     * @return
     */
    public CompletableFuture<Tuple<Limiter<?>, Random>> getCompletableFuture(String limiterName) {
        CompletableFuture<Tuple<Limiter<?>, Random>> future = completableFutureMap.get(limiterName);
        if (future == null) {
            future = new CompletableFuture<>();
            completableFutureMap.put(limiterName, future);
        }
        return future;
    }

    /**
     * Verifies that all the CompletableFutures that have been generated have successfully finished.
     * @return
     */
    public CompletableFuture finishedPopulating() {
        List<CompletableFuture> futures = completableFutureMap.entrySet().stream()
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());

        futures.addAll(setterFutures);

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
    }

    public Consumer<CompletableFuture<?>> addFutureListener() {
        return (inFuture) -> {
            this.setterFutures.add(inFuture);
        };
    }
}
