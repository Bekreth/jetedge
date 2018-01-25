package com.rainbowpunch.jetedge.spi;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * This Class allows for users to create a list of Limiters that should be used by default.
 */
public class DefaultDataLimiter {
    private Collection<Limiter> defaultLimiters;

    public DefaultDataLimiter() {
        this.defaultLimiters = new ArrayList<>();
    }

    public DefaultDataLimiter(Collection<Limiter> limiters) {
        this.defaultLimiters = limiters;
    }

    public DefaultDataLimiter(Limiter... limiters) {
        this.defaultLimiters = Arrays.asList(limiters);
    }

    public void addDefaultLimiter(Limiter limiter) {
        this.defaultLimiters.add(limiter);
    }

    public void addDefaultLimiters(Collection<Limiter> limiters) {
        this.defaultLimiters.addAll(limiters);
    }

    public void addDefaultLimiter(Limiter... limiters) {
        this.defaultLimiters.addAll(Arrays.asList(limiters));
    }

    public Collection<Limiter> getDefaultLimiters() {
        return defaultLimiters;
    }
}
