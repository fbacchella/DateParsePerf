package fr.loghub.scanners;

import java.time.Instant;

import fr.loghub.Scanner;
import fr.loghub.com.axibase.date.DatetimeProcessor;

public class NewAxisBaseByPatternISO8601 extends Scanner {

    public static final String PATTERN = "yyyy-MM-ddTHH:mm:ss.SSSXXX";

    @Override
    protected ScannerRunner getScanner() {
        DatetimeProcessor dtp = DatetimeProcessor.of(PATTERN);
        return s -> dtp.parseInstant(s).toEpochMilli();
    }

    @Override
    public String getToParse(long timestamp) {
        return DatetimeProcessor.of(PATTERN).print(Instant.ofEpochMilli(timestamp));
    }

}
