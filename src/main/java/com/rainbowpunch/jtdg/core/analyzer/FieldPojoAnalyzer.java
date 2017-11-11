package com.rainbowpunch.jtdg.core.analyzer;

import com.rainbowpunch.jtdg.core.FieldSetter;
import com.rainbowpunch.jtdg.core.PojoAttributes;
import com.rainbowpunch.jtdg.core.reflection.ClassAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldPojoAnalyzer<T> implements PojoAnalyzer<T> {
    private static final Logger log = LoggerFactory.getLogger(FieldPojoAnalyzer.class);

    @Override
    @SuppressWarnings("unchecked")
    public void parsePojo(Class<T> clazz, PojoAttributes<T> attributes) {
        log.info("Parsing class: {}", clazz.getCanonicalName());
        ClassAttributes classAttributes = ClassAttributes.create(clazz);

        classAttributes.getFields().stream()
                .filter(f -> !attributes.shouldIgnore(f.getName().toLowerCase()))
                .forEach(f -> {
                    log.debug("Setting field with: {}", f.getName());
                    final FieldSetter fs = FieldSetter.create(f.getType());
                    fs.setConsumer(f.getSetter());
                    attributes.putFieldSetter(f.getName(), fs);
                });
    }
}
