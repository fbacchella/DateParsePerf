package fr.loghub.scanners;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

import fr.loghub.Scanner;
import fr.loghub.com.axibase.date.DatetimeProcessor;

public class JavaDtfISO8601 extends Scanner {

    public static final String PATTERN = "yyyy-MM-ddTHH:mm:ss.SSSXXX";

    @Override
    protected ScannerRunner getScanner() {
        DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
        return s -> dtf.parse(s).query(Instant::from).toEpochMilli();
    }


    @Override
    public String getToParse(long timestamp) {
        return DatetimeProcessor.of(PATTERN).print(Instant.ofEpochMilli(timestamp));
    }

}
