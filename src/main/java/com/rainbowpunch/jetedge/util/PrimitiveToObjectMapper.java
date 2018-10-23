package com.rainbowpunch.jetedge.util;

/**
 * Utility for handling the transitions between primitives and their associated objects. e.g. int vs Integer
 */
public final class PrimitiveToObjectMapper {

    private PrimitiveToObjectMapper() {

    }

    /**
     * If an incoming class is of a primitive type, this maps it to its corresponding Object type, else, it returns
     *      the object
     * @param clazz
     *          The class that will have a primitive type check run against it.
     * @return An object class type
     */
    public static Class<?> mapPrimitiveToObject(Class<?> clazz) {
        Class outputClass = clazz;

        if (clazz.equals(int.class)) outputClass = Integer.class;
        else if (clazz.equals(boolean.class)) outputClass = Boolean.class;
        else if (clazz.equals(short.class)) outputClass = Short.class;
        else if (clazz.equals(long.class)) outputClass = Long.class;
        else if (clazz.equals(float.class)) outputClass = Float.class;
        else if (clazz.equals(double.class)) outputClass = Double.class;
        else if (clazz.equals(char.class)) outputClass = Character.class;
        else if (clazz.equals(byte.class)) outputClass = Byte.class;

        return outputClass;
    }
}
