package com.rainbowpunch.jtdg.spi;

import com.rainbowpunch.jtdg.core.FieldSetter;
import com.rainbowpunch.jtdg.core.analyzer.DefaultPojoAnalyzer;
import com.rainbowpunch.jtdg.core.FieldDataGenerator;
import com.rainbowpunch.jtdg.core.analyzer.PojoAnalyzer;
import com.rainbowpunch.jtdg.core.PojoAttributes;
import com.rainbowpunch.jtdg.core.limiters.Limiter;
import com.rainbowpunch.jtdg.core.reflection.ClassAttributes;

import java.lang.reflect.ParameterizedType;
import java.util.Random;

import static java.util.Objects.requireNonNull;

public final class PojoGeneratorBuilder<T> implements Cloneable {
    private final Class<T> clazz;
    private final PojoAttributes<T> pojoAttributes;
    private int randomSeed;
    private PojoAnalyzer pojoAnalyzer;

    public PojoGeneratorBuilder(Class<T> clazz) {
        this(clazz, new Random().nextInt());
    }

    public PojoGeneratorBuilder(Class<T> clazz, PojoAnalyzer pojoAnalyzer) {
        this(clazz, new Random().nextInt(), pojoAnalyzer);
    }

    public PojoGeneratorBuilder(Class<T> clazz, int randomSeed) {
        this(clazz, randomSeed, new PojoAttributes<>(clazz, DefaultPojoAnalyzer.class, randomSeed), new DefaultPojoAnalyzer());
    }

    public PojoGeneratorBuilder(Class<T> clazz, int randomSeed, PojoAnalyzer pojoAnalyzer) {
        this(clazz, randomSeed, new PojoAttributes<>(clazz, pojoAnalyzer.getClass(), randomSeed), pojoAnalyzer);
    }

    private PojoGeneratorBuilder(Class<T> clazz, int randomSeed, PojoAttributes<T> pojoAttributes, PojoAnalyzer pojoAnalyzer) {
        this.clazz = clazz;
        this.randomSeed = randomSeed;
        this.pojoAttributes = pojoAttributes;
        this.pojoAnalyzer = pojoAnalyzer;
    }


    public PojoGeneratorBuilder<T> andLimitField(String fieldName, Limiter<?> limiter) {
        pojoAttributes.putFieldLimiter(fieldName, limiter);
        return this;
    }

    public PojoGeneratorBuilder<T> andIgnoreField(String fieldName) {
        pojoAttributes.ignoreField(fieldName);
        return this;
    }

    public PojoGeneratorBuilder<T> andLimitAllFieldsOf(Limiter<?> limiter) {
        System.out.println(limiter.getClass().getGenericInterfaces().length);
        Class clazz = ((Class) ((ParameterizedType) limiter.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        pojoAttributes.putAllFieldLimiter(clazz, limiter);
        return this;
    }

    public PojoGeneratorBuilder<T> andUseAnalyzer(PojoAnalyzer pojoAnalyzer) {
        this.pojoAnalyzer = requireNonNull(pojoAnalyzer);
        return this;
    }

    public PojoGeneratorBuilder<T> andUseRandomSeed(int randomSeed) {
        this.randomSeed = randomSeed;
        return this;
    }

    public PojoGenerator<T> build() {
        ClassAttributes classAttributes = ClassAttributes.create(clazz);
        pojoAnalyzer.extractFields(classAttributes)
                .filter(f -> !pojoAttributes.shouldIgnore(f.getName().toLowerCase()))
                .forEach(f -> pojoAttributes.putFieldSetter(
                        f.getName(),
                        FieldSetter.create(f.getType(), f.getSetter())
                ));
        new FieldDataGenerator<T>(randomSeed).populateSuppliers(pojoAttributes);
        return () -> {
            try {
                T newInstance = clazz.newInstance();
                pojoAttributes.apply(newInstance);
                return newInstance;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public PojoGeneratorBuilder<T> clone() {
        return new PojoGeneratorBuilder<>(clazz, randomSeed, pojoAttributes.clone(), pojoAnalyzer);
    }
}
