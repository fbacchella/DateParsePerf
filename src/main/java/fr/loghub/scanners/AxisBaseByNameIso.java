package fr.loghub.scanners;

import com.axibase.date.DatetimeProcessor;
import com.axibase.date.PatternResolver;

import fr.loghub.Scanner;

public class AxisBaseByNameIso extends Scanner {

    @Override
    protected ScannerRunner getScanner() {
        DatetimeProcessor dtp = PatternResolver.createNewFormatter("iso");
        return dtp::parseMillis;
    }

}
