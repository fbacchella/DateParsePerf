package fr.loghub.scanners;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import fr.loghub.Scanner;

public class Joda extends Scanner {

    @Override
    protected ScannerRunner getScanner() {
        DateTimeFormatter dtf = ISODateTimeFormat.dateTime();
        return s -> dtf.parseMillis(s);
    }

}
