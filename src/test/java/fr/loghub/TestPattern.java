package fr.loghub;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Test;

import com.axibase.date.DatetimeProcessor;
import com.axibase.date.OnMissingDateComponentAction;
import com.axibase.date.PatternResolver;

public class TestPattern {

    static {
        System.setProperty("cache.formatters.maximum.size", "0");
    }

    private Date parse(String format, String toparse) {
        DatetimeProcessor dtp = PatternResolver.createNewFormatter(format);
        System.out.println(dtp);
        return new Date(dtp.parse(toparse).toInstant().toEpochMilli());
    }

    private Instant parseInstant(String format, String toparse) {
        DatetimeProcessor dtp = PatternResolver.createNewFormatter(format);
        return dtp.parse(toparse).toInstant();
    }

    private Date parse(String format, String toparse, ZoneId zoneId) {
        DatetimeProcessor dtp = PatternResolver.createNewFormatter(format, zoneId);
        return new Date(dtp.parse(toparse).toInstant().toEpochMilli());
    }

    @Test
    public void testISO_1() {
        Instant date = parseInstant("iso", "1970-01-01T00:00:00.000001+01:00");
        Assert.assertEquals("date not parsed", -3600, date.getEpochSecond());
        Assert.assertEquals("date not parsed", 1000, date.getNano());
    }

    @Test
    public void testISO_2() {
        Instant date = parseInstant("iso", "1970-01-01T00:00:00.001+01:00");
        Assert.assertEquals("date not parsed", -3600, date.getEpochSecond());
        Assert.assertEquals("date not parsed", 1000000, date.getNano());
    }

    @Test
    public void testISO_3() {
        Instant date = parseInstant("iso", "1970-01-01T00:00:00.000+0100");
        Assert.assertEquals("date not parsed", -3600, date.getEpochSecond());
        Assert.assertEquals("date not parsed", 0, date.getNano());
    }

    @Test
    public void testISO_4() {
        Instant date = parseInstant("iso", "1970-01-01T00:00:00.000Z");
        Assert.assertEquals("date not parsed", 0, date.getEpochSecond());
        Assert.assertEquals("date not parsed", 0, date.getNano());
    }

    @Test
    public void test2() {
        Date date = parse("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", "1970-01-01T00:00:00.000000+01:00");
        Assert.assertEquals("date not parsed", -3600000, date.getTime());
    }


    @Test
    public void test3() {
        Date date = parse("yyyy-MM-dd'T'HH:m:ss", "1970-01-01T00:00:00", ZoneId.of("Z"));
        Assert.assertEquals("date not parsed", 0L, date.getTime());
    }

    @Test
    public void test4() {
        Date date = parse("MMM dd yyyy HH:mm:ss zzzz", "Dec 05 2009 16:25:53 Central European Time", ZoneId.of("Z"));
        Assert.assertEquals("date not parsed", 1260026753000L, date.getTime());
    }

    @Test
    public void testOther() {
        Instant date = parseInstant("MMM d HH:mm:ss zzz", "Jul 13 12:50:23 CEST");
        System.out.println(date);
    }

    @Test
    public void test5() {
        Date date1 = parse("MMM d HH:mm:ss zzzz", "Dec 05 16:25:53 Central European Time", ZoneId.of("Z"));
        System.out.println(date1);
        TemporalAccessor date2 = java.time.format.DateTimeFormatter.ofPattern("MMM d HH:mm:ss zzzz", Locale.ROOT).parse("Dec 05 16:25:53 Central European Time");
        System.out.println(date2);
        //Assert.assertEquals("date not parsed", 0L, date.getTime());
    }

    //
    //    @Test
    //    public void testIncomplete() throws ProcessorException {
    //        DateParser parse = new DateParser();
    //        parse.setPattern("MMM dd HH:mm:ss");
    //        parse.setTimezone("Z");
    //        parse.setField(new String[] {"field"});
    //        Assert.assertTrue(parse.configure(new Properties(Collections.emptyMap())));
    //        Event event = Tools.getEvent();
    //        event.put("field", "Jul 26 16:40:22");
    //        parse.process(event);
    //        Date date = (Date) event.get("field");
    //        OffsetDateTime t = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of("GMT"));
    //        int year = OffsetDateTime.now().get(ChronoField.YEAR);
    //        Assert.assertEquals("date not parsed", year, t.getLong(ChronoField.YEAR));
    //    }
    //
    @Test
    public void testAgain() {
        Date date = parse("yyyy-MM-dd'T'HH:m:ss.SSSxx", "2016-08-04T18:57:37.238+0000", ZoneId.of("CET"));
        //Assert.assertEquals("date not parsed", 0L, date.getTime());
        OffsetDateTime t = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of("GMT"));
        Assert.assertEquals("date not parsed", 18, t.getLong(ChronoField.HOUR_OF_DAY));
    }

    @Test
    public void testAgain2() {
        Date date = parse("MMM dd HH:mm:ss.SSS", "Jul 26 16:40:22.238", ZoneId.of("CET"));
        OffsetDateTime t = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of("GMT"));
        Assert.assertEquals("date not parsed", 15, t.getLong(ChronoField.HOUR_OF_DAY));
    }

    @Test
    public void testEmblem() {
        Locale.setDefault(Locale.forLanguageTag("en"));
        String dateString = "Jul 15 11:20:01 CEST";
        //TimeZone.setDefault(TimeZone.getTimeZone("CET"));
        //PatternResolver.createNewFormatter("MMM d HH:mm:ss zzz");
        DatetimeProcessor dtp = PatternResolver.createNewFormatter("MMM d HH:mm:ss zzz", ZoneId.of("CET"), OnMissingDateComponentAction.SET_CURRENT).withLocale(Locale.forLanguageTag("en"));
        System.out.println(dtp.parse(dateString));
    }


    //    @Test
    //    public void testBadPattern() throws ProcessorException {
    //        DateParser parse = new DateParser();
    //        parse.setPattern("failed");
    //        Assert.assertFalse(parse.configure(new Properties(Collections.emptyMap())));
    //    }
    //
    private void checkTZ(String tz, String tzFormat, int expectedHour) {
        String parseformat = "yyyy MMM dd HH:mm:ss.SSS " + tzFormat;
        String parsedDate = "2019 Sep 18 07:53:09.504 " + tz;
        try {
            DatetimeProcessor dtp = PatternResolver.createNewFormatter(parseformat, ZoneId.of("America/Los_Angeles"));
            ZonedDateTime zdt = dtp.parse(parsedDate);
            System.out.format("%s %s %s\n", parseformat, parsedDate, zdt);
            OffsetDateTime t = OffsetDateTime.ofInstant(zdt.toInstant(), ZoneId.of("GMT"));
            Assert.assertEquals("failed to parse " + parsedDate + " with " + parseformat, expectedHour, t.getLong(ChronoField.HOUR_OF_DAY));
        } catch (Exception e) {
            System.out.format("%s %s: %s\n", parseformat, parsedDate, e.getMessage());
        }
        //Assert.assertEquals("failed to parse " + parsedDate + " with " + parseformat, expectedHour, zdt.getLong(ChronoField.HOUR_OF_DAY));
        //        
        //        DateParser parse = new DateParser();
        //        parse.setPatterns(new String[] {parseformat});
        //        parse.setTimezone("America/Los_Angeles");
        //        parse.setField(new String[] {"field"});
        //        Assert.assertTrue(parse.configure(new Properties(Collections.emptyMap())));
        //        Event event = Tools.getEvent();
        //        event.put("field", parsedDate);
        //        Assert.assertTrue("failed to parse " + parsedDate + " with " + parseformat, parse.process(event));
        //        Date date = (Date) event.get("field");
        //        OffsetDateTime t = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of("GMT"));
    }

    @Test
    public void testTZFormats() {
        checkTZ("UTC", "zzz", 7);
        checkTZ("Z", "zzz", 7);
        checkTZ("GMT", "zzz", 7);
        checkTZ("CET", "zzz", 5);
        checkTZ("Europe/Paris", "VV", 5);
        checkTZ("+0200", "Z", 5);
        checkTZ("+02:00", "xxx", 5);
        checkTZ("", "", 14);
    }

    @Test
    public void parseOldJava() throws ParseException {
        long sourceTimestamp = ThreadLocalRandom.current().nextLong(System.currentTimeMillis());
        String date = ISODateTimeFormat.dateTime().withZoneUTC().print(sourceTimestamp);

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        System.out.println(sdf.parse(date));
    }


}
