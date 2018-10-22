package com.rainbowpunch.jetedge.core.limiters.maps;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.exception.PojoConstructionException;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.RequiresDefaultLimiter;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import static com.rainbowpunch.jetedge.core.limiters.maps.MapLimiter.ConflictResolutionStrategy.DROP_ENTRY;

/**
 * A Limiter for a Map.  By default it will try to populate your map with between 3 and 7 values.
 *      If there is a conflict with keys, it will use the DROP_VALUE strategy by default.
 * @param <T>
 * @param <U>
 */
public class MapLimiter<T, U> extends SimpleAbstractLimiter<Map<T, U>>
        implements RequiresDefaultLimiter<MapLimiter<T, U>> {

    private static final int DEFAULT_RANGE = 2;
    private static final int DEFAULT_OFFSET = 5;

    private final int range;
    private final int offset;
    private Limiter<T> keyLimiter;
    private Limiter<U> valueLimiter;
    private final ConflictResolver resolver;

    /**
     * Get a MapLimiterBuilder to aide in constructing a MapLimiter.
     */
    public static <T, U> MapLimiterBuilder<T, U> builder() {
        return new MapLimiterBuilder<>();
    }

    /**
     * A Constructor to configure the number of elements the limiter should attempt to generate
     * @param range Range of count possibilities
     * @param offset Offset from 0
     */
    public MapLimiter(int range, int offset) {
        this(null, null, range, offset, DROP_ENTRY);
    }

    /**
     * A constructor to configure what limiters should be provided to the Key and Value sets.
     *      Leave an entry null to have it be automatically populated by Jetedge during analysis.
     * @param keyLimiter Limiter to generate keys
     * @param valueLimiter Limiter to generate values
     */
    public MapLimiter(Limiter<T> keyLimiter, Limiter<U> valueLimiter) {
        this(keyLimiter, valueLimiter, DEFAULT_RANGE, DEFAULT_OFFSET, DROP_ENTRY);
    }

    /**
     * A constructor to configure all properties of the MapLimiter.  Provide null values to the Key-Value limiters to
     *      have them be automatically populated by Jetedge during analysis.  Additionally, users can provide their own
     *      resolution strategy that should be used should there be a key collision, or use one the predefined ones in
     *      MapLimiter.ConflictResolutionStrategy.
     * @param keyLimiter Limiter to generate keys
     * @param valueLimiter Limiter to generate values
     * @param range Range of count possibilities
     * @param offset Offset from 0
     * @param resolver The strategy to be used to resolve key collisions.
     */
    public MapLimiter(Limiter<T> keyLimiter, Limiter<U> valueLimiter, int range, int offset,
                      ConflictResolver resolver) {
        this.keyLimiter = keyLimiter;
        this.valueLimiter = valueLimiter;
        this.range = range;
        this.offset = offset;
        this.resolver = resolver;
        validate();
    }

    private void validate() {
        if (resolver == null) {
            throw new LimiterConstructionException("Error creating MapLimiter : Resolver cannot be null");
        } else if (range < 0 || offset < 0) {
            throw new LimiterConstructionException("Error creating MapLimiter : "
                    + "Offset and Range cannot be less than 0");
        }
    }

    @Override
    public boolean hasLimiter() {
        return keyLimiter != null
                && valueLimiter != null;
    }

    @Override
    public MapLimiter<T, U> reconcile(MapLimiter<T, U> baseLimiter) {
        Limiter<T> key = keyLimiter == null ? baseLimiter.keyLimiter : keyLimiter;
        Limiter<U> values = valueLimiter == null ? baseLimiter.valueLimiter : valueLimiter;
        return new MapLimiter<>(key, values, range, offset, resolver);
    }

    @Override
    public Supplier<Map<T, U>> generateSupplier(Random random) {
        return () -> {
            try {
                int count = range == 0 ? offset : random.nextInt(range) + offset;

                Supplier<T> keySupplier = keyLimiter.generateSupplier(random);
                Supplier<U> valueSupplier = valueLimiter.generateSupplier(random);

                return IntStream.range(0, count)
                        .mapToObj(i -> {
                            T key = keySupplier.get();
                            U value = valueSupplier.get();
                            return new AbstractMap.SimpleEntry<>(key, value);
                        })
                        .collect(Collector.of(HashMap::new,
                                this.addElementToMap(keySupplier, valueSupplier),
                                this.compressMaps(keySupplier, valueSupplier)));

            } catch (Exception e) {
                throw new PojoConstructionException("Failed to create objects for MapLimiter: ", e);
            }
        };
    }

    private BiConsumer<Map<T, U>, Map.Entry<T, U>> addElementToMap(Supplier<T> keySupplier, Supplier<U> valueSupplier) {
        return (collectingMap, entry) -> {
            T key = entry.getKey();
            U value = entry.getValue();

            if (collectingMap.containsKey(key)) {
                resolver.applyRules(collectingMap, entry, keySupplier, valueSupplier);
            } else {
                collectingMap.put(key, value);
            }
        };
    }

    private BinaryOperator<Map<T, U>> compressMaps(Supplier<T> keySupplier, Supplier<U> valueSupplier) {
        return (map1, map2) -> {
            BiConsumer<Map<T, U>, Map.Entry<T, U>> biConsumer = addElementToMap(keySupplier, valueSupplier);
            map2.entrySet().forEach((entry) -> biConsumer.accept(map1, entry));
            return map1;
        };
    }


    /**
     * A generic interface that will set rules on how to handle different forms of collision in Maps during generation.
     */
    public interface ConflictResolver {
        void applyRules(Map consumingMap, Map.Entry entry, Supplier keySupplier, Supplier valueSupplier);
    }

    /**
     * A set of default ConflictResolutionStrategies.
     */
    @SuppressWarnings("unchecked")
    public enum ConflictResolutionStrategy implements ConflictResolver {
        DROP_ENTRY {
            @Override
            public void applyRules(Map consumingMap, Map.Entry entry, Supplier keySupplier, Supplier valueSupplier) {
                //NOOP
            }
        },
        OVERWRITE_ENTRY {
            @Override
            public void applyRules(Map consumingMap, Map.Entry entry, Supplier keySupplier, Supplier valueSupplier) {
                consumingMap.put(entry.getKey(), entry.getValue());
            }
        },
        REMAKE_KEY {
            @Override
            public void applyRules(Map consumingMap, Map.Entry entry, Supplier keySupplier, Supplier valueSupplier) {
                Object newKey = keySupplier.get();
                if (consumingMap.containsKey(newKey)) {
                    REMAKE_KEY.applyRules(consumingMap, entry, keySupplier, valueSupplier);
                } else {
                    consumingMap.put(newKey, entry.getValue());
                }
            }
        },
        REMAKE_ENTRY {
            @Override
            public void applyRules(Map consumingMap, Map.Entry entry, Supplier keySupplier, Supplier valueSupplier) {
                Object newKey = keySupplier.get();
                if (consumingMap.containsKey(newKey)) {
                    REMAKE_ENTRY.applyRules(consumingMap, entry, keySupplier, valueSupplier);
                } else {
                    consumingMap.put(newKey, valueSupplier.get());
                }
            }
        },
        THROW_EXCEPTION {
            @Override
            public void applyRules(Map consumingMap, Map.Entry entry, Supplier keySupplier, Supplier valueSupplier) {
                throw new PojoConstructionException("Key Collision while tying to populate Map.  "
                        + "Consider widening the provided KeyLimiter");
            }
        }
    }

    /**
     * Builder to be used creatinga MapLimiter.
     */
    private static final class MapLimiterBuilder<T, U> {
        private int range;
        private int offset;
        private Limiter<T> keyLimiter;
        private Limiter<U> valueLimiter;
        private ConflictResolutionStrategy strategy;

        private MapLimiterBuilder() {

        }

        public MapLimiterBuilder<T, U> range(int range) {
            this.range = range;
            return this;
        }

        public MapLimiterBuilder<T, U> offset(int offset) {
            this.offset = offset;
            return this;
        }

        public MapLimiterBuilder<T, U> valueLimiter(Limiter<T> keyLimiter) {
            this.keyLimiter = keyLimiter;
            return this;
        }

        public MapLimiterBuilder<T, U> keyLimiter(Limiter<U> valueLimiter) {
            this.valueLimiter = valueLimiter;
            return this;
        }

        public MapLimiterBuilder<T, U> strategy(ConflictResolutionStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public MapLimiter<T, U> build() {
            return new MapLimiter<T, U>(keyLimiter, valueLimiter, range, offset, strategy);
        }

    }

}
