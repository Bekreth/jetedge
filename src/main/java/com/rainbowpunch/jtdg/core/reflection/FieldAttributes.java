package com.rainbowpunch.jtdg.core.reflection;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

public class FieldAttributes {
    private final Field field;

    public FieldAttributes(Field field) {
        this.field = field;
    }

    public String getName() {
        return field.getName();
    }

    public Field getField() {
        return field;
    }

    public ClassAttributes getType() {
        return ClassAttributes.create(field.getType(), field.getGenericType());
    }

    public <T, U> BiConsumer<T, U> getSetter() {
        final Field f = field; // limit closure
        return (instance, value) -> {
            f.setAccessible(true);
            try {
                f.set(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
