package com.rainbowpunch.jetedge.core.limiters;

import java.util.Random;
import java.util.function.Supplier;

/**
 * An interface for specifying how data of type <code>T</code> should be generated.
 */
@FunctionalInterface
public interface Limiter<T> {
    Supplier<T> generateSupplier(Random random);
}
