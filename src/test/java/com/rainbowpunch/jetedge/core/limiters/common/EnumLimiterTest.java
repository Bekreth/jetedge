package com.rainbowpunch.jetedge.core.limiters.common;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class EnumLimiterTest {

    @Test
    public void testEnum() {
        Random random = new Random();
        EnumLimiter<BreakfastFood> foodEnumLimiter = new EnumLimiter<>(BreakfastFood.class);
        for (int i = 0; i < 10; i ++) {
            BreakfastFood food = foodEnumLimiter.generateSupplier(random).get();
            Arrays.asList(BreakfastFood.values()).contains(food);
        }
    }

    private enum BreakfastFood {
        EGGS,
        BACON,
        BAGEL
    }

}