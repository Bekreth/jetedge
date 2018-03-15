package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class BigDecimalLimiterTest {

    private static final int RANDOM_SEED = 82634567;

    private Random random;

    @Before
    public void init() {
        random = new Random(RANDOM_SEED);
    }


    @Test(expected = LimiterConstructionException.class)
    public void testUnbalancedPrecision() {
        new BigDecimalLimiter("100.000", "0");
    }

    @Test(expected = LimiterConstructionException.class)
    public void testUnallowedCharacters_firstHalf() {
        new BigDecimalLimiter("abc.abd", "000.000");
    }

    @Test(expected = LimiterConstructionException.class)
    public void testUnallowedCharacters_secondHalf() {
        new BigDecimalLimiter("000.000", "abc.abd");
    }

    @Test
    public void testSimpleSuccess() {
        BigDecimalLimiter limiter = new BigDecimalLimiter();
        BigDecimal upperLimit = new BigDecimal("100.00");
        BigDecimal lowerLimit = new BigDecimal("000.00");

        for (int i = 0; i < 100; i ++) {
            BigDecimal decimal = limiter.generateSupplier(random).get();
            assertTrue(decimal.compareTo(upperLimit) <= 0);
            assertTrue(decimal.compareTo(lowerLimit) >= 0);
        }
    }

    @Test
    public void testSimpleSuccess_providedUpperbound() {
        BigDecimalLimiter limiter = new BigDecimalLimiter("446.715");
        BigDecimal upperLimit = new BigDecimal("446.715");
        BigDecimal lowerLimit = new BigDecimal("000.000");

        for (int i = 0; i < 100; i ++) {
            BigDecimal decimal = limiter.generateSupplier(random).get();
            assertTrue(decimal.compareTo(upperLimit) <= 0);
            assertTrue(decimal.compareTo(lowerLimit) >= 0);
        }
    }

    @Test
    public void testSimpleSuccess_providedUpperAndLowerBound() {
        BigDecimalLimiter limiter = new BigDecimalLimiter("100.015", "-200.700");
        BigDecimal upperLimit = new BigDecimal("100.015");
        BigDecimal lowerLimit = new BigDecimal("-200.700");

        for (int i = 0; i < 100; i ++) {
            BigDecimal decimal = limiter.generateSupplier(random).get();
            assertTrue(decimal.compareTo(upperLimit) <= 0);
            assertTrue(decimal.compareTo(lowerLimit) >= 0);
        }
    }

}