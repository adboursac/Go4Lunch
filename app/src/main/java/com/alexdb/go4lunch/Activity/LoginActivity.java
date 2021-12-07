package com.alexdb.go4lunch.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.databinding.ActivityLoginBinding;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupListeners();
    }

    @Override
    ActivityLoginBinding getViewBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }

    private void setupListeners(){
        // Login Button
        mBinding.loginButton.setOnClickListener(view -> startSignInActivity());
    }

    private void startSignInActivity(){
        // Authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Custom layout
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth_ui)
                .setGoogleButtonId(R.id.btn_google_login)
                .setFacebookButtonId(R.id.btn_facebook_login)
                .build();

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.AppTheme_Authentication)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setAuthMethodPickerLayout(customLayout)
                .build();
        signInLauncher.launch(signInIntent);
    }

    //Sign In Launcher
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            showSnackBar(getString(R.string.connection_succeed));
        } else {
            // Sign in failed
            if (response == null) {
                showSnackBar(getString(R.string.error_authentication_canceled));
            } else if (response.getError()!= null) {
                if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    showSnackBar(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(getString(R.string.error_unknown_error));
                }
            }
        }
    }

    // Show Snack Bar with a message
    private void showSnackBar( String message){
        Snackbar.make(mBinding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}