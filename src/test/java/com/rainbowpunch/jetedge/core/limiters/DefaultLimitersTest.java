package com.rainbowpunch.jetedge.core.limiters;

import com.rainbowpunch.jetedge.core.PojoAttributes;
import com.rainbowpunch.jetedge.core.limiters.common.EnumLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.BooleanLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.ByteLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.CharacterLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.DoubleLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.FloatLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.IntegerLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.LongLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.ShortLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.StringLimiter;
import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

public class DefaultLimitersTest {

    private PojoAttributes pojoAttributes = Mockito.mock(PojoAttributes.class);

    @Before
    public void init() {
        Mockito.reset(pojoAttributes);
    }

    @Test
    public void testIntegerLimiter() {
        ClassAttributes attributes = ClassAttributes.create(Integer.class);
        Limiter limiter = DefaultLimiters.getDefaultLimiter(attributes, pojoAttributes, "");
        assertTrue(limiter instanceof IntegerLimiter);
    }

    @Test
    public void testShortLimiter() {
        ClassAttributes attributes = ClassAttributes.create(Short.class);
        Limiter limiter = DefaultLimiters.getDefaultLimiter(attributes, pojoAttributes, "");
        assertTrue(limiter instanceof ShortLimiter);
    }

    @Test
    public void testLongLimiter() {
        ClassAttributes attributes = ClassAttributes.create(Long.class);
        Limiter limiter = DefaultLimiters.getDefaultLimiter(attributes, pojoAttributes, "");
        assertTrue(limiter instanceof LongLimiter);
    }

    @Test
    public void testBooleanLimiter() {
        ClassAttributes attributes = ClassAttributes.create(Boolean.class);
        Limiter limiter = DefaultLimiters.getDefaultLimiter(attributes, pojoAttributes, "");
        assertTrue(limiter instanceof BooleanLimiter);
    }

    @Test
    public void testFloatLimiter() {
        ClassAttributes attributes = ClassAttributes.create(Float.class);
        Limiter limiter = DefaultLimiters.getDefaultLimiter(attributes, pojoAttributes, "");
        assertTrue(limiter instanceof FloatLimiter);
    }

    @Test
    public void testDoubleLimiter() {
        ClassAttributes attributes = ClassAttributes.create(Double.class);
        Limiter limiter = DefaultLimiters.getDefaultLimiter(attributes, pojoAttributes, "");
        assertTrue(limiter instanceof DoubleLimiter);
    }

    @Test
    public void testCharacterLimiter() {
        ClassAttributes attributes = ClassAttributes.create(Character.class);
        Limiter limiter = DefaultLimiters.getDefaultLimiter(attributes, pojoAttributes, "");
        assertTrue(limiter instanceof CharacterLimiter);
    }

    @Test
    public void testStringLimiter() {
        ClassAttributes attributes = ClassAttributes.create(String.class);
        Limiter limiter = DefaultLimiters.getDefaultLimiter(attributes, pojoAttributes, "");
        assertTrue(limiter instanceof StringLimiter);
    }

    @Test
    public void testByteLimiter() {
        ClassAttributes attributes = ClassAttributes.create(Byte.class);
        Limiter limiter = DefaultLimiters.getDefaultLimiter(attributes, pojoAttributes, "");
        assertTrue(limiter instanceof ByteLimiter);
    }

    @Test
    public void testEnumLimiter() {
        ClassAttributes attributes = ClassAttributes.create(InnerTestEnum.class);
        Limiter limiter = DefaultLimiters.getDefaultLimiter(attributes, pojoAttributes, "");
        assertTrue(limiter instanceof EnumLimiter);
    }

    private enum InnerTestEnum {
        ;
    }

}