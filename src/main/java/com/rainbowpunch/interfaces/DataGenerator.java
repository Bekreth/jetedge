package com.rainbowpunch.interfaces;

import com.rainbowpunch.interfaces.limiters.Limiter;
import com.rainbowpunch.interfaces.limiters.DefaultLimiters;

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
        Map<String, Limiter<?>> limiters = attributes.getLimiters();

        attributes.fieldSetterStream()
                .filter(entry -> shouldCreateSupplier(limiters.get(entry.getKey())))
                .sorted(this::alphabetical)
                .forEach(entry -> {
                    String entryName = entry.getKey().substring(3).toLowerCase();
                    updateFieldSetterWithSupplier(entry.getValue(),
                            limiters.getOrDefault(entryName,
                                    DefaultLimiters.getSimpleLimiter(entry.getValue().getClazz()))); // TODO: 7/29/17 get or default
                    System.out.println(limiters.get(entryName));
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
