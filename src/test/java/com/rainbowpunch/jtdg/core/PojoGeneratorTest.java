package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.spi.PojoGenerator;
import com.rainbowpunch.jtdg.core.falseDomain.ClassA;
import com.rainbowpunch.jtdg.core.limiters.primitive.IntegerLimiter;
import com.rainbowpunch.jtdg.core.limiters.NestedLimiter;
import com.rainbowpunch.jtdg.core.limiters.parameters.NumberSign;
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
    public void test() {
        PojoGenerator<ClassA> generator = new PojoGenerator<>(ClassA.class)
                .analyzePojo();
        ClassA objA = generator.generatePojo();
        classFieldsNotNull(objA);
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