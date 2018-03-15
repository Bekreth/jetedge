package com.rainbowpunch.jetedge.core.limiters.common.java;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.IntegerLimiter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A limiter for BigDecimal data fields.
 */
public class BigDecimalLimiter extends SimpleAbstractLimiter<BigDecimal> {

    private static final String DEFAULT_UPPERBOUND = "100.00";
    private static final String ALLOWABLE_CHARACTERS = "-1234567890.";
    private static final int ASCII_0 = 48;


    private IntegerLimiter topLimiter;
    private IntegerLimiter bottomLimiter;
    private BigDecimal lowerLimit;

    public BigDecimalLimiter() {
        this(DEFAULT_UPPERBOUND);
    }

    public BigDecimalLimiter(String upperValueTemplate) {
        this(upperValueTemplate, null);
    }

    public BigDecimalLimiter(String upperValueTemplate, String lowerValueTemplate) {
        if (lowerValueTemplate == null) {
            lowerValueTemplate = defaultLowerBound(upperValueTemplate);
        }
        validate(upperValueTemplate, lowerValueTemplate);

        BigDecimal upperLimit = new BigDecimal(upperValueTemplate);
        BigDecimal lowerLimit = new BigDecimal(lowerValueTemplate);

        String regexBase = upperLimit.subtract(lowerLimit).toString();
        this.lowerLimit = lowerLimit;

        String[] valueHalves = regexBase.split("\\.");
        this.topLimiter = new IntegerLimiter(Integer.valueOf(valueHalves[0]));
        if(valueHalves.length == 2) {
            this.bottomLimiter = new IntegerLimiter(Integer.valueOf("1" + valueHalves[1]));
        }
    }

    private static void validate(String upperValueTemplate, String lowerValueTemplate) {
        Consumer<IntStream> contains = (inCharacters) -> {
            inCharacters.mapToObj((inChar) -> {
                Character character = (char) inChar;
                return character.toString();
            })
            .filter((inString) -> !ALLOWABLE_CHARACTERS.contains(inString)).findAny()
            .ifPresent((obj) -> {
                throw new LimiterConstructionException("Provided String contains unallowed characters");
            });
        };

        contains.accept(upperValueTemplate.chars());
        contains.accept(lowerValueTemplate.chars());


        if (upperValueTemplate.contains(".") || lowerValueTemplate.contains(".")) {
            try {
                int upperPrecision = upperValueTemplate.split("\\.")[1].length();
                int lowerPrecision = lowerValueTemplate.split("\\.")[1].length();
                if (upperPrecision != lowerPrecision) {
                    throw new LimiterConstructionException("Upper and lower bounds require the same amount of precision");
                }
            } catch (Exception e) {
                throw new LimiterConstructionException("Upper and lower bounds require the same amount of precision");
            }
        }

    }

    private static String defaultLowerBound(String upperValueTemplate) {
        return Arrays.stream(upperValueTemplate.split("\\."))
                .map((inString) -> {
                    return IntStream.range(0, inString.length())
                            .mapToObj((inInt) -> String.valueOf((char) ASCII_0))
                            .collect(Collectors.joining());
                })
                .collect(Collectors.joining("."));
    }

    @Override
    public Supplier<BigDecimal> generateSupplier(Random random) {
        return () -> {
            String topNumber = topLimiter.generateSupplier(random).get().toString();
            String bottomNumber = bottomLimiter == null
                    ? ""
                    : "." + bottomLimiter.generateSupplier(random).get().toString().replaceFirst("1", "");

            String numberString = topNumber + bottomNumber;
            BigDecimal decimal = new BigDecimal(numberString);
            return decimal.add(lowerLimit);
        };
    }
}
