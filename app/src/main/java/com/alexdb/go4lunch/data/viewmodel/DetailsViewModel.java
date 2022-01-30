package com.alexdb.go4lunch.data.viewmodel;

import android.content.res.Resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.RestaurantDetailsStateItem;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.model.maps.MapsOpeningHours;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.ui.helper.MapsOpeningHoursHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DetailsViewModel extends ViewModel {

    private final RestaurantDetailsRepository mRestaurantDetailsRepository;
    private final UserRepository mUserRepository;
    private MediatorLiveData<RestaurantDetailsStateItem> mRestaurantDetailsLiveData;
    private Resources mResources;

    public DetailsViewModel(RestaurantDetailsRepository restaurantDetailsRepository, UserRepository userRepository, Resources resources) {
        mRestaurantDetailsRepository = restaurantDetailsRepository;
        mUserRepository = userRepository;
        mResources = resources;
        initRestaurantDetailsLiveData();
    }

    public LiveData<RestaurantDetailsStateItem> getRestaurantsDetailsLiveData() {
        return mRestaurantDetailsLiveData;
    }

    public void fetchRestaurantDetails(String placeId) {
        mRestaurantDetailsRepository.fetchRestaurantDetails(placeId);
        mUserRepository.fetchWorkmates();
    }

    /**
     * Merge restaurant Details and current user live data from repositories into a single observable live data
     */
    public void initRestaurantDetailsLiveData() {
        mRestaurantDetailsLiveData = new MediatorLiveData<>();
        LiveData<MapsPlaceDetails> placeDetailsLiveData = mRestaurantDetailsRepository.getRestaurantDetailsLiveData();
        LiveData<User> currentUserLiveData = mUserRepository.getCurrentUserLiveData();
        LiveData<List<User>> workmatesLiveData = mUserRepository.getWorkmatesLiveData();


        mRestaurantDetailsLiveData.addSource(placeDetailsLiveData, restaurantDetails ->
                mapDataToViewState(
                        restaurantDetails,
                        currentUserLiveData.getValue(),
                        workmatesLiveData.getValue())
        );

        mRestaurantDetailsLiveData.addSource(currentUserLiveData, user ->
                mapDataToViewState(
                        placeDetailsLiveData.getValue(),
                        user,
                        workmatesLiveData.getValue())
        );

        mRestaurantDetailsLiveData.addSource(workmatesLiveData, workmates ->
                mapDataToViewState(
                        placeDetailsLiveData.getValue(),
                        currentUserLiveData.getValue(),
                        workmates)
        );
    }

    /**
     * Map restaurant Details and current user data from repositories to view data as a RestaurantDetailsStateItem instance,
     * and store it in the Mediator Live Data mRestaurantDetailsLiveData
     * @param placeDetails data from restaurant detail repository
     * @param currentUser current user data from user repository
     */
    private void mapDataToViewState(MapsPlaceDetails placeDetails, User currentUser, List<User> workmates) {
            if ((placeDetails == null)||(currentUser == null)) return;

        boolean[] closingSoon = {false};
        String openingStatus = MapsOpeningHoursHelper.generateOpeningString(placeDetails.getOpening_hours(), closingSoon, mResources);

        RestaurantDetailsStateItem restaurantDetailsStateItem = new RestaurantDetailsStateItem(
                placeDetails.getPlace_id(),
                placeDetails.getName(),
                openingStatus,
                closingSoon[0],
                placeDetails.getWebsite(),
                placeDetails.getInternational_phone_number(),
                placeDetails.getFormatted_address(),
                placeDetails.getRating(),
                mRestaurantDetailsRepository.getPictureUrl(placeDetails.getFirstPhotoReference()),
                currentUser.hasBookedPlace(placeDetails.getPlace_id()),
                currentUser.getLikedPlaces().contains(placeDetails.getPlace_id()),
                getBookedWorkmates(workmates,placeDetails.getPlace_id()));

        mRestaurantDetailsLiveData.setValue(restaurantDetailsStateItem);
    }

    public void toggleRestaurantBookingStatus() {
        RestaurantDetailsStateItem restaurant = mRestaurantDetailsLiveData.getValue();
        if (restaurant == null) return;
        if (restaurant.isBooked()) mUserRepository.updateCurrentUserBooking(null, null);
        else mUserRepository.updateCurrentUserBooking(restaurant.getPlaceId(), restaurant.getName());
    }

    public void toggleCurrentUserLikedPlace() {
        RestaurantDetailsStateItem restaurant = mRestaurantDetailsLiveData.getValue();
        if (restaurant == null) return;
        mUserRepository.toggleCurrentUserLikedPlace(restaurant.getPlaceId());
    }

    /**
     * Return workmates who booked the restaurant given as parameter
     * @param allWorkmates list to filter
     * @param placeId place id of restaurant
     * @return filtered list of workmates
     */
    public List<User> getBookedWorkmates(List<User> allWorkmates, String placeId) {
        if (allWorkmates == null) return new ArrayList<>();
        return allWorkmates.stream()
                .filter(user -> user.hasBookedPlace(placeId))
                .collect(Collectors.toList());
    }
}
