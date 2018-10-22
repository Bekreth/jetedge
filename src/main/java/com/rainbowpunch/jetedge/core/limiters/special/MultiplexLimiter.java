package com.rainbowpunch.jetedge.core.limiters.special;

import com.rainbowpunch.jetedge.core.exception.ValueGenerationException;
import com.rainbowpunch.jetedge.core.limiters.Limiter;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This Limiter takes several limiters as input and returns a mixed set of values from them.
 *      This allows for a limiter with "holes". e.g. an Integer between 0-10 or 20-30.
 * @param <T>
 */
public class MultiplexLimiter<U extends Limiter<T>, T> extends SimpleAbstractLimiter<T> {

    private static final float HUNDRED = 100f;

    private final List<Function<Integer, Limiter<T>>> limiterPicker;

    public MultiplexLimiter(Collection<LimiterDistribution> collectionOfLimiterDistribution) {
        limiterPicker = new ArrayList<>();
        float pickChance = 0;
        Iterator<LimiterDistribution> iterator = collectionOfLimiterDistribution.iterator();
        while (iterator.hasNext()) {
            LimiterDistribution distribution = iterator.next();
            float lowBound = pickChance;
            //This makes sure any rounding errors are taken care of.
            float highBound = iterator.hasNext() ? pickChance + distribution.getDistribution() : HUNDRED;
            pickChance += distribution.getDistribution();
            limiterPicker.add((intObj) -> {
                boolean shouldPick = intObj >= lowBound && intObj < highBound;
                return shouldPick ? distribution.getLimiter() : null;
            });
        }
    }

    public static <U extends Limiter<T>, T> MultiplexLimiter<U, T> generateFlatDistribution(U... limiters) {
        return generateFlatDistribution(Arrays.asList(limiters));
    }

    public static <U extends Limiter<T>, T> MultiplexLimiter<U, T> generateFlatDistribution(List<U> limiters) {
        float distribution = HUNDRED / limiters.size();
        List<LimiterDistribution> limiterDistributions = limiters.stream()
                .map(limiter -> new LimiterDistribution(limiter, distribution))
                .collect(Collectors.toList());
        return new MultiplexLimiter<>(limiterDistributions);
    }


    @Override
    public Supplier<T> generateSupplier(Random random) {
        return () -> {
            int randomNumber = random.nextInt((int) HUNDRED);
            Optional<Limiter<T>> limiter = limiterPicker.stream()
                    .map(func -> func.apply(randomNumber))
                    .filter((obj) -> obj != null)
                    .findAny();
            if (!limiter.isPresent()) {
                throw new ValueGenerationException("An error was encountered while trying to multiplex.");
            }
            return limiter.get().generateSupplier(random).get();
        };
    }


    public static class LimiterDistribution {
        private Limiter limiter;
        private float distribution;

        public LimiterDistribution(Limiter limiter, float distribution) {
            this.limiter = limiter;
            this.distribution = distribution;
        }

        public Limiter getLimiter() {
            return limiter;
        }

        public float getDistribution() {
            return distribution;
        }
    }

}
