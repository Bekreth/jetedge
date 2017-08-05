package com.rainbowpunch.jtdg.core;

import sun.reflect.generics.tree.Tree;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 *
 */
public class FieldSetter<T, U> {

    private Class clazz;
    private BiConsumer<T, U> consumer;
    private Supplier<U> supplier;

    public static <V> FieldSetter makeFieldSetter(Class clazz) {
        FieldSetter fieldSetter = null;

        if (clazz == Integer.class || clazz == int.class) fieldSetter = new FieldSetter<V, Integer>();
        else if (clazz == Short.class || clazz == short.class) fieldSetter = new FieldSetter<V, Short>();
        else if (clazz == Float.class || clazz == float.class) fieldSetter = new FieldSetter<V, Float>();
        else if (clazz == Double.class  || clazz == double.class) fieldSetter = new FieldSetter<V, Double>();
        else if (clazz == Long.class || clazz == long.class) fieldSetter = new FieldSetter<V, Long>();
        else if (clazz == Character.class || clazz == char.class) fieldSetter = new FieldSetter<V, Character>();
        else if (clazz == String.class) fieldSetter = new FieldSetter<V, String>();
        else if (clazz == Set.class) fieldSetter = new FieldSetter<V, Set>();
        else if (clazz == List.class) fieldSetter = new FieldSetter<V, List>();
        else if (clazz == Queue.class) fieldSetter = new FieldSetter<V, Queue>();
        else if (clazz == Tree.class) fieldSetter = new FieldSetter<V, Tree>();
        else if (clazz == Map.class) fieldSetter = new FieldSetter<V, Map>();
        else fieldSetter = new FieldSetter<V, Object>();

        if (fieldSetter == null) {
            throw new RuntimeException("FieldSetter failed to match class : " + clazz);
        }

        fieldSetter.clazz = clazz;
        return fieldSetter;
    }

    private FieldSetter() {

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

    public void apply(T instance) {
        consumer.accept(instance, supplier.get());
    }
}
