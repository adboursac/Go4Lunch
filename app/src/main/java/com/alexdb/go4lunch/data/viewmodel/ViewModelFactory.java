package com.alexdb.go4lunch.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.PlacePredictionRepository;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.PermissionHelper;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.alexdb.go4lunch.ui.MainApplication;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static volatile ViewModelFactory sFactory;
    @NonNull
    private final PermissionHelper mPermissionHelper;
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

        mPermissionHelper = new PermissionHelper(application);
        mUserRepository = new UserRepository(new UserApiFirebase());
        mLocationRepository = new LocationRepository(
                LocationServices.getFusedLocationProviderClient(application));
        mMapsPlacesRepository = new RestaurantPlacesRepository(Executors.newSingleThreadExecutor());
        mRestaurantDetailsRepository = new RestaurantDetailsRepository();
        mPlacePredictionRepository = new PlacePredictionRepository();
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
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(mUserRepository);
        }
        else if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(mPermissionHelper, mLocationRepository, mMapsPlacesRepository, mUserRepository, mPlacePredictionRepository);
        }
        else if (modelClass.isAssignableFrom(ListViewModel.class)) {
            return (T) new ListViewModel(mMapsPlacesRepository, mLocationRepository, mUserRepository, mPlacePredictionRepository);
        }
        else if (modelClass.isAssignableFrom(DetailsViewModel.class)) {
            return (T) new DetailsViewModel(mRestaurantDetailsRepository, mUserRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}