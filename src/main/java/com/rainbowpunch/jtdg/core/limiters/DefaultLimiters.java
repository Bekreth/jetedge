package com.rainbowpunch.jtdg.core.limiters;

import com.rainbowpunch.jtdg.core.limiters.primitive.*;
import com.rainbowpunch.jtdg.spi.PojoGenerator;
import com.rainbowpunch.jtdg.core.FieldSetter;
import com.rainbowpunch.jtdg.core.PojoAttributes;
import com.rainbowpunch.jtdg.core.limiters.collections.ListLimiter;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Maps classes to limiters
 */
public enum DefaultLimiters {
    SHORT(new ShortLimiter(), short.class, Short.class),
    INT(new IntegerLimiter(), int.class, Integer.class),
    BOOLEAN(new BooleanLimiter(), boolean.class, Boolean.class),
    FLOAT(new FloatLimiter(), float.class, Float.class),
    DOUBLE(new DoubleLimiter(), double.class, Double.class),
    LONG(new LongLimiter(), long.class, Long.class),
    CHAR(new CharacterLimiter(), char.class, Character.class),
    STRING(new StringLimiter(), String.class),
    ENUM(EnumLimiter::createEnumLimiter, Enum.class, 0),
    LIST(ListLimiter::createListLimiter, List.class);

    private List<Class<?>> clazz;
    private Limiter<?> limiter;
    private Function<Limiter<?>, Limiter<?>> listLimiterFunction;
    private Function<Class<?>, ObjectLimiter<?>> enumLimiterFunction;

    DefaultLimiters(Limiter<?> limiter, Class<?>... clazz) {
        this.limiter = limiter;
        this.clazz = Arrays.asList(clazz);
    }

    DefaultLimiters(Function<Class<?>, ObjectLimiter<?>> enumLimiterFunction, Class<?> clazz, int a) {
        this.enumLimiterFunction = enumLimiterFunction;
        this.clazz = Arrays.asList(clazz);
    }


    DefaultLimiters(Function<Limiter<?>, Limiter<?>> listLimiterFunction, Class<?> clazz) {
        this.clazz = Arrays.asList(clazz);
        this.listLimiterFunction = listLimiterFunction;
    }

    private static DefaultLimiters defaultLimiters(Class clazz) {
        DefaultLimiters defaultLimiters = null;
        for (DefaultLimiters simpleLimiters : DefaultLimiters.values()) {
            if (simpleLimiters.clazz.contains(clazz)) {
                defaultLimiters = simpleLimiters;
                break;
            }
        }
        if (defaultLimiters == null && Enum.class.isAssignableFrom(clazz)) {
            defaultLimiters = ENUM;
        }
        return defaultLimiters;
    }

    public static Limiter<?> getSimpleLimiter(Class clazz, FieldSetter fieldSetter, PojoAttributes attributes) {
        DefaultLimiters defaultLimiters = defaultLimiters(clazz);
        Limiter limiter = null;
        if (defaultLimiters != null) {
            if (defaultLimiters.listLimiterFunction != null) {
                Class genClass = (Class) fieldSetter.getGenericFields().get(0);
                DefaultLimiters genDefaultLimiter = defaultLimiters(genClass);
                Limiter<?> intermediateLimiter = null;
                if (genDefaultLimiter == null) {
                    intermediateLimiter = new DefaultPojoLimiter<>(genClass, attributes);
                } else if (genDefaultLimiter.limiter != null) {
                    intermediateLimiter = genDefaultLimiter.limiter;
                } else if (genDefaultLimiter.enumLimiterFunction != null) {
                    intermediateLimiter = genDefaultLimiter.enumLimiterFunction.apply(genClass);
                }
                limiter = defaultLimiters.listLimiterFunction.apply(intermediateLimiter);
            } else if (defaultLimiters.enumLimiterFunction != null) {
                limiter = defaultLimiters.enumLimiterFunction.apply(fieldSetter.getClazz());
            } else {
                limiter = defaultLimiters.limiter;
            }
        }
        if (limiter == null) {
            limiter = new DefaultPojoLimiter(clazz, attributes);
        }
        return limiter;
    }


    private static class DefaultPojoLimiter<T extends Object> implements Limiter<T> {

        private PojoGenerator<T> generator;

        public DefaultPojoLimiter(Class<T> clazz, PojoAttributes<T> parentAttributes) {
            this.generator = new PojoGenerator<>(clazz, parentAttributes.getRandomSeed());
            parentAttributes.getLimiters()
                    .entrySet()
                    .stream()
                    .filter(entry -> !entry.getKey().equals(parentAttributes.getPojoClazz()))
                    .flatMap(this::flattenLimiterMap)
                    .forEach(entry -> {
                        generator.andLimitField(entry.getKey(), entry.getValue());
                    });
            generator.analyzePojo();
        }

        private Stream<Map.Entry<String, Limiter<?>>> flattenLimiterMap(Map.Entry<Class, Map<String, Limiter<?>>> entry) {
            return entry.getValue()
                    .entrySet()
                    .stream();
        }

        @Override
        public Supplier<T> generateSupplier(Random random) {
            return () -> {
                return generator.generatePojo();
            };
        }
    }
}
