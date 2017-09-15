package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.core.falseDomain.ClassA;
import com.rainbowpunch.jtdg.core.falseDomain.ClassD;
import com.rainbowpunch.jtdg.core.falseDomain.ClassE;
import com.rainbowpunch.jtdg.core.limiters.ObjectLimiter;
import com.rainbowpunch.jtdg.spi.PojoGenerator;
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
        ClassE e1 = new ClassE();
        e1.setValue1("Hello");
        e1.setValue2(99);

        ClassE e2 = new ClassE();
        e2.setValue1("World");
        e2.setValue2(100);

        PojoGenerator<ClassA> generator = new PojoGenerator<>(ClassA.class)
                .andLimitField("objE", ObjectLimiter.ofObjects(Arrays.asList(e1, e2)))
                .analyzePojo();
        ClassA objA = generator.generatePojo();
        classFieldsNotNull(objA);

        if (!objA.getObjE().equals(e1) && !objA.getObjE().equals(e2)) {
            fail();
        }
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
                    String assertMessage =String.format("Parent Obj : {}, Field Name : {}",
                            object.getClass(), field.getName());
                    assertNotNull(assertMessage, field);
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