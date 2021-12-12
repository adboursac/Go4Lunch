package com.alexdb.go4lunch.data.model;

import androidx.annotation.Nullable;

public class User {
    private String uid;
    private String name;
    private String email;
    private String profilePictureUrl;

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

    public User(String uid, String name, String email, @Nullable String urlProfilePicture) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.profilePictureUrl = urlProfilePicture;
    }
}
