package com.alexdb.go4lunch.data.repository;

import android.content.Context;
import android.text.PrecomputedText;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provide all users data with liveData
 * Provide also a dedicated liveData for current app user
 */
public class UserRepository {

    private final UserApiFirebase mUserApiService;
    private MutableLiveData<User> mCurrentUserLiveData = new MutableLiveData<>();
    private MutableLiveData<List<User>> mWorkmatesLiveData = new MutableLiveData<>(new ArrayList<>());

    public UserRepository(UserApiFirebase userApiService) {
        mUserApiService = userApiService;
    }

    public LiveData<User> getCurrentUserLiveData() {
        return mCurrentUserLiveData;
    }

    public LiveData<List<User>> getWorkmatesLiveData() {
        return mWorkmatesLiveData;
    }

    /**
     * Create user from given user instance
     *
     * @param user user to store in database
     */
    public Task<Void> createUser(User user) {
        Task<Void> task = mUserApiService.createUser(user);
        if (task != null) task.addOnFailureListener(e -> Log.w("User Repository", "createUser Error", e));
        return task;
    }

    /**
     * fetch current user from database and update currentUserLiveData
     *
     * @return current logged user
     */
    public Task<User> fetchCurrentUser() {
        return mUserApiService.getCurrentUser().continueWith(task -> {
            mCurrentUserLiveData.setValue(task.getResult());
            return task.getResult();
        }).addOnFailureListener(e -> Log.w("User Repository", "fetchCurrentUser Error", e));
    }

    /**
     * Fetch all users in database except current user, cast them as User model Object
     * and update workmates Live Data
     */
    public Task<List<User>> fetchWorkmates() {
        String currentUserId = mUserApiService.getFirebaseAuthCurrentUser().getUid();
        return mUserApiService.getAllUsers().continueWith(task -> {
            List<User> workmates = task.getResult().getDocuments().stream()
                    .map(documentSnapshot -> documentSnapshot.toObject(User.class))
                    .filter(user -> user != null && !(currentUserId.contentEquals(user.getUid())))
                    .collect(Collectors.toList());
            mWorkmatesLiveData.setValue(workmates);
            return workmates;
        }).addOnFailureListener(e -> Log.w("User Repository", "fetchWorkmates Error", e));
    }

    /**
     * Add authenticated User in our database if not already in.
     */
    public void addAuthenticatedUserInDatabase() {
        //Get User instance from Firebase Authentication SDK
        User authenticatedUser = mUserApiService.getFirebaseAuthCurrentUser();
        //Add authenticated User in our database if not already in.
        mUserApiService.getUser(authenticatedUser.getUid())
                .continueWith(task -> createUser(authenticatedUser));
    }

    /**
     * Tell if current user is already logged
     *
     * @return true if user is logged, false instead
     */
    public boolean isCurrentUserLogged() {
        return mUserApiService.isCurrentUserLogged();
    }

    /**
     * Sign current user out.
     *
     * @param context context
     * @return resulting task
     */
    public Task<Void> signOut(Context context) {
        return mUserApiService.signOut(context);
    }

    /**
     * Updates current user booking attributes bookedPlacedId and bookedDate
     * on both remote database and local LiveData
     *
     * @param placeId id of the booked place
     */
    public Task<Void> updateCurrentUserBooking(String placeId, String placeName) {
        User currentUser = mCurrentUserLiveData.getValue();
        Date now = new Date(System.currentTimeMillis());
        return mUserApiService.updateBookedPlace(
                Objects.requireNonNull(currentUser).getUid(),
                placeId,
                placeName,
                now
        ).continueWith(task -> {
            currentUser.setBookedPlaceId(placeId);
            currentUser.setBookedPlaceName(placeName);
            currentUser.setBookedDate(now);
            return task.getResult();
        });
        //.addOnFailureListener(e -> Log.w("User Repository", "updateCurrentUserBooking Error", e));
    }

    /**
     * Updates the current user's like status of the given place by removing it from liked places list or adding it
     * on both remote database and local LiveData
     *
     * @param placeId id of the liked place
     */
    public Task<Void> toggleCurrentUserLikedPlace(String placeId) {
        User currentUser = mCurrentUserLiveData.getValue();
        if (currentUser == null) return null;

        if (currentUser.getLikedPlaces().contains(placeId)) {
            return mUserApiService.removeLikedPlace(currentUser.getUid(), placeId).continueWith(task -> {
                currentUser.removeLikedPlace(placeId);
                mCurrentUserLiveData.setValue(currentUser);
                return task.getResult();
            });
            //.addOnFailureListener(e -> Log.w("User Repository", "removeCurrentUserLikedPlace Error", e));
        }
        return mUserApiService.addLikedPlace(currentUser.getUid(), placeId).continueWith(task -> {
            currentUser.addLikedPlace(placeId);
            mCurrentUserLiveData.setValue(currentUser);
            return task.getResult();
        });
        //.addOnFailureListener(e -> Log.w("User Repository", "addCurrentUserLikedPlace Error", e));
    }

    /*
     * Delete current user account.
     *
     * @param context context
     * @return resulting task
     */
    /*
    public Task<Void> deleteCurrentUserAccount(Context context) {
        return mUserApiService.deleteCurrentUserAccount(context);
    }
    */
}
