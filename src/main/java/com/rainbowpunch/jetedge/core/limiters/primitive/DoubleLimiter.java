package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;
import com.rainbowpunch.jetedge.math.UniformDistribution;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A limiter for Double data fields.
 */
public class DoubleLimiter extends SimpleAbstractLimiter<Double> {
    private final Function<Random, Double> randomVariable;

    /**
     * Constructs a limiter that generates doubles that are distributed uniformly between 0 and 1 (inclusive).
     */
    public DoubleLimiter() {
        this(rng -> rng.nextDouble());
    }

    /**
     * Constructs a limiter that generates doubles that are distributed uniformly between {@code min} and {@code max}.
     * If {@code min} is greater than {@code max}, the limiter will generate only the zero value.
     * @param min the minimum value the limiter can generate
     * @param max the maximum value the limiter can generate
     */
    public DoubleLimiter(double min, double max) {
        this(UniformDistribution.of(min, max));
    }

    /**
     * Constructs a limiter that generates doubles via a call to the distribution function {@code dist}.
     * @param dist a Function that accepts a Random as input and outputs a double.
     */
    public DoubleLimiter(Function<Random, Double> dist) {
        this.randomVariable = dist;
    }

    @Override
    public Supplier<Double> generateSupplier(Random random) {
        return () -> this.randomVariable.apply(random);
    }
}
