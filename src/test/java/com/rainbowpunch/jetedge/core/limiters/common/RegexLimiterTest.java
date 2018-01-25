package com.rainbowpunch.jetedge.core.limiters.common;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.util.ReadableCharList;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegexLimiterTest {

    @Test
    public void testCharRanges_lower() {
        RegexLimiter limiter = new RegexLimiter("[a-d]");
        String output = limiter.generateSupplier(new Random()).get();
        assertTrue("abcd".contains(output));
    }

    @Test
    public void testCharRanges_upper() {
        RegexLimiter limiter = new RegexLimiter("[A-D]");
        String output = limiter.generateSupplier(new Random()).get();
        assertTrue("ABCD".contains(output));
    }

    @Test
    public void testCharRanges_digits() {
        RegexLimiter limiter = new RegexLimiter("[0-9]");
        String output = limiter.generateSupplier(new Random()).get();
        assertTrue("0123456789".contains(output));
    }

    @Test
    public void testCompleteCharRange() {
        RegexLimiter limiter = new RegexLimiter("[A-Za-z0-9]");
        String output = limiter.generateSupplier(new Random()).get();
        assertTrue(ReadableCharList.LIST_OF_ALPHA_NUMERIC_CHAR.contains(output.toCharArray()[0]));
    }

    @Test(expected = LimiterConstructionException.class)
    public void testCharRanges_failedCharSetMismatch1() {
        RegexLimiter limiter = new RegexLimiter("[0-a]");
    }

    @Test(expected = LimiterConstructionException.class)
    public void testCharRanges_failedCharSetMismatch2() {
        RegexLimiter limiter = new RegexLimiter("[0-A]");
    }

    @Test(expected = LimiterConstructionException.class)
    public void testCharRanges_failedCharSetMismatch3() {
        RegexLimiter limiter = new RegexLimiter("[a-A]");
    }

    @Test(expected = LimiterConstructionException.class)
    public void testCharRanges_failedUnderlappingCharSet() {
        RegexLimiter limiter = new RegexLimiter("[D-A]");
    }

    @Test
    public void testEscapeDotCharacter() {
        RegexLimiter limiter = new RegexLimiter("\\.");
        String output = limiter.generateSupplier(new Random()).get();
        assertEquals(".", output);
    }

}