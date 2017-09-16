package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.core.falseDomain.ClassA;
import com.rainbowpunch.jtdg.core.falseDomain.ClassC;
import com.rainbowpunch.jtdg.core.falseDomain.ClassD;
import com.rainbowpunch.jtdg.core.falseDomain.ClassE;
import com.rainbowpunch.jtdg.core.limiters.NestedLimiter;
import com.rainbowpunch.jtdg.core.limiters.ObjectLimiter;
import com.rainbowpunch.jtdg.core.limiters.RegexLimiter;
import com.rainbowpunch.jtdg.spi.PojoGenerator;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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
                .andLimitField("phoneNumber", new RegexLimiter("(\\d{3})-\\d{3}-\\d{4}"))
                .andLimitField("strangeString", new NestedLimiter<>(ClassC.class, new RegexLimiter("xy[acd]{1,4}.\\s\\d{2}[^peatd]\\[")))
                .andLimitField("objE", ObjectLimiter.ofObjects(Arrays.asList(e1, e2)))
                .analyzePojo();
        ClassA objA = generator.generatePojo();

        classFieldsNotNull(objA);
        assertTrue(Pattern.compile("\\(\\d{3}\\)-\\d{3}-\\d{4}").matcher(objA.getPhoneNumber()).find());
        objA.getObjC().stream()
                .forEach(oc -> {
                    assertTrue(Pattern.compile("xy[acd]{1,4}.\\s\\d{2}[^peatd]\\[").matcher(oc.getStrangeString()).find());
                });
        if (!objA.getObjE().equals(e1) && !objA.getObjE().equals(e2)) {
            fail();
        }
    }

    @Ignore
    @Test
    public void loadTest() {
        int cycles = 1000000;

        ClassE e1 = new ClassE();
        e1.setValue1("Hello");
        e1.setValue2(99);

        ClassE e2 = new ClassE();
        e2.setValue1("World");
        e2.setValue2(100);

        PojoGenerator<ClassA> generator = new PojoGenerator<>(ClassA.class)
                .andLimitField("phoneNumber", new RegexLimiter("(\\d{3})-\\d{3}-\\d{4}"))
                .andLimitField("strangeString", new NestedLimiter<>(ClassC.class, new RegexLimiter("xy[acd]{1,4}.\\s\\d{2}[^peatd]\\[")))
                .andLimitField("objE", ObjectLimiter.ofObjects(Arrays.asList(e1, e2)))
                .analyzePojo();

        List<ClassA> listOfObjA = new ArrayList<>();
        List<Long> buildTime = new ArrayList<>();

        for(int i = 0; i < cycles; i++) {
            long start = System.nanoTime();
            listOfObjA.add(generator.generatePojo());
            long end = System.nanoTime();
            buildTime.add(end - start);
            if (i % 10000 == 0) {
                System.out.println(i);
            }
        }

        double averageTime = 0L;

        for (Long aLong : buildTime) {
            averageTime += aLong;
        }
        averageTime /= cycles;
        System.out.println("------------------------");
        System.out.println("------------------------");
        System.out.println("Average nano : " + averageTime);
        averageTime /= 1000000000;
        System.out.println("Average seconds : " + averageTime);

        assertTrue(50000 < averageTime);

        Pattern phoneNumberPattern = Pattern.compile("\\(\\d{3}\\)-\\d{3}-\\d{4}");
        Pattern strangeStringPattern = Pattern.compile("xy[acd]{1,4}.\\s\\d{2}[^peatd]\\[");

        for (ClassA objA : listOfObjA) {
            classFieldsNotNull(objA);
            assertTrue(phoneNumberPattern.matcher(objA.getPhoneNumber()).find());
            objA.getObjC().stream()
                    .forEach(oc -> {
                        assertTrue(strangeStringPattern.matcher(oc.getStrangeString()).find());
                    });
            if (!objA.getObjE().equals(e1) && !objA.getObjE().equals(e2)) {
                fail();
            }
        }
    }

    @Test
    public void cloneTest() {

        ClassE e1 = new ClassE();
        e1.setValue1("Hello");
        e1.setValue2(99);

        ClassE e2 = new ClassE();
        e2.setValue1("World");
        e2.setValue2(100);

        PojoGenerator<ClassA> baseGen = new PojoGenerator<>(ClassA.class, 196809462)
                .andLimitField("phoneNumber", new RegexLimiter("(\\d{3})-\\d{3}-\\d{4}"))
                .andLimitField("strangeString", new NestedLimiter<>(ClassC.class, new RegexLimiter("xy[acd]{1,4}.\\s\\d{2}[^peatd]\\[")))
                .andLimitField("objE", ObjectLimiter.ofObjects(Arrays.asList(e1, e2)));

        PojoGenerator<ClassA> generator1 = baseGen.clone().analyzePojo();
        PojoGenerator<ClassA> generator2 = baseGen.clone().analyzePojo();

        ClassA classA1 = generator1.generatePojo();
        ClassA classA2 = generator2.generatePojo();

        assertEquals(classA1, classA2);
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