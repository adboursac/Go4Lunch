package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapsOpeningHours {

    @SerializedName("open_now")
    @Expose
    private final Boolean open_now;

    @SerializedName("periods")
    @Expose
    private final List<PlaceOpeningHoursPeriod> periods;

    public MapsOpeningHours(Boolean open_now, List<PlaceOpeningHoursPeriod> periods) {
        this.open_now = open_now;
        this.periods = periods;
    }

    public Boolean getOpen_now() {
        return open_now;
    }

    public List<PlaceOpeningHoursPeriod> getPeriods() { return periods; }
}
