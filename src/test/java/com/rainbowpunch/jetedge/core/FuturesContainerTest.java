package com.rainbowpunch.jetedge.core;

import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.common.ConstantValueLimiter;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FuturesContainerTest {



    @Test(expected = TimeoutException.class)
    public void testFinishedPopulatingCheck_timeout() throws Exception {
        FuturesContainer container = new FuturesContainer();
        container.getCompletableFuture("hello.world");
        container.getCompletableFuture("goodnight.moon");

        CompletableFuture future = container.finishedPopulating();
        future.get(2, TimeUnit.SECONDS);
        fail();
    }

    @Test
    public void testFinishedPopulatingCheck_populateBefore() throws Exception {
        FuturesContainer container = new FuturesContainer();

        CompletableFuture<Tuple<Limiter<?>, Random>> future1 = container.getCompletableFuture("hello.world");
        CompletableFuture<Tuple<Limiter<?>, Random>> future2 = container.getCompletableFuture("goodnight.moon");

        Random random1 = new Random();
        Random random2 = new Random();

        future1.complete(new TestTuple<>(new ConstantValueLimiter<String>("hello.world"), random1));
        future2.complete(new TestTuple<>(new ConstantValueLimiter<String>("goodnight.moon"), random2));

        CompletableFuture future = container.finishedPopulating();
        future.get();

        assertTrue(true);
    }

    @Test
    public void testFinishedPopulatingCheck_populateAfter() throws Exception {
        FuturesContainer container = new FuturesContainer();

        CompletableFuture<Tuple<Limiter<?>, Random>> future1 = container.getCompletableFuture("hello.world");
        CompletableFuture<Tuple<Limiter<?>, Random>> future2 = container.getCompletableFuture("goodnight.moon");

        Random random1 = new Random();
        Random random2 = new Random();


        CompletableFuture future = container.finishedPopulating();

        future1.complete(new TestTuple<>(new ConstantValueLimiter<String>("hello.world"), random1));
        future2.complete(new TestTuple<>(new ConstantValueLimiter<String>("goodnight.moon"), random2));

        future.get();

        assertTrue(true);
    }

    private class TestTuple<T, U> extends Tuple<T, U> {
        TestTuple(T t, U u) {
            super(t, u);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}