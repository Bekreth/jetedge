package com.rainbowpunch.jetedge.core;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * This is a container for all of the CompleteableFutures to be generated. One instance of this will be created per PojoGeneratorBuilder
 */
public class FuturesContainer {

    private Map<String, CompletableFuture<Tuple<Limiter<?>, Random>>> completableFutureMap;

    public FuturesContainer() {
        completableFutureMap = new HashMap<>();
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
        return CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                List<CompletableFuture<Tuple<Limiter<?>, Random>>> listOfFutures = new ArrayList<>(completableFutureMap.values());
                while (true) {
                    List<CompletableFuture<Tuple<Limiter<?>, Random>>> itemsToRemove = new ArrayList<>();
                    for (CompletableFuture<Tuple<Limiter<?>, Random>> future : listOfFutures) {
                        if (future.isDone()) {
                            itemsToRemove.add(future);
                        }
                    }
                    listOfFutures.removeAll(itemsToRemove);
                    if (listOfFutures.isEmpty()) {
                        break;
                    }
                }
            }
        });
    }

}
