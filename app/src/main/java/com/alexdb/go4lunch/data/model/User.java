package com.alexdb.go4lunch.data.model;

import android.text.format.DateUtils;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class User {
    private String uid;
    private String name;
    private String email;
    private String profilePictureUrl;
    private Date bookedDate;
    private String bookedPlaceId;
    private String bookedPlaceName;
    private List<String> likedPlaces;

    public User() {}

    public User(String uid, String name, String email, String profilePictureUrl, Date bookedDate, String bookedPlaceId, String bookedPlaceName, List<String> likedPlaces) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.bookedDate = bookedDate;
        this.bookedPlaceId = bookedPlaceId;
        this.bookedPlaceName = bookedPlaceName;
        this.likedPlaces = likedPlaces;
    }

    public User(String uid) {
        this.uid = uid;
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
    public Date getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(Date bookedDate) {
        this.bookedDate = bookedDate;
    }

    public String getBookedPlaceId() {
        return bookedPlaceId;
    }

    public void setBookedPlaceId(String bookedPlaceId) {
        this.bookedPlaceId = bookedPlaceId;
    }

    public String getBookedPlaceName() { return bookedPlaceName; }

    public void setBookedPlaceName(String bookedPlaceName) { this.bookedPlaceName = bookedPlaceName; }

    public boolean hasValidBookingDate() {
        if (bookedDate == null || bookedPlaceId == null) return false;
        return DateUtils.isToday(bookedDate.getTime());
    }

    public boolean hasBookedPlace(String placeId) {
        if (bookedPlaceId == null) return false;
        return ( hasValidBookingDate() && bookedPlaceId.contentEquals(placeId));
    }

    public List<String> getLikedPlaces() {
        return likedPlaces;
    }

    public void addLikedPlace(String placeId) {
        likedPlaces.add(placeId);
    }

    public void removeLikedPlace(String placeId) {
        likedPlaces.remove(placeId);
    }
}
