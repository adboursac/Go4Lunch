package com.alexdb.go4lunch.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {
    private String uid;
    private String name;
    private String email;
    private String profilePictureUrl;
    private Date bookingDate;
    private String bookingPlaceId;

    public User() {}

    public User(String uid, String name, String email, String profilePictureUrl, Date bookingDate, String bookingPlaceId) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.bookingDate = bookingDate;
        this.bookingPlaceId = bookingPlaceId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    @ServerTimestamp
    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingPlaceId() {
        return bookingPlaceId;
    }

    public void setBookingPlaceId(String bookingPlaceId) {
        this.bookingPlaceId = bookingPlaceId;
    }
}
