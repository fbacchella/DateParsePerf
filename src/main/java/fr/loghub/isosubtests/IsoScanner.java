package fr.loghub.isosubtests;

import java.time.LocalDateTime;
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
public abstract class IsoScanner {

    protected interface ScannerRunner {
        LocalDateTime scan(String toScan) throws Exception;
    }

    private static final DateTimeFormatter ISO_FORMATTER_JODA = ISODateTimeFormat.dateTime().withZoneUTC();
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private long sourceTimestamp;
    private String date;
    private ScannerRunner scanner;

    @Setup
    public void setup() {
        sourceTimestamp = getSourceTimestamp();
        date = getToParse(sourceTimestamp);
        scanner = getScanner();
    }

    protected abstract ScannerRunner getScanner();

    protected long getSourceTimestamp() {
        return random.nextLong(System.currentTimeMillis());
    }

    public String getToParse(long timestamp) {
        return ISO_FORMATTER_JODA.print(timestamp);
    }

    @Benchmark
    public void scan(Blackhole bh) throws Exception {
        LocalDateTime result = scanner.scan(date);
        bh.consume(result);
    }

}
