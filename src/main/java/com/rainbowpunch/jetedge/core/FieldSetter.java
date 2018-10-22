package com.rainbowpunch.jetedge.core;

import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * This class aligns data generators with the consumers that apply the data to the appropriate field within the POJO.
 */
public class FieldSetter<T, U> {

    private final ClassAttributes classAttributes;
    private final BiConsumer<T, U> consumer;
    private Supplier<U> supplier;

    @SuppressWarnings("unchecked")
    public static <T> FieldSetter create(ClassAttributes classAttributes, BiConsumer<T, ?> consumer) {
        if (classAttributes.is(Integer.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Integer>) consumer);
        else if (classAttributes.is(Boolean.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Boolean>) consumer);
        else if (classAttributes.is(Short.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Short>) consumer);
        else if (classAttributes.is(Long.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Long>) consumer);
        else if (classAttributes.is(Float.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Float>) consumer);
        else if (classAttributes.is(Double.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Double>) consumer);
        else if (classAttributes.is(Character.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Character>) consumer);
        else if (classAttributes.is(Byte.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Byte>) consumer);
        else if (classAttributes.is(String.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, String>) consumer);
        else if (classAttributes.is(List.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, List>) consumer);
        else if (classAttributes.is(Enum.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Enum>) consumer);
        else return new FieldSetter<>(classAttributes, (BiConsumer<T, Object>) consumer);
    }

    private FieldSetter(ClassAttributes classAttributes, BiConsumer<T, U> consumer) {
        this.classAttributes = classAttributes;
        this.consumer = consumer;
    }

    public void setSupplier(Supplier<U> supplier) {
        this.supplier = supplier;
    }

    public ClassAttributes getClassAttributes() {
        return classAttributes;
    }

    public void apply(T instance) {
        consumer.accept(instance, supplier.get());
    }
}
