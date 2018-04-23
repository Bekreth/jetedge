package com.rainbowpunch.jetedge.spi;

import com.rainbowpunch.jetedge.core.PojoAttributes;
import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;

public class PojoGeneratorImpl<T> implements PojoGenerator<T> {

    private final ClassAttributes classAttributes;
    private final PojoAttributes<T> pojoAttributes;

    PojoGeneratorImpl(ClassAttributes classAttributes, PojoAttributes<T> pojoAttributes) {
        this.classAttributes = classAttributes;
        this.pojoAttributes = pojoAttributes;
    }

    @Override
    public T generatePojo() {
        try {
            T newInstance = classAttributes.newInstance(pojoAttributes.getConstructorObjectList());
            pojoAttributes.apply(newInstance);
            return newInstance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClassAttributes getClassAttributes() {
        return classAttributes;
    }

    @Override
    public PojoAttributes<T> getPojoAttributes() {
        return pojoAttributes;
    }
}
