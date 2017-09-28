package com.rainbowpunch.jtdg.core;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 *
 */
public class DefaultPojoAnalyzer<T> implements PojoAnalyzer<T> {

    private PojoAttributes<T> attributes;
    private static final Pattern pattern = Pattern.compile("set.*");

    @Override
    public void parsePojo(Class<T> clazz, PojoAttributes<T> attributes) {
        try {
            System.out.println("Processing class : " + clazz.toString());
            this.attributes = attributes;
            List<Method> methods = new ArrayList<>();

            Class currentClazz = clazz;
            while (currentClazz != Object.class) {
                methods.addAll(Arrays.asList(currentClazz.getDeclaredMethods()));
                currentClazz = currentClazz.getSuperclass();
            }

            methods.stream()
                .map(this::getMethodName)
                .filter(this::getSetterMethods)
                .filter(this::removeIgnored)
                .map(this::getMethodParameters)
                .forEach(method -> {
                    System.out.println(method.toString());
                    FieldSetter fieldSetter = FieldSetter.makeFieldSetter(method.getValue());
                    fieldSetter.setConsumer(createBiConsumer(method.getKey()));
                    attributes.putFieldSetter(method.getKey().getName(), fieldSetter);
                });

        } catch (Exception e) {
            throw new RuntimeException("Error while parsing : ", e);
        }
    }

    private Map.Entry<Method, String> getMethodName(Method method) {
        return new AbstractMap.SimpleEntry<>(method, method.getName());
    }

    private boolean getSetterMethods(Map.Entry<Method, String> entry) {
        return pattern.matcher(entry.getValue()).find();
    }

    private boolean removeIgnored(Map.Entry<Method, String> entry) {
        return !attributes.shouldIgnore(entry.getValue().toLowerCase());
    }

    private Map.Entry<Method, Type> getMethodParameters(Map.Entry<Method, String> entry) {
        Type type = null;
        try {
            type = entry.getKey().getGenericParameterTypes()[0];
        } catch (Exception e) {
            type = entry.getKey().getParameterTypes()[0];
        }
        return new AbstractMap.SimpleEntry<>(entry.getKey(), type);
    }

    private BiConsumer<T, ?> createBiConsumer(Method method) {
        return (instance, value) -> {
            try {
                method.setAccessible(true);
                method.invoke(instance, value);
            } catch (Exception e) {
                throw new RuntimeException("Error creating BiConsumer : ", e); // TODO: 7/29/17
            }
        };
    }
}
