package com.rainbowpunch.jetedge.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ReadableCharListImmutabilityTest {

    @Parameterized.Parameters(name = "{index}: {1}")
    public static Collection<Object[]> data() {
        // collect all the static fields from ReadableCharList as parameters
        // obviously this could be an issue if someone adds a non char list
        // field but I think it's safe to cross that bridge when we get there.
        return Arrays.stream(ReadableCharList.class.getDeclaredFields())
                .map(f -> {
                    List<Character> cs = null;
                    try {
                        cs = (List<Character>) f.get(ReadableCharList.class);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    return new Object[]{cs, f.getName()};
                })
                .collect(Collectors.toList());
    }

    @Parameterized.Parameter(0)
    public Collection<Character> chars;

    @Parameterized.Parameter(1)
    public String fieldName;

    @Test(expected = UnsupportedOperationException.class)
    public void cannotAdd() throws IllegalAccessException {
        chars.add(new Character('z'));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cannotRemove() throws IllegalAccessException {
        chars.remove(new Character('z'));
    }
}