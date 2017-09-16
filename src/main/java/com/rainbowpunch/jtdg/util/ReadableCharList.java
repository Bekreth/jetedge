package com.rainbowpunch.jtdg.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReadableCharList {

    public static List<Character> LIST_OF_ALL_CHAR = IntStream.range(32, 126)
            .mapToObj(i -> (char) i)
            .collect(Collectors.toList());

    public static List<Character> LIST_OF_CHAR_NO_SPACE = LIST_OF_ALL_CHAR.subList(1, LIST_OF_ALL_CHAR.size());

    public static List<Character> LIST_OF_CHAR_DIGITS = LIST_OF_ALL_CHAR.subList(16, 26);

    public static List<Character> LIST_OF_CHAR_NO_DIGITS = new ArrayList<>();

    static {
        LIST_OF_CHAR_NO_DIGITS.addAll(LIST_OF_ALL_CHAR.subList(0, 15));
        LIST_OF_CHAR_NO_DIGITS.addAll(LIST_OF_ALL_CHAR.subList(27, LIST_OF_ALL_CHAR.size()));
    }

}