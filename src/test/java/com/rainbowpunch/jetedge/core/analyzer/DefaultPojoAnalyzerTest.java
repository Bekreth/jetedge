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

public class DefaultPojoAnalyzerTest {
    @Test
    public void testDefaultPojoAnalyzer() {
        Set<String> expectedFields = new HashSet<>(asList("hasTintedWindows", "name", "numWheels", "engineType", "owners", "maxSpeed", "salesPerson"));
        Set<String> actualFields = Analyzers.DEFAULT.extractFields(ClassAttributes.create(Vehicle.class))
                .map(FieldAttributes::getName)
                .collect(toSet());
        assertEquals(expectedFields, actualFields);
    }
}
