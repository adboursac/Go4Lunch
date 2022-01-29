package com.alexdb.go4lunch.ui.helper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateTimeHelper {

    public static DateTimeFormatter getTimeFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm");
    }

    public static String timeToString(LocalTime time) {
        return time.format(getTimeFormatter());
    }

    public static LocalTime stringToTime(String timeString, LocalTime defaultTime) {
        LocalTime time;
        try {
            time = LocalTime.parse(timeString, getTimeFormatter());
        } catch (Exception e) {
            time = defaultTime;
        }
        return time;
    }

    public static boolean isToday(Date date) {
        if (date == null) return false;
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.isEqual(LocalDate.now());
    }
}
