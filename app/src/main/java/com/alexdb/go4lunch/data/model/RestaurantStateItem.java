package com.alexdb.go4lunch.data.model;

import android.location.Location;

public class RestaurantStateItem {
    private String placeId;
    private String name;
    private String openStatus;
    private boolean closingSoon;
    private String address;
    private Location location;
    private int distance;
    private Float rating;
    private String photoUrl;
    private int workmatesAmount;

    public RestaurantStateItem(String placeId, String name, String openStatus, boolean closingSoon, String address, Location location, int distance, Float rating, String photoUrl, int workmatesAmount) {
        this.placeId = placeId;
        this.name = name;
        this.openStatus = openStatus;
        this.closingSoon = closingSoon;
        this.address = address;
        this.location = location;
        this.distance = distance;
        this.rating = rating;
        this.photoUrl = photoUrl;
        this.workmatesAmount = workmatesAmount;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenStatus() {
        return openStatus;
    }

    public boolean isClosingSoon() { return closingSoon; }

    public String getAddress() {
        return address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getDistance() { return distance; }

    public Float getRating() {
        if (rating == null) return 0F;
        return rating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getWorkmatesAmount() { return workmatesAmount; }

}
