package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.ObjectLimiter;
import com.rainbowpunch.jtdg.util.ReadableCharList;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 *
 */
public class CharacterLimiter extends ObjectLimiter<Character> {

    @Override
    protected List<Character> configureObjectList() {
        return ReadableCharList.LIST_OF_ALL_CHAR;
    }

    public CharacterLimiter() {

    }

    public CharacterLimiter(List<Character> charList) {
        this.updateObjectList(charList);
    }

    @Override
    public Supplier<Character> generateSupplier(Random random) {
        return () -> super.generateSupplier(random).get();
    }
}
