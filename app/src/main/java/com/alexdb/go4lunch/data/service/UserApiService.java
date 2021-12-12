package com.alexdb.go4lunch.data.service;

import android.content.Context;
import com.alexdb.go4lunch.data.model.User;
import com.google.android.gms.tasks.Task;


/**
 * User API Client
 */
public interface UserApiService {

    /**
     * Get current user.
     * @return current logged user
     */
    User getCurrentUser();

    /**
     * Tell if current user is already logged
     * @return true if user is logged, false instead
     */
    boolean isCurrentUserLogged();

    /**
     * Sign current user out.
     * @param context context
     * @return resulting task
     */
    Task<Void> signOut(Context context);

    /**
     * Delete current user account.
     * @param context context
     * @return resulting task
     */
    Task<Void> deleteCurrentUserAccount(Context context);
}
