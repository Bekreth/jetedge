package com.rainbowpunch.jetedge.core.limiters.special;

import com.rainbowpunch.jetedge.core.exception.PojoConstructionException;
import org.junit.Test;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class SequentialLimiterTest {

    @Test
    public void testSimpleSuccess_infiniteLooping() {
        Stream<Integer> integerStream = IntStream.range(0, 10).mapToObj(Integer::new);
        SequentialLimiter<Integer> integerLimiter = new SequentialLimiter<>(Integer::new, true, integerStream);

        Supplier<Integer> supplier = integerLimiter.generateSupplier(new Random());

        for (int i = 0; i < 100; i++) {
            assertEquals((int) i % 10, (int) supplier.get());
        }
    }

    @Test(expected = PojoConstructionException.class)
    public void testSimpleSuccess_finiteLooping_error() {
        Stream<Integer> integerStream = IntStream.range(0, 10).mapToObj(Integer::new);
        SequentialLimiter<Integer> integerLimiter = new SequentialLimiter<>(Integer::new, false, integerStream);

        Supplier<Integer> supplier = integerLimiter.generateSupplier(new Random());

        for (int i = 0; i < 100; i++) {
            assertEquals((int) i % 10, (int) supplier.get());
        }
    }


}