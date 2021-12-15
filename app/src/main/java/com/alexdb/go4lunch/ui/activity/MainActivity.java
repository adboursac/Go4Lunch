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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private UserViewModel mUserViewModel;
    private NavController mNavController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        initNavigationComponents();
        ConfigureNavigationComponentsDisplayRules();
        updateDrawerHeaderData();
        setupListeners();
    }

    @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mNavController.navigate(R.id.nav_map_view_fragment);
    }

    /**
     * Get required View Model for Main Activity
     */
    private void initViewModel() {
        mUserViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserViewModel.class);
    }

    /**
     * Init navigation components ( navigation drawer, toolbar and bottom navigation)
     * according to the navigation graph with label display on toolbar
     */
    private void initNavigationComponents() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        mNavController = navHostFragment.getNavController();
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration
                .Builder(mNavController.getGraph())
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
     * @param visible true show both toolbar and bottom navigation
     */
    private void ShowToolbarAndBottomNavigation( boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBinding.activityMainContent.toolbar.setVisibility(visibility);
        mBinding.activityMainContent.bottomNavigation.setVisibility(visibility);
    }

    /** Set specific listeners for Main Activity
     * - listener on Sign Out button for navigation drawer
     */
    private void setupListeners(){
        // Logout Button
        Menu menu = mBinding.drawerContent.getMenu();
        MenuItem item = menu.findItem(R.id.menu_drawer_logout);
        item.setOnMenuItemClickListener(i -> {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            mUserViewModel.signOut(this).addOnSuccessListener(aVoid -> finish());
            return true;
        });
    }

    /**
     * update drawer header with user data
     */
    private void updateDrawerHeaderData(){
        if(mUserViewModel.isCurrentUserLogged()){
            User currentUser = mUserViewModel.getCurrentUser();
            View drawerHeaderView = mBinding.drawerContent.getHeaderView(0);

            if(currentUser.getProfilePictureUrl() != null){
                setDrawerHeaderProfilePicture(currentUser.getProfilePictureUrl(), drawerHeaderView);
            }
            setDrawerHeaderTexts(currentUser, drawerHeaderView);
        }
    }

    /**
     * Set drawer header profile picture with given url.
     * @param profilePictureUrl profile string url
     * @param drawerHeaderView main activity drawer header view
     */
    private void setDrawerHeaderProfilePicture(String profilePictureUrl, View drawerHeaderView){
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into((ImageView) drawerHeaderView.findViewById(R.id.drawer_profile_picture));
    }

    /**
     * Fill drawer header texts with current user data.
     * @param user current user
     * @param drawerHeaderView main activity drawer header view
     */
    private void setDrawerHeaderTexts(User user, View drawerHeaderView){
        TextView nameView = drawerHeaderView.findViewById(R.id.drawer_name);
        TextView emailView = drawerHeaderView.findViewById(R.id.drawer_email);
        //Get email & username from User and set it to views
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.noEmail) : user.getEmail();
        String name = TextUtils.isEmpty(user.getName()) ? getString(R.string.noName) : user.getName();
        nameView.setText(name);
        emailView.setText(email);
    }
}
