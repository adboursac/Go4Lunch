package com.alexdb.go4lunch.data.model;

import android.location.Location;

public class RestaurantStateItem {
    private String placeId;
    private String name;
    private String openStatus;
    private String address;
    private Location location;
    private int distance;
    private Float rating;
    private String photoUrl;
    private int workmatesAmount;

    public RestaurantStateItem(String placeId, String name, String openStatus, String address, Location location, int distance, Float rating, String photoUrl, int workmatesAmount) {
        this.placeId = placeId;
        this.name = name;
        this.openStatus = openStatus;
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

    public void setOpenStatus(String openStatus) {
        this.openStatus = openStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getDistance() { return distance; }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getWorkmatesAmount() { return workmatesAmount; }

    public void setWorkmatesAmount(int workmatesAmount) { this.workmatesAmount = workmatesAmount; }
}
