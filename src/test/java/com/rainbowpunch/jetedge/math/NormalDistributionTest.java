package com.rainbowpunch.jetedge.math;

import org.junit.Test;

import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NormalDistributionTest {
    @Test
    public void testConstructNormalDistribution() {
        final NormalDistribution dist = NormalDistribution.of(3.14, 2.18);
        assertEquals(3.14, dist.getMean());
        assertEquals(2.18, dist.getStddev());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNormalDistributionWithInvalidArguments() {
        NormalDistribution.of(0, -2);
    }

    @Test
    public void testConstructNormalDistributionOfDefaults() {
        final NormalDistribution dist = NormalDistribution.ofDefaults();
        assertEquals(0.0, dist.getMean());
        assertEquals(1.0, dist.getStddev());
    }

    @Test(expected = NullPointerException.class)
    public void testNormalDistributionApplyNullRNG() {
        final NormalDistribution dist = NormalDistribution.ofDefaults();
        dist.apply(null);
    }

    @Test
    public void testNormalDistributionApply() {
        final NormalDistribution dist = NormalDistribution.of(2.0, 3.0);
        final Random rng = mock(Random.class);
        when(rng.nextGaussian()).thenReturn(5.0);
        assertEquals(17.0, dist.apply(rng));
    }
}
