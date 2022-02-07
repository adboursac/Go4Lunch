package com.alexdb.go4lunch.data.model.maps;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapsPlace {

    @SerializedName("place_id")
    @Expose
    private final String placeId;

    @SerializedName("name")
    @Expose
    private final String name;

    @SerializedName("vicinity")
    @Expose
    private final String vicinity;

    @SerializedName("geometry")
    @Expose
    private final MapsGeometry geometry;

    @SerializedName("opening_hours")
    @Expose
    private final MapsOpeningHours opening_hours;

    @SerializedName("rating")
    @Expose
    private Float rating;

    @SerializedName("photos")
    @Expose
    private final List<MapsPhoto> photos;

    public MapsPlace(String placeId, String name, String vicinity, MapsGeometry geometry, MapsOpeningHours opening_hours, Float rating, List<MapsPhoto> photos) {
        this.placeId = placeId;
        this.name = name;
        this.vicinity = vicinity;
        this.geometry = geometry;
        this.opening_hours = opening_hours;
        this.rating = rating;
        this.photos = photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public MapsGeometry getGeometry() { return geometry; }

    public Location getLocation() {
        Location location = new Location("");
        location.setLatitude(geometry.getLocation().getLat());
        location.setLongitude(geometry.getLocation().getLng());
        return location;
    }

    public Float getRating() {
        return rating;
    }

    public List<MapsPhoto> getPhotos() { return photos; }

    public String getFirstPhotoReference() {
        if (photos == null) return null;
        return photos.get(0).getPhotoReference();
    }

    public MapsOpeningHours getOpening_hours() {
        return opening_hours;
    }

    public boolean hasSameId (MapsPlace place) {
        return this.getPlaceId().contentEquals(place.getPlaceId());
    }

    public boolean hasSameId (List<MapsPlace> list) {
        for (MapsPlace p : list) {
            if (hasSameId(p)) return true;
        }
        return false;
    }
}
