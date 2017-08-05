package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.api.PojoGenerator;
import com.rainbowpunch.jtdg.core.falseDomain.Address;
import com.rainbowpunch.jtdg.core.falseDomain.Employee;
import com.rainbowpunch.jtdg.core.falseDomain.HomeState;
import com.rainbowpunch.jtdg.core.limiters.IntegerLimiter;
import com.rainbowpunch.jtdg.core.limiters.NestedLimiter;
import com.rainbowpunch.jtdg.core.limiters.parameters.NumberType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class PojoGeneratorTest {

    @Test
    public void test() {
        PojoGenerator<Employee> generator = new PojoGenerator<>(Employee.class)
                .andLimitField("age", new IntegerLimiter(10, 10, NumberType.POSITIVE))
                .andLimitField("houseNumber", new NestedLimiter<>(Address.class, new IntegerLimiter(5)))
                .analyzePojo();

        Employee employee = generator.generatePojo();

        assertNotEquals(0, employee.getAge());
        assertNotNull(employee.getName());
        assertNotNull(employee.getPhoneNumber());
    }


}