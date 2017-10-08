package com.rainbowpunch.jtdg.core;

import static java.util.stream.Collectors.toSet;

import java.util.Optional;
import java.util.Set;

import com.rainbowpunch.jtdg.core.reflection.ClassAttributes;
import com.rainbowpunch.jtdg.core.reflection.MethodAttributes;

/**
 *
 */
public class DefaultPojoAnalyzer<T> implements PojoAnalyzer<T> {

    @Override
    public void parsePojo(Class<T> clazz, PojoAttributes<T> attributes) {
        final ClassAttributes classAttributes = ClassAttributes.create(clazz);

        // Get a list of all field names with public setters
        final Set<String> fieldsWithPublicSetters = classAttributes.getMethods().stream()
                .filter(MethodAttributes::isSetter)
                .map(MethodAttributes::getAssociatedFieldName)
                .filter(Optional::isPresent).map(Optional::get)
                .collect(toSet());

        System.out.println("Field names: " + fieldsWithPublicSetters);

        classAttributes.getFields().stream()
                .filter(f -> fieldsWithPublicSetters.contains(f.getName()))
                .filter(f -> !attributes.shouldIgnore(f.getName().toLowerCase()))
                .forEach(f -> {
                    final FieldSetter fs = FieldSetter.create(f.getType());
                    fs.setConsumer(f.getSetter());
                    attributes.putFieldSetter(f.getName(), fs);
                });
    }
}
