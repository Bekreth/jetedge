package com.rainbowpunch.jetedge.math;

import java.util.Random;
import java.util.function.Function;

public class UniformDistribution implements Function<Random, Double> {
    private final double min;
    private final double max;

    private UniformDistribution(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be greater than or equal to max");
        }
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    @Override
    public Double apply(Random random) {
        return random.nextDouble() * (this.max - this.min) + this.min;
    }

    public static UniformDistribution ofDefaults() {
        return of(0, 1);
    }

    public static UniformDistribution of(double min, double max) {
        return new UniformDistribution(min, max);
    }
}
