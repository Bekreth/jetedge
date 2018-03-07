package com.rainbowpunch.jetedge.core.reflection;

import java.util.function.Supplier;

public class ConstructorParameter {

    private Supplier<?> objectSupplier;
    private Class<?> objectType;

    public ConstructorParameter(Object object) {
        objectSupplier = () -> object;
        objectType = object.getClass();
    }

    public Supplier<?> getObjectSupplier() {
        return objectSupplier;
    }

    public Class<?> getObjectType() {
        return objectType;
    }
}
