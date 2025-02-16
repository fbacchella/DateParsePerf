package fr.loghub.com.axibase.date;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRules;
import java.util.Locale;

public class DatetimeProcessorIso8601ReuseContext implements DatetimeProcessor {

    private static final int ISO_LENGTH = "1970-01-01T00:00:00.000000000+00:00:00".length();
    private static final Instant MOCK = Instant.now();
    private static final ParsingContext localContext = new ParsingContext("");

    private final int fractionsOfSecond;
    private final AppendOffset zoneOffsetType;
    private final ZoneId zoneId;
    private final char delimiter;

    public DatetimeProcessorIso8601ReuseContext(int fractionsOfSecond, AppendOffset zoneOffsetType, char delimiter) {
        this.fractionsOfSecond = fractionsOfSecond;
        this.zoneOffsetType = zoneOffsetType;
        this.zoneId = ZoneId.systemDefault();
        this.delimiter = delimiter;
    }

    private DatetimeProcessorIso8601ReuseContext(int fractionsOfSecond, AppendOffset zoneOffsetType, char delimiter, ZoneId zoneId) {
        this.fractionsOfSecond = fractionsOfSecond;
        this.zoneOffsetType = zoneOffsetType;
        this.zoneId = zoneId;
        this.delimiter = delimiter;
    }

    @Override
    public Instant parseInstant(String datetime) {
        return parse(datetime).toInstant();
    }

    @Override
    public ZonedDateTime parse(String datetime) {
        return parseIso8601AsZonedDateTime(datetime, delimiter, zoneId, zoneOffsetType);
    }

    @Override
    public String print(Instant timestamp) {
        return printIso8601(timestamp, delimiter, zoneId, zoneOffsetType, fractionsOfSecond);
    }

    @Override
    public String print(ZonedDateTime zonedDateTime) {
        return printIso8601(zonedDateTime.toLocalDateTime(), zonedDateTime.getOffset(), zoneOffsetType, delimiter, fractionsOfSecond);
    }

    @Override
    public DatetimeProcessor withLocale(Locale locale) {
        return this;
    }

    @Override
    public DatetimeProcessor withDefaultZone(ZoneId zoneId) {
        return this.zoneId.equals(zoneId) ? this :
                new DatetimeProcessorIso8601ReuseContext(fractionsOfSecond, zoneOffsetType, delimiter, zoneId);
    }

    private ZonedDateTime parseIso8601AsZonedDateTime(String date, char delimiter,
            ZoneId defaultOffset, AppendOffset offsetType) {
        ParsingContext context = localContext.reset(date);
        try {
            // extract year
            int year = context.parseInt(-1);
            context.checkOffset('-');

            // extract month
            int month = context.parseInt(2);
            context.checkOffset('-');

            // extract day
            int day = context.parseInt(2);
            context.checkOffset(delimiter);

            // extract hours, minutes, seconds and milliseconds
            int hour = context.parseInt(2);
            context.checkOffset(':');

            int minutes = context.parseInt(2);

            // seconds can be optional
            int seconds;
            if (context.datetime.charAt(context.offset) == ':') {
                context.offset++;
                seconds = context.parseInt(2);
            } else {
                seconds = 0;
            }
            int nanos = context.parseNano();
            ZoneId zoneId = context.extractOffset(offsetType, defaultOffset);
            return ZonedDateTime.of(year, month, day, hour, minutes, seconds, nanos, zoneId);
        } catch (DateTimeException e) {
            throw new DateTimeParseException("Failed to parse date " + date + ": " + e.getMessage(), date, 0, e);
        } finally {
            context.clean();
        }
    }

    /**
     * Optimized print of a timestamp in ISO8601 or local format: yyyy-MM-dd[T| ]HH:mm:ss[.SSS]Z
     * @param timestamp milliseconds since epoch
     * @param offsetType Zone offset format: ISO (+HH:mm), RFC (+HHmm), or NONE
     * @return String representation of the timestamp
     */
    private String printIso8601(Instant timestamp, char delimiter, ZoneId zone, AppendOffset offsetType, int fractionsOfSecond) {
        return printIso8601(timestamp, delimiter, zone, offsetType, fractionsOfSecond, new StringBuilder(ISO_LENGTH));
    }

    private String printIso8601(Instant timestamp, char delimiter, ZoneId zone, AppendOffset offsetType, int fractionsOfSecond, StringBuilder sb) {
        ZoneOffset offset;
        long secs;
        int nanos;
        if (zone instanceof ZoneOffset) {
            secs = timestamp.getEpochSecond();
            nanos = timestamp.getNano();
            offset = (ZoneOffset) zone;
        } else {
            ZoneRules rules = zone.getRules();
            if (rules.isFixedOffset()) {
                secs = timestamp.getEpochSecond();
                nanos = timestamp.getNano();
                offset = rules.getOffset(MOCK);
            } else {
                secs = timestamp.getEpochSecond();
                nanos = timestamp.getNano();
                offset = rules.getOffset(timestamp);
            }
        }
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(secs, nanos, offset);
        return printIso8601(ldt, offset, offsetType, delimiter, fractionsOfSecond, sb);
    }

    /**
     * Optimized print of a timestamp in ISO8601 or local format: yyyy-MM-dd[T| ]HH:mm:ss[.SSS]
     * @param dateTime timestamp as LocalDateTime
     * @param offset time zone offset
     * @param offsetType Zone offset format: ISO (+HH:mm), RFC (+HHmm), or NONE
     * @return String representation of the timestamp
     */
    private String printIso8601(LocalDateTime dateTime, ZoneOffset offset, AppendOffset offsetType, char delimiter, int fractionsOfSecond) {
        return printIso8601(dateTime, offset, offsetType, delimiter, fractionsOfSecond, new StringBuilder(ISO_LENGTH));
    }

    private String printIso8601(LocalDateTime dateTime, ZoneOffset offset, AppendOffset offsetType, char delimiter, int fractionsOfSecond, StringBuilder sb) {
        DatetimeProcessorUtil.adjustPossiblyNegative(sb, dateTime.getYear(), 4).append('-');
        DatetimeProcessorUtil.appendNumberWithFixedPositions(sb, dateTime.getMonthValue(), 2).append('-');
        DatetimeProcessorUtil.appendNumberWithFixedPositions(sb, dateTime.getDayOfMonth(), 2).append(delimiter);
        DatetimeProcessorUtil.appendNumberWithFixedPositions(sb, dateTime.getHour(), 2).append(':');
        DatetimeProcessorUtil.appendNumberWithFixedPositions(sb, dateTime.getMinute(), 2).append(':');
        DatetimeProcessorUtil.appendNumberWithFixedPositions(sb, dateTime.getSecond(), 2);
        DatetimeProcessorUtil.printSubSeconds(fractionsOfSecond, dateTime::getNano, sb);
        offsetType.append(sb, offset, dateTime.atZone(offset).toInstant());
        return sb.toString();
    }

}
