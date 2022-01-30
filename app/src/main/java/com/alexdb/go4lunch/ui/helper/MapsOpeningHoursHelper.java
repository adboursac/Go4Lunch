package com.alexdb.go4lunch.ui.helper;

import android.content.res.Resources;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.maps.MapsOpeningHours;
import com.alexdb.go4lunch.data.model.maps.PlaceOpeningHoursPeriod;

import java.time.LocalTime;
import java.util.List;

/**
 * Class that provide static methods to handle Google MapsOpeningHours objects
 */
public class MapsOpeningHoursHelper {

    public static final int CLOSING_SOON_DELAY = 15;

    /**
     * Generate place Opening String that describes given Google MapsOpeningHours instance from Places api.
     * Updates given boolean with closingSoon status
     *
     * @param openingHours Google MapsOpeningHours from Place api
     * @param closingSoon  Array containing closingSoon boolean that will be updated by this method
     * @param resources    required resources to generate Opening strings
     * @return String that describe given Google MapsOpeningHours instance
     */
    public static String generateOpeningString(MapsOpeningHours openingHours, boolean[] closingSoon, Resources resources) {
        if (openingHours == null || openingHours.getOpen_now() == null)
            return resources.getString(R.string.restaurant_no_schedule);
        else if (openingHours.getPeriods() == null) {
            return openingHours.getOpen_now() ? resources.getString(R.string.restaurant_open)
                    : resources.getString(R.string.restaurant_closed);
        }
        return periodsToString(openingHours.getPeriods(), openingHours.getOpen_now(), closingSoon, resources);
    }

    private static String periodsToString(List<PlaceOpeningHoursPeriod> periods, boolean openNow, boolean[] closingSoon, Resources resources) {
        // As we want to split process into separated methods to handle openingHours and return multiples data
        // We provide Arrays that will be updated by updateAccuratePeriodData method
        LocalTime[] closeAtTime = {null};
        LocalTime[] openAtTime = {null};
        updatePeriodsData(periods,
                closeAtTime,
                openAtTime,
                closingSoon);

        // if place is currently open
        if (closeAtTime[0] != null) {
            if (closingSoon[0]) {
                return String.format(resources.getString(R.string.restaurant_closing_soon),
                        LocalDateTimeHelper.timeToString(closeAtTime[0]));
            }
            return String.format(resources.getString(R.string.restaurant_open_until),
                    LocalDateTimeHelper.timeToString(closeAtTime[0]));
        }
        // if place is closed but we have an opening time
        else if (openAtTime[0] != null ) {
            return String.format(resources.getString(R.string.restaurant_opens_at),
                    LocalDateTimeHelper.timeToString(openAtTime[0]));
        }
        // We didn't found any accurate periods, so we use openNow value
        return openNow ? resources.getString(R.string.restaurant_open)
                    : resources.getString(R.string.restaurant_closed);
    }

    private static void updatePeriodsData(List<PlaceOpeningHoursPeriod> periods,
                                          LocalTime[] closeAtTime,
                                          LocalTime[] openAtTime,
                                          boolean[] closingSoon) {
        for (PlaceOpeningHoursPeriod period : periods) {
            LocalTime now = LocalTime.now();
            // Select periods that occurs today
            if (LocalDateTimeHelper.isToday(period.getOpen().getDay())) {
                // Convert periods opening and closing time Strings to localTime instance
                LocalTime openingTime = LocalDateTimeHelper.googlePlacesStringToTime(period.getOpen().getTime());
                LocalTime closingTime = LocalDateTimeHelper.googlePlacesStringToTime(period.getClose().getTime());
                // if the current period range contains the time we are now
                if (openingTime.isBefore(now) && closingTime.isAfter(now)) {
                    // We take this result as we want to display a 'closing at' message
                    closeAtTime[0] = closingTime;
                    // If it's closing soon
                    if (closingTime.minusMinutes(CLOSING_SOON_DELAY).isBefore(now)) {
                        // we set value to true
                        closingSoon[0] = true;
                    }
                    // We don't need to search anymore
                    return;
                } else if (openingTime.isAfter(now)) {
                    // If we already found a result for openAtTime
                    if (openAtTime[0] != null) {
                        // but this opening time is closer, we replace previous result
                        if (openingTime.isBefore(openAtTime[0])) openAtTime[0] = openingTime;
                    }
                    // we didn't had any result yet so we add it.
                    else openAtTime[0] = openingTime;
                }
            }
        }
    }
}
