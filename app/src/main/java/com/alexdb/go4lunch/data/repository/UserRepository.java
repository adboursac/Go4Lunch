package com.alexdb.go4lunch.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.google.android.gms.tasks.Task;

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
     * @return resulting task
     */
    public Task<Void> createUser(User user) {
        return mUserApiService.createUser(user);
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
    public Task<User> notifyUserAuthentication() {
        //Get User instance from Firebase Authentication SDK
        User authenticatedUser = mUserApiService.getFirebaseAuthCurrentUser();
        //Add authenticated User in our database if not already in.
        return mUserApiService.getUser(authenticatedUser.getUid()).continueWith(task -> {
            if (task.getResult() == null) createUser(authenticatedUser);
            return task.getResult();
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
     * Delete current user account.
     *
     * @param context context
     * @return resulting task
     */
    public Task<Void> deleteCurrentUserAccount(Context context) {
        return mUserApiService.deleteCurrentUserAccount(context);
    }
}
