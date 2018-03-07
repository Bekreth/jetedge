package com.rainbowpunch.jetedge.core.reflection;

import com.rainbowpunch.jetedge.test.Pojos.Person;
import junit.framework.AssertionFailedError;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public class MemberAttributesTest {
    @Test
    public void testClassAttributes_isVoid() {
        final List<MethodAttributes> methods =
                ClassAttributes.create(Person.class).getMethods();

        assertTrue(
                methods.stream()
                        .filter(m -> "setAge".equals(m.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new)
                        .getReturnType()
                        .isVoid()
        );

        assertFalse(
                methods.stream()
                        .filter(m -> "getAge".equals(m.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new)
                        .getReturnType()
                        .isVoid()
        );
    }

}
