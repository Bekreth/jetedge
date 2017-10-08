package com.rainbowpunch.jtdg.core.reflection;

import static com.rainbowpunch.jtdg.core.test.Pojos.Extra;
import static com.rainbowpunch.jtdg.core.test.Pojos.Person;
import static com.rainbowpunch.jtdg.core.test.Pojos.Power;
import static com.rainbowpunch.jtdg.core.test.Pojos.Superhero;
import static com.rainbowpunch.jtdg.core.test.Pojos.SuperheroNetwork;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ClassAttributesTest {
    private ClassAttributes unitUnderTest = null;

    @Before
    public void setup() {
        unitUnderTest = ClassAttributes.create(Superhero.class);
    }

    @Test
    public void testIsSubclassOf() {
        assertTrue(unitUnderTest.isSubclassOf(Object.class));
        assertTrue(unitUnderTest.isSubclassOf(Person.class));
        assertTrue(unitUnderTest.isSubclassOf(Superhero.class));
        assertFalse(unitUnderTest.isSubclassOf(Extra.class));
    }

    @Test
    public void testIsSubclassOf_multi() {
        assertTrue(unitUnderTest.isSubclassOf(String.class, Person.class));
        assertTrue(unitUnderTest.isSubclassOf(String.class, Superhero.class));
        assertFalse(unitUnderTest.isSubclassOf(Extra.class, SuperheroNetwork.class));
    }

    @Test(expected = NullPointerException.class)
    public void testIsSubclassOf_null() {
        unitUnderTest.isSubclassOf(null);
    }

    @Test
    public void testIs() {
        assertFalse(unitUnderTest.is(Object.class));
        assertFalse(unitUnderTest.is(Person.class));
        assertTrue(unitUnderTest.is(Superhero.class));
        assertFalse(unitUnderTest.is(Extra.class));
    }

    @Test
    public void testIs_multi() {
        assertFalse(unitUnderTest.is(String.class, Person.class));
        assertTrue(unitUnderTest.is(String.class, Superhero.class));
        assertFalse(unitUnderTest.is(Extra.class, SuperheroNetwork.class));
    }

    @Test(expected = NullPointerException.class)
    public void testIs_null() {
        unitUnderTest.is(null);
    }

    @Test
    public void testIsArray() {
        assertFalse(unitUnderTest.isArray());
        final int[] array = new int[] { };
        assertTrue(ClassAttributes.create(array.getClass()).isArray());
    }

    @Test
    public void testIsCollection() {
        assertFalse(unitUnderTest.isCollection());
        final Collection<Integer> coll = new ArrayList<>();
        assertTrue(ClassAttributes.create(coll.getClass()).isCollection());
    }

    @Test
    public void testIsMap() {
        assertFalse(unitUnderTest.isMap());
        final Map<String, Integer> map = new HashMap<>();
        assertTrue(ClassAttributes.create(map.getClass()).isMap());
    }

    @Test
    public void testIsEnum() {
        assertFalse(unitUnderTest.isEnum());
        assertTrue(ClassAttributes.create(Power.class).isEnum());
    }

    @Test
    public void testIsVoid() {
        assertFalse(unitUnderTest.isVoid());
        final MethodAttributes methodAttributes = unitUnderTest.getMethods().get(0);
        assertTrue(methodAttributes.getReturnType().isVoid());
    }

    @Test
    public void testGetElementType() {
        assertFalse(unitUnderTest.getElementType().isPresent());
    }

    @Test
    public void testGetKeyType() {
        assertFalse(unitUnderTest.getKeyType().isPresent());
    }

    @Test
    public void testGetValueType() {
        assertFalse(unitUnderTest.getValueType().isPresent());
    }

    @Test
    public void testGetParameterizedTypes() {
        assertTrue(unitUnderTest.getParameterizedTypes().isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testCreate_null() {
        ClassAttributes.create(null);
    }
}
