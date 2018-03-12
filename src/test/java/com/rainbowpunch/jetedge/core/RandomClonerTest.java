package com.rainbowpunch.jetedge.core;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class RandomClonerTest {

    @Test
    public void testSuccessfulRandomCloning() {
        Random random = new Random();
        Random clone = RandomCloner.cloneRandom(random);

        for (int i = 0; i < 1000; i++) {
            assertEquals(random.nextInt(), clone.nextInt());
        }
    }

    @Test
    public void testSuccessfulRandomCloning_Seeded() {
        int seed = 81757348;
        Random random = new Random(seed);
        Random clone = RandomCloner.cloneRandom(random);

        for (int i = 0; i < 1000; i++) {
            assertEquals(random.nextInt(), clone.nextInt());
        }
    }

}