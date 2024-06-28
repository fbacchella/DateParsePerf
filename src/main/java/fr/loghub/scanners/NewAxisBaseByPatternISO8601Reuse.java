package fr.loghub.scanners;

import java.time.Instant;

import fr.loghub.com.axibase.date.PatternResolver;

import fr.loghub.Scanner;
import fr.loghub.com.axibase.date.DatetimeProcessor;
import fr.loghub.com.axibase.date.DatetimeProcessorIso8601ReuseContext;

public class NewAxisBaseByPatternISO8601Reuse extends Scanner {

    public static final String PATTERN = "yyyy-MM-ddTHH:mm:ss.SSSXXX";

    @Override
    protected ScannerRunner getScanner() {
        DatetimeProcessor dtp = new DatetimeProcessorIso8601ReuseContext(3, PatternResolver.resolveZoneOffset("XXX"), 'T');
        return s -> dtp.parseInstant(s).toEpochMilli();
    }

    @Override
    public String getToParse(long timestamp) {
        return DatetimeProcessor.of(PATTERN).print(Instant.ofEpochMilli(timestamp));
    }

}
