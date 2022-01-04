package com.alexdb.go4lunch.data.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserViewModel extends ViewModel {

    @NonNull
    private final UserRepository mUserRepository;

    public UserViewModel(@NonNull UserRepository userRepository) {
        mUserRepository = userRepository;
    }

    public LiveData<User> getCurrentUserLiveData() {
        return mUserRepository.getCurrentUserLiveData();
    }

    public LiveData<List<User>> getWorkmatesLiveData() {
        return mUserRepository.getWorkmatesLiveData();
    }

    /**
     * refresh current user data
     */
    public void fetchCurrentUser() {
        mUserRepository.fetchCurrentUser();
    }

    /**
     * refresh workmates live data
     */
    public void fetchWorkmates() {
        mUserRepository.fetchWorkmates();
    }

    /**
     * Add authenticated User in our database if not already in.
     */
    public void notifyUserAuthentication() {
        mUserRepository.notifyUserAuthentication();
    }

    /**
     * Tell if current user is already logged
     * @return true if user is logged, false instead
     */
    public Boolean isCurrentUserLogged() {
        return mUserRepository.isCurrentUserLogged();
    }

    /**
     * Sign current user out.
     * @param context context
     * @return resulting task
     */
    public Task<Void> signOut(Context context) {
        return mUserRepository.signOut(context);
    }
}
