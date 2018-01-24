package com.rainbowpunch.jetedge.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class provides named subsets for how to divide up readable characters.
 */
public final class ReadableCharList {

    public static final List<Character> LIST_OF_ALL_CHAR = IntStream.range(32, 126)
            .mapToObj(i -> (char) i)
            .collect(Collectors.toList());

    public static final List<Character> LIST_OF_CHAR_NO_SPACE = LIST_OF_ALL_CHAR.subList(1, LIST_OF_ALL_CHAR.size());

    public static final List<Character> LIST_OF_CHAR_DIGITS = LIST_OF_ALL_CHAR.subList(16, 26);

    public static final List<Character> LIST_OF_CHAR_NO_DIGITS = new ArrayList<>();

    public static final List<Character> LIST_OF_ALL_UPPER_CASE_CHAR = new ArrayList<>();

    public static final List<Character> LIST_OF_ALL_LOWER_CASE_CHAR = new ArrayList<>();

    public static final List<Character> LIST_OF_ALL_ALPHA_CHAR = new ArrayList<>();

    public static final List<Character> LIST_OF_ALPHA_NUMERIC_CHAR = new ArrayList<>();

    static {
        LIST_OF_CHAR_NO_DIGITS.addAll(LIST_OF_ALL_CHAR.subList(0, 15));
        LIST_OF_CHAR_NO_DIGITS.addAll(LIST_OF_ALL_CHAR.subList(27, LIST_OF_ALL_CHAR.size()));
        LIST_OF_ALL_UPPER_CASE_CHAR.addAll(LIST_OF_ALL_CHAR.subList(33, 59));
        LIST_OF_ALL_LOWER_CASE_CHAR.addAll(LIST_OF_ALL_CHAR.subList(65, 91));

        LIST_OF_ALL_ALPHA_CHAR.addAll(LIST_OF_ALL_UPPER_CASE_CHAR);
        LIST_OF_ALL_ALPHA_CHAR.addAll(LIST_OF_ALL_LOWER_CASE_CHAR);

        LIST_OF_ALPHA_NUMERIC_CHAR.addAll(LIST_OF_ALL_ALPHA_CHAR);
        LIST_OF_ALPHA_NUMERIC_CHAR.addAll(LIST_OF_CHAR_DIGITS);
    }

}