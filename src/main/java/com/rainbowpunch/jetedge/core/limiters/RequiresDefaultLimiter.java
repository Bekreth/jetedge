package com.rainbowpunch.jetedge.core.limiters;

/**
 * This interface provides information on whether a given Limiter requires population by the DefaultLimiters class.  This is predominantly used
 *      for collections where the user can specify what should be used to fill the collection, or allow Jetedge to determine population itself.
 * @param <T>
 *          The Limiter that type this interface is implemented by.
 */
public interface RequiresDefaultLimiter<T extends Limiter<?>> {

    boolean hasLimiter();

    T reconcile(T baseLimiter);
}
