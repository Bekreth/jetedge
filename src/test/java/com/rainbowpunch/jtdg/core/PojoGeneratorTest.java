package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.api.PojoGenerator;
import com.rainbowpunch.jtdg.core.falseDomain.Address;
import com.rainbowpunch.jtdg.core.falseDomain.Employee;
import com.rainbowpunch.jtdg.core.limiters.primative.IntegerLimiter;
import com.rainbowpunch.jtdg.core.limiters.NestedLimiter;
import com.rainbowpunch.jtdg.core.limiters.parameters.NumberSign;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.*;

/**
 *
 */
public class PojoGeneratorTest {

    @Test
    public void test() {
        PojoGenerator<Employee> generator = new PojoGenerator<>(Employee.class)
                .andLimitField("age", new IntegerLimiter(10, 10, NumberSign.POSITIVE))
                .andLimitField("houseNumber", new NestedLimiter<>(Address.class, new IntegerLimiter(5)))
                //.andLimitField("arrayField", new ListLimiter<>())
                .analyzePojo();

        Employee employee = generator.generatePojo();

        assertNotEquals(0, employee.getAge());
        assertNotNull(employee.getName());
        assertNotNull(employee.getPhoneNumber());
    }

    @Test
    public void test1() throws Exception {
        Method method = Employee.class.getDeclaredMethod("setName", String.class);
        Class type = method.getParameterTypes()[0];
        System.out.println("");
    }

    @Test
    public void test2() throws Exception {



    }


}