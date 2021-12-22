package com.alexdb.go4lunch.data.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.data.model.maps.RestaurantPlace;
import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.RestaurantRepository;

import java.util.List;

public class ListViewModel extends ViewModel {

    private final RestaurantRepository mRestaurantRepository;
    private final LocationRepository mLocationRepository;


    ListViewModel(RestaurantRepository restaurantRepository,
                  LocationRepository locationRepository) {
        mRestaurantRepository = restaurantRepository;
        mLocationRepository = locationRepository;
    }

    public MutableLiveData<List<RestaurantPlace>> getRestaurantsLiveData() {
        return mRestaurantRepository.getRestaurantPlacesMutableLiveData();
    }

    public void fetchRestaurants() {
        mRestaurantRepository.fetchRestaurantPlaces(mLocationRepository.getLocationLiveData().getValue());
    }
}
