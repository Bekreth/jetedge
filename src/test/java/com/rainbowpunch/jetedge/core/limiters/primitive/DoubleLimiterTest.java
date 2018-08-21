package com.rainbowpunch.jetedge.core.limiters.primitive;

import org.junit.Test;

import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class DoubleLimiterTest {
    @Test
    public void testLimiterConstructedWithDefaultArguments() {
        final DoubleLimiter lim = new DoubleLimiter();
        final Random rng = new Random();
        final double val = lim.generateSupplier(rng).get();
        assertTrue(val >= 0);
        assertTrue(val <= 1);
    }

    @Test
    public void testLimiterConstructedWithMinAndMax() {
        final DoubleLimiter lim = new DoubleLimiter(20, 30);
        final Random rng = new Random();
        final double val = lim.generateSupplier(rng).get();
        assertTrue(val >= 20);
        assertTrue(val <= 30);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLimiterConstructedWithInvalidRange() {
        new DoubleLimiter(20, -30);
    }

    @Test
    public void testLimiterConstructedWithDistribution() {
        final DoubleLimiter lim = new DoubleLimiter(random -> 3.14);
        final Random rng = new Random();
        assertEquals(3.14, lim.generateSupplier(rng).get());
    }
}
