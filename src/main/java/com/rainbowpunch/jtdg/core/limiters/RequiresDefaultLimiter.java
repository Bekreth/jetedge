package com.rainbowpunch.jtdg.core.limiters;

public interface RequiresDefaultLimiter<T extends Limiter<?>> {

    boolean hasLimiter();

    T reconcile(T baseLimiter);
}
