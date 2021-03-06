package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ShortLimiterTest {

    private static int RANDOM_SEED = 197853;

    private Random random;

    @Before
    public void init() {
        random = new Random(RANDOM_SEED);
    }

    @Test
    public void testSimpleSuccess() {
        ShortLimiter limiter = new ShortLimiter();

        for (int i = 0; i < 100; i++) {
            Short output1 = limiter.generateSupplier(random).get();
            Short output2 = limiter.generateSupplier(random).get();

            assertNotNull(output1);
            assertNotNull(output2);

            assertNotEquals(output1, output2);
        }
    }

    @Test
    public void testRangeLimitationSuccess() {
        ShortLimiter limiter = new ShortLimiter((short) 1000);

        for (int i = 0; i < 100; i++) {
            assertTrue(limiter.generateSupplier(random).get() < 1000);
        }
    }

    @Test
    public void testRangeOffsetLimitationSuccess() {
        ShortLimiter limiter = new ShortLimiter((short) 1000, (short) 1000);

        for (int i = 0; i < 100; i++) {
            Short output = limiter.generateSupplier(random).get();
            assertTrue(output >= 1000 && output < 2000);
        }
    }

    @Test
    public void testRangeOffsetLimitationSuccess_negativeValues() {
        ShortLimiter limiter = new ShortLimiter((short) 1000, (short) -1000);

        for (int i = 0; i < 100; i++) {
            Short output = limiter.generateSupplier(random).get();
            assertTrue(output >= -1000 && output < 0);
        }
    }

    @Test(expected = LimiterConstructionException.class)
    public void testLimiterConstructionFailure() {
        new ShortLimiter((short) -1);
    }

}