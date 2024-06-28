package fr.loghub.scanners;

import org.apache.commons.lang3.time.FastDateFormat;

import fr.loghub.Scanner;

public class ApacheCommons extends Scanner {

    @Override
    protected ScannerRunner getScanner() {
        FastDateFormat dtf = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return s -> dtf.parse(s).getTime();
    }

}
