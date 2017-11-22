package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.core.reflection.ClassAttributes;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 *
 */
public class FieldSetter<T, U> {

    private final ClassAttributes classAttributes;
    private final BiConsumer<T, U> consumer;
    private Supplier<U> supplier;

    @SuppressWarnings("unchecked")
    public static <T> FieldSetter create(ClassAttributes classAttributes, BiConsumer<T, ?> consumer) {
        if (classAttributes.is(Integer.class, int.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Integer>) consumer);
        else if (classAttributes.is(Boolean.class, boolean.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Boolean>) consumer);
        else if (classAttributes.is(Short.class, short.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Short>) consumer);
        else if (classAttributes.is(Long.class, long.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Long>) consumer);
        else if (classAttributes.is(Float.class, float.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Float>) consumer);
        else if (classAttributes.is(Double.class, double.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Double>) consumer);
        else if (classAttributes.is(Character.class, char.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Character>) consumer);
        else if (classAttributes.is(Byte.class, byte.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Byte>) consumer);
        else if (classAttributes.is(String.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, String>) consumer);
        else if (classAttributes.is(List.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, List>) consumer);
        else if (classAttributes.is(Enum.class))
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Enum>) consumer);
        else
            return new FieldSetter<>(classAttributes, (BiConsumer<T, Object>) consumer);
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
