package fr.loghub.scanners;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import fr.loghub.Scanner;

public class OldJavaISO8601 extends Scanner {

    @Override
    protected ScannerRunner getScanner() {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return s -> sdf.parse(s).getTime();
    }

}
