package com.alexdb.go4lunch.data.repository;

import android.content.Context;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.google.android.gms.tasks.Task;

public final class  UserRepository {

    private final UserApiFirebase mUserApiService;

    public UserRepository(UserApiFirebase userApiService) {
        mUserApiService = userApiService;
    }

    /**
     * Create user from given user instance
     * @param user user to store in database
     * @return resulting task
     */
    public Task<Void> createUser(User user) {
        return mUserApiService.createUser(user);
    }

    /**
     * Get current user.
     * @return current logged user
     */
    public User getCurrentUser(){
        return mUserApiService.getCurrentUser();
    }

    /**
     * Tell if current user is already logged
     * @return true if user is logged, false instead
     */
    public boolean isCurrentUserLogged() {
        return mUserApiService.isCurrentUserLogged();
    }

    /**
     * Sign current user out.
     * @param context context
     * @return resulting task
     */
    public Task<Void> signOut(Context context){
        return mUserApiService.signOut(context);
    }

    /**
     * Delete current user account.
     * @param context context
     * @return resulting task
     */
    public Task<Void> deleteCurrentUserAccount(Context context){
        return mUserApiService.deleteCurrentUserAccount(context);
    }
}
