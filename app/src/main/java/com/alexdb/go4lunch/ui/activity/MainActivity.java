package com.alexdb.go4lunch.ui.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.viewmodel.UserViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private UserViewModel mUserViewModel;
    private NavController mNavController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        initNavigationComponents();
        ConfigureNavigationComponentsDisplayRules();
        setupListeners();
    }

    @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavController.navigate(R.id.nav_map_view_fragment);
    }

    /**
     * Init navigation components ( navigation drawer, toolbar and bottom navigation)
     * according with navigation graph with label display on toolbar
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
     * @param visible visibility status
     */
    private void ShowToolbarAndBottomNavigation( boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBinding.activityMainContent.toolbar.setVisibility(visibility);
        mBinding.activityMainContent.bottomNavigation.setVisibility(visibility);
    }

    /**
     * Set specific listeners for Main Activity
     * - listener on Sign Out button for navigation drawer
     */
    private void setupListeners(){
        // Logout Button
        Menu menu = mBinding.drawerContent.getMenu();
        MenuItem item = menu.findItem(R.id.menu_drawer_logout);
        item.setOnMenuItemClickListener(i -> {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            signOut();
            return true;
        });
    }

    /**
     * Sign the user out
     */
    private void signOut() {
        mUserViewModel.signOut(this).addOnSuccessListener(aVoid -> finish());
    }

    /**
     * Get required View Models for Main Activity
     */
    private void initViewModel() {
        mUserViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserViewModel.class);
    }
}
