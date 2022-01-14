package com.alexdb.go4lunch.data.model;

import java.util.ArrayList;
import java.util.List;

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
    private List<User> bookedWorkmates;

    public RestaurantDetailsStateItem(String placeId, String name, String openStatus, String website, String phoneNumber, String address, Float rating, String photoUrl, boolean booked, boolean liked, List<User> bookedWorkmates) {
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
        this.bookedWorkmates = bookedWorkmates;
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
        if (rating == null) return 0F;
        return rating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public boolean isBooked() { return booked; }

    public void setBooked(boolean booked) { this.booked = booked; }

    public boolean isLiked() { return liked; }

    public void setLiked(boolean liked) { this.liked = liked; }

    public List<User> getBookedWorkmates() {
        if (bookedWorkmates == null) return new ArrayList<>();
        return bookedWorkmates; }

    public void setBookedWorkmates(List<User> bookedWorkmates) { this.bookedWorkmates = bookedWorkmates; }
}

