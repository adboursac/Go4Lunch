package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapsPhoto {

    @SerializedName("height")
    @Expose
    private final Integer height;

    @SerializedName("width")
    @Expose
    private final Integer width;

    @SerializedName("photo_reference")
    @Expose
    private final String photo_reference;

    public MapsPhoto(Integer height, Integer width, List<String> html_attributions, String photo_reference) {
        this.height = height;
        this.width = width;
        this.photo_reference = photo_reference;
    }

    public String getPhotoReference() {
        return photo_reference;
    }
}
