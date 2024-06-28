package fr.loghub.scanners;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import fr.loghub.Scanner;

public class JavaTime extends Scanner {

    @Override
    protected ScannerRunner getScanner() {
        DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ISO_INSTANT;
        return s -> Instant.from(dtf.parse(s)).toEpochMilli();
    }

}
