package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * An entity of all the Pojo's attributes
 */
public class PojoAttributes<T> {

    private Class<T> pojoClazz;
    private Map<String, Limiter<?>> limiters;
    private Map<String, FieldSetter<T, ?>> fieldSetterMap;

    public PojoAttributes() {
        this.limiters = new HashMap<>();
        this.fieldSetterMap = new HashMap<>();
    }

    public Class<T> getPojoClazz() {
        return pojoClazz;
    }

    public void setPojoClazz(Class<T> pojoClazz) {
        this.pojoClazz = pojoClazz;
    }

    public Map<String, Limiter<?>> getLimiters() {
        return limiters;
    }

    public FieldSetter<T, ?> getFieldSetter(String fieldName) {
        return fieldSetterMap.get(fieldName);
    }

    public void putFieldSetter(String fieldName, FieldSetter<T, ?> fieldSetter) {
        fieldSetterMap.put(fieldName, fieldSetter);
    }

    public void putFieldLimiter(String fieldName, Limiter limiter) {
        limiters.put(fieldName, limiter);
    }

    public Stream<Map.Entry<String, FieldSetter<T, ?>>> fieldSetterStream() {
        return fieldSetterMap.entrySet().stream();
    }

    public void apply(T pojo) {
        fieldSetterMap.entrySet()
                .stream()
                .forEach(entry -> {
                    entry.getValue().apply(pojo);
                });
    }

}
