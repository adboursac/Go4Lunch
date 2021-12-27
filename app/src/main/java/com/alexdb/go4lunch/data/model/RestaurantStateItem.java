package com.alexdb.go4lunch.data.model;

import android.location.Location;

public class RestaurantStateItem {
    private String placeId;
    private String name;
    private String openStatus;
    private String address;
    private Location location;
    private String distance;
    private String photoUrl;

    public RestaurantStateItem(String placeId, String name, String openStatus, String address, Location location, String distance, String photoUrl) {
        this.placeId = placeId;
        this.name = name;
        this.openStatus = openStatus;
        this.address = address;
        this.location = location;
        this.distance = distance;
        this.photoUrl = photoUrl;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
