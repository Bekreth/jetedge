package com.rainbowpunch.jetedge.math;

import java.util.Random;
import java.util.function.Function;

public class NormalDistribution implements Function<Random, Double> {
    private final double mean;
    private final double stddev;

    private NormalDistribution(double mean, double stddev) {
        this.mean = mean;
        this.stddev = stddev;
    }

    public double getMean() {
        return mean;
    }

    public double getStddev() {
        return stddev;
    }

    @Override
    public Double apply(Random random) {
        return random.nextGaussian() * this.stddev + this.mean;
    }

    public static NormalDistribution ofDefaults() {
        return of(0, 1);
    }

    public static NormalDistribution of(double mean, double stddev) {
        return new NormalDistribution(mean, stddev);
    }
}
