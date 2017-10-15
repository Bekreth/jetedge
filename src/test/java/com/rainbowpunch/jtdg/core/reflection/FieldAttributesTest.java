package com.rainbowpunch.jtdg.core.reflection;

import static org.junit.Assert.assertEquals;

import java.util.function.BiConsumer;

import com.rainbowpunch.jtdg.core.test.Pojos.Person;
import junit.framework.AssertionFailedError;
import org.junit.Test;

public class FieldAttributesTest {
    @Test
    public void testFieldSetter() {
        final FieldAttributes fieldAttributes =
                ClassAttributes.create(Person.class)
                        .getFields().stream()
                        .filter(f -> "age".equals(f.getName()))
                        .findFirst().orElseThrow(AssertionFailedError::new);

        final BiConsumer setter = fieldAttributes.getSetter();
        final Person instance = new Person();
        final int age = 29;
        setter.accept(instance, age);
        assertEquals(age, instance.getAge());
    }
}
