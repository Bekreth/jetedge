package com.rainbowpunch.jtdg.core.reflection;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import com.rainbowpunch.jtdg.core.test.Pojos.City;
import com.rainbowpunch.jtdg.core.test.Pojos.Person;
import com.rainbowpunch.jtdg.core.test.Pojos.Power;
import com.rainbowpunch.jtdg.core.test.Pojos.Superhero;
import com.rainbowpunch.jtdg.core.test.Pojos.SuperheroNetwork;
import junit.framework.AssertionFailedError;
import org.junit.Test;

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

    @Test
    public void testClassAttributes_getElementType() {
        final List<FieldAttributes> fields =
                ClassAttributes.create(Superhero.class).getFields();

        assertFalse(
                fields.stream()
                        .filter(f -> "archNemesis".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new)
                        .getType()
                        .getElementType()
                        .isPresent()
        );

        assertEquals(
                Power.class,
                fields.stream()
                        .filter(f -> "superPowers".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new)
                        .getType()
                        .getElementType().orElseThrow(AssertionFailedError::new)
                        .getClazz()
        );
    }

    @Test
    public void testClassAttributes_getKeyType() {
        final List<FieldAttributes> fields =
                ClassAttributes.create(SuperheroNetwork.class).getFields();

        assertFalse(
                fields.stream()
                        .filter(f -> "planetName".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new)
                        .getType()
                        .getKeyType()
                        .isPresent()
        );

        assertEquals(
                City.class,
                fields.stream()
                        .filter(f -> "protectorMap".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new)
                        .getType()
                        .getKeyType().orElseThrow(AssertionFailedError::new)
                        .getClazz()
        );
    }

    @Test
    public void testClassAttributes_getValueType() {
        final List<FieldAttributes> fields =
                ClassAttributes.create(SuperheroNetwork.class).getFields();

        assertFalse(
                fields.stream()
                        .filter(f -> "planetName".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new)
                        .getType()
                        .getValueType()
                        .isPresent()
        );

        assertEquals(
                Superhero.class,
                fields.stream()
                        .filter(f -> "protectorMap".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new)
                        .getType()
                        .getValueType().orElseThrow(AssertionFailedError::new)
                        .getClazz()
        );
    }

    @Test
    public void testClassAttributes_getParameterizedTypes() {
        final ClassAttributes classAttributes =
                ClassAttributes.create(SuperheroNetwork.class)
                        .getFields().stream()
                        .filter(f -> "protectorMap".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new)
                        .getType();

        assertEquals(
                Arrays.asList(
                        City.class,
                        Superhero.class
                ),
                classAttributes.getParameterizedTypes()
        );
    }
}
