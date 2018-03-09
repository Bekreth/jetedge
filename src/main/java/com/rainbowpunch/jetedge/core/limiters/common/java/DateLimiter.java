package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Date;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Date data fields.
 * DateLimiter will take in either one or two epochmilisecond in long
 * Default origin is 0L (January 1, 1970 12:00:00 AM)
 * Default bound is 4102444800000L (January 1, 2100 12:00:00 AM)
 */
public class DateLimiter implements Limiter<Date> {

    private final static Long DEFAULT_ORIGIN = 0L;
    private final static Long DEFAULT_BOUND = 4102444800000L;
    private final Long origin;
    private final Long bound;

    /**
     * This constructor will generate random date between January 1, 1970 12:00:00 AM(inclusive) and January 1, 2100 12:00:00 AM(exclusive)
     */
    public DateLimiter() {
        this(DEFAULT_ORIGIN, DEFAULT_BOUND);
    }

    /**
     * This constructor will generate random date between January 1, 1970 12:00:00 AM(inclusive) and boundDate(exclusive)
     *
     * @param boundDate exclusive right bound
     */
    public DateLimiter(Date boundDate) {
        this(DEFAULT_ORIGIN, boundDate.getTime());
    }

    /**
     * This constructor will generate random date between originDate(inclusive) and boundDate(exclusive)
     *
     * @param originDate inclusive left bound
     * @param boundDate exclusive right bound
     */
    public DateLimiter(Date originDate, Date boundDate) {
        this(originDate.getTime(), boundDate.getTime());
    }

    /**
     * This constructor will create new Date with boundLong as epochmilisecond and generate random date between January 1, 1970 12:00:00 AM
     * (inclusive) and new Date(boundLong)(exclusive)
     *
     * @param boundLong exclusive right bound
     */
    public DateLimiter(Long boundLong) {
        this(DEFAULT_ORIGIN, boundLong);
    }

    /**
     * This constructor will create new Dates with originLong/boundLong as epochmilisecond and generate random date between new Date(originLong)
     * (inclusive) and new Date(boundLong)(exclusive)
     *
     * @param originLong inclusive left bound
     * @param boundLong exclusive right bound
     */
    public DateLimiter(Long originLong, Long boundLong) {
        if (boundLong < originLong || originLong < 0 || boundLong < 0) {
            throw new LimiterConstructionException("Origin can't be greater than the bound!");
        }
        this.origin = originLong;
        this.bound = boundLong;
    }

    @Override
    public Supplier<Date> generateSupplier(Random random) {
        return () -> new Date(random.longs(origin, bound).findFirst().getAsLong());
    }
}
