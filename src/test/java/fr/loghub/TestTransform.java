package fr.loghub;

import org.junit.Test;

import fr.loghub.scanners.AxisBaseByPatternM;

public class TestTransform {
    
    @Test
    public void transformBad() {
        System.out.println(AxisBaseByPatternM.PATTERN + " -> " + preprocessPattern(AxisBaseByPatternM.PATTERN));
    }
    /**
     * Replace documented FDF symbols to their JSR-310 analogs. The conversions are performed:
     * unquoted T -> quoted T
     * u -> ccccc (day of week starting from Monday)
     * ZZ -> XX (zone offset in RFC format: +HHmm, Z for UTC)
     * ZZ -> XXX (zone offset in ISO format: +HH:mm, Z for UTC)
     * ZZZ -> VV (zone id)
     * @param pattern time formatting pattern
     * @return JSR-310 compatible pattern
     */
    private static String preprocessPattern(String pattern) {
        final int length = pattern.length();
        boolean insideQuotes = false;
        final StringBuilder sb = new StringBuilder(pattern.length() + 5);
        final DateFormatParsingState state = new DateFormatParsingState();
        for (int i = 0; i < length; i++) {
            final char c = pattern.charAt(i);
            if (c != 'u') {
                state.updateU(sb);
            }
            if (c != 'Z') {
                state.updateZ(sb);
            }
            switch (c) {
                case '\'':
                    insideQuotes = !insideQuotes;
                    sb.append(c);
                    break;
                case 'T':
                    if (!insideQuotes) {
                        sb.append("'T'");
                    } else {
                        sb.append(c);
                    }
                    break;
                case 'Z':
                    if (!insideQuotes) {
                        ++state.zCount;
                    }
                    sb.append(c);
                    break;
                case 'u':
                    if (!insideQuotes) {
                        ++state.uCount;
                    }
                    sb.append(c);
                    break;
                case 'y':
                    sb.append('u');
                    break;
                default:
                    sb.append(c);
            }
        }
        state.updateU(sb);
        state.updateZ(sb);
        return sb.toString();
    }

    private static final class DateFormatParsingState {
        private int zCount = 0;
        private int uCount = 0;

        private void updateU(StringBuilder sb) {
            if (uCount > 0) {
                sb.setLength(sb.length() - uCount);
                for (int i = 1; i < uCount; i++) {
                    sb.append('0');
                }
                sb.append("ccccc");
            }
            uCount = 0;
        }

        private void updateZ(StringBuilder sb) {
            if (zCount > 0 && zCount <= 3) {
                sb.setLength(sb.length() - zCount);
                if (zCount == 1) {
                    sb.append("XX");
                } else if (zCount == 2) {
                    sb.append("XXX");
                } else {
                    sb.append("VV");
                }
            }
            zCount = 0;
        }

    }

}
