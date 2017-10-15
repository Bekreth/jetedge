package com.rainbowpunch.jtdg.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.rainbowpunch.jtdg.core.falseDomain.ClassA;
import com.rainbowpunch.jtdg.core.falseDomain.ClassAchild;
import com.rainbowpunch.jtdg.core.falseDomain.ClassC;
import com.rainbowpunch.jtdg.core.falseDomain.ClassE;
import com.rainbowpunch.jtdg.core.limiters.BigDecimalLimiter;
import com.rainbowpunch.jtdg.core.limiters.NestedLimiter;
import com.rainbowpunch.jtdg.core.limiters.ObjectLimiter;
import com.rainbowpunch.jtdg.core.limiters.RegexLimiter;
import com.rainbowpunch.jtdg.core.limiters.collections.ListLimiter;
import com.rainbowpunch.jtdg.core.test.Pojos.Extra;
import com.rainbowpunch.jtdg.core.test.Pojos.Storyline;
import com.rainbowpunch.jtdg.core.test.Pojos.Superhero;
import com.rainbowpunch.jtdg.spi.PojoGenerator;
import com.rainbowpunch.jtdg.spi.PojoGeneratorBuilder;

import org.junit.Ignore;
import org.junit.Test;

/**
 *
 */
public class PojoGeneratorImplTest {
    @Test
    public void testGenerateEmptyPojo() {
        // TODO test that all relevant fields have been set properly (use seed)
        assertNotNull(
                new PojoGeneratorBuilder<>(Extra.class)
                        .build()
                        .generatePojo()
        );
    }

    @Test
    public void testGeneratePojo() {
        // TODO test that all relevant fields have been set properly (use seed)
        assertNotNull(
                new PojoGeneratorBuilder<>(Extra.class)
                        .build()
                        .generatePojo()
        );
    }

    @Test
    public void testGenerateNestedPojo() {
        // TODO more comprehensive testing for subfields (use seed)
        final Storyline storyline = new PojoGeneratorBuilder<>(Storyline.class)
                .build()
                .generatePojo();

        assertNotNull(storyline.getSuperhero());
        assertNotNull(storyline.getArchNemesis());
    }

    @Test
    public void testGenerateListPojo() {
        // TODO more comprehensive testing for sublist (use seed)
        final Superhero superhero = new PojoGeneratorBuilder<>(Superhero.class)
                .build()
                .generatePojo();

        assertNotNull(superhero.getSuperPowers());
    }

    @Test
    public void simpleDepthTest() {
        ClassE e1 = new ClassE();
        e1.setValue1("Hello");
        e1.setValue2(99);

        ClassE e2 = new ClassE();
        e2.setValue1("World");
        e2.setValue2(100);

        PojoGenerator<ClassAchild> generator = new PojoGeneratorBuilder<>(ClassAchild.class)
                .andLimitFieldWith("phoneNumber", new RegexLimiter("(\\d{3})-\\d{3}-\\d{4}"))
                .andLimitFieldWith("strangeString", new NestedLimiter<>(ClassC.class, new RegexLimiter("xy[acd]{1,4}.\\s\\d{2}[^peatd]\\[")))
                .andLimitFieldWith("objE", ObjectLimiter.ofObjects(Arrays.asList(e1, e2)))
                .andLimitFieldWith("echildlist", new ListLimiter(2, 4))
                .andLimitAllFieldsWith(new BigDecimalLimiter())
                .build();

        ClassAchild objA = generator.generatePojo();

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
        int cycles = 500000;

        ClassE e1 = new ClassE();
        e1.setValue1("Hello");
        e1.setValue2(99);

        ClassE e2 = new ClassE();
        e2.setValue1("World");
        e2.setValue2(100);

        PojoGenerator<ClassAchild> generator = new PojoGeneratorBuilder<>(ClassAchild.class)
                .andLimitFieldWith("phoneNumber", new RegexLimiter("(\\d{3})-\\d{3}-\\d{4}"))
                .andLimitFieldWith("strangeString", new NestedLimiter<>(ClassC.class, new RegexLimiter("xy[acd]{1,4}.\\s\\d{2}[^peatd]\\[")))
                .andLimitFieldWith("objE", ObjectLimiter.ofObjects(Arrays.asList(e1, e2)))
                .build();

        List<ClassAchild> listOfObjA = new ArrayList<>();
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

        PojoGeneratorBuilder<ClassAchild> baseGen = new PojoGeneratorBuilder<>(ClassAchild.class, 196809462)
                .andLimitFieldWith("phoneNumber", new RegexLimiter("(\\d{3})-\\d{3}-\\d{4}"))
                .andLimitFieldWith("strangeString", new NestedLimiter<>(ClassC.class, new RegexLimiter("xy[acd]{1,4}.\\s\\d{2}[^peatd]\\[")))
                .andLimitAllFieldsWith(new BigDecimalLimiter())
                .andLimitFieldWith("objE", ObjectLimiter.ofObjects(Arrays.asList(e1, e2)));

        PojoGenerator<ClassAchild> generator1 = baseGen.clone().build();
        PojoGenerator<ClassAchild> generator2 = baseGen.clone().build();

        ClassAchild classA1 = generator1.generatePojo();
        ClassAchild classA2 = generator2.generatePojo();

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
                                    .forEach(PojoGeneratorImplTest::classFieldsNotNull);
                        } catch (Exception e) {
                            fail(e.getMessage());
                        }
                    }
                });
    }

}