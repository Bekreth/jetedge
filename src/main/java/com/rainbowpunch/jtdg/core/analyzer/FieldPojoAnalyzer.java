package com.rainbowpunch.jtdg.core.analyzer;

import com.rainbowpunch.jtdg.core.reflection.ClassAttributes;
import com.rainbowpunch.jtdg.core.reflection.FieldAttributes;

import java.util.stream.Stream;

public class FieldPojoAnalyzer implements PojoAnalyzer {
    @Override
    public Stream<FieldAttributes> extractFields(ClassAttributes classAttributes) {
        return classAttributes.getFields().stream();
    }
}
