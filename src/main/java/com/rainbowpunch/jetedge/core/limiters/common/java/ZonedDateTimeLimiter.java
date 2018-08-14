package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A limiter for ZonedDateTime fields.
 * ZonedDateTimeLimiter will take in origin or bound or both in either Long or Date but not in mixed combination
 * Default origin is 0L (January 1, 1970 12:00:00 AM)
 * Default bound is 4102444800000L (January 1, 2100 12:00:00 AM)
 */
public class ZonedDateTimeLimiter extends SimpleAbstractLimiter<ZonedDateTime> {

    private static final Long DEFAULT_ORIGIN = 0L;
    private static final Long DEFAULT_BOUND = 4102444800000L;
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC");
    private static final Set<String> AVAILABLE_ZONE_IDS = ZoneId.getAvailableZoneIds();
    private Random rand = new Random();
    private Long origin;
    private Long bound;
    private Collection<String> outputZoneIds;

    /**
     * This constructor will generate random ZonedDateTime between January 1, 1970 12:00:00 AM(inclusive) and January 1, 2100 12:00:00 AM
     * (exclusive) in UTC timezone
     */
    public ZonedDateTimeLimiter() {
        this(DEFAULT_ORIGIN, DEFAULT_BOUND, DEFAULT_ZONE_ID);
    }

    /**
     * This constructor will generate random ZonedDateTime between January 1, 1970 12:00:00 AM(inclusive) and bound(exclusive) in UTC timezone
     *
     * @param bound in epochmillisecond Long
     */
    public ZonedDateTimeLimiter(Long bound) {
        this(DEFAULT_ORIGIN, bound, DEFAULT_ZONE_ID);
    }

    /**
     * This constructor will generate random ZonedDateTime between January 1, 1970 12:00:00 AM(inclusive) and bound(exclusive) in UTC timezone
     *
     * @param bound in Date
     */
    public ZonedDateTimeLimiter(Date bound) {
        this(DEFAULT_ORIGIN, bound.getTime(), DEFAULT_ZONE_ID);
    }

    /**
     * This constructor will generate random ZonedDateTime between January 1, 1970 12:00:00 AM(inclusive) and bound(exclusive) in RANDOM timezone
     *
     * @param bound                 in epochmillisecond Long
     * @param randomizeOutputZoneId boolean flag for random ZoneId
     */
    public ZonedDateTimeLimiter(Long bound, boolean randomizeOutputZoneId) {
        this(DEFAULT_ORIGIN, bound, randomizeOutputZoneId);
    }

    /**
     * This constructor will generate random ZonedDateTime between January 1, 1970 12:00:00 AM(inclusive) and bound(exclusive) in RANDOM timezone
     *
     * @param bound                 in Date
     * @param randomizeOutputZoneId boolean flag for random ZoneId
     */
    public ZonedDateTimeLimiter(Date bound, boolean randomizeOutputZoneId) {
        this(DEFAULT_ORIGIN, bound.getTime(), randomizeOutputZoneId);
    }

    /**
     * This constructor will generate random ZonedDateTime between January 1, 1970 12:00:00 AM(inclusive) and bound(exclusive) in RANDOM timezone
     * from outputZoneIds
     *
     * @param bound         in epochmillisecond Long
     * @param outputZoneIds Collection of ZoneId to choose random ZoneId from
     */
    public ZonedDateTimeLimiter(Long bound, Collection<String> outputZoneIds) {
        this(DEFAULT_ORIGIN, bound, outputZoneIds);
    }

    /**
     * This constructor will generate random ZonedDateTime between January 1, 1970 12:00:00 AM(inclusive) and bound(exclusive) in RANDOM timezone
     * from outputZoneIds
     *
     * @param bound         in Date
     * @param outputZoneIds Collection of ZoneId to choose random ZoneId from
     */
    public ZonedDateTimeLimiter(Date bound, Collection<String> outputZoneIds) {
        this(DEFAULT_ORIGIN, bound.getTime(), outputZoneIds);
    }

    /**
     * This constructor will generate random ZonedDateTime between January 1, 1970 12:00:00 AM(inclusive) and bound(exclusive) in outputZoneIds timezone
     *
     * @param bound        in epochmillisecond Long
     * @param outputZoneId ZoneId for ZonedDateTime output
     */
    public ZonedDateTimeLimiter(Long bound, ZoneId outputZoneId) {
        this(DEFAULT_ORIGIN, bound, outputZoneId);
    }

    /**
     * This constructor will generate random ZonedDateTime between January 1, 1970 12:00:00 AM(inclusive) and bound(exclusive) in outputZoneIds timezone
     *
     * @param bound        in Date
     * @param outputZoneId ZoneId for ZonedDateTime output
     */
    public ZonedDateTimeLimiter(Date bound, ZoneId outputZoneId) {
        this(DEFAULT_ORIGIN, bound.getTime(), outputZoneId);
    }

    /**
     * This constructor will generate random ZonedDateTime between origin(inclusive) and bound(exclusive) in UTC timezone
     *
     * @param origin in epochmillisecond Long
     * @param bound  in epochmillisecond Long
     */
    public ZonedDateTimeLimiter(Long origin, Long bound) {
        this(origin, bound, DEFAULT_ZONE_ID);
    }

    /**
     * This constructor will generate random ZonedDateTime between origin(inclusive) and bound(exclusive) in UTC timezone
     *
     * @param origin in Date
     * @param bound  in Date
     */
    public ZonedDateTimeLimiter(Date origin, Date bound) {
        this(origin.getTime(), bound.getTime(), DEFAULT_ZONE_ID);
    }

    /**
     * This constructor will generate random ZonedDateTime between origin(inclusive) and bound(exclusive) in RANDOM timezone
     *
     * @param origin                in epochmillisecond Long
     * @param bound                 in epochmillisecond Long
     * @param randomizeOutputZoneId boolean flag for random ZoneId
     */
    public ZonedDateTimeLimiter(Long origin, Long bound, boolean randomizeOutputZoneId) {
        validateAndSetRange(origin, bound);
        if (randomizeOutputZoneId) {
            this.outputZoneIds = AVAILABLE_ZONE_IDS;
        } else {
            this.outputZoneIds = new HashSet<>(Arrays.asList(DEFAULT_ZONE_ID.getId()));
        }
    }

    /**
     * This constructor will generate random ZonedDateTime between origin(inclusive) and bound(exclusive) in RANDOM timezone
     *
     * @param origin                in Date
     * @param bound                 in Date
     * @param randomizeOutputZoneId boolean flag for random ZoneId
     */
    public ZonedDateTimeLimiter(Date origin, Date bound, boolean randomizeOutputZoneId) {
        validateAndSetRange(origin.getTime(), bound.getTime());
        if (randomizeOutputZoneId) {
            this.outputZoneIds = AVAILABLE_ZONE_IDS;
        } else {
            this.outputZoneIds = new HashSet<>(Arrays.asList(DEFAULT_ZONE_ID.getId()));
        }
    }

    /**
     * This constructor will generate random ZonedDateTime between origin(inclusive) and bound(exclusive) in RANDOM timezone from outputZoneIds
     *
     * @param origin        in epochmillisecond Long
     * @param bound         in epochmillisecond Long
     * @param outputZoneIds Collection of ZoneId to choose random ZoneId from
     */
    public ZonedDateTimeLimiter(Long origin, Long bound, Collection<String> outputZoneIds) {
        validateAndSetRange(origin, bound);
        this.outputZoneIds = outputZoneIds;
    }

    /**
     * This constructor will generate random ZonedDateTime between origin(inclusive) and bound(exclusive) in RANDOM timezone from outputZoneIds
     *
     * @param origin        in Date
     * @param bound         in Date
     * @param outputZoneIds Collection of ZoneId to choose random ZoneId from
     */
    public ZonedDateTimeLimiter(Date origin, Date bound, Collection<String> outputZoneIds) {
        validateAndSetRange(origin.getTime(), bound.getTime());
        this.outputZoneIds = outputZoneIds;
    }


    /**
     * This constructor will generate random ZonedDateTime between origin(inclusive) and bound(exclusive) in outputZoneId timezone
     *
     * @param origin       in epochmillisecond Long
     * @param bound        in epochmillisecond Long
     * @param outputZoneId ZoneId for ZonedDateTime output
     */
    public ZonedDateTimeLimiter(Long origin, Long bound, ZoneId outputZoneId) {
        validateAndSetRange(origin, bound);
        this.outputZoneIds = new HashSet<>(Arrays.asList(outputZoneId.getId()));
    }

    /**
     * This constructor will generate random ZonedDateTime between origin(inclusive) and bound(exclusive) in outputZoneId timezone
     *
     * @param origin       in Date
     * @param bound        in Date
     * @param outputZoneId ZoneId for ZonedDateTime output
     */
    public ZonedDateTimeLimiter(Date origin, Date bound, ZoneId outputZoneId) {
        validateAndSetRange(origin.getTime(), bound.getTime());
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
