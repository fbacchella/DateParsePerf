package fr.loghub.scanners;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.axibase.date.DatetimeProcessor;
import com.axibase.date.PatternResolver;

import fr.loghub.Scanner;

public class AxisBaseByPatternM extends Scanner {

    public static final String PATTERN = "MMM d yyyy HH:mm:ss zzzz";

    @Override
    protected ScannerRunner getScanner() {
        DatetimeProcessor dtp = PatternResolver.createNewFormatter(PATTERN);
        return dtp::parseMillis;
    }

    @Override
    public String getToParse(long timestamp) {
        ZonedDateTime i = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault());
        return PatternResolver.createNewFormatter(PATTERN).print(i);
    }

}
