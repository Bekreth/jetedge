package com.rainbowpunch.jetedge.core.limiters.common.java;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class BigIntegerLimiterTest {

    private Random random = new Random();

    @Test
    public void testDefaultBigIntLimiter() {
        BigIntegerLimiter bigIntSupplier = new BigIntegerLimiter();
        BigInteger bigint = BigInteger.valueOf(1048577);

        for (int i = 0; i < 100; i++) {
            BigInteger generatedValue = bigIntSupplier.generateSupplier(random).get();
            assertEquals(1, bigint.compareTo(generatedValue));
        }
    }

    @Test
    public void testBigIntLimiter10Bits() {
        BigIntegerLimiter bigIntSupplier = new BigIntegerLimiter(10);
        BigInteger bigint = BigInteger.valueOf(1025);

        for (int i = 0; i < 100; i++) {
            BigInteger generatedValue = bigIntSupplier.generateSupplier(random).get();
            assertEquals(1, bigint.compareTo(generatedValue));
        }
    }

    @Test
    public void testBigIntLimiter10Bits_range1000() {
        BigIntegerLimiter bigIntSupplier = new BigIntegerLimiter(10, 1000);
        BigInteger bigint = BigInteger.valueOf(2025);
        BigInteger lowerBound = BigInteger.valueOf(999);

        for (int i = 0; i < 100; i++) {
            BigInteger generatedValue = bigIntSupplier.generateSupplier(random).get();
            assertEquals(1, bigint.compareTo(generatedValue));
            assertEquals(-1, lowerBound.compareTo(generatedValue));
        }
    }

}