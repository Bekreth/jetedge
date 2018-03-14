package com.rainbowpunch.jetedge.core.limiters.special;

import com.rainbowpunch.jetedge.core.RandomCloner;
import com.rainbowpunch.jetedge.core.Tuple;
import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.common.ConstantValueLimiter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class CorrelationLimiterTest {

    @Test
    public void testSingleSuccess() throws Exception {
        CorrelationLimiter<Integer> limiter = new CorrelationLimiter<Integer>((random, constLimiter) -> {
            return (Integer) constLimiter.get() + 1;
        },"object1");

        ConstantValueLimiter<Integer> constantValueLimiter = new ConstantValueLimiter<>(10);
        CompletableFuture future = new CompletableFuture<>();
        constantValueLimiter.generateFuture(future, new Random());

        limiter.supplyFuture("object1", future);
        CompletableFuture correlationFuture = new CompletableFuture();

        limiter.generateFuture(correlationFuture, new Random());

        Tuple<Limiter<Integer>, Random> tuple = (Tuple<Limiter<Integer>, Random>) correlationFuture.get();
        Integer output = tuple.getT().generateSupplier(RandomCloner.cloneRandom(tuple.getU())).get();

        assertEquals(Integer.valueOf(11), output);
    }

    @Test
    public void testMultipleSuccess() throws Exception {
        CorrelationLimiter<Integer> limiter = new CorrelationLimiter<Integer>((random, constLimiter) -> {
            int value1 = (Integer) ((Map<String, Supplier<?>>) constLimiter).get("object1").get();
            int value2 = (Integer) ((Map<String, Supplier<?>>) constLimiter).get("object2").get();

            return value1 - value2;
        }, Arrays.asList("object1", "object2"));

        ConstantValueLimiter<Integer> constantValueLimiter1 = new ConstantValueLimiter<>(10);
        ConstantValueLimiter<Integer> constantValueLimiter2 = new ConstantValueLimiter<>(20);

        CompletableFuture future1 = new CompletableFuture<>();
        constantValueLimiter1.generateFuture(future1, new Random());

        CompletableFuture future2 = new CompletableFuture<>();
        constantValueLimiter2.generateFuture(future2, new Random());

        limiter.supplyFuture("object1", future1);
        limiter.supplyFuture("object2", future2);
        CompletableFuture correlationFuture = new CompletableFuture();

        limiter.generateFuture(correlationFuture, new Random());

        Tuple<Limiter<Integer>, Random> tuple = (Tuple<Limiter<Integer>, Random>) correlationFuture.get();
        Integer output = tuple.getT().generateSupplier(RandomCloner.cloneRandom(tuple.getU())).get();

        assertEquals(Integer.valueOf(-10), output);
    }

    // --------- Time Delay tests


    @Test
    public void testSingleSuccess_delayed() throws Exception {
        CorrelationLimiter<Integer> limiter = new CorrelationLimiter<Integer>((random, constLimiter) -> {
            return (Integer) constLimiter.get() + 1;
        },"object1");

        ConstantValueLimiter<Integer> constantValueLimiter = new TestLimiter<>(10);
        CompletableFuture future = new CompletableFuture<>();
        constantValueLimiter.generateFuture(future, new Random());

        limiter.supplyFuture("object1", future);
        CompletableFuture correlationFuture = new CompletableFuture();

        limiter.generateFuture(correlationFuture, new Random());

        Tuple<Limiter<Integer>, Random> tuple = (Tuple<Limiter<Integer>, Random>) correlationFuture.get();
        Integer output = tuple.getT().generateSupplier(RandomCloner.cloneRandom(tuple.getU())).get();

        assertEquals(Integer.valueOf(11), output);
    }

    @Test
    public void testMultipleSuccess_delayed() throws Exception {
        CorrelationLimiter<Integer> limiter = new CorrelationLimiter<Integer>((random, constLimiter) -> {
            int value1 = (Integer) ((Map<String, Supplier<?>>) constLimiter).get("object1").get();
            int value2 = (Integer) ((Map<String, Supplier<?>>) constLimiter).get("object2").get();

            return value1 - value2;
        }, Arrays.asList("object1", "object2"));

        ConstantValueLimiter<Integer> constantValueLimiter1 = new TestLimiter<>(10);
        ConstantValueLimiter<Integer> constantValueLimiter2 = new TestLimiter<>(20);

        CompletableFuture future1 = new CompletableFuture<>();
        constantValueLimiter1.generateFuture(future1, new Random());

        CompletableFuture future2 = new CompletableFuture<>();
        constantValueLimiter2.generateFuture(future2, new Random());

        limiter.supplyFuture("object1", future1);
        limiter.supplyFuture("object2", future2);
        CompletableFuture correlationFuture = new CompletableFuture();

        limiter.generateFuture(correlationFuture, new Random());

        Tuple<Limiter<Integer>, Random> tuple = (Tuple<Limiter<Integer>, Random>) correlationFuture.get();
        Integer output = tuple.getT().generateSupplier(RandomCloner.cloneRandom(tuple.getU())).get();

        assertEquals(Integer.valueOf(-10), output);
    }

    // ---------- ladder dependencies

    @Test
    public void dependcyLadderTest() throws Exception {
        CorrelationLimiter<Integer> limiterDependent = new CorrelationLimiter<Integer>((random, constLimiter) -> {
            return (Integer) constLimiter.get() + 1;
        },"object1");

        CorrelationLimiter<Integer> limiter = new CorrelationLimiter<Integer>((random, constLimiter) -> {
            return (Integer) constLimiter.get() + 1;
        },"object2");


        ConstantValueLimiter<Integer> constantValueLimiter = new ConstantValueLimiter<>(10);
        CompletableFuture future = new CompletableFuture<>();
        constantValueLimiter.generateFuture(future, new Random());

        limiterDependent.supplyFuture("object1", future);
        CompletableFuture correlationFuture = new CompletableFuture();
        limiterDependent.generateFuture(correlationFuture, new Random());

        limiter.supplyFuture("object2", correlationFuture);
        CompletableFuture correlationFuture2 = new CompletableFuture();
        limiter.generateFuture(correlationFuture2, new Random());


        Tuple<Limiter<Integer>, Random> tuple = (Tuple<Limiter<Integer>, Random>) correlationFuture2.get();
        Integer output = tuple.getT().generateSupplier(RandomCloner.cloneRandom(tuple.getU())).get();

        assertEquals(Integer.valueOf(12), output);
    }

    @Test
    public void dependcyLadderTest_delayed() throws Exception {
        CorrelationLimiter<Integer> limiterDependent = new CorrelationLimiter<Integer>((random, constLimiter) -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return (Integer) constLimiter.get() + 1;
        },"object1");

        CorrelationLimiter<Integer> limiter = new CorrelationLimiter<Integer>((random, constLimiter) -> {
            return (Integer) constLimiter.get() + 1;
        },"object2");


        ConstantValueLimiter<Integer> constantValueLimiter = new ConstantValueLimiter<>(10);
        CompletableFuture future = new CompletableFuture<>();
        constantValueLimiter.generateFuture(future, new Random());

        limiterDependent.supplyFuture("object1", future);
        CompletableFuture correlationFuture = new CompletableFuture();
        limiterDependent.generateFuture(correlationFuture, new Random());

        limiter.supplyFuture("object2", correlationFuture);
        CompletableFuture correlationFuture2 = new CompletableFuture();
        limiter.generateFuture(correlationFuture2, new Random());


        Tuple<Limiter<Integer>, Random> tuple = (Tuple<Limiter<Integer>, Random>) correlationFuture2.get();
        Integer output = tuple.getT().generateSupplier(RandomCloner.cloneRandom(tuple.getU())).get();

        assertEquals(Integer.valueOf(12), output);
    }

    // ------------------- LimiterConstructionExceptions

    @Test
    public void testConstructorValidation() {
        try {
            new CorrelationLimiter<>(null, "something");
            fail();
        } catch (LimiterConstructionException e) {
            assertTrue(true);
        }
        try {
            new CorrelationLimiter<>((in1, in2) -> 0, "");
            fail();
        } catch (LimiterConstructionException e) {
            assertTrue(true);
        }
        try {
            new CorrelationLimiter<>((in1, in2) -> 0, new ArrayList<>());
            fail();
        } catch (LimiterConstructionException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSupplyFutureValidator() {
        CorrelationLimiter<Integer> limiter = new CorrelationLimiter<>((in1, in2) -> 9, "pink");
        try {
            limiter.supplyFuture(null, new CompletableFuture<>());
            fail();
        } catch (LimiterConstructionException e) {
            assertTrue(true);
        }
        try {
            limiter.supplyFuture("", new CompletableFuture<>());
            fail();
        } catch (LimiterConstructionException e) {
            assertTrue(true);
        }
        try {
            limiter.supplyFuture("something", null);
            fail();
        } catch (LimiterConstructionException e) {
            assertTrue(true);
        }
    }


    private static class TestLimiter<T> extends ConstantValueLimiter<T> {
        public TestLimiter(T object) {
            super(object);
        }

        @Override
        public Supplier<T> generateSupplier(Random random) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return super.generateSupplier(random);
        }
    }

}