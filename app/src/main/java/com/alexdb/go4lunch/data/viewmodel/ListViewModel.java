package com.alexdb.go4lunch.data.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;

import java.util.List;

public class ListViewModel extends ViewModel {

    private final RestaurantPlacesRepository mMapsPlacesRepository;
    private final LocationRepository mLocationRepository;


    ListViewModel(RestaurantPlacesRepository mapsPlacesRepository,
                  LocationRepository locationRepository) {
        mMapsPlacesRepository = mapsPlacesRepository;
        mLocationRepository = locationRepository;
    }

    public LiveData<List<MapsPlace>> getRestaurantsLiveData() {
        return mMapsPlacesRepository.getRestaurantPlacesLiveData();
    }

    public void fetchRestaurants() {
        mMapsPlacesRepository.fetchRestaurantPlaces(mLocationRepository.getLocationLiveData().getValue());
    }
}
