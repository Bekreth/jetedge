package com.rainbowpunch.jtdg.core.limiters;

import java.util.Random;
import java.util.function.Supplier;

/**
 *
 */
public interface Limiter<T> {

    Supplier<T> generateSupplier(Random random);

}
