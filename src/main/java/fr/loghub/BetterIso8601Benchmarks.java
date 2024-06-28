package fr.loghub;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.util.Statistics;

import ethanjones.boxplot.BoxPlot;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class BetterIso8601Benchmarks {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless","true");
        OptionParser parser = new OptionParser("f");
        OptionSpec<String> svgNameOption = parser.accepts("s").withOptionalArg().ofType(String.class);
        OptionSpec<String> testsOption = parser.accepts("t").withOptionalArg().ofType(String.class);
        OptionSpec<String> packagesNameOption = parser.accepts("p").withOptionalArg().ofType(String.class).defaultsTo("fr.loghub.scanners");
        OptionSet options = parser.parse(args);
        options.nonOptionArguments();

        long warmupTime;
        int warmupIterations;
        long measurementTime;
        int measurementIterations;
        int forks;
        if(options.has("f")) {
            warmupTime = 1;
            warmupIterations = 1;
            measurementTime = 1;
            measurementIterations = 1;
            forks = 1;
        } else {
            warmupTime = 1;
            warmupIterations = 10;
            measurementTime = 6;
            measurementIterations = 10;
            forks = 2;
        }
        String svgName = options.valueOf(svgNameOption);
        List<String> tests = options.valuesOf(testsOption);
        String packageName = options.valueOf(packagesNameOption);
        ChainedOptionsBuilder builder = new OptionsBuilder()
                        .shouldFailOnError(false)
                        .warmupIterations(warmupIterations)
                        .warmupTime(TimeValue.seconds(warmupTime))
                        .measurementIterations(measurementIterations)
                        .measurementTime(TimeValue.seconds(measurementTime))
                        .shouldDoGC(true)
                        .forks(forks);
        
        if (tests.size() == 0) {
            String packageRegex;
            if ("fr.loghub.scanners".equals(packageName)) {
                packageRegex = "fr\\.loghub\\.scanners\\.*";
            } else if ("fr.loghub.isosubtests".equals(packageName)) {
                packageRegex = "fr\\.loghub\\.isosubtests\\.*";
            } else {
                throw new IllegalArgumentException(packageName);
            }
            builder.include(packageRegex);
        } else {
            tests.stream().map(t -> packageName + "." + t).forEach(builder::include);
        }

        Options opt = builder.build();

        Collection<RunResult> results = new Runner(opt).run();
        
        if (svgName != null) {
            BoxPlot[] plots = new BoxPlot[results.size()];
            int bpRank=0;
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            for (RunResult rr: results) {
                System.out.println("******");
                String name = rr.getParams().getBenchmark().replace("fr.loghub.scanners.", "").replace(".scan", "");
                System.out.println("    Benchmark: " + rr.getParams().getBenchmark());
                System.out.println("    generatedBenchmark: " + rr.getParams().generatedBenchmark());
                Statistics stats = rr.getPrimaryResult().getStatistics();
                System.out.println("    min; " + stats.getMin());
                System.out.println("    %25; " + stats.getPercentile(25));
                System.out.println("    %50; " + stats.getPercentile(50));
                System.out.println("    %75; " + stats.getPercentile(75));
                System.out.println("    max; " + stats.getMax());

                BoxPlot bp = new BoxPlot(name);
                bp.q1 = stats.getPercentile(25);
                bp.q2 = stats.getPercentile(50);
                bp.q3 = stats.getPercentile(75);
                bp.low = stats.getMin();
                bp.high = stats.getPercentile(95);
                bp.outliers = new double[]{};
                plots[bpRank++] = bp;
                max = Math.max(max, (int) bp.high);
                min = Math.min(min, (int)stats.getMin());
                //            BenchmarkResult br = rr.getAggregatedResult();
                //            System.out.println("Benchmark: " + br.getParams().getBenchmark());
                //            System.out.println("generatedBenchmark: " + br.getParams().generatedBenchmark());
                //            Statistics stats = br.getPrimaryResult().getStatistics();
                //            System.out.println("min; " + stats.getMin());
                //            System.out.println("%25; " + stats.getPercentile(25));
                //            System.out.println("%50; " + stats.getPercentile(50));
                //            System.out.println("%75; " + stats.getPercentile(75));
                //            System.out.println("max; " + stats.getMax());
                //
                //            Statistics prStats = rr.getAggregatedResult().getPrimaryResult().getStatistics();
            }
            System.out.format("%s min=%s max=%s %s %s%n", "DateParsers", 0, max, "ns", Arrays.toString(plots));
            BoxPlot.generate(svgName, 0, max, max / 5, 0.25, "ns ", "", plots);
        }
    }

}
