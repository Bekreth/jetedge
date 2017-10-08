package com.rainbowpunch.jtdg.core.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public boolean isSetter() {
        return getMethodName().isSetter() &&
                getParameterCount() == 1 &&
                getReturnType().isVoid();
    }

    public Optional<String> getAssociatedFieldName() {
        return Optional.ofNullable(methodName.getAssociatedFieldName());
    }

    public ClassAttributes getReturnType() {
        return ClassAttributes.create(method.getReturnType(), method.getGenericReturnType());
    }

    public int getParameterCount() {
        return method.getParameterCount();
    }

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
