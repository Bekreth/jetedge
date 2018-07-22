package com.rainbowpunch.jetedge.math;

import org.junit.Test;

import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UniformDistributionTest {
    @Test
    public void testConstructUniformDistribution() {
        final UniformDistribution dist = UniformDistribution.of(-1, 3);
        assertEquals(-1.0, dist.getMin());
        assertEquals(3.0, dist.getMax());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructUniformDistributionWithInvalidArguments() {
        UniformDistribution.of(3, -3);
    }

    @Test
    public void testConstructUniformDistributionOfDefaults() {
        final UniformDistribution dist = UniformDistribution.ofDefaults();
        assertEquals(0.0, dist.getMin());
        assertEquals(1.0, dist.getMax());
    }

    @Test(expected = NullPointerException.class)
    public void testUniformDistributionApplyNullRNG() {
        final UniformDistribution dist = UniformDistribution.ofDefaults();
        dist.apply(null);
    }

    @Test
    public void testUniformDistributionApply() {
        final UniformDistribution dist = UniformDistribution.of(2.0, 4.0);
        final Random rng = mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.5);
        assertEquals(3.0, dist.apply(rng));
    }
}
