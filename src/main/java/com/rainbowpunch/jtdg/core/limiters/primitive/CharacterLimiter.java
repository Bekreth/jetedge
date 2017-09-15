package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.ObjectLimiter;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class CharacterLimiter extends ObjectLimiter<Character> {

    @Override
    protected List<Character> configureObjectList() {
        return IntStream.range(32, 126)
                .mapToObj(i -> (char) i)
                .collect(Collectors.toList());
    }

    @Override
    public Supplier<Character> generateSupplier(Random random) {
        return () -> {
            return super.generateSupplier(random).get();
        };
    }
}
