package fr.loghub.isosubtests;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Axibase extends IsoScanner {

    @Override
    protected ScannerRunner getScanner() {
        return this::run;
    }

    private static final class ParsingContext {
        private int offset;
    }

    public LocalDateTime run(String date) {
        ParsingContext context = new ParsingContext();
        return parseIso8601AsLocalDateTime(date, 'T', context);
    }

    private static LocalDateTime parseIso8601AsLocalDateTime(String date, char delimiter, ParsingContext context) {
        final int length = date.length();
        int offset = context.offset;

        // extract year
        int year = parseInt(date, offset, offset += 4, length);
        checkOffset(date, offset, '-');

        // extract month
        int month = parseInt(date, offset += 1, offset += 2, length);
        checkOffset(date, offset, '-');

        // extract day
        int day = parseInt(date, offset += 1, offset += 2, length);
        checkOffset(date, offset, delimiter);

        // extract hours, minutes, seconds and milliseconds
        int hour = parseInt(date, offset += 1, offset += 2, length);
        checkOffset(date, offset, ':');

        int minutes = parseInt(date, offset += 1, offset += 2, length);

        // seconds can be optional
        final int seconds;
        if (date.charAt(offset) == ':') {
            seconds = parseInt(date, offset += 1, offset += 2, length);
        } else {
            seconds = 0;
        }

        // milliseconds can be optional in the format
        final int nanos;
        if (offset < length && date.charAt(offset) == '.') {
            final int startPos = ++offset;
            final int endPosExcl = Math.min(offset + 9, length);
            int frac = resolveDigitByCode(date, offset++);
            while (offset < endPosExcl) {
                final int digit = date.charAt(offset) - '0';
                if (digit < 0 || digit > 9) {
                    break;
                }
                frac = frac * 10 + digit;
                ++offset;
            }
            nanos = parseNanos(frac, offset - startPos);
        } else {
            nanos = 0;
        }
        context.offset = offset;
        return LocalDateTime.of(year, month, day, hour, minutes, seconds, nanos);
    }

    private static int resolveDigitByCode(String value, int index) {
        final char c = value.charAt(index);
        final int result = c - '0';
        if (result < 0 || result > 9) {
            throw new DateTimeParseException("Failed to parse number at index ", value, index);
        }
        return result;
    }

    private static int parseNanos(int value, int digits) {
        return value * powerOfTen(9 - digits);
    }

    private static int powerOfTen(int pow) {
        switch (pow) {
        case 0: return 1;
        case 1: return 10;
        case 2: return 100;
        case 3: return 1_000;
        case 4: return 10_000;
        case 5: return 100_000;
        case 6: return 1_000_000;
        case 7: return 10_000_000;
        case 8: return 100_000_000;
        case 9: return 1_000_000_000;
        }
        for (int accum = 1, b = 10;; pow >>= 1) {
            if (pow == 1) {
                return b * accum;
            } else {
                accum *= ((pow & 1) == 0) ? 1 : b;
                b *= b;
            }
        }
    }

    private static int parseInt(String value, int beginIndex, int endIndex, int valueLength) throws NumberFormatException {
        if (beginIndex < 0 || endIndex > valueLength || beginIndex >= endIndex) {
            throw new DateTimeParseException("Failed to parse number at index ", value, beginIndex);
        }
        int result = resolveDigitByCode(value, beginIndex);
        for (int i = beginIndex + 1; i < endIndex; ++i) {
            result = result * 10 + resolveDigitByCode(value, i);
        }
        return result;
    }

    private static void checkOffset(String value, int offset, char expected) throws IndexOutOfBoundsException {
        char found = value.charAt(offset);
        if (found != expected) {
            throw new DateTimeParseException("Expected '" + expected + "' character but found '" + found + "'", value, offset);
        }
    }

}
