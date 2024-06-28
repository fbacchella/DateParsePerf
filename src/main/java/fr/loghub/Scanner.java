package fr.loghub;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public abstract class Scanner {
    
    static {
        Locale.setDefault(Locale.ENGLISH);
    }
    
    protected interface ScannerRunner {
        long scan(String toScan) throws Exception;
    }

    private static final DateTimeFormatter ISO_FORMATTER_JODA = ISODateTimeFormat.dateTime().withZoneUTC();
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private long sourceTimestamp;
    private String date;
    private ScannerRunner scanner;

    protected abstract ScannerRunner getScanner();

    @Setup
    public void setup() {
        sourceTimestamp = getSourceTimestamp();
        date = getToParse(sourceTimestamp);
        scanner = getScanner();
    }

    protected long getSourceTimestamp() {
        return random.nextLong(System.currentTimeMillis());
    }

    public String getToParse(long timestamp) {
        return ISO_FORMATTER_JODA.print(timestamp);
    }

    public void scan() throws Exception {
        long result = scanner.scan(date);
        if (result != sourceTimestamp) {
            fail(sourceTimestamp, result, date);
        }
    }

    @Benchmark
    public void scan(Blackhole bh) throws Exception {
        long result = scanner.scan(date);
        bh.consume(result);
    }

    private void fail(long expected, long result, String input) {
        throw new IllegalStateException("Expected: " + expected + " but was " + result + " for input " + input);
    }

}
