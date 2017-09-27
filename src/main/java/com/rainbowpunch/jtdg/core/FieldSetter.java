package com.rainbowpunch.jtdg.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 */
public class FieldSetter<T, U> {

    private Class clazz;
    private BiConsumer<T, U> consumer;
    private Supplier<U> supplier;
    private List<Class<?>> genericFields;

    public static <V> FieldSetter makeFieldSetter(Type type) {
        FieldSetter fieldSetter = null;

        List<Class<?>> genericFields = null;
        Class clazz = null;
        try {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            clazz = Class.forName(parameterizedType.getRawType().getTypeName());
            genericFields = Arrays.stream(parameterizedType.getActualTypeArguments())
                    .map(FieldSetter::getGenericClass)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            clazz = (Class) type;
        }

        if (clazz == Integer.class || clazz == int.class) fieldSetter = new FieldSetter<V, Integer>();
        else if (clazz == Boolean.class || clazz == boolean.class) fieldSetter = new FieldSetter<V, Boolean>();
        else if (clazz == Short.class || clazz == short.class) fieldSetter = new FieldSetter<V, Short>();
        else if (clazz == Float.class || clazz == float.class) fieldSetter = new FieldSetter<V, Float>();
        else if (clazz == Double.class  || clazz == double.class) fieldSetter = new FieldSetter<V, Double>();
        else if (clazz == Long.class || clazz == long.class) fieldSetter = new FieldSetter<V, Long>();
        else if (clazz == Character.class || clazz == char.class) fieldSetter = new FieldSetter<V, Character>();
        else if (clazz == String.class) fieldSetter = new FieldSetter<V, String>();
        else if (clazz == Enum.class) fieldSetter = new FieldSetter<V, Enum>();
        else if (clazz == List.class) fieldSetter = new FieldSetter<V, List>();
        else fieldSetter = new FieldSetter<V, Object>();

        if (fieldSetter == null) {
            throw new RuntimeException("FieldSetter failed to match class : " + clazz);
        }

        fieldSetter.clazz = clazz;
        fieldSetter.genericFields = genericFields;
        return fieldSetter;
    }

    private FieldSetter() {

    }

    private static Class<?> getGenericClass(Type type) {
        try {
            return Class.forName(type.getTypeName());
        } catch (Exception e) {
            throw new RuntimeException("Error getting generic class", e);
        }
    }

    public void setConsumer(BiConsumer<T, U> consumer) {
        this.consumer = consumer;
    }

    public void setSupplier(Supplier<U> supplier) {
        this.supplier = supplier;
    }

    public Class getClazz() {
        return clazz;
    }

    public List<Class<?>> getGenericFields() {
        return genericFields;
    }

    public void apply(T instance) {
        consumer.accept(instance, supplier.get());
    }
}
