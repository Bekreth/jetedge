package com.rainbowpunch.jetedge.core.limiters.special;

import com.rainbowpunch.jetedge.core.limiters.common.ConstantValueLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.EnumLimiter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class MorphLimiterTest {


    @Test
    public void testEnum_getInnerValue() {
        final int seed = 98322;
        final int testCount = 10;

        List<Flatware> flatware = new ArrayList<>();
        List<Integer> tinesOfFlatware = new ArrayList<>();
        List<String> fallSounds = new ArrayList<>();

        Random random = new Random(seed);
        EnumLimiter<Flatware> flatwareLimiter = new EnumLimiter<>(Flatware.class);
        for (int i = 0; i < testCount; i ++) {
            flatware.add(flatwareLimiter.generateSupplier(random).get());
        }

        random = new Random(seed);
        MorphLimiter<Flatware, Integer> tineLimiter = new MorphLimiter<>(flatwareLimiter, (flat) -> flat.getTines());
        for (int i = 0; i < testCount; i ++) {
            tinesOfFlatware.add(tineLimiter.generateSupplier(random).get());
        }

        random = new Random(seed);
        MorphLimiter<Flatware, String> fallSoundLimiter = new MorphLimiter<>(flatwareLimiter, (flat) -> flat.getSoundItMakesWhenDropped());
        for (int i = 0; i < testCount; i ++) {
            fallSounds.add(fallSoundLimiter.generateSupplier(random).get());
        }


        for (int i = 0; i < testCount; i++) {
            assertEquals((Integer) flatware.get(i).getTines(), tinesOfFlatware.get(i));
            assertEquals(flatware.get(i).getSoundItMakesWhenDropped(), fallSounds.get(i));
        }

    }

    @Test
    public void testClassCasting() {
        MorphLimiter limiter = MorphLimiter.castToString(new ConstantValueLimiter<>(new NotAString()));
        String output = (String) limiter.generateSupplier(new Random()).get();
        assertEquals("I became a string", output);
    }


    private static class NotAString {
        @Override
        public String toString() {
            return "I became a string";
        }
    }

    private enum Flatware {
        FORK(3, "pa-ping"),
        KNIFE(1, "ding"),
        SPOON(0, ".....");

        private Integer tines;
        private String soundItMakesWhenDropped;

        Flatware(int tines, String soundItMakesWhenDropped) {
            this.tines = tines;
            this.soundItMakesWhenDropped = soundItMakesWhenDropped;
        }

        public int getTines() {
            return tines;
        }

        public String getSoundItMakesWhenDropped() {
            return soundItMakesWhenDropped;
        }
    }

}