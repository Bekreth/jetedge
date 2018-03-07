package com.rainbowpunch.jetedge.core.reflection;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.rainbowpunch.jetedge.test.Pojos.Extra;
import static com.rainbowpunch.jetedge.test.Pojos.Person;
import static com.rainbowpunch.jetedge.test.Pojos.Power;
import static com.rainbowpunch.jetedge.test.Pojos.Superhero;
import static com.rainbowpunch.jetedge.test.Pojos.SuperheroNetwork;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        ClassAttributes attributes = ClassAttributes.create(array.getClass());
        assertTrue(attributes.isArray());
        assertEquals(Integer.class, attributes.getClazz()); // check primitive erasure
    }

    @Test
    public void testIsCollection() {
        assertFalse(unitUnderTest.isCollection());
        final Collection<Integer> coll = new ArrayList<>();
        assertTrue(ClassAttributes.create(null, coll.getClass(), Arrays.asList(Integer.class)).isCollection());
    }

    @Test
    public void testIsEnum() {
        assertFalse(unitUnderTest.isEnum());
        assertTrue(ClassAttributes.create(Power.class).isEnum());
    }

    @Test
    public void testIsVoid() {
        assertFalse(unitUnderTest.isVoid());
    }

    @Test(expected = NullPointerException.class)
    public void testCreate_null() {
        ClassAttributes.create(null);
    }
}
