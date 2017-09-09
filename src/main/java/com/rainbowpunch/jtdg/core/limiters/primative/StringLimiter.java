package com.rainbowpunch.jtdg.core.limiters.primative;

import com.rainbowpunch.jtdg.core.limiters.ObjectLimiter;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class StringLimiter extends ObjectLimiter<String> {

    private Integer length = 30;

    @Override
    protected List<String> configureObjectList() {
        return IntStream.range(32, 126)
                .mapToObj(i -> {
                    return Character.toString((char) i);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Supplier<String> generateSupplier(Random random) {
        // TODO: 8/7/17 Make this more effecient by using collectionLimiter instead of objectLimiter
        return () -> {
            return IntStream.range(0, length)
                    .sequential()
                    .mapToObj((i) -> super.generateSupplier(random))
                    .map(Supplier::get)
                    .collect(Collectors.joining());
        };
    }
}
