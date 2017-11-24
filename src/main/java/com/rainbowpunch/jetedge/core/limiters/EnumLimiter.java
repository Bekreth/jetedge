package com.rainbowpunch.jetedge.core.limiters;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class EnumLimiter<T extends Enum<T>> extends ObjectLimiter<T> {

    public static EnumLimiter<?> createEnumLimiter(Class<?> tEnum) {
        return new EnumLimiter(tEnum);
    }

    public EnumLimiter(Class<T> enumType) {
        this.acceptableObjectList = new ArrayList<>(EnumSet.allOf(enumType));
    }

    @Override
    protected List<T> configureObjectList() {
        return null;
    }
}