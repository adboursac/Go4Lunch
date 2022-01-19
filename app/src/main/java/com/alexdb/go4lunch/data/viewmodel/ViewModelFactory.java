package com.alexdb.go4lunch.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.PlacePredictionRepository;
import com.alexdb.go4lunch.data.repository.SettingsRepository;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.PermissionHelper;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.alexdb.go4lunch.ui.MainApplication;
import com.alexdb.go4lunch.ui.helper.NotificationHelper;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static volatile ViewModelFactory sFactory;
    @NonNull
    private final PermissionHelper mPermissionHelper;
    @NonNull
    private final SettingsRepository mSettingsRepository;
    @NonNull
    private final UserRepository mUserRepository;
    @NonNull
    private final LocationRepository mLocationRepository;
    @NonNull
    private final RestaurantPlacesRepository mMapsPlacesRepository;
    @NonNull
    private final RestaurantDetailsRepository mRestaurantDetailsRepository;
    @NonNull
    private final PlacePredictionRepository mPlacePredictionRepository;

    private ViewModelFactory() {
        Application application = MainApplication.getApplication();
        UserApiFirebase userApi = new UserApiFirebase();

        mPermissionHelper = new PermissionHelper(application);
        mSettingsRepository = new SettingsRepository(application);
        mUserRepository = new UserRepository(userApi);
        mLocationRepository = new LocationRepository(
                LocationServices.getFusedLocationProviderClient(application),
                mPermissionHelper);
        mMapsPlacesRepository = new RestaurantPlacesRepository(Executors.newSingleThreadExecutor());
        mRestaurantDetailsRepository = new RestaurantDetailsRepository();
        mPlacePredictionRepository = new PlacePredictionRepository();

        NotificationHelper notificationHelper = NotificationHelper.getInstance();
        notificationHelper.setUserApi(userApi);
        notificationHelper.setUserRepository(mUserRepository);
    }

    public static ViewModelFactory getInstance() {
        if (sFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (sFactory == null) {
                    sFactory = new ViewModelFactory();
                }
            }
        }
        return sFactory;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(mSettingsRepository);
        }
        else if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(mUserRepository);
        }
        else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(mLocationRepository, mMapsPlacesRepository, mUserRepository, mPlacePredictionRepository, mSettingsRepository);
        }
        else if (modelClass.isAssignableFrom(DetailsViewModel.class)) {
            return (T) new DetailsViewModel(mRestaurantDetailsRepository, mUserRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}