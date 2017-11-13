package com.rainbowpunch.jtdg.core.analyzer;

import com.rainbowpunch.jtdg.core.reflection.ClassAttributes;
import com.rainbowpunch.jtdg.core.reflection.FieldAttributes;
import com.rainbowpunch.jtdg.core.reflection.MethodAttributes;
import com.rainbowpunch.jtdg.core.reflection.MethodName;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class DefaultPojoAnalyzer implements PojoAnalyzer {
    @Override
    public Stream<FieldAttributes> extractFields(ClassAttributes ca) {
        // Get a list of all field names with public setters
        Set<String> fieldsWithPublicSetters = ca.getMethods().stream()
                .filter(ma -> ma.getParameterCount() == 1)
                .map(MethodAttributes::getMethodName)
                .filter(MethodName::isPrefixedWithSet)
                .map(MethodName::getAssociatedFieldName)
                .filter(Optional::isPresent).map(Optional::get)
                .collect(toSet());

        return ca.getFields().stream()
                .filter(f -> fieldsWithPublicSetters.contains(f.getName()));
    }
}
