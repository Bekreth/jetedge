package com.rainbowpunch.jetedge.core.limiters.common;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class ObjectLimiterTest {
    private static Set<Integer> possibleValues = new HashSet<>();

    @BeforeClass
    public static void init() {
        possibleValues.add(2);
        possibleValues.add(4);
        possibleValues.add(6);
        possibleValues.add(8);
        possibleValues.add(10);
    }

    @Test
    public void testObjectLimiter_withCollection() {
        Supplier<Integer> objectLimiter = ObjectLimiter.ofObjects(possibleValues)
                .generateSupplier(new Random());

        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(possibleValues.contains(objectLimiter.get()));
        }
    }

    @Test
    public void testObjectLimiter_withVarargs() {
        Supplier<Integer> objectLimiter = ObjectLimiter.ofObjects(2, 4, 6, 8, 10)
                .generateSupplier(new Random());

        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(possibleValues.contains(objectLimiter.get()));
        }
    }

    @Test(expected = LimiterConstructionException.class)
    public void testObjectLimiter_withEmptyCollection() {
        Supplier<Integer> objectLimiter = ObjectLimiter.ofObjects(new HashSet<Integer>())
                .generateSupplier(new Random());
    }

}