package com.alexdb.go4lunch.ui.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.databinding.ActivityLoginBinding;
import com.alexdb.go4lunch.data.viewmodel.UserViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    private UserViewModel mUserViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSignInStatusAndRedirect();
    }

    @Override
    ActivityLoginBinding getViewBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }

    /**
     * Check if user is already logged in.
     * If user is logged in, starts main activity, or display sign in interface instead.
     */
    private void checkSignInStatusAndRedirect() {
        if (mUserViewModel.isCurrentUserLogged()) {
            startMainActivity();
        }
        else startSignInActivity();
    }

    /**
     * Create and launch sign-in intent to log the user in.
     * Available providers : Google, Facebook.
     */
    private void startSignInActivity(){
        // Authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Custom layout
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_firebase_auth_ui)
                .setGoogleButtonId(R.id.btn_google_login)
                .setFacebookButtonId(R.id.btn_facebook_login)
                .build();

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.AppTheme_Default)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setAuthMethodPickerLayout(customLayout)
                .build();

        signInLauncher.launch(signInIntent);
    }

    /**
     * Sign In Launcher required to launch sign in intent and handle its result.
     */
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    /**
     * Informs the user of the result of its connection attempt, showing Snack Bar messages.
     * Add user in database if not already stored in.
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            mUserViewModel.addAuthenticatedUserInDatabase();
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

    /**
     * Show Snack Bar with a message
     * @param message message to display
     */
    private void showSnackBar( String message){
        Snackbar.make(mBinding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Starts Main activity
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}