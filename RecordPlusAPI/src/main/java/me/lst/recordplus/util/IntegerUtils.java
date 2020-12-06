package me.lst.recordplus.util;

public class IntegerUtils {
    private static final IntegerParseResult FAILURE = new IntegerParseResult(false, 0);

    public static IntegerParseResult parseInt(String string) {
        if (string == null) {
            return FAILURE;
        }
        int length = string.length();

        if (length == 0) {
            return FAILURE;
        }
        int number = 0;
        int previous;

        if (string.charAt(0) == '-') {
            for (int i = 1; i < length; ++i) {
                char character = string.charAt(i);

                if (!CharacterUtils.isInteger(character)) {
                    return FAILURE;
                }
                previous = number;
                number *= 10;
                number -= CharacterUtils.getAsInteger(character);

                if (previous < number) {
                    return FAILURE;
                }
            }
        } else {
            for (int i = 0; i < length; ++i) {
                char character = string.charAt(i);

                if (!CharacterUtils.isInteger(character)) {
                    return FAILURE;
                }
                previous = number;
                number *= 10;
                number += CharacterUtils.getAsInteger(character);

                if (previous > number) {
                    return FAILURE;
                }
            }
        }
        return new IntegerParseResult(true, number);
    }

    public static int unsafelyParseInt(String string) {
        int length = string.length();
        int number = 0;

        if (string.charAt(0) == '-') {
            for (int i = 1; i < length; ++i) {
                number *= 10;
                number -= CharacterUtils.getAsInteger(string.charAt(i));
            }
        } else {
            for (int i = 0; i < length; ++i) {
                number *= 10;
                number += CharacterUtils.getAsInteger(string.charAt(i));
            }
        }
        return number;
    }

    public static class IntegerParseResult {
        private final boolean successful;
        private final int value;

        public IntegerParseResult(boolean successful, int value) {
            this.successful = successful;
            this.value = value;
        }

        public boolean wasSuccessful() {
            return this.successful;
        }

        public boolean isPositive() {
            return this.successful && this.value > 0;
        }

        public int getValue() {
            return this.value;
        }
    }
}