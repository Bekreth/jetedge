package com.rainbowpunch.jetedge.core.reflection;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for inferring a method's purpose from its name.
 */
public class MethodName {
    private static final Pattern METHOD_NAME_REGEX = Pattern.compile("^([gs]et)([A-Z]\\w*)$");
    private final String accessorPrefix;
    private final String associatedFieldName;

    /**
     * This will get the useful name of a getter/setter method, stripping away getHelloWorld() to helloWorld.
     * @param methodName
     */
    public MethodName(String methodName) {
        Matcher matcher = METHOD_NAME_REGEX.matcher(methodName);
        String accessorSuffix;
        if (matcher.matches()) {
            accessorPrefix = matcher.group(1);
            accessorSuffix = matcher.group(2);
            associatedFieldName = uncapitalize(accessorSuffix);
        } else {
            accessorPrefix = null;
            associatedFieldName = null;
        }
    }

    public Optional<String> getPrefix() {
        return Optional.ofNullable(accessorPrefix);
    }

    public boolean isPrefixedWithGet() {
        return "get".equals(accessorPrefix);
    }

    public boolean isPrefixedWithSet() {
        return "set".equals(accessorPrefix);
    }

    public Optional<String> getAssociatedFieldName() {
        return Optional.ofNullable(associatedFieldName);
    }

    /**
     * @param s string to uncapitalize (yes, it's not a word).
     * @return the uncapitalized string: "FooBar" -> "fooBar".
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

}
