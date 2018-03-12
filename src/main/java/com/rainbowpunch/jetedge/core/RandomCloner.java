package com.rainbowpunch.jetedge.core;

import com.rainbowpunch.jetedge.core.exception.CriticalJetedgeException;
import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class takes in a Random, and returns a clone of it.
 */
public final class RandomCloner {

    private static final Field field;

    static {
        try {
            field = Random.class.getDeclaredField("seed");
            field.setAccessible(true);
        } catch (Exception e) {
            throw new CriticalJetedgeException(e); // TODO: 3/12/18 Come up with a better exception
        }
    }

    /**
     * This method takes in a Random and returns a clone of it.
     * @param randomToClone
     * @return
     */
    public static Random cloneRandom(Random randomToClone) {
        try {
            long longSeed = ((AtomicLong) field.get(randomToClone)).get();
            Random limiterRandomClone = new Random();
            field.set(limiterRandomClone, new AtomicLong(longSeed));

            return limiterRandomClone;
        } catch (Exception e) {
            throw new LimiterConstructionException("Failed to clone Random for generator seeding.");
        }
    }
}
