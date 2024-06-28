package fr.loghub.scanners;

import java.time.Instant;

import fr.loghub.Scanner;

public class JavaInstant extends Scanner {

    @Override
    protected ScannerRunner getScanner() {
        return s -> Instant.parse(s).toEpochMilli();
    }

}
