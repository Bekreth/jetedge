package com.rainbowpunch.jetedge.spi;

import com.rainbowpunch.jetedge.core.FieldDataGenerator;
import com.rainbowpunch.jetedge.core.FieldSetter;
import com.rainbowpunch.jetedge.core.PojoAttributes;
import com.rainbowpunch.jetedge.core.analyzer.Analyzers;
import com.rainbowpunch.jetedge.core.analyzer.PojoAnalyzer;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;
import com.rainbowpunch.jetedge.core.reflection.FieldAttributes;

import java.lang.reflect.ParameterizedType;
import java.util.Random;

public final class PojoGeneratorBuilder<T> implements Cloneable {
    private final Class<T> clazz;
    private final PojoAttributes<T> pojoAttributes;

    public PojoGeneratorBuilder(Class<T> clazz) {
        this(clazz, new Random().nextInt());
    }

    public PojoGeneratorBuilder(Class<T> clazz, PojoAnalyzer pojoAnalyzer) {
        this(clazz, new Random().nextInt(), pojoAnalyzer);
    }

    public PojoGeneratorBuilder(Class<T> clazz, int randomSeed) {
        this(clazz, randomSeed, Analyzers.DEFAULT);
    }

    public PojoGeneratorBuilder(Class<T> clazz, int randomSeed, PojoAnalyzer pojoAnalyzer) {
        this(clazz, new PojoAttributes<>(clazz, pojoAnalyzer, randomSeed));
    }

    private PojoGeneratorBuilder(Class<T> clazz, PojoAttributes<T> pojoAttributes) {
        this.clazz = clazz;
        this.pojoAttributes = pojoAttributes;
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
        Class clazz = ((Class) ((ParameterizedType) limiter.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        pojoAttributes.putAllFieldLimiter(clazz, limiter);
        return this;
    }

    public PojoGeneratorBuilder<T> andUseAnalyzer(PojoAnalyzer pojoAnalyzer) {
        pojoAttributes.setPojoAnalyzer(pojoAnalyzer);
        return this;
    }

    public PojoGeneratorBuilder<T> andUseRandomSeed(int randomSeed) {
        pojoAttributes.setRandomSeed(randomSeed);
        return this;
    }

    public PojoGenerator<T> build(ClassAttributes classAttributes) {
        pojoAttributes.getParentPojoAnalyzer().extractFields(classAttributes)
                .filter(f -> filterFields(classAttributes, f))
                .forEach(this::createFieldSetters);

        new FieldDataGenerator<T>(pojoAttributes.getRandomSeed()).populateSuppliers(pojoAttributes); // TODO: 11/24/17 Look at making this static

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

    public PojoGenerator<T> build() {
        return this.build(ClassAttributes.create(clazz));
    }

    private void createFieldSetters(FieldAttributes f) {
        pojoAttributes.putFieldSetter(f.getName(), FieldSetter.create(f.getType(), f.getSetter()));
    }

    private boolean filterFields (ClassAttributes classAttributes, FieldAttributes fieldAttributes) {
        String classPrepender = classAttributes.getFieldNameOfClass();
        String fieldName = fieldAttributes.getName().toLowerCase();
        String qualifiedFieldName = !classPrepender.isEmpty() ? classPrepender + "." + fieldName : fieldName;
        return !pojoAttributes.shouldIgnore(qualifiedFieldName);
    }

    @Override
    public PojoGeneratorBuilder<T> clone() {
        return new PojoGeneratorBuilder<>(clazz, pojoAttributes.clone());
    }
}
