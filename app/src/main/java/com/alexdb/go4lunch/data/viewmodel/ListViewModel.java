package com.alexdb.go4lunch.data.viewmodel;

import android.content.res.Resources;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.RestaurantStateItem;
import com.alexdb.go4lunch.data.model.maps.MapsOpeningHours;
import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;
import com.alexdb.go4lunch.data.service.GoogleMapsApiClient;
import com.alexdb.go4lunch.ui.MainApplication;

import java.util.ArrayList;
import java.util.List;

public class ListViewModel extends ViewModel {

    private final RestaurantPlacesRepository mMapsPlacesRepository;
    private final LocationRepository mLocationRepository;

    ListViewModel(RestaurantPlacesRepository mapsPlacesRepository,
                  LocationRepository locationRepository) {
        mMapsPlacesRepository = mapsPlacesRepository;
        mLocationRepository = locationRepository;
    }

    public LiveData<List<RestaurantStateItem>> getRestaurantsLiveData() {
        return mapDataToViewState(mMapsPlacesRepository.getRestaurantPlacesLiveData());
    }

    public void fetchRestaurantPlaces() {
        mMapsPlacesRepository.fetchRestaurantPlaces(mLocationRepository.getLocationLiveData().getValue());
    }

    //Mapping data from remote source to view data
    //
    private LiveData<List<RestaurantStateItem>> mapDataToViewState(LiveData<List<MapsPlace>> placesLiveData) {
        return Transformations.map(placesLiveData, places -> {
            List<RestaurantStateItem> stateItems = new ArrayList<>();
            for (MapsPlace p : places) {
                stateItems.add(new RestaurantStateItem(
                        p.getPlaceId(),
                        p.getName(),
                        mapOpeningStatus(p.getOpening_hours()),
                        p.getVicinity(),
                        p.getLocation(),
                        generateDistance(p.getLocation()),
                        GoogleMapsApiClient.getPictureUrl(p.getFirstPhotoReference()))
                );
            }
            return stateItems;
        });
    }

    private String mapOpeningStatus(MapsOpeningHours openingHours) {
        Resources resources = MainApplication.getApplication().getResources();
        if (openingHours == null) return resources.getString(R.string.restaurant_no_schedule);
        else {
            return openingHours.getOpen_now() ? resources.getString(R.string.restaurant_open)
                    : resources.getString(R.string.restaurant_closed);
        }
    }

    private String generateDistance(Location location) {
        Location userLocation = mLocationRepository.getLocationLiveData().getValue();
        int distance = Math.round(userLocation.distanceTo(location));
        return distance+"m";
    }
}
