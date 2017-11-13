package com.rainbowpunch.jtdg.core.limiters;

import com.rainbowpunch.jtdg.core.PojoAttributes;
import com.rainbowpunch.jtdg.spi.PojoGenerator;
import com.rainbowpunch.jtdg.spi.PojoGeneratorBuilder;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DefaultPojoLimiter<T> implements Limiter<T> {

    private final PojoGenerator<T> generator;

    public DefaultPojoLimiter(Class<T> clazz, PojoAttributes<T> parentAttributes) {
        PojoGeneratorBuilder<T> builder = new PojoGeneratorBuilder<>(clazz,
                parentAttributes.getRandomSeed(), parentAttributes.getParentPojoAnalyzer());

        parentAttributes.getLimiters().entrySet().stream()
                .filter(entry -> !entry.getKey().equals(parentAttributes.getPojoClazz()))
                .flatMap(this::flattenLimiterMap)
                .forEach(entry -> builder.andLimitField(entry.getKey(), entry.getValue()));

        parentAttributes.getAllFieldLimiterMap().entrySet().stream()
                .map(Map.Entry::getValue)
                .forEach(builder::andLimitAllFieldsOf);

        generator = builder.build();
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
