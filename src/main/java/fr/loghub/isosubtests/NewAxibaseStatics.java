package fr.loghub.isosubtests;

import java.time.LocalDateTime;

import fr.loghub.com.axibase.date.ParsingContext;

public class NewAxibaseStatics extends IsoScanner {

    @Override
    protected ScannerRunner getScanner() {
        return this::run;
    }

    public LocalDateTime run(String date) {
        ParsingContext context = new ParsingContext(date);
        return parseIso8601AsLocalDateTime('T', context);
    }

    private LocalDateTime parseIso8601AsLocalDateTime(char delimiter, ParsingContext context) {
        // extract year
        int year = context.parseInt(-1);
        context.checkOffset('-');

        // extract month
        int month = context.parseInt(2);
        context.checkOffset('-');

        // extract day
        int day = context.parseInt(2);
        context.checkOffset(delimiter);

        // extract hours, minutes, seconds and milliseconds
        int hour = context.parseInt(2);
        context.checkOffset(':');

        int minutes = context.parseInt(2);

        // seconds can be optional
        int seconds;
        if (context.datetime.charAt(context.offset) == ':') {
            context.offset++;
            seconds = context.parseInt(2);
        } else {
            seconds = 0;
        }
        int nanos = context.parseNano();
        return LocalDateTime.of(year, month, day, hour, minutes, seconds, nanos);
    }

}
