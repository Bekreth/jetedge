package com.rainbowpunch.jetedge.math;

import java.util.Random;
import java.util.function.Function;

/**
 * A function that generates values from a normal distribution.
 */
public final class NormalDistribution implements Function<Random, Double> {
    private final double mean;
    private final double stddev;

    private NormalDistribution(double mean, double stddev) {
        this.mean = mean;
        this.stddev = stddev;
    }

    /**
     * @return the mean parameter of the distribution.
     */
    public double getMean() {
        return mean;
    }

    /**
     * @return the standard deviation parameter of the distribution.
     */
    public double getStddev() {
        return stddev;
    }

    /**
     * Use the provided instance of {@code Random} to generate a normally distributed value.
     * @param random the random number generator to use.
     * @return a normally distributed value.
     */
    @Override
    public Double apply(Random random) {
        return random.nextGaussian() * this.stddev + this.mean;
    }

    /**
     * @return a standard normal distribution. That is, a normal distribution with a mean of 0 and a standard
     *      deviation of 1.
     */
    public static NormalDistribution ofDefaults() {
        return of(0, 1);
    }

    /**
     * @param mean the mean.
     * @param stddev the standard deviation.
     * @return a normal distribution parameterized by {@code mean} and {@code stddev}.
     * @throws IllegalArgumentException if {@code stddev} is negative.
     */
    public static NormalDistribution of(double mean, double stddev) {
        if (stddev < 0) {
            throw new IllegalArgumentException("stddev must not be negative");
        }
        return new NormalDistribution(mean, stddev);
    }
}
