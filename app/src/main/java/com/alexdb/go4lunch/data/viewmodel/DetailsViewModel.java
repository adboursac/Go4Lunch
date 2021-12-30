package com.alexdb.go4lunch.data.viewmodel;

import android.content.res.Resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.RestaurantDetailsStateItem;
import com.alexdb.go4lunch.data.model.maps.MapsOpeningHours;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
import com.alexdb.go4lunch.data.service.GoogleMapsApiClient;
import com.alexdb.go4lunch.ui.MainApplication;

public class DetailsViewModel extends ViewModel {

    private final RestaurantDetailsRepository mRestaurantDetailsRepository;

    DetailsViewModel(RestaurantDetailsRepository restaurantDetailsRepository) {
        mRestaurantDetailsRepository = restaurantDetailsRepository;
    }

    public LiveData<RestaurantDetailsStateItem> getRestaurantsDetailsLiveData() {
        return mapDataToViewState(mRestaurantDetailsRepository.getRestaurantDetailsLiveData());
    }

    public void fetchRestaurantDetails(String placeId) {
        mRestaurantDetailsRepository.fetchRestaurantDetails(placeId);
    }

    //Mapping data from remote source to view data
    private LiveData<RestaurantDetailsStateItem> mapDataToViewState(LiveData<MapsPlaceDetails> detailsLiveData) {
        return Transformations.map(detailsLiveData, placeDetails -> new RestaurantDetailsStateItem(
                placeDetails.getPlace_id(),
                placeDetails.getName(),
                mapOpeningStatus(placeDetails.getOpening_hours()),
                placeDetails.getWebsite(),
                placeDetails.getInternational_phone_number(),
                placeDetails.getFormatted_address(),
                GoogleMapsApiClient.getPictureUrl(placeDetails.getFirstPhotoReference())));
    }

    private String mapOpeningStatus(MapsOpeningHours openingHours) {
        Resources resources = MainApplication.getApplication().getResources();
        if (openingHours == null) return resources.getString(R.string.restaurant_no_schedule);
        else {
            return openingHours.getOpen_now() ? resources.getString(R.string.restaurant_open)
                    : resources.getString(R.string.restaurant_closed);
        }
    }
}
