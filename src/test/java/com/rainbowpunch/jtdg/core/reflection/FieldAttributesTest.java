package com.rainbowpunch.jtdg.core.reflection;

import static org.junit.Assert.assertEquals;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.rainbowpunch.jtdg.core.falseDomain.ClassA;
import com.rainbowpunch.jtdg.test.Pojos.Person;
import junit.framework.AssertionFailedError;
import org.junit.Test;

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
}
