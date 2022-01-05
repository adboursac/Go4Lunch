package com.alexdb.go4lunch.ui.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.viewmodel.UserViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.databinding.ActivityMainBinding;
import com.alexdb.go4lunch.ui.fragment.MapViewFragmentDirections;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private UserViewModel mUserViewModel;
    private NavController mNavController;
    private MenuItem drawerSignOutButton;
    private MenuItem drawerBookedPlaceButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        initNavigationComponents();
        ConfigureNavigationComponentsDisplayRules();

        Menu menu = mBinding.drawerContent.getMenu();
        drawerSignOutButton = menu.findItem(R.id.menu_drawer_logout);
        drawerBookedPlaceButton = menu.findItem(R.id.menu_drawer_booked_place);

        initSignOutButton();
    }

    @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    /**
     * Get required View Model for Main Activity
     */
    private void initViewModel() {
        mUserViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserViewModel.class);
        mUserViewModel.getCurrentUserLiveData().observe(this, currentUser -> {
            updateDrawerHeaderData(currentUser);
            updateBookedPlaceButton(currentUser);
        });
        mUserViewModel.fetchCurrentUser();
    }

    /**
     * Init navigation components ( navigation drawer, toolbar and bottom navigation)
     * according to the navigation graph with label display on toolbar
     */
    private void initNavigationComponents() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) return;
        mNavController = navHostFragment.getNavController();
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration
                //We define multiple top-level destinations instead of graph's start destination
                //Thus, the drawer button will remain in the toolbar on all destinations
                .Builder(R.id.drawer_layout, R.id.nav_map_view_fragment, R.id.nav_list_view_fragment, R.id.nav_workmates_view_fragment)
                .setOpenableLayout(mBinding.drawerLayout)
                .build();

        NavigationUI.setupWithNavController(mBinding.drawerContent, mNavController);
        NavigationUI.setupWithNavController(mBinding.activityMainContent.toolbar, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.activityMainContent.bottomNavigation, mNavController);
    }

    /**
     * Hide bottom navigation and toolbar for specifics fragments
     * ( Restaurant details fragment and settings fragment )
     */
    @SuppressLint("NonConstantResourceId")
    private void ConfigureNavigationComponentsDisplayRules() {
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.nav_restaurant_details_fragment:
                case R.id.nav_settings_fragment:
                    ShowToolbarAndBottomNavigation(false);
                    break;
                default:
                    ShowToolbarAndBottomNavigation(true);
            }
        });
    }

    /**
     * Set visibility to both toolbar and bottom navigation
     *
     * @param visible true show both toolbar and bottom navigation
     */
    private void ShowToolbarAndBottomNavigation(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBinding.activityMainContent.toolbar.setVisibility(visibility);
        mBinding.activityMainContent.bottomNavigation.setVisibility(visibility);
    }

    /**
     * init Sign Out button for navigation drawer
     * -
     */
    private void initSignOutButton() {
        drawerSignOutButton.setOnMenuItemClickListener(i -> {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            mUserViewModel.signOut(this).addOnSuccessListener(aVoid -> finish());
            return true;
        });
    }

    /**
     * init booked place details button for navigation drawer
     * -
     */
    private void updateBookedPlaceButton(User currentUser) {
        if (currentUser.hasValidBookingDate()) {
            drawerBookedPlaceButton.setOnMenuItemClickListener(i -> {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                mNavController.getCurrentDestination();
                mNavController.navigate(
                        MapViewFragmentDirections.navigateToDetails().setPlaceId(currentUser.getBookedPlaceId())
                );
                return true;
            });
        } else {
            drawerBookedPlaceButton.setOnMenuItemClickListener(i -> {
                showSnackBar(getResources().getString(R.string.navigation_drawer_no_booking_yet));
                return true;
            });
        }
    }

    @Override
    public void onBackPressed() {
        //Close Drawer if it's open
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START))
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            //Navigate back except if we already are on start destination : Map view fragment
        else if (Objects.requireNonNull(mNavController.getCurrentDestination()).getId() != R.id.nav_map_view_fragment)
            super.onBackPressed();
    }

    /**
     * update drawer header with user data
     */
    private void updateDrawerHeaderData(User currentUser) {
        if (mUserViewModel.isCurrentUserLogged()) {
            View drawerHeaderView = mBinding.drawerContent.getHeaderView(0);

            if (currentUser.getProfilePictureUrl() != null) {
                setDrawerHeaderProfilePicture(currentUser.getProfilePictureUrl(), drawerHeaderView);
            }
            setDrawerHeaderTexts(currentUser, drawerHeaderView);
        }
    }

    /**
     * Set drawer header profile picture with given url.
     *
     * @param profilePictureUrl profile string url
     * @param drawerHeaderView  main activity drawer header view
     */
    private void setDrawerHeaderProfilePicture(String profilePictureUrl, View drawerHeaderView) {
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .timeout(10000)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into((ImageView) drawerHeaderView.findViewById(R.id.drawer_profile_picture));
    }

    /**
     * Fill drawer header texts with current user data.
     *
     * @param user             current user
     * @param drawerHeaderView main activity drawer header view
     */
    private void setDrawerHeaderTexts(User user, View drawerHeaderView) {
        TextView nameView = drawerHeaderView.findViewById(R.id.drawer_name);
        TextView emailView = drawerHeaderView.findViewById(R.id.drawer_email);
        //Get email & username from User and set it to views
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.noEmail) : user.getEmail();
        String name = TextUtils.isEmpty(user.getName()) ? getString(R.string.noName) : user.getName();
        nameView.setText(name);
        emailView.setText(email);
    }

    /**
     * Show Snack Bar with a message
     *
     * @param message message to display
     */
    private void showSnackBar(String message) {
        Snackbar.make(mBinding.drawerLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
