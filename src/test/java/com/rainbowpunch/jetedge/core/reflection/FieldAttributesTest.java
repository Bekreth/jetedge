package com.rainbowpunch.jetedge.core.reflection;

import com.rainbowpunch.jetedge.test.Pojos.Person;

import java.util.function.BiConsumer;
import java.util.function.Function;
import junit.framework.AssertionFailedError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldAttributesTest {
    @Test
    public void testFieldSetter() {
        FieldAttributes fieldAttributes =
                ClassAttributes.create(Person.class)
                        .getFields().stream()
                        .filter(f -> "age".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new);

        BiConsumer setter = fieldAttributes.getSetter();
        Person instance = new Person();
        int age = 29;

        setter.accept(instance, age);
        assertEquals(age, instance.getAge());
    }

    @Test
    public void testFieldSetterAcceptsNullValue() {
        FieldAttributes fieldAttributes =
                ClassAttributes.create(Person.class)
                        .getFields().stream()
                        .filter(f -> "name".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new);
        BiConsumer setter = fieldAttributes.getSetter();
        Person instance = new Person();
        setter.accept(instance, null);
        assertEquals(null, instance.getName());
    }

    @Test
    public void testFieldGetter() {
        FieldAttributes fieldAttributes =
                ClassAttributes.create(Person.class)
                        .getFields().stream()
                        .filter(f -> "age".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new);

        Function<Person, Integer> getter = fieldAttributes.getGetter();

        Person person = new Person();
        person.setAge(99);

        assertEquals(Integer.valueOf(person.getAge()), getter.apply(person));
    }


    @Test
    public void testFieldSetterWithArray() {
        FieldAttributes fieldAttributes =
                ClassAttributes.create(Person.class)
                        .getFields().stream()
                        .filter(f -> "secretName".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new);
        BiConsumer setter = fieldAttributes.getSetter();
        Person instance = new Person();
        Character[] secretName = {'s', 'u', 'p', 'e', 'r', 'm', 'a', 'n'};

        setter.accept(instance, secretName);
        char[] fromInstance = instance.getSecretName();
        for (int i = 0; i < secretName.length; i++) {
            assertTrue(secretName[i].equals(fromInstance[i]));
        }
    }
}
