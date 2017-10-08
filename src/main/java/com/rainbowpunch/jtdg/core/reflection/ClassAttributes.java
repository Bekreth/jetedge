package com.rainbowpunch.jtdg.core.reflection;

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

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Friendly wrapper around the Java reflection API.
 */
public class ClassAttributes {
    private final Class<?> clazz;
    private final Type genericTypeHint;
    private List<Class<?>> parameterizedTypes = null;

    // Cache these as they are unlikely to go out of date
    private List<MethodAttributes> methods = null;
    private List<FieldAttributes> fields = null;

    /**
     * @param clazz class to wrap.
     * @param genericTypeHint an optional generic type hint.
     */
    private ClassAttributes(Class<?> clazz, Type genericTypeHint) {
        this.clazz = requireNonNull(clazz);
        // This is the Type that is stored on a Field or Method object. Providing this type
        // helps us get around the type erasure of parameterized types such as List<> or Map<>.
        this.genericTypeHint = genericTypeHint;
    }

    public String getName() {
        return clazz.getName();
    }

    /**
     * @return the wrapped Class object.
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * @return a list of all methods on the Class object and those inherited by parent classes.
     */
    public List<MethodAttributes> getMethods() {
        if (methods == null) {
            methods = Arrays.stream(clazz.getMethods())
                    .map(MethodAttributes::new)
                    .collect(toList());
        }
        return methods;
    }

    /**
     * @return a list of all fields on the Class object and those inherited by parent classes.
     */
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

    /**
     * @param others classes to compare against.
     * @return true if the Class object is a subclass of any class in others.
     * @throws NullPointerException if any class in others is null.
     */
    public boolean isSubclassOf(Class<?> ...others) {
        return Arrays.stream(others)
                .filter(Objects::nonNull)
                .anyMatch(o -> o.isAssignableFrom(clazz));
    }

    /**
     * @param others classes to compare against.
     * @return true if the Class object is exactly equal to any class in others.
     * @throws NullPointerException if any class in others is null.
     */
    public boolean is(Class<?> ...others) {
        return Arrays.stream(others)
                .filter(Objects::nonNull)
                .anyMatch(o -> Objects.equals(o, clazz));
    }

    /**
     * @return true if the Class object is a Java array.
     */
    public boolean isArray() {
        return clazz.isArray();
    }

    /**
     * @return true if the Class object is a subclass of java.util.Collection.
     */
    public boolean isCollection() {
        return isSubclassOf(Collection.class);
    }

    /**
     * @return true if the Class object is a subclass of java.util.Map
     */
    public boolean isMap() {
        return isSubclassOf(Map.class);
    }

    /**
     * @return true if the Class object is type Void or void.
     */
    public boolean isVoid() {
        return is(void.class) || is(Void.class);
    }

    /**
     * @return true if the Class object is an Enum type.
     */
    public boolean isEnum() { return isSubclassOf(Enum.class); }

    /**
     * @return the array or Collection underlying element type for the Class object.
     */
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

    /**
     * @return the underlying Map key type for the Class object.
     */
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

    /**
     * @return the underlying Map value type for the Class object.
     */
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

    /**
     * @return a list of parameter types associated with the Class object.
     */
    public List<Class<?>> getParameterizedTypes() {
        if (parameterizedTypes == null) {
            parameterizedTypes = extractParameterizedTypes(genericTypeHint);
        }
        return parameterizedTypes;
    }

    /**
     * @param clazz the Class object to wrap.
     * @param genericTypeHint an optional generic type hint.
     * @return a wrapped attributes object for clazz.
     */
    public static ClassAttributes create(Class<?> clazz, Type genericTypeHint) {
        return new ClassAttributes(clazz, genericTypeHint);
    }

    /**
     * @param clazz the Class object to wrap.
     * @return a wrapped attributes object for clazz.
     */
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
