package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.core.limiters.Limiter;
import com.rainbowpunch.jtdg.core.limiters.NestedLimiter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * An entity of all the Pojo's attributes
 */
public class PojoAttributes<T> implements Cloneable {

    private Class<T> pojoClazz;
    private Map<Class, Map<String, Limiter<?>>> masterLimiterMap;
    private Map<Class, Limiter<?>> allFieldLimiterMap;
    private Map<String, FieldSetter<T, ?>> fieldSetterMap;
    private Set<String> fieldsToIgnore;
    private int randomSeed;

    private PojoAttributes() {

    }

    public PojoAttributes(Class<T> pojoClazz, int randomSeed) {
        this.pojoClazz = pojoClazz;
        this.randomSeed = randomSeed;

        this.masterLimiterMap = new HashMap<>();
        this.masterLimiterMap.put(this.pojoClazz, new HashMap<>());
        this.fieldSetterMap = new HashMap<>();
        this.allFieldLimiterMap = new HashMap<>();
        this.fieldsToIgnore = new HashSet<>();
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

    public void putAllFieldLimiter(Class clazz, Limiter<?> limiter) {
        allFieldLimiterMap.put(clazz, limiter);
    }
    public void putFieldLimiter(String fieldName, NestedLimiter limiter) {
        masterLimiterMap.get(limiter.getClazz()).put(fieldName, limiter);
    }

    public Stream<Map.Entry<String, FieldSetter<T, ?>>> fieldSetterStream() {
        return fieldSetterMap.entrySet().stream();
    }

    public Map<Class, Limiter<?>> getAllFieldLimiterMap() {
        return allFieldLimiterMap;
    }

    public void ignoreField(String fieldName) {
        fieldsToIgnore.add("set" + fieldName.toLowerCase());
    }

    public boolean shouldIgnore(String fieldName) {
        return fieldsToIgnore.contains(fieldName);
    }

    public int getRandomSeed() {
        return randomSeed;
    }

    @Override
    public PojoAttributes<T> clone() {
        PojoAttributes<T> attributes = new PojoAttributes<>();

        try {
            attributes.pojoClazz = (Class<T>) Class.forName(this.pojoClazz.getName());
        } catch (Exception e) {
            throw new RuntimeException("Failed cloning object: ", e);
        }
        attributes.masterLimiterMap = (Map) ((HashMap) this.masterLimiterMap).clone();
        attributes.fieldSetterMap = (Map) ((HashMap) this.fieldSetterMap).clone();
        attributes.allFieldLimiterMap = (Map) ((HashMap) this.allFieldLimiterMap).clone();
        attributes.fieldsToIgnore = (Set) ((HashSet) this.fieldsToIgnore).clone();

        return attributes;
    }

    public void apply(T pojo) {
        fieldSetterMap.forEach((key, value) -> value.apply(pojo));
    }

}
