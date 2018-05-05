package com.rainbowpunch.jetedge.core.limiters.common;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ConstantValueLimiterTest {

    @Test
    public void constructedWithSimpleValue() {
        ConstantValueLimiter<String> cvl = new ConstantValueLimiter<String>("abc");

        IntStream.range(0, 25).forEach(i -> {
            String val = cvl.generateSupplier(null).get();
            assertEquals("abc", val);
        });
    }

    @Test
    public void constructedWithSupplier() {
        ConstantValueLimiter<String> cvl = new ConstantValueLimiter<String>(() -> "abc");

        IntStream.range(0, 25).forEach(i -> {
            String val = cvl.generateSupplier(null).get();
            assertEquals("abc", val);
        });
    }

    @Test
    public void constructedWithRotatingSupplier() {
        // given
        int from = 0, to = 25;
        List<String> zeroToTwentyFive = IntStream.range(from, to)
                .mapToObj(Integer::toString)
                .collect(Collectors.toList());

        Stack<String> returnValues = new Stack<>();
        returnValues.addAll(zeroToTwentyFive);

        // our supplier will simply pop a value off the stack we constructed above
        ConstantValueLimiter<String> cvl = new ConstantValueLimiter<String>(returnValues::pop);

        // when
        List<String> returnedValues = IntStream.range(from, to)
                .mapToObj(i -> cvl.generateSupplier(null).get())
                .collect(Collectors.toList());
        Collections.reverse(returnedValues); // there were popped off stack, so they'll be backwards

        // then
        assertEquals(zeroToTwentyFive, returnedValues);
    }

    @Test
    public void constructedWithFunction() {
        // given
        Set<Random> received = new HashSet<>();

        // our supplier will simply pop a value off the stack we constructed above
        Function<Random, String> fn = (r) -> {
            received.add(r);
            return "abc";
        };

        ConstantValueLimiter<String> cvl = new ConstantValueLimiter<String>(fn);

        // when
        IntStream.range(0, 25).forEach(i -> {
            cvl.generateSupplier(new Random()).get();
        });

        // then
        assertEquals("We should have 25 distinct 'random' objects we received",
                25,
                received.size());
    }

    @Test
    public void canBeStableWhenRandomIsSeeded() {
        Random r = new Random(777);

        // our generator function returns random boolean values as 0 and 1
        // based on the random value supplied to us
        Function<Random, Integer> fn = random -> Math.abs(random.nextInt()) % 2;

        ConstantValueLimiter<Integer> cvl = new ConstantValueLimiter<Integer>(fn);
        String zerosAndOnes = IntStream.range(0, 16)
                .mapToObj(i -> cvl.generateSupplier(r).get().toString())
                .collect(Collectors.joining());

        assertEquals("1110010111110100", zerosAndOnes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantConstructWithNullSupplier() {
        Supplier<String> s = null;
        new ConstantValueLimiter<>(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantConstructWithNullFunction() {
        Function<Random, String> fn = null;
        new ConstantValueLimiter<>(fn);
    }

}