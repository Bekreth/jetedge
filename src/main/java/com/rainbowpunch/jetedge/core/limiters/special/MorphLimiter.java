package com.rainbowpunch.jetedge.core.limiters.special;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * There are times when its easier to create data in one way, and then use a conversion function to create the type you're actually needing.
 *      A primary example of this is accessing Enum fields.  It is easier to use an Enum Limiter to create an enum object and then access
 *      that enum's methods via a function.
 * @param <T>
 * @param <U>
 */
public class MorphLimiter<T, U> implements Limiter<U> {

    private Limiter<T> dataCreator;
    private Function<T, U> morphingFunction;

    public MorphLimiter(Limiter<T> dataCreator, Function<T, U> morphingFunction) {
        this.dataCreator = dataCreator;
        this.morphingFunction = morphingFunction;
    }

    public static <X> MorphLimiter castToString(Limiter<X> dataCreator) {
        return new MorphLimiter<X, String>(dataCreator, (data) -> data.toString());
    }

    @Override
    public Supplier<U> generateSupplier(Random random) {
        return () -> {
            T data = dataCreator.generateSupplier(random).get();
            return morphingFunction.apply(data);
        };
    }

}
