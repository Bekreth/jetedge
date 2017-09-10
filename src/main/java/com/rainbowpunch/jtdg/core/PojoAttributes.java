package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.core.limiters.Limiter;
import com.rainbowpunch.jtdg.core.limiters.NestedLimiter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An entity of all the Pojo's attributes
 */
public class PojoAttributes<T> {

    private Class<T> pojoClazz;
    private Map<Class, Map<String, Limiter<?>>> masterLimiterMap;
    private Map<String, FieldSetter<T, ?>> fieldSetterMap;

    public PojoAttributes(Class pojoClazz) {
        this.pojoClazz = pojoClazz;

        this.masterLimiterMap = new HashMap<>();
        this.masterLimiterMap.put(this.pojoClazz, new HashMap<>());
        this.fieldSetterMap = new HashMap<>();
    }

    public Class<T> getPojoClazz() {
        return pojoClazz;
    }

    public Map<Class, Map<String, Limiter<?>>> getLimiters() {
        return masterLimiterMap;
    }

    public FieldSetter<T, ?> getFieldSetter(String fieldName) {
        return fieldSetterMap.get(fieldName);
    }

    public void putFieldSetter(String fieldName, FieldSetter<T, ?> fieldSetter) {
        fieldSetterMap.put(fieldName, fieldSetter);
    }

    public void putFieldLimiter(String fieldName, Limiter limiter) {
        if (limiter instanceof NestedLimiter) {
            Class clazz = ((NestedLimiter) limiter).getClazz();
            if (!masterLimiterMap.containsKey(clazz)) masterLimiterMap.put(clazz, new HashMap<>());
            masterLimiterMap.get(clazz).put(fieldName.toLowerCase(), limiter);
        } else {
            masterLimiterMap.get(pojoClazz).put(fieldName.toLowerCase(), limiter);
        }
    }

    public void putFieldLimiter(String fieldName, NestedLimiter limiter) {
        masterLimiterMap.get(limiter.getClazz()).put(fieldName, limiter);
    }

    public Stream<Map.Entry<String, FieldSetter<T, ?>>> fieldSetterStream() {
        return fieldSetterMap.entrySet().stream();
    }

    public void apply(T pojo) {
        fieldSetterMap.entrySet()
                .forEach(entry -> {
                    entry.getValue().apply(pojo);
                });
    }

}
