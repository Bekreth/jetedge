package com.rainbowpunch.jetedge.core.limiters.common;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;
import com.rainbowpunch.jetedge.util.ReadableCharList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This Limiter takes simplified Regex expression, and reverse engineers a string that matches it.
 */
public class RegexLimiter extends SimpleAbstractLimiter<String> {

    private List<EncodedChar> encodedChars;

    private boolean openSquareBracket = false;
    private boolean openRange = false;
    private boolean openCurlyBracket = false;
    private boolean openEscape = false;
    private boolean antiQualifier = false;

    private List<Character> listeningCharList = new ArrayList<>();

    /**
     * Creates a String that matches the provided Regex pattern.
     * @param pattern
     *          The regex pattern to use as a template.
     */
    public RegexLimiter(String pattern) {
        try {
            encodedChars = new ArrayList<>();
            for (char c : pattern.toCharArray()) {
                if (openCurlyBracket) {
                    quantityEncoding(c);
                } else if (openSquareBracket) {
                    rangeEncoding(c);
                } else if (openEscape) {
                    escapeEncoding(c);
                } else {
                    encodingSearch(c);
                }
            }
        } catch (Exception e) {
            throw new LimiterConstructionException("Could not parse Regex", e);
        }
    }

    @Override
    public Supplier<String> generateSupplier(Random random) {
        return () -> {
            int randomNum = random.nextInt();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < encodedChars.size(); i++) {
                stringBuilder.append(encodedChars.get(i).outputEncodedString(randomNum, i));
            }
            return stringBuilder.toString();
        };
    }


    private void quantityEncoding(char c) {
        if (c == '}') {
            openCurlyBracket = false;
            encodedChars.add(new EncodedChar(Flag.QUANTITY, listeningCharList));

            listeningCharList = new ArrayList<>();
        } else {
            listeningCharList.add(c);
        }
    }

    private void rangeEncoding(char c) {
        if (c == ']') {
            EncodedChar encodedChar = antiQualifier
                    ? new EncodedChar(Flag.ANTI_CHAR_SET, listeningCharList)
                    : new EncodedChar(Flag.CHAR_SET, listeningCharList);
            encodedChars.add(encodedChar);

            listeningCharList = new ArrayList<>();
            openSquareBracket = false;
            antiQualifier = false;
        } else if (c == '^') {
            antiQualifier = true;
        } else  if (c == '-') {
            openRange = true;
        } else if (openRange) {
            int finalIndex = listeningCharList.size() - 1;

            char startChar = listeningCharList.get(finalIndex);
            char endChar = c;

            listeningCharList.remove(finalIndex);

            validateRangeWithinSingleCharSet(startChar, endChar);

            IntStream.range((int) startChar, ((int) endChar) + 1)
                    .mapToObj(i -> (char) i)
                    .forEach(listeningCharList::add);
            openRange = false;
        } else {
            listeningCharList.add(c);
        }
    }

    private void escapeEncoding(char c) {
        openEscape = false;
        //Because dot has special meaning when escaped, it's treated specially
        Flag flag = Flag.getFlag(c) == Flag.DOT ? null : Flag.getFlag(c);
        EncodedChar encodedChar = flag == null
                ? new EncodedChar(c)
                : new EncodedChar(flag);
        encodedChars.add(encodedChar);
    }

    private void encodingSearch(char c) {
        if (c == '{') {
            openCurlyBracket = true;
        } else if (c == '[') {
            openSquareBracket = true;
        } else if (c == '\\') {
            openEscape = true;
        } else if (c == '.') {
            encodedChars.add(new EncodedChar(Flag.DOT));
        } else {
            encodedChars.add(new EncodedChar(c));
        }
    }

    private void validateRangeWithinSingleCharSet(char c1, char c2) {
        if ((int) c1 >= (int) c2) {
            throw new LimiterConstructionException("The provide char range is inverted.");
        }
        LimiterConstructionException exception = new LimiterConstructionException("Cannot construct Regex Limiter.  "
                + "Char range is invalid");
        if (ReadableCharList.LIST_OF_CHAR_DIGITS.contains(c1)) {
            if (!ReadableCharList.LIST_OF_CHAR_DIGITS.contains(c2)) {
                throw exception;
            }
        } else if (ReadableCharList.LIST_OF_UPPER_CASE_CHAR.contains(c1)) {
            if (!ReadableCharList.LIST_OF_UPPER_CASE_CHAR.contains(c2)) {
                throw exception;
            }
        } else if (ReadableCharList.LIST_OF_LOWER_CASE_CHAR.contains(c1)) {
            if (!ReadableCharList.LIST_OF_LOWER_CASE_CHAR.contains(c2)) {
                throw exception;
            }
        } else {
            throw exception;
        }
    }


    // ------------ Support Inner Classes ----------
    private class EncodedChar {
        private final Flag flag;
        private List<Character> possibleCharacter;
        private Integer startInt;
        private Integer endInt;

        EncodedChar(char character) {
            this.flag = Flag.SINGLE_CHARACTER;
            possibleCharacter = Collections.singletonList(character);
        }

        EncodedChar(Flag flag) {
            this.flag = flag;
            possibleCharacter = flag.getCharacterList();
        }

        EncodedChar(Flag flag, List<Character> characters) {
            this.flag = flag;
            if (flag.equals(Flag.CHAR_SET)) {
                possibleCharacter = characters;
            } else if (flag.equals(Flag.ANTI_CHAR_SET)) {
                // TODO: 9/15/17 figure out hyphen
                possibleCharacter = ReadableCharList.LIST_OF_ALL_CHAR.stream()
                        .sequential()
                        .filter(c -> !characters.contains(c))
                        .collect(Collectors.toList());
            } else if (flag.equals(Flag.QUANTITY)) {
                StringBuilder builder = new StringBuilder(characters.size());
                for (Character ch: characters) {
                    builder.append(ch);
                }
                String quantity = builder.toString();
                if (quantity.contains(",")) {
                    String[] quantities = quantity.split(",");
                    startInt = Integer.parseInt(quantities[0]);
                    if (quantities.length == 2) {
                        endInt = Integer.parseInt(quantities[1]);
                    }
                } else {
                    startInt = Integer.parseInt(quantity);
                    endInt = startInt;
                }
            } else {
                throw new RuntimeException("Encoded character failed");
            }
        }

        String outputEncodedString(int random, int position) {
            random = random < 0 ? random * -1 : random;
            if (flag.equals(Flag.QUANTITY)) {
                StringBuilder builder = new StringBuilder();
                if (startInt.equals(endInt)) {
                    for (int i = 1; i < endInt; i++) {
                        builder.append(getPastCharacter((random + i) * i, position));
                    }
                } else {
                    Random innerRandom = new Random(random);
                    if (endInt == null) {
                        endInt = innerRandom.nextInt(4);
                    }
                    int endingInt = innerRandom.nextInt(endInt - startInt) + startInt;
                    for (int i = 1; i < startInt + endingInt; i++) {
                        builder.append(getPastCharacter((random + i) * i, position));
                    }
                }
                return builder.toString();
            } else {
                if (possibleCharacter.size() == 1) {
                    return Character.toString(possibleCharacter.get(0));
                } else {
                    return Character.toString(possibleCharacter.get((random + position) % possibleCharacter.size()));
                }
            }
        }

        private String getPastCharacter(int random, int currentPosition) {
            return encodedChars.get(currentPosition - 1).outputEncodedString(random, currentPosition);
        }
    }

    private enum Flag {
        SINGLE_CHARACTER(),
        DOT(".", ReadableCharList.LIST_OF_ALL_CHAR),
        WHITE_SPACE("s", Collections.singletonList(' ')),
        ANTI_WHITE_SPACE("S", ReadableCharList.LIST_OF_CHAR_NO_SPACE),
        CHAR_SET(),
        ANTI_CHAR_SET(),
        DIGIT("d", ReadableCharList.LIST_OF_CHAR_DIGITS),
        ANTI_DIGIT("D", ReadableCharList.LIST_OF_CHAR_NO_DIGITS),
        QUANTITY();

        private String matchString;
        private List<Character> characterList;

        Flag() {

        }

        Flag(String matchString, List<Character> characterList) {
            this.matchString = matchString;
            this.characterList = characterList;
        }

        public List<Character> getCharacterList() {
            return characterList;
        }

        static Flag getFlag(char c) {
            for (Flag flag : Flag.values()) {
                if (flag.matchString != null && flag.matchString.equals(Character.toString(c))) {
                    return flag;
                }
            }
            return null;
        }
    }
}