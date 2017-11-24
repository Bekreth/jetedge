package com.rainbowpunch.jtdg.core.limiters.collections;

import com.rainbowpunch.jtdg.core.exception.LimiterConstructionException;
import com.rainbowpunch.jtdg.core.exception.PojoConstructionException;
import com.rainbowpunch.jtdg.core.limiters.Limiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.IntegerLimiter;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class ArrayLimiterTest {

    @Test(expected = LimiterConstructionException.class)
    public void testNegativeRange() {
        new ArrayLimiter(-1,4);
    }

    @Test(expected = LimiterConstructionException.class)
    public void testNegativeOffset() {
        new ArrayLimiter(1,-4);
    }

    @Test(expected = LimiterConstructionException.class)
    public void testNullInternalLimiter() {
        new ArrayLimiter(3, 3).generateSupplier(new Random());
    }

    @Test(expected = PojoConstructionException.class)
    public void testBadInternalLimiter() {
        Limiter<Integer> exceptionLimiter = new Limiter<Integer>() {
            @Override
            public Supplier<Integer> generateSupplier(Random random) {
                return () -> {
                    throw new RuntimeException("I failed!");
                };
            }
        };

        new ArrayLimiter(exceptionLimiter, 3, 3).generateSupplier(new Random()).get();
    }

    @Test
    public void testFixedListSize() {
        Limiter intListLimiter = new ArrayLimiter(new IntegerLimiter(), 0, 5);
        for (int i = 0; i < 10; i++) {
            Integer[] intList = ((Integer[]) intListLimiter.generateSupplier(new Random()).get());
            assertEquals(5, intList.length);
        }
    }

    @Test
    public void testProperListSize() {
        Limiter intListLimiter = new ArrayLimiter(new IntegerLimiter(), 3, 10);
        for (int i = 0; i < 10; i++) {
            Integer[] intList = ((Integer[]) intListLimiter.generateSupplier(new Random()).get());
            int size = intList.length;
            assertTrue("Expected list size to be between 10-13, was " + size, size >=10 && size < 13);
        }
    }


}