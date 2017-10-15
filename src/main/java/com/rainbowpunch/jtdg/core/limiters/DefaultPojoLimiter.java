package com.rainbowpunch.jtdg.core.limiters;

import com.rainbowpunch.jtdg.core.PojoAttributes;
import com.rainbowpunch.jtdg.spi.PojoGenerator;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DefaultPojoLimiter<T> implements Limiter<T> {

    private final PojoGenerator<T> generator;

    public DefaultPojoLimiter(Class<T> clazz, PojoAttributes<T> parentAttributes) {
        this.generator = new PojoGenerator<>(clazz, parentAttributes.getRandomSeed());
        parentAttributes.getLimiters()
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(parentAttributes.getPojoClazz()))
                .flatMap(this::flattenLimiterMap)
                .forEach(entry -> generator.andLimitField(entry.getKey(), entry.getValue()));
        parentAttributes.getAllFieldLimiterMap()
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .forEach(generator::andLimitAllFieldsOf);
        generator.analyzePojo();
    }

    private Stream<Map.Entry<String, Limiter<?>>> flattenLimiterMap(Map.Entry<Class, Map<String, Limiter<?>>> entry) {
        return entry.getValue()
                .entrySet()
                .stream();
    }

    @Override
    public Supplier<T> generateSupplier(Random random) {
        return generator::generatePojo;
    }
}
