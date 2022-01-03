package com.alexdb.go4lunch.data.service;

import android.content.Context;

import com.alexdb.go4lunch.data.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class UserApiFirebase {

    /**
     * get users collection
     *
     * @return users collection
     */
    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    /**
     * Create user from given user instance
     *
     * @param user user to store in database
     * @return resulting task
     */
    public Task<Void> createUser(User user) {
        return getUsersCollection().document(user.getUid()).set(user);
    }

    /**
     * Get the user from database and cast it to a User model Object
     *
     * @param uid the id of the required user
     * @return resulting task with User model Object
     */
    public Task<User> getUser(String uid) {
        return getUsersCollection().document(uid).get()
                .continueWith(task -> Objects.requireNonNull(task.getResult().toObject(User.class)));
    }

    /**
     * Get every user from database
     *
     * @return resulting task containing a list with all users as DocumentSnapshot
     */
    public Task<QuerySnapshot> getAllUsers() {
        return getUsersCollection().get();
    }

    /**
     * Update user's booked place
     *
     * @param uid     user's uid
     * @param placeId place id
     * @param date    booking date
     * @return resulting task
     */
    public Task<Void> updateBookedPlace(String uid, String placeId, Date date) {
        return getUsersCollection().document(uid).update(
                "bookedPlaceId", placeId,
                "bookedDate", new Timestamp(date)
        );
    }

    /**
     * Add place into liked places list
     *
     * @param uid     user's uid
     * @param placeId liked place id
     * @return resulting task
     */
    public Task<Void> addLikedPlace(String uid, String placeId) {
        return getUsersCollection().document(uid).update("likedPlaces", FieldValue.arrayUnion(placeId));
    }

    /**
     * Remove place from liked places list
     *
     * @param uid     user's uid
     * @param placeId place id to remove
     * @return resulting task
     */
    public Task<Void> removeLikedPlace(String uid, String placeId) {
        return getUsersCollection().document(uid).update("likedPlaces", FieldValue.arrayRemove(placeId));
    }

    /**
     * Get current user instance from Firebase Authentication SDK and cast it to a User model Object
     *
     * @return current logged user
     */
    public User getFirebaseAuthCurrentUser() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) return new User(
                fUser.getUid(),
                fUser.getDisplayName(),
                fUser.getEmail(),
                fUser.getPhotoUrl() != null ? fUser.getPhotoUrl().toString() : null,
                null,
                null,
                new ArrayList<>());
        else return null;
    }

    /**
     * Get current user instance from database and cast it to a User model Object
     *
     * @return current logged user
     */
    public Task<User> getCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return null;
        return getUser(currentUser.getUid());
    }

    /**
     * Tell if current user is already logged
     *
     * @return true if user is logged, false instead
     */
    public boolean isCurrentUserLogged() {
        return (FirebaseAuth.getInstance().getCurrentUser() != null);
    }

    /**
     * Sign current user out.
     *
     * @param context context
     * @return resulting task
     */
    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    /**
     * Delete current user account.
     *
     * @param context context
     * @return resulting task
     */
    public Task<Void> deleteCurrentUserAccount(Context context) {
        return AuthUI.getInstance().delete(context);
    }
}
