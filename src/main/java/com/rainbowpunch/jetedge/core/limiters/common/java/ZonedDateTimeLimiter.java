package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class ZonedDateTimeLimiter extends SimpleAbstractLimiter<ZonedDateTime> {

    private static final Long DEFAULT_ORIGIN = 0L;
    private static final Long DEFAULT_BOUND = 4102444800000L;
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC");
    private static final Set<String> AVAILABLE_ZONE_IDS = ZoneId.getAvailableZoneIds();
    private Random rand = new Random();
    private Long origin;
    private Long bound;
    private Collection<String> outputZoneIds;

    public ZonedDateTimeLimiter() {
        this(DEFAULT_ORIGIN, DEFAULT_BOUND, DEFAULT_ZONE_ID);
    }

    public ZonedDateTimeLimiter(Long bound) {
        this(DEFAULT_ORIGIN, bound, DEFAULT_ZONE_ID);
    }

    public ZonedDateTimeLimiter(Long bound, boolean randomizeOutputZoneId) {
        this(DEFAULT_ORIGIN, bound, randomizeOutputZoneId);
    }

    public ZonedDateTimeLimiter(Long bound, Collection<String> outputZoneIds) {
        this(DEFAULT_ORIGIN, bound, outputZoneIds);
    }

    public ZonedDateTimeLimiter(Long bound, ZoneId outputZoneId) {
        this(DEFAULT_ORIGIN, bound, outputZoneId);
    }

    public ZonedDateTimeLimiter(Long origin, Long bound) {
        this(origin, bound, DEFAULT_ZONE_ID);
    }

    public ZonedDateTimeLimiter(Long origin, Long bound, boolean randomizeOutputZoneId) {
        validateAndSetRange(origin, bound);
        if (randomizeOutputZoneId) {
            this.outputZoneIds = AVAILABLE_ZONE_IDS;
        } else {
            this.outputZoneIds = new HashSet<>(Arrays.asList(DEFAULT_ZONE_ID.getId()));
        }
    }

    public ZonedDateTimeLimiter(Long origin, Long bound, Collection<String> outputZoneIds) {
        validateAndSetRange(origin, bound);
        this.outputZoneIds = outputZoneIds;
    }

    public ZonedDateTimeLimiter(Long origin, Long bound, ZoneId outputZoneId) {
        validateAndSetRange(origin, bound);
        this.outputZoneIds = new HashSet<>(Arrays.asList(outputZoneId.getId()));
    }

    @Override
    public Supplier<ZonedDateTime> generateSupplier(Random random) {
        Long randomLong = random.longs(origin, bound).findFirst().getAsLong();
        Instant instantOfEpochMilli = Instant.ofEpochMilli(randomLong);
        return () -> ZonedDateTime.ofInstant(instantOfEpochMilli, getRandomZoneId(outputZoneIds));
    }

    private void validateAndSetRange(Long origin, Long bound) {
        if (bound < origin || origin < 0 || bound < 0) {
            throw new LimiterConstructionException("Origin can't be greater than the bound!");
        }
        this.origin = origin;
        this.bound = bound;
    }

    private ZoneId getRandomZoneId(Collection<String> outputZoneIds) {
        int index = rand.nextInt(outputZoneIds.size());
        Iterator<String> iter = outputZoneIds.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return ZoneId.of(iter.next());
    }
}
