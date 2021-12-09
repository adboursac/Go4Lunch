package com.alexdb.go4lunch.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class UserViewModel extends ViewModel {

    @NonNull
    private final UserRepository mUserRepository;

    public UserViewModel (@NotNull UserRepository userRepository) {
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
