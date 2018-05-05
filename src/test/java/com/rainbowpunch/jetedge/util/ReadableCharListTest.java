package com.rainbowpunch.jetedge.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReadableCharListTest {

    public static final int TOTAL_CHAR_COUNT = (127 - 32);
    public static final int LETTER_COUNT = 26;
    public static final int DIGIT_COUNT = 10;

    @Test
    public void alphanumeric() {
        // these single character checks seem odd but they're simple checks to ensure any
        // indexed access is not 'off by one' since these chars are on the edge of each included
        // range. So they seem odd but i actually found a defect (and fixed it) because of them.
        assertTrue("Should have a", ReadableCharList.LIST_OF_ALPHA_NUMERIC_CHAR.contains('a'));
        assertTrue("Should have z", ReadableCharList.LIST_OF_ALPHA_NUMERIC_CHAR.contains('z'));
        assertTrue("Should have A", ReadableCharList.LIST_OF_ALPHA_NUMERIC_CHAR.contains('A'));
        assertTrue("Should have Z", ReadableCharList.LIST_OF_ALPHA_NUMERIC_CHAR.contains('Z'));
        assertTrue("Should have 0", ReadableCharList.LIST_OF_ALPHA_NUMERIC_CHAR.contains('0'));
        assertTrue("Should have 9", ReadableCharList.LIST_OF_ALPHA_NUMERIC_CHAR.contains('9'));

        ReadableCharList.LIST_OF_ALPHA_NUMERIC_CHAR.forEach(c -> {
            assertEquals(2 * LETTER_COUNT + DIGIT_COUNT, ReadableCharList.LIST_OF_ALPHA_NUMERIC_CHAR.size());
            assertTrue(c + " should be alphanumeric", c.toString().matches("[a-zA-Z0-9]"));
        });
    }
    @Test
    public void alpha() {
        assertTrue("Should have a", ReadableCharList.LIST_OF_ALPHA_CHAR.contains('a'));
        assertTrue("Should have z", ReadableCharList.LIST_OF_ALPHA_CHAR.contains('z'));
        assertTrue("Should have A", ReadableCharList.LIST_OF_ALPHA_CHAR.contains('A'));
        assertTrue("Should have Z", ReadableCharList.LIST_OF_ALPHA_CHAR.contains('Z'));

        assertEquals(2 * LETTER_COUNT, ReadableCharList.LIST_OF_ALPHA_CHAR.size());

        ReadableCharList.LIST_OF_ALPHA_CHAR.forEach(c -> {
            assertTrue(c + " should be alpha", c.toString().matches("[A-Za-z]"));
        });
    }
    @Test
    public void lowerAlpha() {
        assertTrue("Should have a", ReadableCharList.LIST_OF_LOWER_CASE_CHAR.contains('a'));
        assertTrue("Should have z", ReadableCharList.LIST_OF_LOWER_CASE_CHAR.contains('z'));

        assertEquals(LETTER_COUNT, ReadableCharList.LIST_OF_LOWER_CASE_CHAR.size());

        ReadableCharList.LIST_OF_LOWER_CASE_CHAR.forEach(c -> {
            assertTrue(c + " should be lowercase alpha", c.toString().matches("[a-z]"));
        });
    }
    @Test
    public void upperAlpha() {
        assertTrue("Should have A", ReadableCharList.LIST_OF_UPPER_CASE_CHAR.contains('A'));
        assertTrue("Should have Z", ReadableCharList.LIST_OF_UPPER_CASE_CHAR.contains('Z'));

        assertEquals(LETTER_COUNT, ReadableCharList.LIST_OF_UPPER_CASE_CHAR.size());

        ReadableCharList.LIST_OF_UPPER_CASE_CHAR.forEach(c -> {
            assertTrue(c + " should be upper case alpha", c.toString().matches("[A-Z]"));
        });
    }
    @Test
    public void charsNoDigits() {
        assertTrue("Should have /", ReadableCharList.LIST_OF_CHAR_NO_DIGITS.contains('/'));
        assertTrue("Should have :", ReadableCharList.LIST_OF_CHAR_NO_DIGITS.contains(':'));

        assertEquals(TOTAL_CHAR_COUNT - DIGIT_COUNT, ReadableCharList.LIST_OF_CHAR_NO_DIGITS.size());

        ReadableCharList.LIST_OF_CHAR_DIGITS.forEach(digit -> {
            assertFalse("Shouldn't have " + digit, ReadableCharList.LIST_OF_CHAR_NO_DIGITS.contains(digit));
        });
    }
    @Test
    public void digits() {
        assertTrue("Should have 0", ReadableCharList.LIST_OF_CHAR_DIGITS.contains('0'));
        assertTrue("Should have 9", ReadableCharList.LIST_OF_CHAR_DIGITS.contains('9'));

        assertEquals(DIGIT_COUNT, ReadableCharList.LIST_OF_CHAR_DIGITS.size());

        ReadableCharList.LIST_OF_CHAR_DIGITS.forEach(c -> {
            assertTrue(c + " should be a digit", c.toString().matches("[0-9]"));
        });
    }
    @Test
    public void charsWithoutSpace() {
        assertFalse("Should not have the space", ReadableCharList.LIST_OF_CHAR_NO_SPACE.contains(' '));

        assertEquals(TOTAL_CHAR_COUNT - 1, ReadableCharList.LIST_OF_CHAR_NO_SPACE.size());
    }

    @Test
    public void listOfAlphaNumericChars() {
        assertTrue("Should have space", ReadableCharList.LIST_OF_ALL_CHAR.contains(' '));
        assertTrue("Should have tilde", ReadableCharList.LIST_OF_ALL_CHAR.contains('~'));

        assertEquals(TOTAL_CHAR_COUNT, ReadableCharList.LIST_OF_ALL_CHAR.size());

        ReadableCharList.LIST_OF_ALL_CHAR.forEach(c -> {
            assertTrue(c + " should be in the 32-126 range",
                    c.toString().matches("[\\u0020-\\u007E]"));
        });

    }
}