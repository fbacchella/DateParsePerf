These benchmarks aim to evaluate the performance gains of different date parsing methods.
They are based on optimizations made by Axibase, but the code is no longer accessible today.
Various improvements have been made, notably the detection of dates in RFC822 or RFC 3164 format.
It can generate svg files.

A quick test run can be launched with

    mvn package
    java -jar target/dateparseperf.jar -f  -s /tmp/dateparseperf.svg

The `-f` argument tells to run a quick run, to test the benchs. With the `-s` argument, it generates an SVG.
The `-t` argument can be used to run only some tests. The `-p` argument specify the package name to search tests.
