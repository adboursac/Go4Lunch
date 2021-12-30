package com.alexdb.go4lunch.data.model;

public class RestaurantDetailsStateItem {

    private String placeId;
    private String name;
    private String openStatus;
    private String website;
    private String phoneNumber;
    private String address;
    private String photoUrl;

    public RestaurantDetailsStateItem(String placeId, String name, String openStatus, String website, String phoneNumber, String address, String photoUrl) {
        this.placeId = placeId;
        this.name = name;
        this.openStatus = openStatus;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.photoUrl = photoUrl;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public String getOpenStatus() {
        return openStatus;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}

