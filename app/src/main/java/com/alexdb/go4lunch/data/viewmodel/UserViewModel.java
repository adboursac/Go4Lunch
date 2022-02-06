package com.alexdb.go4lunch.data.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.repository.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {

    @NonNull
    private final UserRepository mUserRepository;

    public UserViewModel(@NonNull UserRepository userRepository) {
        mUserRepository = userRepository;
    }

    public LiveData<List<User>> getWorkmatesLiveData() {
        return mUserRepository.getWorkmatesLiveData();
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
    public void addAuthenticatedUserInDatabase() {
        mUserRepository.addAuthenticatedUserInDatabase();
    }

    /**
     * Tell if current user is already logged
     *
     * @return true if user is logged, false instead
     */
    public Boolean isCurrentUserLogged() {
        return mUserRepository.isCurrentUserLogged();
    }


    /*
     * get CurrentUser LiveData
     *
     * @return User model Object instance of current user
     *
    public LiveData<User> getCurrentUserLiveData() {
        return mUserRepository.getCurrentUserLiveData();
    }
    */


    /*
     * refresh current user data
     *
    public void fetchCurrentUser() {
        mUserRepository.fetchCurrentUser();
    }
    */


    /*
     * Sign current user out.
     * @param context context
     * @return resulting task
    public Task<Void> signOut(Context context) {
        return mUserRepository.signOut(context);
    }
    */
}
