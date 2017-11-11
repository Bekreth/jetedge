package com.rainbowpunch.jtdg.core.analyzer;

import com.rainbowpunch.jtdg.core.FieldSetter;
import com.rainbowpunch.jtdg.core.PojoAttributes;
import com.rainbowpunch.jtdg.core.reflection.ClassAttributes;
import com.rainbowpunch.jtdg.core.reflection.MethodAttributes;

import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toSet;

/**
 *
 */
public class DefaultPojoAnalyzer<T> implements PojoAnalyzer<T> {
    private static final Logger log = LoggerFactory.getLogger(DefaultPojoAnalyzer.class);

    @Override
    @SuppressWarnings("unchecked")
    public void parsePojo(Class<T> clazz, PojoAttributes<T> attributes) {
        log.info("Parsing class: {}", clazz.getCanonicalName());
        ClassAttributes classAttributes = ClassAttributes.create(clazz);

        // Get a list of all field names with public setters
        final Set<String> fieldsWithPublicSetters = classAttributes.getMethods().stream()
                .filter(DefaultPojoAnalyzer::methodIsSetter)
                .map(m -> m.getMethodName().getAssociatedFieldName())
                .filter(Optional::isPresent).map(Optional::get)
                .collect(toSet());

        classAttributes.getFields().stream()
                .filter(f -> fieldsWithPublicSetters.contains(f.getName()))
                .filter(f -> !attributes.shouldIgnore(f.getName().toLowerCase()))
                .forEach(f -> {
                    log.debug("Setting field with: {}", f.getName());
                    final FieldSetter fs = FieldSetter.create(f.getType());
                    fs.setConsumer(f.getSetter());
                    attributes.putFieldSetter(f.getName(), fs);
                });
    }

    private static boolean methodIsSetter(MethodAttributes methodAttributes) {
        return methodAttributes.getMethodName().isPrefixedWithSet() &&
                methodAttributes.getParameterCount() == 1;
    }
}
