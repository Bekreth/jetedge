package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.core.limiters.DefaultLimiters;
import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.Map;
import java.util.Random;

/**
 *
 */
public class DataGenerator<T> {

    private Random random;
    private int generatorSeed;

    public DataGenerator() {
        random = new Random();
    }

    public DataGenerator(int generatorSeed) {
        this.generatorSeed = generatorSeed;
        random = new Random(generatorSeed);
    }

    public void setGeneratorSeed(Integer seed) {
        generatorSeed = seed;
    }

    public void populateSuppliers(PojoAttributes<T> attributes) {
        Map<Class, Map<String, Limiter<?>>> limiters = attributes.getLimiters();
        Map<String, Limiter<?>> limiterOfCurrentObjects = limiters.get(attributes.getPojoClazz());

        attributes.fieldSetterStream()
                .filter(entry -> shouldCreateSupplier(limiterOfCurrentObjects.get(entry.getKey())))
                .sorted(this::alphabetical)
                .forEach(entry -> {
                    String entryName = entry.getKey().substring(3);
                    entryName = entryName.substring(0, 1).toLowerCase() + entryName.substring(1);

                    Limiter defaultLimiter = DefaultLimiters.getSimpleLimiter(entry.getValue().getClazz(),
                            entry.getValue(), attributes);

                    Limiter limiter = limiterOfCurrentObjects.getOrDefault(entryName, defaultLimiter);

                    updateFieldSetterWithSupplier(entry.getValue(), limiter); // TODO: 7/29/17 get or default
                });
    }

    private boolean shouldCreateSupplier(Limiter limiter) {
        boolean shouldCreateSupplier = true;
        // TODO: 7/30/17
        return shouldCreateSupplier;
    }

    private int alphabetical(Map.Entry<String, FieldSetter<T, ?>> entry1, Map.Entry<String, FieldSetter<T, ?>> entry2) {
        return String.CASE_INSENSITIVE_ORDER.compare(entry1.getKey(), entry2.getKey());
    }

    private void updateFieldSetterWithSupplier(FieldSetter<T, ?> fieldSetter, Limiter limiter) {
        fieldSetter.setSupplier(limiter.generateSupplier(random));
    }
}
