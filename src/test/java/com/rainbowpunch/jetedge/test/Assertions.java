package com.rainbowpunch.jetedge.test;

import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;
import com.rainbowpunch.jetedge.core.reflection.FieldAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public final class Assertions {
    private Assertions() {}

    private static final Set<String> OBJECT_FIELDS;

    static {
        OBJECT_FIELDS = ClassAttributes.create(Object.class)
                .getFields().stream()
                .map(FieldAttributes::getName)
                .collect(toSet());
    }

    /**
     * Perform a shallow comparison of two objects. If the objects are not
     * shallowly equal, throw an AssertionError. One or both of the objects
     * can be null.
     * @param lhs an object to compare.
     * @param rhs an object to compare.
     * @param <T> the type of the objects to compare.
     * @throws AssertionError if the objects are not equal.
     */
    public static <T> void assertPojosShallowEqual(T lhs, T rhs) {
        if (Objects.equals(lhs, rhs)) {
            return;
        }
        assertNotNull("left hand side is null", lhs);
        assertNotNull("right hand side is null", rhs);
        List<FieldAttributes> fieldAttributesList = ClassAttributes.create(lhs.getClass())
                .getFields().stream()
                .filter(f -> !OBJECT_FIELDS.contains(f.getName()))
                .collect(toList());

        for (FieldAttributes fa : fieldAttributesList) {
            Function<T, ?> getter = fa.getGetter();
            assertEquals(getter.apply(lhs), getter.apply(rhs));
        }
    }
}
