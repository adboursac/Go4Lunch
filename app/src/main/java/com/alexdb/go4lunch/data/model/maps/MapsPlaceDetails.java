package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapsPlaceDetails {

    @SerializedName("place_id")
    @Expose
    private final String place_id;

    @SerializedName("name")
    @Expose
    private final String name;

    @SerializedName("opening_hours")
    @Expose
    private final MapsOpeningHours opening_hours;

    @SerializedName("website")
    @Expose
    private final String website;

    @SerializedName("international_phone_number")
    @Expose
    private final String international_phone_number;

    @SerializedName("formatted_address")
    @Expose
    private final String formatted_address;

    @SerializedName("rating")
    private Float rating;

    @SerializedName("photos")
    @Expose
    private final List<MapsPhoto> photos;

    public MapsPlaceDetails(String place_id, String name, MapsOpeningHours opening_hours, String website, String international_phone_number, String formatted_address, Float rating, List<MapsPhoto> photos) {
        this.place_id = place_id;
        this.name = name;
        this.opening_hours = opening_hours;
        this.website = website;
        this.international_phone_number = international_phone_number;
        this.formatted_address = formatted_address;
        this.rating = rating;
        this.photos = photos;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String getName() {
        return name;
    }

    public MapsOpeningHours getOpening_hours() {
        return opening_hours;
    }

    public String getWebsite() {
        return website;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public Float getRating() {
        return rating;
    }

    public String getFirstPhotoReference() {
        if (photos == null) return null;
        return photos.get(0).getPhotoReference();
    }
}
