package com.alexdb.go4lunch.data.viewmodel;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.PlacePredictionRepository;
import com.alexdb.go4lunch.data.repository.SettingsRepository;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.GoogleMapsApi;
import com.alexdb.go4lunch.data.service.PermissionHelper;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.alexdb.go4lunch.data.service.NotificationHelper;
import com.alexdb.go4lunch.ui.MainApplication;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static volatile ViewModelFactory sFactory;
    @NonNull
    private final PermissionHelper mPermissionHelper;
    @NonNull
    private final UserApiFirebase mUserApiFirebase;
    @NonNull
    private final GoogleMapsApi mGoogleMapsApi;
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
    @NonNull
    private final NotificationHelper mNotificationHelper;

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

    private ViewModelFactory() {
        mPermissionHelper = new PermissionHelper();
        mUserApiFirebase = new UserApiFirebase();
        mGoogleMapsApi = new GoogleMapsApi();
        mSettingsRepository = new SettingsRepository(new RxPreferenceDataStoreBuilder(MainApplication.getApplication(), "settings").build());
        mUserRepository = new UserRepository(mUserApiFirebase);
        mLocationRepository = new LocationRepository(mPermissionHelper,
                LocationServices.getFusedLocationProviderClient(MainApplication.getApplication()));
        mMapsPlacesRepository = new RestaurantPlacesRepository(mGoogleMapsApi);
        mRestaurantDetailsRepository = new RestaurantDetailsRepository(mGoogleMapsApi);
        mPlacePredictionRepository = new PlacePredictionRepository(mGoogleMapsApi);
        mNotificationHelper = new NotificationHelper(mSettingsRepository);
    }

    @NonNull
    public UserApiFirebase getUserApiFirebase() { return mUserApiFirebase; }
    @NonNull
    public GoogleMapsApi getGoogleMapsApi() { return mGoogleMapsApi; }
    @NonNull
    public UserRepository getUserRepository() { return mUserRepository; }
    @NonNull
    public NotificationHelper getNotificationHelper() { return mNotificationHelper; }

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
            return (T) new DetailsViewModel(mRestaurantDetailsRepository, mUserRepository, MainApplication.getApplication().getResources());
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}