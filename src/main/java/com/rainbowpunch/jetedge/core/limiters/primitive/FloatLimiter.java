package com.rainbowpunch.jetedge.core.limiters.primitive;

import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A limiter for Float data fields.  For a detailed description of the parameters and their values
 * an effect on behavior see {@link FloatLimiter#FloatLimiter(Float, Float, Integer)}
 */
public class FloatLimiter extends SimpleAbstractLimiter<Float> {
    private final BigDecimal offset;

    private final BigDecimal range;

    private final Integer precision;

    /**
     * Defaults to {@link Float#MAX_VALUE} for the range and 0f for the offset with no precision specified.
     */
    public FloatLimiter() {
        this(null, 0f);
    }

    /**
     * Defaults to 0f for the offset with no precision specified.
     * @param range a positive/negative float value that reflects the upper boundary (default {@link Float#MAX_VALUE})
     */
    public FloatLimiter(Float range) {
        this(range, null);
    }

    /**
     * Defaults to no precision specified.
     * @param range a positive/negative float value that reflects the upper boundary (default {@link Float#MAX_VALUE})
     * @param offset a positive/negative float value that reflects the lower bound (default 0.0f)
     */
    public FloatLimiter(Float range, Float offset) {
        this(range, offset, null);
    }

    /**
     * Constructs a float limiter that will generate a supplier which produces a number within the range specified
     * by {@code range} and {@code offset}.  If {@code precision} is specified then the float returned will be
     * truncated to that number of values past the decimal point.
     *
     * <b>Note</b> - if {@code precision} is specified then the {@code range} and {@code offset} are also truncated
     * using {@link BigDecimal#ROUND_DOWN} to construct the 'true range' to use when generating values.  Thus, the
     * following is invalid @{code new FloatLimiter(0.15f, 0.1f, 1) } as the range would be truncated to 0.1 and there
     * are no numbers between the offset (0.1) and the range (0.1).
     *
     * @param range a positive/negative float value that reflects the upper boundary (default {@link Float#MAX_VALUE})
     * @param offset a positive/negative float value that reflects the lower bound (default 0.0f)
     * @param precision a natural number, the number of digits past the decimal for the return values
     */
    public FloatLimiter(Float range, Float offset, Integer precision) {
        float offs = offset != null ? offset : 0f;
        float rang = range != null ? range : Float.MAX_VALUE;

        this.precision = precision;
        if (this.precision != null) {
            if (this.precision < 1) {
                throw new IllegalArgumentException("Precision cannot be < 1");
            }
            this.range = new BigDecimal(rang).setScale(precision, BigDecimal.ROUND_DOWN);
            this.offset = new BigDecimal(offs).setScale(precision, BigDecimal.ROUND_DOWN);
        } else {
            this.range = new BigDecimal(rang);
            this.offset = new BigDecimal(offs);
        }

        if (Math.abs(this.range.subtract(this.offset).floatValue()) <= 0) {
            throw new IllegalArgumentException("Your offset cannot be >= the range (offset = " + this.offset + ", range=" + this.range + ", precision = " + this.precision);
        }
    }

    /**
     * Returns the range value used to generate random numbers.  This may differ from the value passed into the
     * constructor if a precision was also passed in. see {@link FloatLimiter#FloatLimiter(Float, Float, Integer)}
     *
     * @return the range value used in random number generation
     */
    public BigDecimal getRange() {
        return this.range;
    }

    /**
     * Returns the offset value used to generate random numbers.  This may differ from the value passed into the
     * constructor if a precision was also passed in. see {@link FloatLimiter#FloatLimiter(Float, Float, Integer)}
     *
     * @return the offset value used in random number generation
     */
    public BigDecimal getOffset() {
        return this.offset;
    }

    /**
     * @return the
     */
    public Optional<Integer> getPrecision() {
        return Optional.ofNullable(this.precision);
    }

    @Override
    public Supplier<Float> generateSupplier(final Random random) {
        return () -> {
            // generate the random value and truncate it per the 'precision' value
            BigDecimal bd = range.subtract(offset).multiply(new BigDecimal(random.nextDouble())).add(offset);
            if (precision != null) {
                bd = bd.setScale(precision, BigDecimal.ROUND_DOWN);
            }
            return bd.floatValue();
        };
    }
}