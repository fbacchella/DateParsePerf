package fr.loghub.scanners;

import java.time.Instant;

import fr.loghub.com.axibase.date.DatetimeProcessor;

import fr.loghub.Scanner;

public class NewAxisBaseByPatternRFC822 extends Scanner {

    public static final String PATTERN = "eee, d MMM yyyy HH:mm:ss Z";

    @Override
    protected ScannerRunner getScanner() {
        DatetimeProcessor dtp = DatetimeProcessor.of(PATTERN);
        return (s) -> dtp.parseInstant(s).toEpochMilli();
    }

    @Override
    protected long getSourceTimestamp() {
        // A second precision for the time stamp
        return Instant.ofEpochMilli(super.getSourceTimestamp()).getEpochSecond() * 1000;
    }

    @Override
    public String getToParse(long timestamp) {
        return DatetimeProcessor.of(PATTERN).print(Instant.ofEpochMilli(timestamp));
    }

}
