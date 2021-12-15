package com.alexdb.go4lunch.data.service;

import android.content.Context;

import com.alexdb.go4lunch.data.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserApiFirebase {

    /**
     * get users collection
     * @return users collection
     */
    private CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    /**
     * Create user from given user instance
     * @param user user to store in database
     * @return resulting task
     */
    public Task<Void> createUser(User user) {
        return getUsersCollection().document(user.getUid()).set(user);
    }

    /**
     * Get current user.
     * @return current logged user
     */
    public User getCurrentUser(){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) return new User(fUser.getUid(),
                                            fUser.getDisplayName(),
                                            fUser.getEmail(),
                                            fUser.getPhotoUrl().toString());
        else return null;
    }

    /**
     * Tell if current user is already logged
     * @return true if user is logged, false instead
     */
    public boolean isCurrentUserLogged() {
        return (FirebaseAuth.getInstance().getCurrentUser() != null);
    }

    /**
     * Sign current user out.
     * @param context context
     * @return resulting task
     */
    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    /**
     * Delete current user account.
     * @param context context
     * @return resulting task
     */
    public Task<Void> deleteCurrentUserAccount(Context context){
        return AuthUI.getInstance().delete(context);
    }
}
