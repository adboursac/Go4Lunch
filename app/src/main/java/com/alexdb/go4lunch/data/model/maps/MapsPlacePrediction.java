package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapsPlacePrediction {

    @SerializedName("place_id")
    @Expose
    private final String place_id;

    @SerializedName("structured_formatting")
    private final MapsStructuredFormattingText structured_formatting;

    public MapsPlacePrediction(String place_id, MapsStructuredFormattingText structured_formatting) {
        this.place_id = place_id;
        this.structured_formatting = structured_formatting;
    }

    public String getPlace_id() {
        return place_id;
    }

    public MapsStructuredFormattingText getStructured_formatting() {
        return structured_formatting;
    }
}
