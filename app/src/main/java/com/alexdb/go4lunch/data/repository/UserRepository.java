package com.alexdb.go4lunch.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import java.util.Date;

public class UserRepository {

    private final UserApiFirebase mUserApiService;
    private MutableLiveData<User> mCurrentUserLiveData = new MutableLiveData<>();

    public UserRepository(UserApiFirebase userApiService) {
        mUserApiService = userApiService;
    }

    public LiveData<User> getCurrentUserLiveData() {
        return mCurrentUserLiveData;
    }

    /**
     * Create user from given user instance
     *
     * @param user user to store in database
     */
    public void createUser(User user) {
        mUserApiService.createUser(user)
                .addOnFailureListener(e -> Log.w("User Repository", "createUser Error", e));
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
        });
    }

    /**
     * Add authenticated User in our database if not already in.
     */
    public void notifyUserAuthentication() {
        //Get User instance from Firebase Authentication SDK
        User authenticatedUser = mUserApiService.getFirebaseAuthCurrentUser();
        //Add authenticated User in our database if not already in.
        mUserApiService.getUser(authenticatedUser.getUid()).continueWith(task -> {
            if (!task.isSuccessful()) createUser(authenticatedUser);
            return null;
        });
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
    public void updateCurrentUserBooking(String placeId) {
        User currentUser = mCurrentUserLiveData.getValue();
        Date now = new Date(System.currentTimeMillis());
        mUserApiService.updateBookedPlace(
                currentUser.getUid(),
                placeId,
                now
        ).addOnSuccessListener(aVoid -> {
            currentUser.setBookedPlaceId(placeId);
            currentUser.setBookedDate(now);
            mCurrentUserLiveData.setValue(currentUser);
        }).addOnFailureListener(e -> Log.w("User Repository", "updateCurrentUserBooking Error", e));
    }

    /**
     * Delete current user account.
     *
     * @param context context
     * @return resulting task
     */
    public Task<Void> deleteCurrentUserAccount(Context context) {
        return mUserApiService.deleteCurrentUserAccount(context);
    }
}
