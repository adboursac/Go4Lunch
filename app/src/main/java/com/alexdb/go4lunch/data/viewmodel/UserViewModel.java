package com.alexdb.go4lunch.data.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends ViewModel {

    @NonNull
    private final UserRepository mUserRepository;

    public UserViewModel (@NonNull UserRepository userRepository) {
        mUserRepository = userRepository;
    }

    /**
     * Create user from given user instance
     * @param user user to store in database
     * @return resulting task
     */
    public Task<Void> createUser(User user) {
        return mUserRepository.createUser(user);
    }

    /**
     * Get current user.
     * @return current logged user
     */
    public User getCurrentUser() {return mUserRepository.getCurrentUser(); }

    /**
     * Tell if current user is already logged
     * @return true if user is logged, false instead
     */
    public Boolean isCurrentUserLogged(){
       return mUserRepository.isCurrentUserLogged();
    }

    /**
     * Sign current user out.
     * @param context context
     * @return resulting task
     */
    public Task<Void> signOut(Context context){
        return mUserRepository.signOut(context);
    }
}
