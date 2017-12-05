package com.rainbowpunch.jetedge.core.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A friendly wrapper for Method objects.
 */
public class MethodAttributes {
    private final Method method;
    private final ClassAttributes parentClassAttributes;

    private MethodName methodName = null;
    private List<ClassAttributes> parameterTypes = null;

    public MethodAttributes(ClassAttributes parentClassAttributes, Method method) {
        this.parentClassAttributes = parentClassAttributes;
        this.method = method;
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
        return ClassAttributes.create(parentClassAttributes, method.getReturnType(), method.getGenericReturnType());
    }

    /**
     * @return the parameter count of the method.
     */
    public int getParameterCount() {
        return method.getParameterCount();
    }

    /**
     * @return a list of wrapped Class objects which are the parameter types of the method.
     */
    public List<ClassAttributes> getParameterTypes() {
        if (parameterTypes == null) {
            parameterTypes = new ArrayList<>();
            Class<?>[] parameterClasses = method.getParameterTypes();
            Type[] parameterGenericTypes = method.getGenericParameterTypes();
            for (int i = 0; i < parameterClasses.length; i++) {
                Class<?> parameterType = parameterClasses[i];
                if (parameterType == null) {
                    // FIXME we should probably bail instead of continuing with null values
                    parameterTypes.add(null);
                } else {
                    Type types = i < parameterGenericTypes.length ? parameterGenericTypes[i] : null;
                    ClassAttributes classAttributes = ClassAttributes.create(parentClassAttributes, parameterType, types);
                    parameterTypes.add(classAttributes);
                }
            }
        }
        return parameterTypes;
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
