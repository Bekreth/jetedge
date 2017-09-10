package com.rainbowpunch.jtdg.core.limiters;

import com.rainbowpunch.jtdg.spi.PojoGenerator;
import com.rainbowpunch.jtdg.core.FieldSetter;
import com.rainbowpunch.jtdg.core.PojoAttributes;
import com.rainbowpunch.jtdg.core.limiters.collections.ListLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.IntegerLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.ShortLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.StringLimiter;

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
    //FLOAT(null),
    //DOUBLE(null),
    //LONG(null),
    //CHAR(null),
    STRING(new StringLimiter(), String.class),
    LIST(ListLimiter::createListLimiter, List.class);

    private List<Class<?>> clazz;
    private Limiter<?> limiter;
    private Function<Limiter<?>, Limiter<?>> function;

    DefaultLimiters(Limiter<?> limiter, Class<?>... clazz) {
        this.limiter = limiter;
        this.clazz = Arrays.asList(clazz);
    }

    DefaultLimiters(Function<Limiter<?>, Limiter<?>> function, Class<?> clazz) {
        this.clazz = Arrays.asList(clazz);
        this.function = function;
    }

    private static DefaultLimiters defaultLimiters(Class clazz) {
        DefaultLimiters defaultLimiters = null;
        for (DefaultLimiters simpleLimiters : DefaultLimiters.values()) {
            if (simpleLimiters.clazz.contains(clazz)) {
                defaultLimiters = simpleLimiters;
                break;
            }
        }
        return defaultLimiters;
    }

    public static Limiter<?> getSimpleLimiter(Class clazz, FieldSetter fieldSetter, PojoAttributes attributes) {
        DefaultLimiters defaultLimiters = defaultLimiters(clazz);
        Limiter limiter = null;
        if (defaultLimiters != null) {
            if (defaultLimiters.function == null) {
                limiter = defaultLimiters.limiter;
            } else {
                Class genClass = (Class) fieldSetter.getGenericFields().get(0);
                DefaultLimiters genDefaultLimiter = defaultLimiters(genClass);
                Limiter<?> intermediateLimiter = genDefaultLimiter == null
                        ? new DefaultPojoLimiter<>(genClass, attributes) : genDefaultLimiter.limiter;
                limiter = defaultLimiters.function.apply(intermediateLimiter);
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
            this.generator = new PojoGenerator<>(clazz);
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
