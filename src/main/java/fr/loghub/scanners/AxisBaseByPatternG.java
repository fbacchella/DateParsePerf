package fr.loghub.scanners;

import com.axibase.date.DatetimeProcessor;
import com.axibase.date.PatternResolver;

import fr.loghub.Scanner;

public class AxisBaseByPatternG extends Scanner {

    public static final String PATTERN = "MMM  d yyyy HH:mm:ss zzz";

    @Override
    protected ScannerRunner getScanner() {
        DatetimeProcessor dtp = PatternResolver.createNewFormatter(PATTERN);
        return dtp::parseMillis;
    }

    @Override
    public String getToParse(long timestamp) {
        return PatternResolver.createNewFormatter(PATTERN).print(timestamp);
    }

}
