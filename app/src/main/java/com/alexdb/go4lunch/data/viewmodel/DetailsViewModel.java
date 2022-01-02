package com.alexdb.go4lunch.data.viewmodel;

import android.content.res.Resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.RestaurantDetailsStateItem;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.model.maps.MapsOpeningHours;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.GoogleMapsApiClient;
import com.alexdb.go4lunch.ui.MainApplication;

public class DetailsViewModel extends ViewModel {

    private final RestaurantDetailsRepository mRestaurantDetailsRepository;
    private final UserRepository mUserRepository;
    private MediatorLiveData<RestaurantDetailsStateItem> mRestaurantDetailsLiveData;

    DetailsViewModel(RestaurantDetailsRepository restaurantDetailsRepository, UserRepository userRepository) {
        mRestaurantDetailsRepository = restaurantDetailsRepository;
        mUserRepository = userRepository;
        initRestaurantDetailsLiveData();
    }

    public LiveData<RestaurantDetailsStateItem> getRestaurantsDetailsLiveData() {
        return mRestaurantDetailsLiveData;
    }

    public void fetchRestaurantDetails(String placeId) {
        mRestaurantDetailsRepository.fetchRestaurantDetails(placeId);
    }

    /**
     * Merge restaurant Details and current user live data from repositories into a single observable live data
     */
    public void initRestaurantDetailsLiveData() {
        mRestaurantDetailsLiveData = new MediatorLiveData<>();
        LiveData<MapsPlaceDetails> placeDetailsLiveData = mRestaurantDetailsRepository.getRestaurantDetailsLiveData();
        LiveData<User> currentUserLiveData = mUserRepository.getCurrentUserLiveData();

        mRestaurantDetailsLiveData.addSource(placeDetailsLiveData, (Observer<MapsPlaceDetails>) restaurantDetails ->
                mapDataToViewState(
                        restaurantDetails,
                        currentUserLiveData.getValue())
        );

        mRestaurantDetailsLiveData.addSource(currentUserLiveData, (Observer<User>) user ->
                mapDataToViewState(
                        placeDetailsLiveData.getValue(),
                        user)
        );
    }

    /**
     * Map restaurant Details and current user data from repositories to view data as a RestaurantDetailsStateItem instance,
     * and store it in the Mediator Live Data mRestaurantDetailsLiveData
     * @param placeDetails data from restaurant detail repository
     * @param currentUser current user data from user repository
     */
    private void mapDataToViewState(MapsPlaceDetails placeDetails, User currentUser) {
            if ((placeDetails == null)||(currentUser == null)) return;

        RestaurantDetailsStateItem restaurantDetailsStateItem = new RestaurantDetailsStateItem(
                placeDetails.getPlace_id(),
                placeDetails.getName(),
                mapOpeningStatus(placeDetails.getOpening_hours()),
                placeDetails.getWebsite(),
                placeDetails.getInternational_phone_number(),
                placeDetails.getFormatted_address(),
                placeDetails.getRating(),
                GoogleMapsApiClient.getPictureUrl(placeDetails.getFirstPhotoReference()),
                currentUser.hasBookedPlace(placeDetails.getPlace_id()),
                currentUser.getLikedPlaces().contains(placeDetails.getPlace_id()));

        mRestaurantDetailsLiveData.setValue(restaurantDetailsStateItem);
    }

    private String mapOpeningStatus(MapsOpeningHours openingHours) {
        Resources resources = MainApplication.getApplication().getResources();
        if (openingHours == null) return resources.getString(R.string.restaurant_no_schedule);
        else {
            return openingHours.getOpen_now() ? resources.getString(R.string.restaurant_open)
                    : resources.getString(R.string.restaurant_closed);
        }
    }

    public void toggleRestaurantBookingStatus() {
        RestaurantDetailsStateItem restaurant = mRestaurantDetailsLiveData.getValue();
        String newBookingPlaceId = restaurant.isBooked() ? null : restaurant.getPlaceId();
        mUserRepository.updateCurrentUserBooking(newBookingPlaceId);
    }

    public void toggleCurrentUserLikedPlace() {
        RestaurantDetailsStateItem restaurant = mRestaurantDetailsLiveData.getValue();
        mUserRepository.toggleCurrentUserLikedPlace(restaurant.getPlaceId());
    }
}
