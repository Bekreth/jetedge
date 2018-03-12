package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.common.ObjectLimiter;
import com.rainbowpunch.jetedge.util.ReadableCharList;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A limiter for String data fields.
 */
public class StringLimiter extends ObjectLimiter<String> {

    private final Integer length;

    public StringLimiter() {
        length = 30;
    }

    /**
     * Provides a StringLimiter for Strings of a given length
     * @param length
     *          Desired String length
     */
    public StringLimiter(int length) {
        this(ReadableCharList.LIST_OF_ALL_CHAR, length);
    }

    /**
     * Provides a StringLimiter for Strings created with a given set of characters
     * @param charList
     *          List of characters that the String should be made of.
     */
    public StringLimiter(List<Character> charList) {
        this(charList, 30);
    }

    /**
     * Provides a StringLimiter for String created with a given set of characters and a given length.
     * @param charList
     *          List of characters that the String should be made of.
     * @param length
     *          Desired String length
     */
    public StringLimiter(List<Character> charList, int length) {
        this.length = length;
        List<String> stringList = charListToStringList(charList);
        this.updateObjectList(stringList);
    }

    private List<String> charListToStringList(List<Character> charList) {
        return charList.stream()
                .map(ch -> Character.toString(ch))
                .collect(Collectors.toList());
    }

    @Override
    protected List<String> configureObjectList() {
        return charListToStringList(ReadableCharList.LIST_OF_ALL_CHAR);
    }

    @Override
    public Supplier<String> generateSupplier(Random random) {
        return () -> IntStream.range(0, length)
                .sequential()
                .mapToObj((i) -> super.generateSupplier(random))
                .map(Supplier::get)
                .collect(Collectors.joining());
    }
}
