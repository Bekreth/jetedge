package com.rainbowpunch.jetedge.core.reflection;

import lombok.Getter;

import java.util.function.Supplier;

/**
 * A class for containing the constructor properties to be used in generating end classes.
 */
@Getter
public class ConstructorParameter {

    private Supplier<?> objectSupplier;
    private Class<?> objectType;

    public ConstructorParameter(Object object) {
        objectSupplier = () -> object;
        objectType = object.getClass();
    }
}
