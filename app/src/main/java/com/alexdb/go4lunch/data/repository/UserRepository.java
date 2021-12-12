package com.alexdb.go4lunch.data.repository;

import android.content.Context;

import androidx.annotation.Nullable;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.service.UserApiService;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public final class UserRepository {

    private final UserApiService mUserApiService;

    public UserRepository(UserApiService userApiService) {
        mUserApiService = userApiService;
    }

    public User getCurrentUser(){
        return mUserApiService.getCurrentUser();
    }

    public boolean isCurrentUserLogged() {
        return mUserApiService.isCurrentUserLogged();
    }

    public Task<Void> signOut(Context context){
        return mUserApiService.signOut(context);
    }

    public Task<Void> deleteCurrentUserAccount(Context context){
        return mUserApiService.deleteCurrentUserAccount(context);
    }
}
