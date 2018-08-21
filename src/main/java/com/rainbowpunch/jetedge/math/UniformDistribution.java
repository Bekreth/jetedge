package com.rainbowpunch.jetedge.math;

import java.util.Random;
import java.util.function.Function;

/**
 * A function that generates values from a continuous uniform distribution.
 */
public class UniformDistribution implements Function<Random, Double> {
    private final double min;
    private final double max;

    private UniformDistribution(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @return the min parameter of the distribution.
     */
    public double getMin() {
        return min;
    }

    /**
     * @return the max parameter of the distribution.
     */
    public double getMax() {
        return max;
    }

    /**
     * Use the provided instance of {@code Random} to generate a uniformly distributed value.
     * @param random the random number generator to use.
     * @return a normally distributed value.
     */
    @Override
    public Double apply(Random random) {
        return random.nextDouble() * (this.max - this.min) + this.min;
    }

    /**
     * @return a uniform distribution with a minimum of 0 and a maximum of 1 (inclusive).
     */
    public static UniformDistribution ofDefaults() {
        return of(0, 1);
    }

    /**
     * @param min the minimum.
     * @param max the maximum.
     * @return a uniform distribution with a minimum of {@code min} and a maximum of {@code max} (inclusive).
     * @throws IllegalArgumentException if {@code min} is greater than {@code max}.
     */
    public static UniformDistribution of(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be greater than or equal to max");
        }
        return new UniformDistribution(min, max);
    }
}
