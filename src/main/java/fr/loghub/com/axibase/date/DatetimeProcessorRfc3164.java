package fr.loghub.com.axibase.date;

import java.text.DateFormatSymbols;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fr.loghub.com.axibase.date.DatetimeProcessorUtil.adjustPossiblyNegative;
import static fr.loghub.com.axibase.date.DatetimeProcessorUtil.appendNumberWithFixedPositions;

public class DatetimeProcessorRfc3164 implements DatetimeProcessor {

    private final int dayLength;
    private final boolean withYear;
    private final int fractions;
    private final Locale locale;
    private final ZoneId zoneId;
    private final Map<String, Integer> monthsMapping;
    private final String[] shortMonths;
    private final AppendOffset zoneOffsetType;

    DatetimeProcessorRfc3164(int dayLength, boolean withYear, int fractions, AppendOffset zoneOffsetType) {
        this(dayLength, withYear, fractions, Locale.getDefault(), ZoneId.systemDefault(), zoneOffsetType);
    }

    private DatetimeProcessorRfc3164(int dayLength, boolean withYear, int fractions, Locale locale, ZoneId zoneId, AppendOffset zoneOffsetType) {
        this.dayLength = dayLength;
        this.fractions = fractions;
        this.withYear = withYear;
        this.locale = locale;
        this.zoneId = zoneId;
        this.zoneOffsetType = zoneOffsetType;
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        this.shortMonths = symbols.getShortMonths();
        String[] monthsSymbols = symbols.getShortMonths();
        monthsMapping = IntStream.range(0, monthsSymbols.length).mapToObj(i -> Map.entry(monthsSymbols[i].toUpperCase(locale), i + 1))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Instant parseInstant(String datetime) {
        return parse(datetime).toInstant();
    }

    @Override
    public ZonedDateTime parse(String datetime) {
        ParsingContext context = new ParsingContext(datetime);
        //Skip space
        context.skipSpaces();
        String monthName = context.findWord().toUpperCase(locale);
        Integer month = monthsMapping.get(monthName);
        if (month == null) {
            throw new DateTimeParseException("Invalid month name", datetime, context.offset);
        }
        context.skipSpaces();
        int day = context.parseInt(2);
        context.skipSpaces();
        int year;
        if (withYear) {
            year = context.parseInt(-1);
            context.skipSpaces();
        } else {
            year = LocalDateTime.now().getYear();
        }
        int hour = context.parseInt(2);
        context.checkOffset(':');
        int minutes = context.parseInt(2);
        context.checkOffset(':');
        int seconds = context.parseInt(2);
        int nanos = context.parseNano();
        context.skipSpaces();
        String zoneInfo = context.findWord();
        ZoneId parsedZoneId = this.zoneId;
        if (! zoneInfo.isEmpty()) {
            try {
                parsedZoneId = ZoneId.of(zoneInfo).normalized();
            } catch (DateTimeException ex) {
                throw new DateTimeParseException(ex.getMessage(), datetime, context.offset);
            }
        }
        return ZonedDateTime.of(year, month, day, hour, minutes, seconds, nanos, parsedZoneId);
    }

    @Override
    public String print(Instant timestamp) {
        return print(timestamp.atZone(zoneId));
    }

    @Override
    public String print(ZonedDateTime zonedDateTime) {
        StringBuilder formatted = new StringBuilder();
        formatted.append(shortMonths[zonedDateTime.getMonthValue() -1]).append(" ");
        int day = zonedDateTime.getDayOfMonth();
        formatted.append((day <= 9 && dayLength == 2) ? "0" : "").append(day);
        if (withYear) {
            formatted.append(" ");
            adjustPossiblyNegative(formatted, zonedDateTime.getYear(), 4);
        }
        formatted.append(" ");
        appendNumberWithFixedPositions(formatted, zonedDateTime.getHour(), 2).append(':');
        appendNumberWithFixedPositions(formatted, zonedDateTime.getMinute(), 2).append(':');
        appendNumberWithFixedPositions(formatted, zonedDateTime.getSecond(), 2);
        DatetimeProcessorUtil.printSubSeconds(fractions, zonedDateTime::getNano, formatted);
        if (zoneOffsetType != null) {
            formatted.append(" ");
            zoneOffsetType.append(formatted, zonedDateTime.getOffset(), zonedDateTime.toInstant());
        }
        return formatted.toString();
    }

    @Override
    public DatetimeProcessor withLocale(Locale locale) {
        return new DatetimeProcessorRfc3164(this.dayLength, this.withYear, this.fractions, locale, this.zoneId, zoneOffsetType);
    }

    @Override
    public DatetimeProcessor withDefaultZone(ZoneId zoneId) {
        return new DatetimeProcessorRfc3164(this.dayLength, this.withYear, this.fractions, this.locale, zoneId, zoneOffsetType);
    }

}
