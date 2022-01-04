package com.alexdb.go4lunch.data.viewmodel;

import android.content.res.Resources;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.RestaurantStateItem;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.model.maps.MapsOpeningHours;
import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.GoogleMapsApiClient;
import com.alexdb.go4lunch.ui.MainApplication;

import java.util.ArrayList;
import java.util.List;

public class ListViewModel extends ViewModel {

    private final RestaurantPlacesRepository mMapsPlacesRepository;
    private final LocationRepository mLocationRepository;
    private final UserRepository mUserRepository;
    private MediatorLiveData<List<RestaurantStateItem>> mRestaurantsLiveData;

    ListViewModel(RestaurantPlacesRepository mapsPlacesRepository,
                  LocationRepository locationRepository,
                  UserRepository userRepository) {
        mMapsPlacesRepository = mapsPlacesRepository;
        mLocationRepository = locationRepository;
        mUserRepository = userRepository;
        initRestaurantsLiveData();
    }

    public LiveData<List<RestaurantStateItem>> getRestaurantsLiveData() {
        return mRestaurantsLiveData;
    }

    public void fetchRestaurants() {
        mMapsPlacesRepository.fetchRestaurantPlaces(mLocationRepository.getLocationLiveData().getValue());
        mUserRepository.fetchWorkmates();
    }

    /**
     * Merge restaurant Details and current user live data from repositories into a single observable live data
     */
    public void initRestaurantsLiveData() {
        mRestaurantsLiveData = new MediatorLiveData<>();
        LiveData<List<MapsPlace>> placesLiveData = mMapsPlacesRepository.getRestaurantPlacesLiveData();
        LiveData<Location> locationLiveData = mLocationRepository.getLocationLiveData();
        LiveData<List<User>> workmatesLiveData = mUserRepository.getWorkmatesLiveData();

        mRestaurantsLiveData.addSource(placesLiveData, places ->
                mapDataToViewState(
                        places,
                        locationLiveData.getValue(),
                        workmatesLiveData.getValue())
        );

        mRestaurantsLiveData.addSource(locationLiveData, location ->
                mapDataToViewState(
                        placesLiveData.getValue(),
                        location,
                        workmatesLiveData.getValue())
        );

        mRestaurantsLiveData.addSource(workmatesLiveData, workmates ->
                mapDataToViewState(
                        placesLiveData.getValue(),
                        locationLiveData.getValue(),
                        workmates)
        );
    }

    /**
     * Map restaurant places and workmates data from repositories to view data as a RestaurantStateItem instance,
     * and store it in the Mediator Live Data mRestaurantsLiveData
     *
     * @param places data from restaurant places repository
     * @param userLocation current user location
     * @param workmates workmates data from User repository
     */
    private void mapDataToViewState(List<MapsPlace> places, Location userLocation, List<User> workmates) {
        if ((places == null)||(userLocation == null)||(workmates == null)) return;
        List<RestaurantStateItem> stateItems = new ArrayList<>();
        for (MapsPlace p : places) {
            stateItems.add(new RestaurantStateItem(
                    p.getPlaceId(),
                    p.getName(),
                    mapOpeningStatus(p.getOpening_hours()),
                    p.getVicinity(),
                    p.getLocation(),
                    generateDistance(userLocation, p.getLocation()),
                    p.getRating(),
                    GoogleMapsApiClient.getPictureUrl(p.getFirstPhotoReference()),
                    calculateWorkmateAmount(p.getPlaceId(), workmates)
            ));
        }
        mRestaurantsLiveData.setValue(stateItems);
    }

    private String mapOpeningStatus(MapsOpeningHours openingHours) {
        Resources resources = MainApplication.getApplication().getResources();
        if (openingHours == null) return resources.getString(R.string.restaurant_no_schedule);
        else {
            return openingHours.getOpen_now() ? resources.getString(R.string.restaurant_open)
                    : resources.getString(R.string.restaurant_closed);
        }
    }

    private int generateDistance(Location userLocation, Location placeLocation) {
        if ((userLocation == null) || (placeLocation == null)) return -1;
        return Math.round(userLocation.distanceTo(placeLocation));
    }

    public int calculateWorkmateAmount(String placeId, List<User> workmates) {
        if (workmates == null) return 0;
        int amount = 0;
        for (User workmate : workmates) {
            if (workmate.hasBookedPlace(placeId)) amount++;
        }
        return amount;
    }
}
