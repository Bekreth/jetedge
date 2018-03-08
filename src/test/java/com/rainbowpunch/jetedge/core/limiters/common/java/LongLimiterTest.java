package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.limiters.primitive.LongLimiter;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LongLimiterTest {

    private Random random = new Random();

    @Test
    public void testDefaultLongLimiter() {
        LongLimiter longSupplier = new LongLimiter();
        Long minComparison = 0L;
        Long maxComparison = Long.MAX_VALUE;

        for (int i = 0; i < 100; i++) {
            Long generatedValue = longSupplier.generateSupplier(random).get();
            assertEquals(-1, minComparison.compareTo(generatedValue));
            assertEquals(1, maxComparison.compareTo(generatedValue));
        }
    }

    @Test
    public void testBoundedLongLimiter() {
        Long bound = 10000000000L;
        LongLimiter longSupplier = new LongLimiter(bound);
        Long comparison = bound;
        for (int i = 0; i < 100; i++) {
            Long generatedValue = longSupplier.generateSupplier(random).get();
            assertEquals(1, comparison.compareTo(generatedValue));
        }
    }

    @Test
    public void testRangedLongLimiter() {
        Long inclusiveMin = 100000L;
        Long exclusiveMax = 10000000000L;
        LongLimiter longSupplier = new LongLimiter(inclusiveMin, exclusiveMax);

        for (int i = 0; i < 100; i++) {
            Long generatedValue = longSupplier.generateSupplier(random).get();
            assertTrue(inclusiveMin.compareTo(generatedValue) == -1 || inclusiveMin.compareTo(generatedValue) == 0);
            assertEquals(1, exclusiveMax.compareTo(generatedValue));
        }
    }

    @Test
    public void testLongLimiterSeedablility() {
        Random seededRandom = new Random(100);
        LongLimiter longSupplier = new LongLimiter();
        Long comparison = 6659363606675156442L;
        Long generatedValue = longSupplier.generateSupplier(seededRandom).get();
        assertEquals(0, comparison.compareTo(generatedValue));
    }
}
