package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.core.falseDomain.ClassD;
import com.rainbowpunch.jtdg.spi.PojoGenerator;
import com.rainbowpunch.jtdg.core.falseDomain.ClassA;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class PojoGeneratorTest {

    @Test
    public void simpleDepthTest() {
        PojoGenerator<ClassA> generator = new PojoGenerator<>(ClassA.class)
                .analyzePojo();
        ClassA objA = generator.generatePojo();
        classFieldsNotNull(objA);
    }

    @Test
    public void cloneTest() {
        PojoGenerator<ClassD> baseGen = new PojoGenerator<>(ClassD.class, 123456);

        PojoGenerator<ClassD> generator1 = baseGen.clone().analyzePojo();
        PojoGenerator<ClassD> generator2 = baseGen.clone().analyzePojo();

        ClassD classD1 = generator1.generatePojo();
        ClassD classD2 = generator2.generatePojo();

        assertEquals(classD1, classD2);

    }

    private static void classFieldsNotNull(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        Arrays.stream(fields)
                .forEach(field -> {
                    String assertMessage = String.format("Parent Obj : {%s}, Field Name : {%s}",
                            object.getClass(), field.getName());
                    field.setAccessible(true);
                    try {
                        assertNotNull(assertMessage, field.get(object));
                    } catch (Exception e) {
                        fail(assertMessage);
                    }
                    if(field.getType().isAssignableFrom(List.class)) {
                        try {
                            field.setAccessible(true);
                            List<?> objects = (List<?>) field.get(object);
                            objects.stream()
                                    .forEach(PojoGeneratorTest::classFieldsNotNull);
                        } catch (Exception e) {
                            fail(e.getMessage());
                        }
                    }
                });
    }

}