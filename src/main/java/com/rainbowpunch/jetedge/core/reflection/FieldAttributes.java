package com.rainbowpunch.jetedge.core.reflection;

import com.rainbowpunch.jetedge.core.exception.PojoConstructionException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A friendly wrapper for Field objects.
 */
public class FieldAttributes {
    private final Field field;
    private final Class clazz;
    private final List<Class> genericsOnClass;
    private final ClassAttributes parentClassAttributes;

    public FieldAttributes(ClassAttributes parentClassAttributes, Field field) {
        this.field = field;

        List<Class> intermediateGenerics = new ArrayList<>();

        Type genericType = field.getGenericType();
        if (genericType instanceof Class) {
            clazz = (Class) genericType;
        } else if (genericType instanceof TypeVariable) {
            clazz = parentClassAttributes.getClassForGenericName(genericType.getTypeName());
        } else {
            clazz = (Class) ((ParameterizedType) genericType).getRawType();
            intermediateGenerics = Arrays.asList(((ParameterizedType) genericType).getActualTypeArguments()).stream()
                    .map(type -> (Class) type)
                    .collect(Collectors.toList());
        }
        genericsOnClass = intermediateGenerics;
        this.parentClassAttributes = parentClassAttributes;
    }

    public String getName() {
        return field.getName();
    }

    public Field getField() {
        return field;
    }

    public ClassAttributes getType() {
        ClassAttributes attributes = ClassAttributes.create(parentClassAttributes, clazz, genericsOnClass);
        attributes.setFieldNameOfClass(field.getName());
        return attributes;
    }

    /**
     * Constructs a setter function that is capable of setting a field on a provided instance.
     * @param <T> the instance type of the object.
     * @param <U> the value type of the field in the object.
     * @return a binary consumer function that mutates a field on an object.
     */
    public <T, U> BiConsumer<T, U> getSetter() {
        final Field f = field; // limit closure
        return (instance, v) -> {
            Object assignment;
            if (requiresPrimitiveMapping(v, f)) {
                Class rootClass  = v.getClass().getComponentType();
                if (rootClass.equals(Boolean.class)) assignment = booleanArrayInversion((Boolean[]) v);
                else if (rootClass.equals(Byte.class)) assignment =  byteArrayInversion((Byte[]) v);
                else if (rootClass.equals(Character.class)) assignment = charArrayInversion((Character[]) v);
                else if (rootClass.equals(Double.class)) assignment = doubleArrayInversion((Double[]) v);
                else if (rootClass.equals(Float.class)) assignment = floatArrayInversion((Float[]) v);
                else if (rootClass.equals(Integer.class)) assignment = intArrayInversion((Integer[]) v);
                else if (rootClass.equals(Long.class)) assignment = longArrayInversion((Long[]) v);
                else if (rootClass.equals(Short.class)) assignment = shortArrayInversion((Short[]) v);
                else throw new PojoConstructionException("Unable to determine primitive type for mapping");
            } else {
                assignment = v;
            }
            f.setAccessible(true);
            try {
                f.set(instance, assignment);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Constructs a getter function that is capable of retrieving the value of a field on a provided instance.
     * @param <T> the instance type of the object.
     * @param <R> the value type of the field in the object.
     * @return a function that accepts an instance and returns a field value.
     */
    public <T, R> Function<T, R> getGetter() {
        final Field f = field;
        return instance -> {
            f.setAccessible(true);
            try {
                //noinspection unchecked
                return (R) f.get(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private boolean requiresPrimitiveMapping(Object obj, Field field) {
        if (obj == null) {
            return false;
        }
        final Class cls = obj.getClass();
        if (!cls.isArray()) {
            return false;
        }
        return cls.getComponentType() != field.getType().getComponentType();
    }

    private boolean[] booleanArrayInversion(Boolean[] input) {
        boolean[] output = new boolean[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    private byte[] byteArrayInversion(Byte[] input) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    private char[] charArrayInversion(Character[] input) {
        char[] output = new char[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    private double[] doubleArrayInversion(Double[] input) {
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    private float[] floatArrayInversion(Float[] input) {
        float[] output = new float[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    private int[] intArrayInversion(Integer[] input) {
        int[] output = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    private long[] longArrayInversion(Long[] input) {
        long[] output = new long[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    private short[] shortArrayInversion(Short[] input) {
        short[] output = new short[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }
}
