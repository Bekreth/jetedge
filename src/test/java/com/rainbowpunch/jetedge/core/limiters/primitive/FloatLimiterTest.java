package com.rainbowpunch.jetedge.core.limiters.primitive;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FloatLimiterTest {
    // the number of scenarios to run for the distribution test, this
    // can be any number, it shouldn't effect the results. the higher
    // it is however the more confidence we can have
    public static final int DISTRIBUTION_TEST_RUNS = 10;

    // this number must be pretty high.  the lower this number goes
    // the less distributed a result will appear.
    public static final int DISTRIBUTION_TEST_POPULATION_SIZE = 1000;

    // this is going to be greatly affected by DISTRIBUTION_TEST_POPULATION_SIZE
    public static final int AT_LEAST_PERCENT_WITHIN_ONE_STDDEV = 55;

    public static final int AT_MOST_PERCENT_WITHIN_ONE_STDDEV = 65;

    public static final int NORMAL_GENERATION_ITERATIONS = 50;

    private final boolean debug = false;

    /**
     * don't use a seeded random.  It wouldn't help us in what we're trying to do here.
     */
    private final Random r = new Random();


    @Test
    public void constructorDefaults() {
        FloatLimiter l = new FloatLimiter();
        assertEquals(0.0f, l.getOffset().floatValue(), 0);
        assertEquals(Float.MAX_VALUE, l.getRange().floatValue(), 0);
        assertEquals(false, l.getPrecision().isPresent());

        l = new FloatLimiter(null);
        assertEquals(0.0f, l.getOffset().floatValue(), 0);
        assertEquals(Float.MAX_VALUE, l.getRange().floatValue(), 0);
        assertEquals(false, l.getPrecision().isPresent());

        l = new FloatLimiter(null, null);
        assertEquals(0.0f, l.getOffset().floatValue(), 0);
        assertEquals(Float.MAX_VALUE, l.getRange().floatValue(), 0);
        assertEquals(false, l.getPrecision().isPresent());

        l = new FloatLimiter(null, null, null);
        assertEquals(0.0f, l.getOffset().floatValue(), 0);
        assertEquals(Float.MAX_VALUE, l.getRange().floatValue(), 0);
        assertEquals(false, l.getPrecision().isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void precisionMustBeNatural() {
        new FloatLimiter(10f, 0f, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void offsetCantBeRange() {
        new FloatLimiter(0.15f, 0.1f, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void offsetCantBeRangeAfterPrecision() {
        new FloatLimiter(0.19f, 0.1f, 1);
    }

    @Test
    public void negativeRangePositiveOffset() {
        final ScenarioArguments args = new ScenarioArguments(-100f, 100f, 2);
        final List<Float> vals = generateValueAndValidate(NORMAL_GENERATION_ITERATIONS, args);
        print(args, vals);
    }

    @Test
    public void precisionAltersRangeAndOffset() {
        float value = 1.23456789f;
        FloatLimiter l = new ScenarioArguments(value, -value, null).limiter();
        assertEquals(new BigDecimal(value), l.getRange());
        assertEquals(new BigDecimal(-value), l.getOffset());

        IntStream.range(1, 25).forEach(precision -> {
            FloatLimiter l2 = new ScenarioArguments(value, -value, precision).limiter();
            // the range and offset values get truncated based on the precision passed into
            // the limiter
            assertEquals(new BigDecimal(value).setScale(precision, BigDecimal.ROUND_DOWN), l2.getRange());
            assertEquals(new BigDecimal(-value).setScale(precision, BigDecimal.ROUND_DOWN), l2.getOffset());
        });
    }

    @Test
    public void negativeRanges() {
        final ScenarioArguments args = new ScenarioArguments(-100f, -1f, null);
        final List<Float> vals = generateValueAndValidate(NORMAL_GENERATION_ITERATIONS, args);
        print(args, vals);
    }


    @Test
    public void smallRanges() {
        final ScenarioArguments small = new ScenarioArguments(0.1f, 0f, null);
        generateValueAndValidate(NORMAL_GENERATION_ITERATIONS, small);

        final ScenarioArguments smallNegative = new ScenarioArguments(-0.2f, -1.0f, null);
        generateValueAndValidate(NORMAL_GENERATION_ITERATIONS, smallNegative);
    }


    @Test
    public void resultsAreDistributed() {
        IntStream.range(0, DISTRIBUTION_TEST_RUNS)
                .mapToObj(i -> ScenarioArguments.random(r))
                .forEach(args -> {

                    final Supplier<Float> s = args.supplier(r);
                    final List<Float> randomFloats = generateValueAndValidate(DISTRIBUTION_TEST_POPULATION_SIZE, args);
                    final Stats stats = new Stats(args, randomFloats);
                    final long percentWithinStdDev = stats.percentWithinStdDevs(1);

                    // we want at least {AT_LEAST_PERCENT_WITHIN_ONE_STDDEV}% to be within a single std dev (not normal, but still distributed ok
                    String assertion = format("There should be at least %d percent of the values within one stddev, but was %d",
                            AT_LEAST_PERCENT_WITHIN_ONE_STDDEV, percentWithinStdDev);
                    assertTrue(assertion, percentWithinStdDev >= AT_LEAST_PERCENT_WITHIN_ONE_STDDEV);

                    // we don't want > {AT_MOST_PERCENT_WITHIN_ONE_STDDEV}% to be clumped in the center
                    assertion = format("There should be at most %d percent of the values within one stddev, but was %d",
                            AT_MOST_PERCENT_WITHIN_ONE_STDDEV, percentWithinStdDev);
                    assertTrue(assertion, percentWithinStdDev < AT_MOST_PERCENT_WITHIN_ONE_STDDEV);
                });

    }

    /**
     * Generates {@code iterations} values and validates those values using
     * {@link FloatLimiterTest#validate(Float, ScenarioArguments, FloatLimiter)}
     * @return the list of generated floats
     */
    private List<Float> generateValueAndValidate(int iterations, ScenarioArguments args) {
        final FloatLimiter limiter = args.limiter();
        Supplier<Float> supplier = limiter.generateSupplier(r);

        return (List<Float>) IntStream.range(0, iterations)
                .mapToObj(i -> {
                    Float rf = supplier.get();
                    validate(rf, args, limiter);
                    return rf;
                })
                .collect(Collectors.toList());
    }

    /**
     * Validates that the given random float value is within the bounds (and precision) specified by {@code args}
     */
    private void validate(Float generatedFloatValue, ScenarioArguments args, FloatLimiter fl) {
        // we use the fl.getRange() and fl.getOffset because the float limiter
        // alters the values passed in to conform with the specified precision.

        if (args.range != null && args.offset!= null) {
            if (args.range < args.offset) {
                assertTrue("with " + args + ", value " + generatedFloatValue + " should be within the range",
                        generatedFloatValue > fl.getRange().floatValue() && generatedFloatValue <= fl.getOffset().floatValue());
            } else {
                assertTrue("with " + args + ", value " + generatedFloatValue + " should be within the range",
                        generatedFloatValue >= fl.getOffset().floatValue() && generatedFloatValue < fl.getRange().floatValue());
            }
        } else if (args.range != null) {
            assertTrue("with " + args + ", range " + generatedFloatValue + " < " + fl.getRange(), Math.abs(generatedFloatValue) < Math.abs(fl.getRange().floatValue()));
        }
        else if (args.offset != null) {
            assertTrue("with " + args + ", offset " + generatedFloatValue + " > " + fl.getOffset(), Math.abs(generatedFloatValue) >= Math.abs(fl.getOffset().floatValue()));
        }

        if (args.precision != null) {
            String srf = Float.toString(generatedFloatValue);
            int decimalPoints = srf.length() - srf.indexOf('.') - 1;
            assertTrue("with " + args + " " + generatedFloatValue + ", should have " + args.precision + " decimal points", decimalPoints <= args.precision);
        }
    }


    static class ScenarioArguments {
        final Float range;
        final Float offset;
        final Integer precision;

        public String toString() {
            return new StringBuilder('{')
                    .append("r=")
                    .append(range)
                    .append(",o=")
                    .append(offset)
                    .append(",p=")
                    .append(precision)
                    .append('}')
                    .toString();
        }

        public ScenarioArguments(Float range, Float offset, Integer precision) {
            this.range = range;
            this.offset = offset;
            this.precision = precision;
        }

        Supplier<Float> supplier(Random r) {
            return limiter().generateSupplier(r);
        }

        FloatLimiter limiter() {
            return new FloatLimiter(range, offset, precision);
        }

        static ScenarioArguments random(Random r) {
            Float randomRange = r.nextFloat();
            Float randomOffset = r.nextFloat();

            // let's just ensure the range is greater than offset
            if (randomOffset != null && randomRange != null) {
                randomRange += randomOffset;
            }
            Integer precision = (r.nextInt() % 10);
            if (precision < 1) {
                precision = null;
            }

            if (precision == null && randomRange != null) {
                randomRange += 10;
            } else if (randomRange != null) {
                randomRange += 10 ^ precision;
            }
            return new ScenarioArguments(randomRange, randomOffset, precision);
        }
    }

    /**
     * Used to gather distribution statistics on generated values.
     */
    static class Stats {
        private final ScenarioArguments args;
        float variance;
        float stddev;
        float mean;
        float total;
        List<Float> values;

        Stats(ScenarioArguments args, List<Float> values) {
            this.args = args;
            this.values = values;
            // is it a normal distribution?
            total = values.stream().reduce(0f, (f1, f2) -> f1 + f2).floatValue();
            //System.out.println("sum of " + x + " is " + total);
            mean = total / (float) values.size();
            //System.out.println("mean = " + mean);
            variance = values.stream()
                    .map(fv -> Math.pow(fv - mean, 2))
                    .reduce(0D, (f1, f2) -> f1 + f2)
                    .floatValue() / values.size();
            //System.out.println("variance = " + variance);
            stddev = new Double(Math.sqrt(variance)).floatValue();
            //System.out.println("stddev = " + stddev);
        }

        long percentWithinStdDevs(int numOfStdDevs) {
            long count = values.stream().filter(fv -> Math.abs(fv - mean) < (numOfStdDevs * stddev)).count();
            return new BigDecimal(count)
                    .divide(new BigDecimal(values.size()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP)
                    .movePointRight(2)
                    .intValue();
        }
    }

    private void print(ScenarioArguments args, List<Float> values) {
        if (debug) {
            System.out.print(args);
            System.out.print(',');
            System.out.println(values.stream().map(Object::toString).collect(Collectors.joining(",")));
        }
    }
}