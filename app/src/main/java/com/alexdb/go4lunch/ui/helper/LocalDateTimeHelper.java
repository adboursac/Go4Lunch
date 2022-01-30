package com.alexdb.go4lunch.ui.helper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateTimeHelper {

    public static DateTimeFormatter getDefaultTimeFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm");
    }

    public static DateTimeFormatter getGooglePlacesTimeFormatter() {
        return DateTimeFormatter.ofPattern("HHmm");
    }

    public static String timeToString(LocalTime time) {
        return time.format(getDefaultTimeFormatter());
    }

    public static LocalTime stringToTime(String timeString, LocalTime defaultTime) {
        LocalTime time;
        try {
            time = LocalTime.parse(timeString, getDefaultTimeFormatter());
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

    public static boolean isToday(int dayNumber) {
        int todayInt = LocalDate.now().getDayOfWeek().getValue();
        return todayInt == dayNumber;
    }

    public static LocalTime googlePlacesStringToTime(String timeString) {
        LocalTime time;
        try {
            time = LocalTime.parse(timeString, getGooglePlacesTimeFormatter());
        } catch (Exception e) {
            time = null;
        }
        return time;
    }
}
