package com.rainbowpunch.jetedge.core.limiters.maps;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.exception.PojoConstructionException;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.ConstantValueLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.IntegerLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.StringLimiter;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapLimiterTest {

    @Test
    public void testSimpleSuccess() {
        IntegerLimiter integerLimiter = new IntegerLimiter();
        StringLimiter stringLimiter = new StringLimiter();
        MapLimiter<Integer, String> mapLimiter = new MapLimiter<>(integerLimiter, stringLimiter);

        Supplier<Map<Integer, String>> mapSupplier = mapLimiter.generateSupplier(new Random());

        for (int i = 0; i < 100; i++) {
            Map<Integer, String> map = mapSupplier.get();
            assertTrue(map.size() >= 3 && map.size() <= 7);
        }
    }

    @Test
    public void testSimpleSuccess_changeRanges() {
        int high = 15;
        int low = 10;
        IntegerLimiter integerLimiter = new IntegerLimiter();
        StringLimiter stringLimiter = new StringLimiter();
        MapLimiter<Integer, String> mapLimiter = new MapLimiter<>(integerLimiter, stringLimiter, 5, 10,
                MapLimiter.ConflictResolutionStrategy.DROP_ENTRY);

        Supplier<Map<Integer, String>> mapSupplier = mapLimiter.generateSupplier(new Random());

        for (int i = 0; i < 100; i++) {
            Map<Integer, String> map = mapSupplier.get();
            assertTrue(map.size() >= low && map.size() <= high);
        }
    }

    @Test
    public void testConflictResolution_DropEntry() {
        Limiter<Integer> keyLimiter = new SimpleAbstractLimiter<Integer>() {
            Queue<Integer> integerStack = new ArrayDeque<>();
            @Override
            public Supplier<Integer> generateSupplier(Random random) {
                IntStream.range(0, 6).sequential().forEach(integerStack::add);
                return () -> {
                    Integer value = integerStack.remove();
                    integerStack.add(value);
                    return value;
                };
            }
        };
        Limiter<String> valueLimiter = new SimpleAbstractLimiter<String>() {
            Queue<String> stringStack = new ArrayDeque<>();
            @Override
            public Supplier<String> generateSupplier(Random random) {
                IntStream.range(97, 122).sequential().mapToObj(i -> Character.toString((char) i))
                        .forEach(stringStack::add);
                return () -> {
                    String value = stringStack.remove();
                    stringStack.add(value);
                    return value;
                };
            }
        };
        MapLimiter<Integer, String> mapLimiter = new MapLimiter<>(keyLimiter, valueLimiter, 0, 10,
                MapLimiter.ConflictResolutionStrategy.DROP_ENTRY);

        Supplier<Map<Integer, String>> mapSupplier = mapLimiter.generateSupplier(new Random());

        Map<Integer, String> map = mapSupplier.get();
        assertTrue(map.size() == 6);
        assertEquals(map.get(0), "a");
        assertEquals(map.get(1), "b");
        assertEquals(map.get(2), "c");
        assertEquals(map.get(3), "d");
        assertEquals(map.get(4), "e");
        assertEquals(map.get(5), "f");
    }

    @Test
    public void testConflictResolution_OverWrite() {
        Limiter<Integer> keyLimiter = new SimpleAbstractLimiter<Integer>() {
            Queue<Integer> integerStack = new ArrayDeque<>();
            @Override
            public Supplier<Integer> generateSupplier(Random random) {
                IntStream.range(0, 6).sequential().forEach(integerStack::add);
                return () -> {
                    Integer value = integerStack.remove();
                    integerStack.add(value);
                    return value;
                };
            }
        };
        Limiter<String> valueLimiter = new SimpleAbstractLimiter<String>() {
            Queue<String> stringStack = new ArrayDeque<>();
            @Override
            public Supplier<String> generateSupplier(Random random) {
                IntStream.range(97, 122).sequential().mapToObj(i -> Character.toString((char) i))
                        .forEach(stringStack::add);
                return () -> {
                    String value = stringStack.remove();
                    stringStack.add(value);
                    return value;
                };
            }
        };
        MapLimiter<Integer, String> mapLimiter = new MapLimiter<>(keyLimiter, valueLimiter, 0, 10,
                MapLimiter.ConflictResolutionStrategy.OVERWRITE_ENTRY);

        Supplier<Map<Integer, String>> mapSupplier = mapLimiter.generateSupplier(new Random());

        Map<Integer, String> map = mapSupplier.get();
        assertTrue(map.size() == 6);
        assertEquals(map.get(0), "g");
        assertEquals(map.get(1), "h");
        assertEquals(map.get(2), "i");
        assertEquals(map.get(3), "j");
        assertEquals(map.get(4), "e");
        assertEquals(map.get(5), "f");
    }

    @Test
    public void testConflictResolution_RemakeKey() {
        Limiter<Integer> keyLimiter = new SimpleAbstractLimiter<Integer>() {
            Queue<Integer> integerStack = new ArrayDeque<>();
            @Override
            public Supplier<Integer> generateSupplier(Random random) {
                for (int i = 0; i < 10; i++) {
                    integerStack.add(i);
                    if (i % 2 == 0) {
                        integerStack.add(i);
                    }
                }
                return () -> {
                    Integer value = integerStack.remove();
                    integerStack.add(value);
                    return value;
                };
            }
        };
        Limiter<String> valueLimiter = new SimpleAbstractLimiter<String>() {
            Queue<String> stringStack = new ArrayDeque<>();
            @Override
            public Supplier<String> generateSupplier(Random random) {
                IntStream.range(97, 122).sequential().mapToObj(i -> Character.toString((char) i))
                        .forEach(stringStack::add);
                return () -> {
                    String value = stringStack.remove();
                    stringStack.add(value);
                    return value;
                };
            }
        };
        MapLimiter<Integer, String> mapLimiter = new MapLimiter<>(keyLimiter, valueLimiter, 0, 6,
                MapLimiter.ConflictResolutionStrategy.REMAKE_KEY);

        Supplier<Map<Integer, String>> mapSupplier = mapLimiter.generateSupplier(new Random());

        Map<Integer, String> map = mapSupplier.get();
        assertTrue(map.size() == 6);
        assertEquals(map.get(0), "a");
        assertEquals(map.get(1), "b");
        assertEquals(map.get(2), "c");
        assertEquals(map.get(3), "d");
        assertEquals(map.get(4), "e");
        assertEquals(map.get(5), "f");
    }

    @Test
    public void testConflictResolution_RemakeEntry() {
        Limiter<Integer> keyLimiter = new SimpleAbstractLimiter<Integer>() {
            Queue<Integer> integerStack = new ArrayDeque<>();
            @Override
            public Supplier<Integer> generateSupplier(Random random) {
                for (int i = 0; i < 10; i++) {
                    integerStack.add(i);
                    if (i % 2 == 0) {
                        integerStack.add(i);
                    }
                }
                return () -> {
                    Integer value = integerStack.remove();
                    integerStack.add(value);
                    return value;
                };
            }
        };
        Limiter<String> valueLimiter = new SimpleAbstractLimiter<String>() {
            Queue<String> stringStack = new ArrayDeque<>();
            @Override
            public Supplier<String> generateSupplier(Random random) {
                IntStream.range(97, 122).sequential().mapToObj(i -> Character.toString((char) i))
                        .forEach(stringStack::add);
                return () -> {
                    String value = stringStack.remove();
                    stringStack.add(value);
                    return value;
                };
            }
        };
        MapLimiter<Integer, String> mapLimiter = new MapLimiter<>(keyLimiter, valueLimiter, 0, 6,
                MapLimiter.ConflictResolutionStrategy.REMAKE_ENTRY);

        Supplier<Map<Integer, String>> mapSupplier = mapLimiter.generateSupplier(new Random());

        Map<Integer, String> map = mapSupplier.get();
        assertTrue(map.size() == 6);
        assertEquals(map.get(0), "a");
        assertEquals(map.get(1), "c");
        assertEquals(map.get(2), "d");
        assertEquals(map.get(3), "f");
        assertEquals(map.get(4), "g");
        assertEquals(map.get(5), "i");
    }

    @Test(expected = PojoConstructionException.class)
    public void testConflictResolution_throwException() {
        Limiter<Integer> keyLimiter = new ConstantValueLimiter<>(5);
        Limiter<String> valueLimiter = new StringLimiter();
        MapLimiter<Integer, String> mapLimiter = new MapLimiter<>(keyLimiter, valueLimiter, 0, 6,
                MapLimiter.ConflictResolutionStrategy.THROW_EXCEPTION);

        Supplier<Map<Integer, String>> mapSupplier = mapLimiter.generateSupplier(new Random());
        mapSupplier.get();
    }

    @Test(expected = LimiterConstructionException.class)
    public void testValidationError_nullStrategy() {
        new MapLimiter<>(new StringLimiter(), new StringLimiter(), 5, 5, null);
    }

    @Test(expected = LimiterConstructionException.class)
    public void testValidationError_negativeRange() {
        new MapLimiter<>(new StringLimiter(), new StringLimiter(), -1, 5,
                MapLimiter.ConflictResolutionStrategy.DROP_ENTRY);
    }

    @Test(expected = LimiterConstructionException.class)
    public void testValidationError_negativeOffset() {
        new MapLimiter<>(new StringLimiter(), new StringLimiter(), 4, -1,
                MapLimiter.ConflictResolutionStrategy.DROP_ENTRY);
    }

    @Test
    public void testReconcile() {
        MapLimiter<Integer, String> mapLimiter = new MapLimiter<>(null, null);
        MapLimiter<Integer, String> reconcileMap = new MapLimiter<Integer, String>(new ConstantValueLimiter<>(1),
                new ConstantValueLimiter<>("hello"));

        MapLimiter<Integer, String> testMapLimiter = mapLimiter.reconcile(reconcileMap);

        Map<Integer, String> map = testMapLimiter.generateSupplier(new Random()).get();
        assertTrue(map.get(1).equals("hello"));
    }

}