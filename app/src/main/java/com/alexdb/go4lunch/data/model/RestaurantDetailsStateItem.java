package com.alexdb.go4lunch.data.model;

public class RestaurantDetailsStateItem {

    private String placeId;
    private String name;
    private String openStatus;
    private String website;
    private String phoneNumber;
    private String address;
    private Float rating;
    private String photoUrl;
    private boolean booked;
    private boolean liked;

    public RestaurantDetailsStateItem(String placeId, String name, String openStatus, String website, String phoneNumber, String address, Float rating, String photoUrl, boolean booked, boolean liked) {
        this.placeId = placeId;
        this.name = name;
        this.openStatus = openStatus;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.rating = rating;
        this.photoUrl = photoUrl;
        this.booked = booked;
        this.liked = liked;
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

    public Float getRating() {
        return rating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public boolean isBooked() { return booked; }

    public void setBooked(boolean booked) { this.booked = booked; }

    public boolean isLiked() { return liked; }

    public void setLiked(boolean liked) { this.liked = liked; }
}

