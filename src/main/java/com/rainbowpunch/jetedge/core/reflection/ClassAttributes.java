package com.rainbowpunch.jetedge.core.reflection;

import com.rainbowpunch.jetedge.core.exception.ConfusedGenericException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Friendly wrapper around the Java reflection API.
 */
public final class ClassAttributes {

    // This extracts out all of the standard Object.class methods that Jetedge shouldn't attempt to resolve.
    private static final Set<String> METHOD_NAMES_TO_IGNORE = Arrays.asList(Object.class.getMethods())
            .stream()
            .map(inMethod -> inMethod.getName())
            .collect(Collectors.toSet());

    private final Class<?> clazz;
    private final Map<String, Class> genericTypeMap = new HashMap<>();
    private final List<Class> genericHints;

    private ClassAttributes parentClassAttribute;
    private boolean isArray = false;
    private List<Constructor> possibleConstructors;
    private String fieldNameOfClass = null;

    // Cache these as they are unlikely to go out of date
    private List<MethodAttributes> methods = null;
    private List<FieldAttributes> fields = null;

    /**
     * @param clazz
     *          class to wrap.
     * @param genericTypeHint an optional generic type hint.
     */
    private ClassAttributes(ClassAttributes classAttributes, Class<?> clazz, List<Class> genericTypeHint,
                            boolean isArray) {
        this.parentClassAttribute = classAttributes;
        this.isArray = isArray;
        this.clazz = clazz;

        List<TypeVariable> genericsOnClass = Arrays.asList(clazz.getTypeParameters());
        if (genericTypeHint.size() != genericsOnClass.size()) {
            throw new ConfusedGenericException(clazz.getName());
        }

        genericHints = genericTypeHint;

        // TODO: 3/7/18 Add the ability to handle Interfaces with generics
        Type superClass = clazz.getGenericSuperclass();
        if (genericsOnClass.size() == 0) {
            if (superClass instanceof ParameterizedType) {
                List<TypeVariable> superGenerics =
                        Arrays.asList(((Class) ((ParameterizedType) superClass).getRawType()).getTypeParameters());
                List<Class> subGenericImpl =
                        Arrays.asList(((ParameterizedType) superClass).getActualTypeArguments())
                        .stream()
                        .sequential()
                        .map(inType -> (Class) inType)
                        .collect(toList());
                for (int i = 0; i < superGenerics.size(); i++) {
                    genericTypeMap.put(superGenerics.get(i).getName(), subGenericImpl.get(i));
                }
            }
        } else {
            if (superClass instanceof ParameterizedType) {
                Iterator<Class> iterator = genericTypeHint.iterator();
                List<TypeVariable> superGenerics =
                        Arrays.asList(((Class) ((ParameterizedType) superClass).getRawType()).getTypeParameters());
                List<Class> subGenericImpl =
                        Arrays.asList(((ParameterizedType) superClass).getActualTypeArguments())
                        .stream()
                        .sequential()
                        .map(inType -> {
                            Class returnObject = null;
                            if (inType instanceof TypeVariable) {
                                returnObject = iterator.next();
                            } else if (inType instanceof Class) {
                                returnObject = (Class) inType;
                            } else {
                                // TODO: 3/8/18 This is absurd and needs a better exception.
                                throw new RuntimeException("BAD BAD BAD");
                            }
                            return returnObject;
                        })
                        .collect(toList());
                for (int i = 0; i < superGenerics.size(); i++) {
                    genericTypeMap.put(superGenerics.get(i).getName(), subGenericImpl.get(i));
                }
            }
        }
        this.possibleConstructors = Arrays.asList(clazz.getConstructors());
    }

    private ClassAttributes(ClassAttributes classAttributes, Class<?> clazz, List<Class> genericTypeHint) {
        this(classAttributes, clazz, genericTypeHint, false);
    }

    /**
     * @param classAttributes
     *          The ClassAttribute of the parent class
     * @param clazz
     *          the Class object to wrap.
     * @param genericTypeHint
     *          an optional generic type hint.
     * @return a wrapped attributes object for clazz.
     */
    public static ClassAttributes create(ClassAttributes classAttributes, Class<?> clazz, List<Class> genericTypeHint) {
        ClassAttributes output = null;
        Class mappedClass = null;
        List<Class> genericHints = genericTypeHint == null ? new ArrayList<>() : genericTypeHint;
        if (clazz.isArray()) {
            mappedClass = mapPrimitiveToObject(clazz.getComponentType());
            output = new ClassAttributes(classAttributes, mappedClass, genericHints, true);
        } else {
            mappedClass = mapPrimitiveToObject(clazz);
            output = new ClassAttributes(classAttributes, mappedClass, genericHints);
        }
        return output;
    }

    /**
     * @param
     *          clazz the Class object to wrap.
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
        if (this.fieldNameOfClass == null) {
            this.fieldNameOfClass = fieldNameOfClass;
        } else {
            throw new RuntimeException("Cannot overwrite fieldNameOfClass from : " + this.fieldNameOfClass);
        }
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
                    .filter(inMethod -> !METHOD_NAMES_TO_IGNORE.contains(inMethod.getName()))
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
    public boolean isSubclassOf(Class<?>... others) {
        return Arrays.stream(others)
                .filter(Objects::nonNull)
                .anyMatch(o -> o.isAssignableFrom(clazz));
    }

    public boolean isParentClassOf(Class<?>... others) {
        return Arrays.stream(others)
                .filter(Objects::nonNull)
                .anyMatch(o -> clazz.isAssignableFrom(o));
    }

    /**
     * @param others classes to compare against.
     * @return true if the Class object is exactly equal to any class in others.
     * @throws NullPointerException if any class in others is null.
     */
    public boolean is(Class<?>... others) {
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
    public boolean isEnum() {
        return isSubclassOf(Enum.class);
    }

    @Override
    public String toString() {
        return String.format("Class[%s]", getName());
    }

    public <T> T newInstance(List<ConstructorParameter> constructorObjectList) throws InstantiationException {
        try {
            T output = null;
            if (constructorObjectList == null || constructorObjectList.size() == 0) {
                output = (T) clazz.newInstance();
            } else {
                Constructor constructor = possibleConstructors.stream()
                        .filter(con -> con.getParameterCount() == constructorObjectList.size())
                        .filter(innerConstructor -> {
                            List<Type> classes = Arrays.stream(innerConstructor.getParameters())
                                    .map(parameter -> mapPrimitiveToObject(parameter.getType()))
                                    .collect(Collectors.toList());
                            boolean returnValue = true;

                            for (int i = 0; i < constructorObjectList.size(); i++) {
                                if (!constructorObjectList.get(i).getObjectType().equals(classes.get(i))) {
                                    returnValue = false;
                                    break;
                                }
                            }

                            return returnValue;
                        })
                        .findFirst()
                        .orElseThrow(InstantiationException::new);

                Object[] constructorObjects = constructorObjectList.stream()
                        .map(constructorParameter -> constructorParameter.getObjectSupplier().get())
                        .toArray();

                output = (T) constructor.newInstance(constructorObjects);
            }

            return output;
        } catch (Exception e) {
            throw new InstantiationException(e.getMessage());
        }
    }

    public Class getClassForGenericName(String genericName) {
        Class returnObject = genericTypeMap.get(genericName);
        if (returnObject == null) {
            throw new RuntimeException("FIX ME NOW PLEASE!");
        }
        return returnObject;
    }

    public Map<String, Class> getGenericTypeMap() {
        return genericTypeMap;
    }

    public List<Class> getGenericHints() {
        return genericHints;
    }

    public ClassAttributes getParentClassAttribute() {
        return parentClassAttribute;
    }

    /**
     * If an incoming class is of a primitive type, this maps it to its corresponding Object type, else, it returns
     *      the object
     * @param clazz
     *          The class that will have a primitive type check run against it.
     * @return An object class type
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

}
