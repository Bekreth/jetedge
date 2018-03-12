package com.rainbowpunch.jetedge.core.limiters.collections;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.exception.PojoConstructionException;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.IntegerLimiter;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class ListLimiterTest {

    @Test(expected = LimiterConstructionException.class)
    public void testNegativeRange() {
        new ListLimiter(-1,4);
    }

    @Test(expected = LimiterConstructionException.class)
    public void testNegativeOffset() {
        new ListLimiter(1,-4);
    }

    @Test(expected = LimiterConstructionException.class)
    public void testNullInternalLimiter() {
        new ListLimiter(3, 3).generateSupplier(new Random());
    }

    @Test(expected = PojoConstructionException.class)
    public void testBadInternalLimiter() {
        Limiter<Integer> exceptionLimiter = new SimpleAbstractLimiter<Integer>() {
            @Override
            public Supplier<Integer> generateSupplier(Random random) {
                return () -> {
                    throw new RuntimeException("I failed!");
                };
            }
        };

        new ListLimiter(exceptionLimiter, 3, 3).generateSupplier(new Random()).get();
    }

    @Test
    public void testFixedListSize() {
        Limiter intListLimiter = new ListLimiter(new IntegerLimiter(), 0, 5);
        for (int i = 0; i < 10; i++) {
            List<Integer> intList = ((List<Integer>) intListLimiter.generateSupplier(new Random()).get());
            assertEquals(5, intList.size());
        }
    }

    @Test
    public void testProperListSize() {
        Limiter intListLimiter = new ListLimiter(new IntegerLimiter(), 3, 10);
        for (int i = 0; i < 10; i++) {
            List<Integer> intList = ((List<Integer>) intListLimiter.generateSupplier(new Random()).get());
            int size = intList.size();
            assertTrue("Expected list size to be between 10-13, was " + size, size >=10 && size < 13);
        }
    }

}