package com.rainbowpunch.jetedge.core;

import com.rainbowpunch.jetedge.core.limiters.DefaultLimiters;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.RequiresDefaultLimiter;

import java.util.Map;
import java.util.Random;

/**
 *
 */
public class FieldDataGenerator<T> {

    private final Random random;

    public FieldDataGenerator(int generatorSeed) {
        random = new Random(generatorSeed);
    }

    public void populateSuppliers(PojoAttributes<T> attributes) {
        Map<Class, Map<String, Limiter<?>>> limiters = attributes.getLimiters();
        Map<String, Limiter<?>> limiterOfCurrentObjects = limiters.get(attributes.getPojoClazz());

        attributes.fieldSetterStream()
                .filter(entry -> shouldCreateSupplier(limiterOfCurrentObjects.get(entry.getKey())))
                .sorted(this::alphabetical)
                .forEach(entry -> {
                    String entryName = entry.getKey().toLowerCase();
                    Class clazz = entry.getValue().getClassAttributes().getClazz();

                    Limiter limiter = attributes.getAllFieldLimiterMap().get(clazz);
                    limiter = limiterOfCurrentObjects.getOrDefault(entryName, limiter);

                    if (limiter == null || requiresPopulation(limiter)) {
                        Limiter defaultLimiter = DefaultLimiters.getDefaultLimiter(entry.getValue().getClassAttributes(), attributes);

                        if (limiter == null) {
                            limiter = defaultLimiter;
                        } else {
                            limiter = ((RequiresDefaultLimiter) limiter).reconcile(defaultLimiter);
                        }
                    }
                    updateFieldSetterWithSupplier(entry.getValue(), limiter); // TODO: 7/29/17 get or default
                });
    }

    private boolean shouldCreateSupplier(Limiter limiter) {
        // TODO: 7/30/17
        return true;
    }

    private int alphabetical(Map.Entry<String, FieldSetter<T, ?>> entry1, Map.Entry<String, FieldSetter<T, ?>> entry2) {
        return String.CASE_INSENSITIVE_ORDER.compare(entry1.getKey(), entry2.getKey());
    }

    private void updateFieldSetterWithSupplier(FieldSetter<T, ?> fieldSetter, Limiter limiter) {
        fieldSetter.setSupplier(limiter.generateSupplier(random));
    }

    private boolean requiresPopulation(Limiter limiter) {
        if (RequiresDefaultLimiter.class.isAssignableFrom(limiter.getClass())) {
            RequiresDefaultLimiter innerLimiter = (RequiresDefaultLimiter) limiter;
            return !innerLimiter.hasLimiter();
        } else {
            return false;
        }
    }
}
