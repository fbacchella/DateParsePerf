package fr.loghub.scanners;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import com.axibase.date.DatetimeProcessor;
import com.axibase.date.PatternResolver;

import fr.loghub.Scanner;

public class AxisBaseByPatternRFC822 extends Scanner {

    public static final String PATTERN = "eee, d MMM yyyy HH:mm:ss Z";

    @Override
    protected ScannerRunner getScanner() {
        DatetimeProcessor dtp = PatternResolver.createNewFormatter(PATTERN);
        return dtp::parseMillis;
    }

    @Override
    protected long getSourceTimestamp() {
        // A second precision for the time stamp
        return Instant.ofEpochMilli(super.getSourceTimestamp()).getEpochSecond() * 1000;
    }

    @Override
    public String getToParse(long timestamp) {
        return PatternResolver.createNewFormatter(PATTERN).print(timestamp);
    }

}
