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

    public User getCurrentUser() {return mUserRepository.getCurrentUser(); }

    public Boolean isCurrentUserLogged(){
       return mUserRepository.isCurrentUserLogged();
    }

    public Task<Void> signOut(Context context){
        return mUserRepository.signOut(context);
    }
}
