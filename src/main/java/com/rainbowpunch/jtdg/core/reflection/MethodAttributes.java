package com.rainbowpunch.jtdg.core.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A friendly wrapper for Method objects.
 */
public class MethodAttributes {
    private final Method method;
    private MethodName methodName = null;
    private List<ClassAttributes> parameterTypes = null;

    public MethodAttributes(Method method) {
        this.method = method;
    }

    public String getName() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }

    /**
     * @return true if the method is a setter method; that is, its name starts with "set" and
     *         it accepts one parameter and has a void return type.
     */
    public boolean isSetter() {
        return getMethodName().isSetter() &&
                getParameterCount() == 1 &&
                getReturnType().isVoid();
    }

    /**
     * @return the expected field name associated with the method if the method appears to be a
     *         getter or a setter.
     */
    public Optional<String> getAssociatedFieldName() {
        return Optional.ofNullable(methodName.getAssociatedFieldName());
    }

    /**
     * @return a wrapped Class object of the return type of the method.
     */
    public ClassAttributes getReturnType() {
        return ClassAttributes.create(method.getReturnType(), method.getGenericReturnType());
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
            final Class<?>[] parameterClasses = method.getParameterTypes();
            final Type[] parameterGenericTypes = method.getGenericParameterTypes();
            for (int i = 0; i < parameterClasses.length; i++) {
                final Class<?> parameterType = parameterClasses[i];
                if (parameterType == null) {
                    // FIXME we should probably bail instead of continuing with null values
                    parameterTypes.add(null);
                } else {
                    parameterTypes.add(ClassAttributes.create(
                            parameterType,
                            i < parameterGenericTypes.length ? parameterGenericTypes[i] : null
                    ));
                }
            }
        }
        return parameterTypes;
    }

    @Override
    public String toString() {
        return String.format("Method[%s]", getName());
    }

    private MethodName getMethodName() {
        if (methodName == null) {
            methodName = new MethodName(getName());
        }
        return methodName;
    }

    /**
     * @param s string to uncapitalize (yes, it's not a word).
     * @return the uncaptalized string: "FooBar" -> "fooBar".
     */
    private static String uncapitalize(String s) {
        if (s == null) {
            return null;
        }
        if (s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    private static class MethodName {
        private static final Pattern METHOD_NAME_REGEX = Pattern.compile("^([gs]et)([A-Z]\\w*)$");
        private final String accessorPrefix;
        private final String accessorSuffix;
        private final String associatedFieldName;

        private MethodName(String methodName) {
            final Matcher matcher = METHOD_NAME_REGEX.matcher(methodName);
            if (matcher.matches()) {
                accessorPrefix = matcher.group(1);
                accessorSuffix = matcher.group(2);
                associatedFieldName = uncapitalize(accessorSuffix);
            } else {
                accessorPrefix = accessorSuffix = associatedFieldName = null;
            }
        }

        boolean isSetter() {
            return "set".equals(accessorPrefix);
        }

        String getAssociatedFieldName() {
            return associatedFieldName;
        }
    }
}
