package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import org.junit.Test;

import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateLimiterTest {

    private Random random = new Random();

    @Test
    public void testDefaultDateLimiter() {
        DateLimiter dateSupplier = new DateLimiter();
        Date minComparison = new Date(0L);
        Date maxComparison = new Date(Long.MAX_VALUE);

        for (int i = 0; i < 100; i++) {
            Date generatedValue = dateSupplier.generateSupplier(random).get();
            assertEquals(-1, minComparison.compareTo(generatedValue));
            assertEquals(1, maxComparison.compareTo(generatedValue));
        }
    }

    @Test
    public void testBoundedDateLimiterWithDate() {
        Date bound = new Date(10000000000L);
        DateLimiter dateSupplier = new DateLimiter(bound);

        for (int i = 0; i < 100; i++) {
            Date generatedValue = dateSupplier.generateSupplier(random).get();
            assertEquals(1, bound.compareTo(generatedValue));
        }
    }

    @Test
    public void testRangedDateLimiterWithDate() {
        Date inclusiveMin = new Date(100000L);
        Date exclusiveMax = new Date(10000000000L);
        DateLimiter dateSupplier = new DateLimiter(inclusiveMin, exclusiveMax);

        for (int i = 0; i < 100; i++) {
            Date generatedValue = dateSupplier.generateSupplier(random).get();
            assertTrue(inclusiveMin.compareTo(generatedValue) == -1 || inclusiveMin.compareTo(generatedValue) == 0);
            assertEquals(1, exclusiveMax.compareTo(generatedValue));
        }
    }

    @Test
    public void testBoundedDateLimiterWithLong() {
        Long boundLong = 10000000000L;
        Date bound = new Date(boundLong);
        DateLimiter dateSupplier = new DateLimiter(boundLong);

        for (int i = 0; i < 100; i++) {
            Date generatedValue = dateSupplier.generateSupplier(random).get();
            assertEquals(1, bound.compareTo(generatedValue));
        }
    }

    @Test
    public void testRangedDateLimiterWithLong() {
        Long originLong = 100000L;
        Long boundLong = 10000000000L;
        Date originDate = new Date(originLong);
        Date exclusiveDate = new Date(boundLong);
        DateLimiter dateSupplier = new DateLimiter(originLong, boundLong);

        for (int i = 0; i < 100; i++) {
            Date generatedValue = dateSupplier.generateSupplier(random).get();
            assertTrue(originDate.compareTo(generatedValue) == -1 || originDate.compareTo(generatedValue) == 0);
            assertEquals(1, exclusiveDate.compareTo(generatedValue));
        }
    }

    @Test
    public void testDateLimiterSeedablility() {
        Random seededRandom = new Random(100);
        DateLimiter dateSupplier = new DateLimiter();
        Date comparison = new Date(343513556442L);
        Date generatedValue = dateSupplier.generateSupplier(seededRandom).get();
        assertEquals(0, comparison.compareTo(generatedValue));
    }

    @Test(expected = LimiterConstructionException.class)
    public void testBoundSmallerThanOrigin() {
        new DateLimiter(Long.MAX_VALUE, Long.MIN_VALUE);
    }

    @Test(expected = LimiterConstructionException.class)
    public void testNegativeOriginPositiveBound() {
        new DateLimiter(-1L, Long.MAX_VALUE);
    }

    @Test(expected = LimiterConstructionException.class)
    public void testPositiveOriginNegativeBound() {
        new DateLimiter(Long.MIN_VALUE, -1L);
    }

    @Test(expected = LimiterConstructionException.class)
    public void testNegativeOriginNegativeBound() {
        new DateLimiter(-1L, -1L);
    }

    @Test(expected = LimiterConstructionException.class)
    public void testNegativeBound() {
        new DateLimiter(Long.MAX_VALUE, Long.MIN_VALUE);
    }
}
