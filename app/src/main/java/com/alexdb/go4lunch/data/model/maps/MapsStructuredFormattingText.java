package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapsStructuredFormattingText {

    @SerializedName("main_text")
    @Expose
    private final String main_text;

    @SerializedName("secondary_text")
    @Expose
    private final String secondary_text;

    public MapsStructuredFormattingText(String main_text, String secondary_text) {
        this.main_text = main_text;
        this.secondary_text = secondary_text;
    }

    public String getMain_text() {
        return main_text;
    }

    public String getSecondary_text() { return secondary_text; }
}
