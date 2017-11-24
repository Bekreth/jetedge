package com.rainbowpunch.jetedge.core.reflection;

import com.rainbowpunch.jetedge.core.exception.PojoConstructionException;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A friendly wrapper for Field objects.
 */
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

    /**
     * Constructs a setter function that is capable of setting a field on a provided instance.
     * @param <T> the instance type of the object.
     * @param <U> the value type of the field in the object.
     * @return a binary consumer function that mutates a field on an object.
     */
    public <T, U> BiConsumer<T, U> getSetter() {
        final Field f = field; // limit closure
        return (instance, value) -> {
            f.setAccessible(true);
            try {
                if (requiresPrimitiveMapping(value, f)) {
                    Class rootClass  = value.getClass().getComponentType();

                    if (rootClass.equals(Boolean.class)) f.set(instance, booleanArrayInversion((Boolean[]) value));
                    else if (rootClass.equals(Byte.class)) f.set(instance, byteArrayInversion((Byte[]) value));
                    else if (rootClass.equals(Character.class)) f.set(instance, charArrayInversion((Character[]) value));
                    else if (rootClass.equals(Double.class)) f.set(instance, doubleArrayInversion((Double[]) value));
                    else if (rootClass.equals(Float.class)) f.set(instance, floatArrayInversion((Float[]) value));
                    else if (rootClass.equals(Integer.class)) f.set(instance, intArrayInversion((Integer[]) value));
                    else if (rootClass.equals(Long.class)) f.set(instance, longArrayInversion((Long[]) value));
                    else if (rootClass.equals(Short.class)) f.set(instance, shortArrayInversion((Short[]) value));
                    else throw new PojoConstructionException("Failed to map primitive array inversion");

                } else {
                    f.set(instance, value);
                }
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
        Class objClazz = obj.getClass();
        if (objClazz.isArray()) {
            if (objClazz.getComponentType() != field.getType().getComponentType()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
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
