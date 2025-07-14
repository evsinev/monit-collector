package com.acme.monit.collector.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

public class Dates {

    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMAT = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    );

    private static final ThreadLocal<SimpleDateFormat> TIME_FORMAT = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("HH:mm:ss")
    );

    private static final ThreadLocal<SimpleDateFormat> MONTH_TIME_FORMAT = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("MMM dd HH:mm:ss", Locale.ENGLISH)
    );


    public static String formatDateTime(Date aDate) {
        return formatDateTime(aDate, LocalDateTime.now(ZoneId.systemDefault()));
    }

    public static String formatDateTime(Date aDate, LocalDateTime now) {
        if(aDate == null) {
            return null;
        }

        LocalDateTime instant = LocalDateTime.ofInstant(aDate.toInstant(), ZoneId.systemDefault());

        switch (getSameDay(instant, now)) {
            case SAME_YEAR: return MONTH_TIME_FORMAT.get().format(aDate);
            case SAME_DAY:  return TIME_FORMAT.get().format(aDate);
            default:        return DATE_TIME_FORMAT.get().format(aDate);
        }
    }


    private static DateEqualType getSameDay(TemporalAccessor aLeft, TemporalAccessor aRight) {
        if(aLeft.get(ChronoField.DAY_OF_YEAR) == aRight.get(ChronoField.DAY_OF_YEAR)) {
            return DateEqualType.SAME_DAY;
        }

        if(aLeft.get(ChronoField.YEAR) == aRight.get(ChronoField.YEAR)) {
            return DateEqualType.SAME_YEAR;
        }

        return DateEqualType.OTHER;
    }
    private enum DateEqualType {
        SAME_DAY, SAME_YEAR, OTHER
    }
}
