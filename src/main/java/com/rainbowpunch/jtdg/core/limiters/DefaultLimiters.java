package com.rainbowpunch.jtdg.core.limiters;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.rainbowpunch.jtdg.core.PojoAttributes;
import com.rainbowpunch.jtdg.core.limiters.collections.ListLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.BooleanLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.CharacterLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.DoubleLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.FloatLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.IntegerLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.LongLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.ShortLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.StringLimiter;
import com.rainbowpunch.jtdg.core.reflection.ClassAttributes;

public class DefaultLimiters {
    private static final Limiter<Integer> INTEGER_LIMITER = new IntegerLimiter();
    private static final Limiter<Short> SHORT_LIMITER = new ShortLimiter();
    private static final Limiter<Long> LONG_LIMITER = new LongLimiter();
    private static final Limiter<Boolean> BOOLEAN_LIMITER = new BooleanLimiter();
    private static final Limiter<Float> FLOAT_LIMITER = new FloatLimiter();
    private static final Limiter<Double> DOUBLE_LIMITER = new DoubleLimiter();
    private static final Limiter<Character> CHARACTER_LIMITER = new CharacterLimiter();
    private static final Limiter<String> STRING_LIMITER = new StringLimiter();

    private static final Map<Class<?>, Function<Class<?>, Limiter<?>>> limiterFactoryMap =
            Collections.unmodifiableMap(new HashMap<Class<?>, Function<Class<?>, Limiter<?>>>() {{
                put(Integer.class, clazz -> INTEGER_LIMITER);
                put(int.class, clazz -> INTEGER_LIMITER);
                put(Short.class, clazz -> SHORT_LIMITER);
                put(short.class, clazz -> SHORT_LIMITER);
                put(Long.class, clazz -> LONG_LIMITER);
                put(long.class, clazz -> LONG_LIMITER);
                put(Boolean.class, clazz -> BOOLEAN_LIMITER);
                put(boolean.class, clazz -> BOOLEAN_LIMITER);
                put(Float.class, clazz -> FLOAT_LIMITER);
                put(float.class, clazz -> FLOAT_LIMITER);
                put(Double.class, clazz -> DOUBLE_LIMITER);
                put(double.class, clazz -> DOUBLE_LIMITER);
                put(Character.class, clazz -> CHARACTER_LIMITER);
                put(char.class, clazz -> CHARACTER_LIMITER);
                put(String.class, clazz -> STRING_LIMITER);
            }});

    public static Limiter<?> getDefaultLimiter(
            ClassAttributes classAttributes,
            PojoAttributes pojoAttributes
    ) {
        final Function<Class<?>, Limiter<?>> limiterFactory =
                limiterFactoryMap.get(classAttributes.getClazz());
        if (limiterFactory != null) {
            return limiterFactory.apply(classAttributes.getClazz());
        }
        if (classAttributes.isEnum()) {
            return EnumLimiter.createEnumLimiter(classAttributes.getClazz());
        }
        if (classAttributes.isSubclassOf(List.class)) {
            return ListLimiter.createListLimiter(
                    getDefaultLimiter(
                            classAttributes.getElementType()
                                    .orElseThrow(RuntimeException::new), pojoAttributes));
        }
        return new DefaultPojoLimiter(classAttributes.getClazz(), pojoAttributes);
    }
}
