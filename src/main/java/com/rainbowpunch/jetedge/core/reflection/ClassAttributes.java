package com.rainbowpunch.jetedge.core.reflection;

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
    private ClassAttributes parentClassAttribute;
    private final Class<?> clazz;
    private boolean isArray = false;
    private final Type genericTypeHint;
    private List<Class<?>> parameterizedTypes = null;
    private String fieldNameOfClass = null;

    // Cache these as they are unlikely to go out of date
    private List<MethodAttributes> methods = null;
    private List<FieldAttributes> fields = null;

    /**
     * @param clazz class to wrap.
     * @param genericTypeHint an optional generic type hint.
     */
    private ClassAttributes(ClassAttributes classAttributes, Class<?> clazz, Type genericTypeHint, boolean isArray) {
        this.parentClassAttribute = classAttributes;
        this.isArray = isArray;
        this.clazz = requireNonNull(clazz);
        // This is the Type that is stored on a Field or Method object. Providing this type
        // helps us get around the type erasure of parameterized types such as List<> or Map<>.
        this.genericTypeHint = genericTypeHint;
    }

    private ClassAttributes(ClassAttributes classAttributes, Class<?> clazz, Type genericTypeHint) {
        this(classAttributes, clazz, genericTypeHint, false);
    }

    /**
     * @param clazz the Class object to wrap.
     * @param genericTypeHint an optional generic type hint.
     * @return a wrapped attributes object for clazz.
     */
    public static ClassAttributes create(ClassAttributes classAttributes, Class<?> clazz, Type genericTypeHint) {
        if (clazz.isArray()) {
            return new ClassAttributes(classAttributes, mapPrimitiveToObject(clazz.getComponentType()), genericTypeHint, true);
        } else {
            return new ClassAttributes(classAttributes, mapPrimitiveToObject(clazz), genericTypeHint);
        }
    }

    /**
     * @param clazz the Class object to wrap.
     * @return a wrapped attributes object for clazz.
     */
    public static ClassAttributes create(Class<?> clazz) {
        return create(null, mapPrimitiveToObject(clazz), null);
    }

    /**
     * @return the name of the Class
     */
    public String getName() {
        return clazz.getName();
    }

    /**
     * @return the name of the field associated with this class from inside the parent object.
     */
    public String getFieldNameOfClass() {
        if (fieldNameOfClass == null) {
            return "";
        } else {
            String returnValue = "";
            String prependValue = "";
            if (parentClassAttribute != null) prependValue = parentClassAttribute.getFieldNameOfClass();
            if (!prependValue.isEmpty()) returnValue = prependValue + "." + fieldNameOfClass;
            else returnValue = fieldNameOfClass;
            return returnValue.toLowerCase();
        }
    }

    public void setFieldNameOfClass(String fieldNameOfClass) {
        if (this.fieldNameOfClass == null) this.fieldNameOfClass = fieldNameOfClass;
        else throw new RuntimeException("Cannot overwrite fieldNameOfClass from : " + this.fieldNameOfClass);
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
                    .map(m -> new MethodAttributes(this, m))
                    .collect(toList());
        }
        return methods;
    }

    /**
     * @return a list of all fields on the Class object and those inherited by parent classes.
     */
    public List<FieldAttributes> getFields() {
        if (fields == null) {
            List<Field> rawFields = new ArrayList<>();
            Class<?> cur = clazz;
            while (cur != null && cur != Object.class) {
                rawFields.addAll(Arrays.asList(cur.getDeclaredFields()));
                cur = cur.getSuperclass();
            }
            fields = rawFields.stream()
                    .map(f -> new FieldAttributes(this, f))
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
                .anyMatch(o -> {
                    boolean isArray = o.isArray();
                    Class innerClazz = isArray ? o.getComponentType() : o;
                    return  (Objects.equals(innerClazz, clazz) && this.isArray == isArray);
                });
    }

    /**
     * @return true if the Class object is a Java array.
     */
    public boolean isArray() {
        return isArray;
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
            return Optional.of(create(clazz));
        }
        if (isCollection()) {
            List<Class<?>> parameterizedTypes = getParameterizedTypes();
            if (!parameterizedTypes.isEmpty()) {
                Class<?> first = parameterizedTypes.get(0);
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
            List<Class<?>> parameterizedTypes = getParameterizedTypes();
            if (parameterizedTypes.size() >= 2) {
                Class<?> first = parameterizedTypes.get(0);
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
            List<Class<?>> parameterizedTypes = getParameterizedTypes();
            if (parameterizedTypes.size() >= 2) {
                Class<?> second = parameterizedTypes.get(1);
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
     * If an incomming class is of a primitive type, this maps it to its corresponding Object type, else, it returns the object
     * @param clazz
     * @return
     */
    public static Class<?> mapPrimitiveToObject(Class<?> clazz) {
        Class outputClass = clazz;

        if (clazz.equals(int.class)) outputClass = Integer.class;
        else if (clazz.equals(boolean.class)) outputClass = Boolean.class;
        else if (clazz.equals(short.class)) outputClass = Short.class;
        else if (clazz.equals(long.class)) outputClass = Long.class;
        else if (clazz.equals(float.class)) outputClass = Float.class;
        else if (clazz.equals(double.class)) outputClass = Double.class;
        else if (clazz.equals(char.class)) outputClass = Character.class;
        else if (clazz.equals(byte.class)) outputClass = Byte.class;

        return outputClass;
    }

    private static List<Class<?>> extractParameterizedTypes(Type type) {
        List<Class<?>> parameters = new ArrayList<>();
        if (type instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
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
