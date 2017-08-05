package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.core.falseDomain.Employee;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class DefaultPojoAnalyzerTest {

    private DefaultPojoAnalyzer<Employee> pojoAnalyzer;

    @Before
    public void init() {
        pojoAnalyzer = new DefaultPojoAnalyzer<>();
    }

    @Test
    public void testAnalyzer() {
        PojoAttributes<Employee> attributes = new PojoAttributes<>();
        pojoAnalyzer.parsePojo(Employee.class, attributes);

        Employee employee = new Employee();



        System.out.println("Test");
    }

}