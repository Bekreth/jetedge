package com.rainbowpunch.jetedge.core.limiters.common.java;

import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZonedDateTimeLimiterTest {

    private static final Long DEFAULT_ORIGIN = 100000L;
    private static final Long DEFAULT_BOUND = 10000000000L;
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC");
    private static final Set<String> DEFAULT_ZONE_ID_COLLECTION = new HashSet<>(Arrays.asList("America/Costa_Rica", "Asia/Katmandu"));
    private Random random = new Random();

    @Test
    public void testDefaultZonedDateTimeLimiter() {
        ZonedDateTimeLimiter zonedDateTimeLimiter = new ZonedDateTimeLimiter();
        ZonedDateTime minComparison = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), DEFAULT_ZONE_ID);
        ZonedDateTime maxComparison = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.MAX_VALUE), DEFAULT_ZONE_ID);

        for (int i = 0; i < 100; i++) {
            ZonedDateTime zonedDateTime = zonedDateTimeLimiter.generateSupplier(random).get();
            assertEquals(-1, minComparison.compareTo(zonedDateTime));
            assertEquals(1, maxComparison.compareTo(zonedDateTime));
        }
    }

    @Test
    public void testZonedDateTimeLimiterWithLongBound() {
        ZonedDateTime bound = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_BOUND), DEFAULT_ZONE_ID);
        ZonedDateTimeLimiter zonedDateTimeLimiter = new ZonedDateTimeLimiter(DEFAULT_BOUND);

        for (int i = 0; i < 100; i++) {
            ZonedDateTime zonedDateTime = zonedDateTimeLimiter.generateSupplier(random).get();
            assertEquals(1, bound.compareTo(zonedDateTime));
        }
    }

    @Test
    public void testZonedDateTimeLimiterWithLongBoundAndRandomZoneId() {
        ZonedDateTime bound = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_BOUND), DEFAULT_ZONE_ID);
        ZonedDateTimeLimiter zonedDateTimeLimiter = new ZonedDateTimeLimiter(DEFAULT_BOUND, true);

        for (int i = 0; i < 100; i++) {
            ZonedDateTime zonedDateTime = zonedDateTimeLimiter.generateSupplier(random).get();
            assertEquals(1, bound.compareTo(zonedDateTime));
            assertTrue(ZoneId.getAvailableZoneIds().contains(zonedDateTime.getZone().getId()));
        }
    }

    @Test
    public void testZonedDateTimeLimiterWithLongBoundAndCollectionOfZoneIdStrings() {
        ZonedDateTime bound = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_BOUND), DEFAULT_ZONE_ID);
        ZonedDateTimeLimiter zonedDateTimeLimiter = new ZonedDateTimeLimiter(DEFAULT_BOUND, DEFAULT_ZONE_ID_COLLECTION);

        for (int i = 0; i < 100; i++) {
            ZonedDateTime zonedDateTime = zonedDateTimeLimiter.generateSupplier(random).get();
            assertEquals(1, bound.compareTo(zonedDateTime));
            assertTrue(DEFAULT_ZONE_ID_COLLECTION.contains(zonedDateTime.getZone().getId()));
        }
    }

    @Test
    public void testZonedDateTimeLimiterWithLongBoundAndZoneId() {
        ZonedDateTime bound = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_BOUND), DEFAULT_ZONE_ID);
        ZonedDateTimeLimiter zonedDateTimeLimiter = new ZonedDateTimeLimiter(DEFAULT_BOUND, DEFAULT_ZONE_ID);

        for (int i = 0; i < 100; i++) {
            ZonedDateTime zonedDateTime = zonedDateTimeLimiter.generateSupplier(random).get();
            assertEquals(1, bound.compareTo(zonedDateTime));
            assertTrue(DEFAULT_ZONE_ID.equals(zonedDateTime.getZone()));
        }
    }

    @Test
    public void testZonedDateTimeLimiterWithLongOriginAndLongBound() {
        ZonedDateTime origin = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_ORIGIN), DEFAULT_ZONE_ID);
        ZonedDateTime bound = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_BOUND), DEFAULT_ZONE_ID);
        ZonedDateTimeLimiter zonedDateTimeLimiter = new ZonedDateTimeLimiter(DEFAULT_ORIGIN, DEFAULT_BOUND);

        for (int i = 0; i < 100; i++) {
            ZonedDateTime zonedDateTime = zonedDateTimeLimiter.generateSupplier(random).get();
            assertTrue(origin.compareTo(zonedDateTime) == -1 || origin.compareTo(zonedDateTime) == 0);
            assertEquals(1, bound.compareTo(zonedDateTime));
        }
    }

    @Test
    public void testZonedDateTimeLimiterWithLongOriginAndLongBoundAndRandomZoneId() {
        ZonedDateTime origin = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_ORIGIN), DEFAULT_ZONE_ID);
        ZonedDateTime bound = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_BOUND), DEFAULT_ZONE_ID);
        ZonedDateTimeLimiter zonedDateTimeLimiter = new ZonedDateTimeLimiter(DEFAULT_ORIGIN, DEFAULT_BOUND, true);

        for (int i = 0; i < 100; i++) {
            ZonedDateTime zonedDateTime = zonedDateTimeLimiter.generateSupplier(random).get();
            assertTrue(origin.compareTo(zonedDateTime) == -1 || origin.compareTo(zonedDateTime) == 0);
            assertEquals(1, bound.compareTo(zonedDateTime));
            assertTrue(ZoneId.getAvailableZoneIds().contains(zonedDateTime.getZone().getId()));
        }
    }

    @Test
    public void testZonedDateTimeLimiterWithLongOriginAndLongBoundAndCollectionOfZoneIdStrings() {
        ZonedDateTime origin = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_ORIGIN), DEFAULT_ZONE_ID);
        ZonedDateTime bound = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_BOUND), DEFAULT_ZONE_ID);
        ZonedDateTimeLimiter zonedDateTimeLimiter = new ZonedDateTimeLimiter(DEFAULT_ORIGIN, DEFAULT_BOUND, DEFAULT_ZONE_ID_COLLECTION);

        for (int i = 0; i < 100; i++) {
            ZonedDateTime zonedDateTime = zonedDateTimeLimiter.generateSupplier(random).get();
            assertTrue(origin.compareTo(zonedDateTime) == -1 || origin.compareTo(zonedDateTime) == 0);
            assertEquals(1, bound.compareTo(zonedDateTime));
            assertTrue(DEFAULT_ZONE_ID_COLLECTION.contains(zonedDateTime.getZone().getId()));
        }
    }

    @Test
    public void testZonedDateTimeLimiterWithLongOriginAndLongBoundAndZoneId() {
        ZonedDateTime origin = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_ORIGIN), DEFAULT_ZONE_ID);
        ZonedDateTime bound = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_BOUND), DEFAULT_ZONE_ID);
        ZonedDateTimeLimiter zonedDateTimeLimiter = new ZonedDateTimeLimiter(DEFAULT_ORIGIN, DEFAULT_BOUND, DEFAULT_ZONE_ID);

        for (int i = 0; i < 100; i++) {
            ZonedDateTime zonedDateTime = zonedDateTimeLimiter.generateSupplier(random).get();
            assertTrue(origin.compareTo(zonedDateTime) == -1 || origin.compareTo(zonedDateTime) == 0);
            assertEquals(1, bound.compareTo(zonedDateTime));
            assertTrue(DEFAULT_ZONE_ID.equals(zonedDateTime.getZone()));
        }
    }


}
