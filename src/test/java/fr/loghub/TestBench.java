package fr.loghub;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import fr.loghub.scanners.ApacheCommons;
import fr.loghub.scanners.AxisBaseByNameIso;
import fr.loghub.scanners.AxisBaseByPatternA;
import fr.loghub.scanners.AxisBaseByPatternB;
import fr.loghub.scanners.AxisBaseByPatternE;
import fr.loghub.scanners.AxisBaseByPatternF;
import fr.loghub.scanners.AxisBaseByPatternG;
import fr.loghub.scanners.AxisBaseByPatternH;
import fr.loghub.scanners.AxisBaseByPatternI;
import fr.loghub.scanners.AxisBaseByPatternISO8601;
import fr.loghub.scanners.AxisBaseByPatternJ;
import fr.loghub.scanners.AxisBaseByPatternK;
import fr.loghub.scanners.AxisBaseByPatternL;
import fr.loghub.scanners.AxisBaseByPatternM;
import fr.loghub.scanners.AxisBaseByPatternN;
import fr.loghub.scanners.AxisBaseByPatternO;
import fr.loghub.scanners.AxisBaseByPatternP;
import fr.loghub.scanners.AxisBaseByPatternQ;
import fr.loghub.scanners.AxisBaseByPatternR;
import fr.loghub.scanners.AxisBaseByPatternRFC822;
import fr.loghub.scanners.AxisBaseByPatternS;
import fr.loghub.scanners.AxisBaseByPatternT;
import fr.loghub.scanners.AxisBaseByPatternU;
import fr.loghub.scanners.AxisBaseByPatternV;
import fr.loghub.scanners.JavaDtfISO8601;
import fr.loghub.scanners.JavaInstant;
import fr.loghub.scanners.JavaTime;
import fr.loghub.scanners.Joda;
import fr.loghub.scanners.NewAxisBaseByPatternISO8601;
import fr.loghub.scanners.NewAxisBaseByPatternISO8601Reuse;
import fr.loghub.scanners.NewAxisBaseByPatternRFC822;
import fr.loghub.scanners.OldJavaISO8601;
import fr.loghub.scanners.JavaDtfRFC822;

public class TestBench {
    
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private void scan(Scanner s) throws Exception {
        s.setup();
        s.scan();
    }

    private void patternScan(char rank, String pattern, Scanner s) throws Exception {
        s.setup();
        long testDate = random.nextLong(System.currentTimeMillis());
        String formatedDate = s.getToParse(testDate);
        Date scanned = new Date(s.getScanner().scan(formatedDate));
        System.out.format("%s: %s -> %s -> %s\n", rank, pattern, formatedDate, scanned);
        //s.scan();
    }

    @Test
    public void parseApacheCommons() throws Exception {
        scan(new ApacheCommons());
    }

    @Test
    public void parseAxibasebByName() throws Exception {
        scan(new AxisBaseByNameIso());
    }

    @Test
    public void parseAxibasebByPatternVariable() throws Exception {
        patternScan('A', AxisBaseByPatternA.PATTERN, new AxisBaseByPatternA());
        patternScan('B', AxisBaseByPatternB.PATTERN, new AxisBaseByPatternB());
        patternScan('E', AxisBaseByPatternE.PATTERN, new AxisBaseByPatternE());
        patternScan('F', AxisBaseByPatternF.PATTERN, new AxisBaseByPatternF());
        patternScan('G', AxisBaseByPatternG.PATTERN, new AxisBaseByPatternG());
        patternScan('H', AxisBaseByPatternH.PATTERN, new AxisBaseByPatternH());
        patternScan('I', AxisBaseByPatternI.PATTERN, new AxisBaseByPatternI());
        patternScan('J', AxisBaseByPatternJ.PATTERN, new AxisBaseByPatternJ());
        patternScan('K', AxisBaseByPatternK.PATTERN, new AxisBaseByPatternK());
        patternScan('L', AxisBaseByPatternL.PATTERN, new AxisBaseByPatternL());
        patternScan('M', AxisBaseByPatternM.PATTERN, new AxisBaseByPatternM()); // Ultra slow
        patternScan('N', AxisBaseByPatternN.PATTERN, new AxisBaseByPatternN());
        patternScan('O', AxisBaseByPatternO.PATTERN, new AxisBaseByPatternO());
        patternScan('P', AxisBaseByPatternP.PATTERN, new AxisBaseByPatternP());
        patternScan('Q', AxisBaseByPatternQ.PATTERN, new AxisBaseByPatternQ());
        patternScan('R', AxisBaseByPatternR.PATTERN, new AxisBaseByPatternR());
        patternScan('S', AxisBaseByPatternS.PATTERN, new AxisBaseByPatternS());
        patternScan('T', AxisBaseByPatternT.PATTERN, new AxisBaseByPatternT());
        patternScan('U', AxisBaseByPatternU.PATTERN, new AxisBaseByPatternU());
        patternScan('V', AxisBaseByPatternV.PATTERN, new AxisBaseByPatternV());
    }
    
    @Test
    public void testM() {
        Locale.setDefault(Locale.ENGLISH);
        DateTimeFormatter dtp = DateTimeFormatter.ofPattern(AxisBaseByPatternM.PATTERN);
        System.out.println(Instant.ofEpochMilli(new Date().getTime()));
        System.out.println(dtp.format(Instant.now().atZone(ZoneId.systemDefault())));
        String formatted = dtp.format(ZonedDateTime.now());
        System.out.println(formatted);
        System.out.println(dtp.parse(formatted));
        //System.out.println(Instant.from(dtp.parse(formatted)));
        System.out.println(dtp.parse("Jul 14 2020 10:36:05 Central European Summer Time"));
        System.out.println(dtp.parse("Dec 24 0001 18:21:51 Central European Time"));
    }

    @Test
    public void parseAxibasebByPatternISO8601() throws Exception {
        scan(new AxisBaseByPatternISO8601());
    }

    @Test
    public void parseAxibasebByPatternRFC822() throws Exception {
        scan(new AxisBaseByPatternRFC822());
    }

    @Test
    public void parseNewAxibasebByPatternRFC822() throws Exception {
        scan(new NewAxisBaseByPatternRFC822());
    }

    @Test
    public void parseNewAxibasebByPatternISO8601() throws Exception {
        scan(new NewAxisBaseByPatternISO8601());
    }

    @Test
    public void parseNewAxibasebByPatternISO8601Reuse() throws Exception {
        scan(new NewAxisBaseByPatternISO8601Reuse());
    }

    @Test
    public void parseJavaDtdRFC822() throws Exception {
        scan(new JavaDtfRFC822());
    }

    @Test
    public void parseJavaDtdISO8601() throws Exception {
        scan(new JavaDtfISO8601());
    }

    @Test
    public void parseJavaInstant() throws Exception {
        scan(new JavaInstant());
    }

    @Test
    public void parseJavaTime() throws Exception {
        scan(new JavaTime());
    }

    @Test
    public void parseJoda() throws Exception {
        scan(new Joda());
    }

   @Test
    public void parseOldJavaISO8601() throws Exception {
       scan(new OldJavaISO8601());
    }

}
