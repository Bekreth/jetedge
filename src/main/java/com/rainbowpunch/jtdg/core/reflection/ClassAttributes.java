package com.rainbowpunch.jtdg.core.reflection;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ClassAttributes {
    private final Class<?> clazz;
    private final Type genericTypeHint;
    private List<Class<?>> parameterizedTypes = null;

    // Cache these as they are unlikely to go out of date
    private List<MethodAttributes> methods = null;
    private List<FieldAttributes> fields = null;

    private ClassAttributes(Class<?> clazz, Type genericTypeHint) {
        this.clazz = requireNonNull(clazz);
        this.genericTypeHint = genericTypeHint;
    }

    public String getName() {
        return clazz.getName();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public List<MethodAttributes> getMethods() {
        if (methods == null) {
            methods = Arrays.stream(clazz.getMethods())
                    .map(MethodAttributes::new)
                    .collect(toList());
        }
        return methods;
    }

    public List<FieldAttributes> getFields() {
        if (fields == null) {
            final List<Field> rawFields = new ArrayList<>();
            Class<?> cur = clazz;
            while (cur != null && cur != Object.class) {
                rawFields.addAll(Arrays.asList(cur.getDeclaredFields()));
                cur = cur.getSuperclass();
            }
            fields = rawFields.stream()
                    .map(FieldAttributes::new)
                    .collect(toList());
        }
        return fields;
    }

    public boolean isSubclassOf(Class<?> ...others) {
        return Arrays.stream(others)
                .filter(Objects::nonNull)
                .anyMatch(o -> o.isAssignableFrom(clazz));
    }

    public boolean is(Class<?> ...others) {
        return Arrays.stream(others)
                .filter(Objects::nonNull)
                .anyMatch(o -> Objects.equals(o, clazz));
    }

    public boolean isArray() {
        return clazz.isArray();
    }

    public boolean isCollection() {
        return isSubclassOf(Collection.class);
    }

    public boolean isMap() {
        return isSubclassOf(Map.class);
    }

    public boolean isVoid() {
        return is(void.class) || is(Void.class);
    }

    public boolean isEnum() { return isSubclassOf(Enum.class); }

    public Optional<ClassAttributes> getElementType() {
        if (isArray()) {
            return Optional.of(create(clazz.getComponentType()));
        }
        if (isCollection()) {
            final List<Class<?>> parameterizedTypes = getParameterizedTypes();
            if (!parameterizedTypes.isEmpty()) {
                final Class<?> first = parameterizedTypes.get(0);
                if (first != null) {
                    return Optional.of(create(first));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<ClassAttributes> getKeyType() {
        if (isMap()) {
            final List<Class<?>> parameterizedTypes = getParameterizedTypes();
            if (parameterizedTypes.size() >= 2) {
                final Class<?> first = parameterizedTypes.get(0);
                if (first != null) {
                    return Optional.of(create(first));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<ClassAttributes> getValueType() {
        if (isMap()) {
            final List<Class<?>> parameterizedTypes = getParameterizedTypes();
            if (parameterizedTypes.size() >= 2) {
                final Class<?> second = parameterizedTypes.get(1);
                if (second != null) {
                    return Optional.of(create(second));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return String.format("Class[%s]", getName());
    }

    public List<Class<?>> getParameterizedTypes() {
        if (parameterizedTypes == null) {
            parameterizedTypes = extractParameterizedTypes(genericTypeHint);
        }
        return parameterizedTypes;
    }

    public static ClassAttributes create(Class<?> clazz, Type genericTypeHint) {
        return new ClassAttributes(clazz, genericTypeHint);
    }

    public static ClassAttributes create(Class<?> clazz) {
        return new ClassAttributes(clazz, null);
    }

    private static List<Class<?>> extractParameterizedTypes(Type type) {
        final List<Class<?>> parameters = new ArrayList<>();
        if (type instanceof ParameterizedType) {
            final Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (Type t : typeArguments) {
                if (t instanceof Class) {
                    parameters.add((Class<?>) t);
                } else {
                    parameters.add(null);
                }
            }
        }
        return parameters;
    }
}
