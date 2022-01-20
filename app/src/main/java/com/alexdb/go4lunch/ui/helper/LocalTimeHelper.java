package com.alexdb.go4lunch.ui.helper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeHelper {

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
}
