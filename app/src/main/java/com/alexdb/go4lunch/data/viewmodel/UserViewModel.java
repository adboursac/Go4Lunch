package com.alexdb.go4lunch.data.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.data.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends ViewModel {

    @NonNull
    private final UserRepository mUserRepository;

    public UserViewModel (@NonNull UserRepository userRepository) {
        mUserRepository = userRepository;
    }

    public FirebaseUser getCurrentUser(){
        return mUserRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context){
        return mUserRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context){
        return mUserRepository.deleteUser(context);
    }
}
