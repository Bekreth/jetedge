package com.rainbowpunch.jetedge.core.analyzer;

import com.rainbowpunch.jetedge.core.reflection.MethodAttributes;
import com.rainbowpunch.jetedge.core.reflection.MethodName;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A list of provided PojoAnalyzers
 */
public final class Analyzers {
    public static final PojoAnalyzer DEFAULT = classAttributes -> {
        // Get a list of all field names with public setters
        Set<String> fieldsWithPublicSetters = classAttributes.getMethods().stream()
                .filter(ma -> ma.getParameterCount() == 1)
                .map(MethodAttributes::getMethodName)
                .filter(MethodName::isPrefixedWithSet)
                .map(MethodName::getAssociatedFieldName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        return classAttributes.getFields().stream()
                .filter(f -> fieldsWithPublicSetters.contains(f.getName()));
    };

    public static final PojoAnalyzer ALL_FIELDS = classAttributes -> classAttributes.getFields().stream();

    private Analyzers() {

    }
}
