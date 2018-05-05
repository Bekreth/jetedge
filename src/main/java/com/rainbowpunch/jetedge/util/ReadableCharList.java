package com.rainbowpunch.jetedge.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
/**
 * This class provides named subsets for how to divide up readable characters.  All
 * fields in the class are immutable.  The character set used in this class are all from the
 * Basic Latin character set.
 *
 * See https://en.wikipedia.org/wiki/List_of_Unicode_characters#Basic_Latin
 */
public final class ReadableCharList {

    /**
     * all characters in the unicode range 0x20 (32) - 0x7E (126) inclusive
     */
    public static final List<Character> LIST_OF_ALL_CHAR;

    /**
     * all the characters in LIST_OF_ALL_CHAR without the space
     */
    public static final List<Character> LIST_OF_CHAR_NO_SPACE;

    /**
     * only the digits 0-9
     */
    public static final List<Character> LIST_OF_CHAR_DIGITS;

    /**
     * all the characters but no digits
     */
    public static final List<Character> LIST_OF_CHAR_NO_DIGITS;

    /**
     * only A-Z
     */
    public static final List<Character> LIST_OF_UPPER_CASE_CHAR;

    /**
     * only a-z
     */
    public static final List<Character> LIST_OF_LOWER_CASE_CHAR;

    /**
     * contains A-Z and a-z
     */
    public static final List<Character> LIST_OF_ALPHA_CHAR;

    /**
     * contains a-z, A-Z and 0-9
     */
    public static final List<Character> LIST_OF_ALPHA_NUMERIC_CHAR;

    static {
        // see https://en.wikipedia.org/wiki/List_of_Unicode_characters#Basic_Latin
        final int lowerBound = 0x20; // latin ' ' space
        final int upperBound = 0x7E; // latin '~' tilde

        LIST_OF_ALL_CHAR = unmodifiableList(IntStream.rangeClosed(lowerBound, upperBound)
                .mapToObj(i -> (char) i)
                .collect(Collectors.toList()));

        LIST_OF_CHAR_NO_SPACE = LIST_OF_ALL_CHAR.subList(1, LIST_OF_ALL_CHAR.size());
        LIST_OF_CHAR_DIGITS = LIST_OF_ALL_CHAR.subList(16, 26);
        LIST_OF_UPPER_CASE_CHAR = LIST_OF_ALL_CHAR.subList(33, 59);
        LIST_OF_LOWER_CASE_CHAR = LIST_OF_ALL_CHAR.subList(65, 91);


        LIST_OF_CHAR_NO_DIGITS = unmodifiableList(
                Stream.concat(LIST_OF_ALL_CHAR.subList(0, 16).stream(),
                LIST_OF_ALL_CHAR.subList(26, LIST_OF_ALL_CHAR.size()).stream())
                .collect(Collectors.toList()));


        LIST_OF_ALPHA_CHAR = unmodifiableList(
                Stream.concat(LIST_OF_UPPER_CASE_CHAR.stream(), LIST_OF_LOWER_CASE_CHAR.stream()).collect(Collectors.toList()));

        LIST_OF_ALPHA_NUMERIC_CHAR = unmodifiableList(
                Stream.concat(LIST_OF_ALPHA_CHAR.stream(), LIST_OF_CHAR_DIGITS.stream()).collect(Collectors.toList()));
    }

}