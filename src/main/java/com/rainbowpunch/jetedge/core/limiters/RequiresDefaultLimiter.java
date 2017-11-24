package com.rainbowpunch.jetedge.core.limiters;

public interface RequiresDefaultLimiter<T extends Limiter<?>> {

    boolean hasLimiter();

    T reconcile(T baseLimiter);
}
