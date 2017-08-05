package com.rainbowpunch.interfaces;

import com.rainbowpunch.interfaces.falseDomain.Employee;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.*;

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