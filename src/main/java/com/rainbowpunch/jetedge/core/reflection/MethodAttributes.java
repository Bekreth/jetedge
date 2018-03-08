package com.rainbowpunch.jetedge.core.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A friendly wrapper for Method objects.
 */
public class MethodAttributes {
    private final Method method;
    private final ClassAttributes parentClassAttributes;
    private final Class genericReturnTypes;
    private final List<Class> genericParameterTypes;

    private MethodName methodName = null;
    private List<ClassAttributes> parameterTypes = null;

    public MethodAttributes(ClassAttributes parentClassAttributes, Method method) {
        this.parentClassAttributes = parentClassAttributes;
        this.method = method;

        this.genericParameterTypes = new ArrayList<>();

        List<Type> parameterTypes = Arrays.asList(method.getGenericParameterTypes());
        for (Type parameterType : parameterTypes) {
            if (parameterType instanceof Class) {
                genericParameterTypes.add((Class) parameterType);
            } else if (parameterType instanceof TypeVariable) {
                Class genericClass = parentClassAttributes.getClassForGenericName(parameterType.getTypeName());
                genericParameterTypes.add(genericClass);
            } else {
                Class specificClass = (Class) ((ParameterizedType) parameterType).getRawType();
                genericParameterTypes.add(specificClass);
            }
        }

        Type returnType = method.getGenericReturnType();
        if (returnType instanceof Class) {
            genericReturnTypes = (Class) returnType;
        } else if (returnType instanceof TypeVariable) {
            genericReturnTypes = parentClassAttributes.getClassForGenericName(returnType.getTypeName());
        } else {
            genericReturnTypes = (Class) ((ParameterizedType) returnType).getRawType();
        }
    }

    public String getName() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }

    /**
     * @return a wrapped Class object of the return type of the method.
     */
    public ClassAttributes getReturnType() {
        return ClassAttributes.create(parentClassAttributes, genericReturnTypes, null);
    }

    /**
     * @return the parameter count of the method.
     */
    public int getParameterCount() {
        return method.getParameterCount();
    }

    public MethodName getMethodName() {
        if (methodName == null) {
            methodName = new MethodName(getName());
        }
        return methodName;
    }

    @Override
    public String toString() {
        return String.format("Method[%s]", getName());
    }

}
