package com.rainbowpunch.jtdg.core.limiters;

import java.util.Random;
import java.util.function.Supplier;

/**
 *
 */
@FunctionalInterface
public interface Limiter<T> {
    Supplier<T> generateSupplier(Random random);
}
