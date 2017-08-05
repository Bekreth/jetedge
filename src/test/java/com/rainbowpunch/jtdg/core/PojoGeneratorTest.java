package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.core.falseDomain.Employee;
import com.rainbowpunch.jtdg.core.limiters.IntegerLimiter;
import com.rainbowpunch.jtdg.core.limiters.parameters.NumberType;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 *
 */
public class PojoGeneratorTest {

    @Test
    public void test() {
        PojoGenerator<Employee> generator = new PojoGenerator<>(Employee.class)
                .andLimitField("age", new IntegerLimiter(10, 10, NumberType.POSITIVE))
                .analyzePojo();

        Employee employee = generator.generatePojo();

        assertNotEquals(0, employee.getAge());
        assertNotNull(employee.getName());
        assertNotNull(employee.getPhoneNumber());
    }

    @Test
    public void test1() {
        Random random = new Random();

        System.out.println(random.nextDouble());
        System.out.println(random.nextDouble());
        System.out.println(random.nextDouble());
        System.out.println(random.nextDouble());
        System.out.println(random.nextDouble());
        System.out.println(random.nextDouble());
        System.out.println(random.nextDouble());
        System.out.println(random.nextDouble());
        System.out.println(random.nextDouble());
        System.out.println(random.nextDouble());

    }


}