package com.alexdb.go4lunch.data.service;

import android.content.Context;

import com.alexdb.go4lunch.data.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserApiFirebase implements UserApiService {

    /**
     * {@inheritDoc}
     */
    @Override
    public User getCurrentUser(){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) return new User(fUser.getUid(),
                                            fUser.getDisplayName(),
                                            fUser.getEmail(),
                                            fUser.getPhotoUrl().toString());
        else return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrentUserLogged() {
        return (FirebaseAuth.getInstance().getCurrentUser() != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task<Void> deleteCurrentUserAccount(Context context){
        return AuthUI.getInstance().delete(context);
    }
}
