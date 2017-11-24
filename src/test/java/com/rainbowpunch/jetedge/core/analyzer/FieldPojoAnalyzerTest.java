package com.rainbowpunch.jetedge.core.analyzer;

import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;
import com.rainbowpunch.jetedge.core.reflection.FieldAttributes;
import com.rainbowpunch.jetedge.test.Pojos.Vehicle;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;

public class FieldPojoAnalyzerTest {
    @Test
    public void testFieldPojoAnalyzer() {
        Set<String> expectedFields = new HashSet<>(asList("maxSpeed", "numWheels", "name", "engineType", "hasTintedWindows"));
        Set<String> actualFields = Analyzers.ALL_FIELDS.extractFields(ClassAttributes.create(Vehicle.class))
                .map(FieldAttributes::getName)
                .collect(toSet());
        assertEquals(expectedFields, actualFields);
    }

}