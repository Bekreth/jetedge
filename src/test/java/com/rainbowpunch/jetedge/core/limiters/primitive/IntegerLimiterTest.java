package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class IntegerLimiterTest {

    private static int RANDOM_SEED = 197853;

    private Random random;

    @Before
    public void init() {
        random = new Random(RANDOM_SEED);
    }

    @Test
    public void testSimpleSuccess() {
        IntegerLimiter limiter = new IntegerLimiter();

        for (int i = 0; i < 100; i++) {
            Integer output1 = limiter.generateSupplier(random).get();
            Integer output2 = limiter.generateSupplier(random).get();

            assertNotNull(output1);
            assertNotNull(output2);

            assertNotEquals(output1, output2);
        }
    }

    @Test
    public void testRangeLimitationSuccess() {
        IntegerLimiter limiter = new IntegerLimiter(1000);

        for (int i = 0; i < 100; i++) {
            assertTrue(limiter.generateSupplier(random).get() < 1000);
        }
    }

    @Test
    public void testRangeOffsetLimitationSuccess() {
        IntegerLimiter limiter = new IntegerLimiter(1000, 1000);

        for (int i = 0; i < 100; i++) {
            Integer output = limiter.generateSupplier(random).get();
            assertTrue(output >= 1000 && output < 2000);
        }
    }

    @Test
    public void testRangeOffsetLimitationSuccess_negativeValues() {
        IntegerLimiter limiter = new IntegerLimiter(1000, -1000);

        for (int i = 0; i < 100; i++) {
            Integer output = limiter.generateSupplier(random).get();
            assertTrue(output >= -1000 && output < 0);
        }
    }

    @Test(expected = LimiterConstructionException.class)
    public void testLimiterConstructionFailure() {
        new IntegerLimiter(-1);
    }
}