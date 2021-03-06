package com.alexdb.go4lunch.data.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.data.model.RestaurantStateItem;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.model.maps.MapsPlacePrediction;
import com.alexdb.go4lunch.data.model.PredictionStateItem;
import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.PlacePredictionRepository;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;
import com.alexdb.go4lunch.data.repository.SettingsRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.ui.helper.MapsOpeningHoursHelper;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainViewModel extends ViewModel {

    @NonNull
    private final LocationRepository mLocationRepository;
    @NonNull
    private final RestaurantPlacesRepository mMapsPlacesRepository;
    @NonNull
    private final RestaurantDetailsRepository mRestaurantDetailsRepository;
    @NonNull
    private final UserRepository mUserRepository;
    @NonNull
    private final PlacePredictionRepository mPlacePredictionRepository;
    @NonNull
    private final SettingsRepository mSettingsRepository;

    private Resources mResources;
    private MediatorLiveData<List<RestaurantStateItem>> mRestaurantsLiveData;

    public MainViewModel(
            @NonNull LocationRepository locationRepository,
            @NonNull RestaurantPlacesRepository mapsPlacesRepository,
            @NonNull RestaurantDetailsRepository restaurantDetailsRepository,
            @NonNull UserRepository userRepository,
            @NonNull PlacePredictionRepository placePredictionRepository,
            @NonNull SettingsRepository settingsRepository,
            @NonNull Resources resources
    ) {
        mLocationRepository = locationRepository;
        mMapsPlacesRepository = mapsPlacesRepository;
        mRestaurantDetailsRepository = restaurantDetailsRepository;
        mUserRepository = userRepository;
        mPlacePredictionRepository = placePredictionRepository;
        mSettingsRepository = settingsRepository;
        mResources = resources;
        initRestaurantsLiveData();
    }

    // --- LiveData ---

    public LiveData<Location> getLocationLiveData() {
        return mLocationRepository.getLocationLiveData();
    }

    public LiveData<Boolean> getLocationPermissionLiveData() {
        return mLocationRepository.getLocationPermissionLiveData();
    }

    public LiveData<User> getCurrentUserLiveData() {
        return mUserRepository.getCurrentUserLiveData();
    }

    public LiveData<List<RestaurantStateItem>> getRestaurantsLiveData() {
        return mRestaurantsLiveData;
    }

    public LiveData<Integer> getMapZoomLiveData() {
        return mSettingsRepository.getMapZoomLiveData();
    }

    public LiveData<List<PredictionStateItem>> getRestaurantPredictionsLivaData() {
        return Transformations.map(mPlacePredictionRepository.getRestaurantPredictionsLiveData(), predictions -> {
            List<PredictionStateItem> predictionsItems = new ArrayList<>();
            for (MapsPlacePrediction p : predictions) {
                if (p.getPlace_id() != null) {
                    predictionsItems.add(new PredictionStateItem(
                            p.getPlace_id(),
                            p.getStructured_formatting().getMain_text(),
                            p.getStructured_formatting().getSecondary_text()
                    ));
                }
            }
            return predictionsItems;
        });
    }

    // --- Fetch requests ---

    public void fetchCurrentUser() {
        mUserRepository.fetchCurrentUser();
    }

    public void fetchWorkmates() {
        mUserRepository.fetchWorkmates();
    }

    public void fetchRestaurantPlaces(Location location) {
        if (location != null) mMapsPlacesRepository.fetchRestaurantPlaces(location);
    }

    /**
     * Merge LiveData from repositories into a single observable live data
     */
    public void initRestaurantsLiveData() {
        mRestaurantsLiveData = new MediatorLiveData<>();
        LiveData<List<MapsPlace>> placesLiveData = mMapsPlacesRepository.getRestaurantPlacesLiveData();
        LiveData<Location> locationLiveData = mLocationRepository.getLocationLiveData();
        LiveData<List<User>> workmatesLiveData = mUserRepository.getWorkmatesLiveData();
        LiveData<User> currentUserLiveData = mUserRepository.getCurrentUserLiveData();
        LiveData<Map<String, MapsPlaceDetails>> detailsLiveData = mRestaurantDetailsRepository.getRestaurantDetailsLiveData();

        mRestaurantsLiveData.addSource(placesLiveData, places ->
                mergeDataToViewState(
                        places,
                        locationLiveData.getValue(),
                        workmatesLiveData.getValue(),
                        currentUserLiveData.getValue(),
                        detailsLiveData.getValue())
        );

        mRestaurantsLiveData.addSource(locationLiveData, location ->
                mergeDataToViewState(
                        placesLiveData.getValue(),
                        location,
                        workmatesLiveData.getValue(),
                        currentUserLiveData.getValue(),
                        detailsLiveData.getValue())
        );

        mRestaurantsLiveData.addSource(workmatesLiveData, workmates ->
                mergeDataToViewState(
                        placesLiveData.getValue(),
                        locationLiveData.getValue(),
                        workmates,
                        currentUserLiveData.getValue(),
                        detailsLiveData.getValue())
        );

        mRestaurantsLiveData.addSource(currentUserLiveData, currentUser ->
                mergeDataToViewState(
                        placesLiveData.getValue(),
                        locationLiveData.getValue(),
                        workmatesLiveData.getValue(),
                        currentUser,
                        detailsLiveData.getValue())
        );

        mRestaurantsLiveData.addSource(detailsLiveData, detailsMap ->
                mergeDataToViewState(
                        placesLiveData.getValue(),
                        locationLiveData.getValue(),
                        workmatesLiveData.getValue(),
                        currentUserLiveData.getValue(),
                        detailsMap)
        );
    }

    // --- State item generation ---

    /**
     * Merge data from repositories to view data as a RestaurantStateItem instance,
     * and store it in the Mediator Live Data mRestaurantsLiveData
     *
     * @param places       data from restaurant places repository
     * @param userLocation current user location
     * @param workmates    workmates data from User repository
     * @param currentUser currentUser data from User repository
     * @param currentDetailsMap details Map object from details repository
     */
    private void mergeDataToViewState(List<MapsPlace> places,
                                      Location userLocation,
                                      List<User> workmates,
                                      User currentUser,
                                      Map<String, MapsPlaceDetails> currentDetailsMap) {
        if ((places == null) || (userLocation == null) || (workmates == null)) return;

        List<RestaurantStateItem> stateItems = new ArrayList<>();

        for (MapsPlace p : places) {

            // Preparing opening hours data
            String openingStatus;
            // ClosingSoon status will be updated while calling generateOpeningString method
            // to avoid going through openHours twice
            boolean[] closingSoon = {false};
            MapsPlaceDetails placeDetails = getPlaceDetails(p.getPlaceId(), currentDetailsMap);

            // If We have already cached this place details,
            if (placeDetails != null) {
                //  We can get openHours from it
                openingStatus = MapsOpeningHoursHelper.generateOpeningString(placeDetails.getOpening_hours(), closingSoon, mResources);
            } else {
                // Instead we will use openHours from mapPlace this time, but we fetch its details for the next time
                openingStatus = MapsOpeningHoursHelper.generateOpeningString(p.getOpening_hours(), closingSoon, mResources);
                mRestaurantDetailsRepository.fetchRestaurantDetails(p.getPlaceId());
            }

            stateItems.add(new RestaurantStateItem(
                    p.getPlaceId(),
                    p.getName(),
                    openingStatus,
                    closingSoon[0],
                    p.getVicinity(),
                    p.getLocation(),
                    calculateDistance(userLocation, p.getLocation()),
                    p.getRating(),
                    mMapsPlacesRepository.getPictureUrl(p.getFirstPhotoReference()),
                    calculateBookedWorkmatesAmount(p.getPlaceId(), workmates),
                    currentUser.getLikedPlaces().contains(p.getPlaceId())
            ));
        }
        mRestaurantsLiveData.setValue(stateItems);
    }

    public MapsPlaceDetails getPlaceDetails(String placeId, Map<String, MapsPlaceDetails>  currentDetailsMap) {
        if (currentDetailsMap == null) return null;
        return currentDetailsMap.get(placeId);
    }

    /**
     * Calculate distance between two given locations
     *
     * @param userLocation  user location
     * @param placeLocation place location
     * @return Distance in meters
     */
    public int calculateDistance(Location userLocation, Location placeLocation) {
        if ((userLocation == null) || (placeLocation == null)) return -1;
        return Math.round(userLocation.distanceTo(placeLocation));
    }

    /**
     * Calculate workmates amount that booked the given place
     *
     * @param placeId   id of the place
     * @param workmates list of workmates
     * @return workmates amount
     */
    public int calculateBookedWorkmatesAmount(String placeId, List<User> workmates) {
        if (workmates == null) return 0;
        int amount = 0;
        for (User workmate : workmates) {
            if (workmate.hasBookedPlace(placeId)) amount++;
        }
        return amount;
    }

    // --- Localisation ---

    public boolean hasLocationPermission() {
        return mLocationRepository.hasLocationPermission();
    }

    public void requestLocationPermission(Activity activity) {
            mLocationRepository.requestLocationPermission(activity);
    }

    public void denyLocationPermission() {
        mLocationRepository.denyLocationPermission();
    }

    public void grantLocationPermission() {
        mLocationRepository.grantLocationPermission();
    }

    public void refreshLocation() { mLocationRepository.refreshLocation(); }

    // --- Search feature ---

    public String getCurrentSearchQuery() {
        return mPlacePredictionRepository.getCurrentSearchQuery();
    }

    /**
     * Request place autocomplete predictions that match given text input
     *
     * @param textInput text input for prediction
     */
    public void requestPlacesPredictions(String textInput) {
        mPlacePredictionRepository.requestRestaurantPredictions(mLocationRepository.getLocationLiveData().getValue(),
                Objects.requireNonNull(mSettingsRepository.getSearchRadiusLiveData().getValue()),
                textInput);
    }

    /**
     * Convert predictions list to place names as strings list
     *
     * @param predictions predictions items
     * @return String list of predicted places
     */
    public List<String> predictionsToStrings(List<PredictionStateItem> predictions) {
        List<String> predictionsStrings = new ArrayList<>();
        for (PredictionStateItem p : predictions) {
            predictionsStrings.add(p.getMainText());
        }
        return predictionsStrings;
    }

    /**
     * Apply a query choice obtained from autocomplete and request places list to be updated to match the query
     *
     * @param restaurantName restaurant name that should come from autocomplete predictions
     */
    public void applySearch(String restaurantName) {
        mPlacePredictionRepository.setCurrentSearchQuery(restaurantName);
        List<MapsPlacePrediction> currentPredictions = mPlacePredictionRepository.getRestaurantPredictionsLiveData().getValue();
        if (currentPredictions == null) return;
        for (MapsPlacePrediction p : currentPredictions) {
            if (restaurantName.contentEquals(p.getStructured_formatting().getMain_text())) {
                mMapsPlacesRepository.requestRestaurant(p.getPlace_id());
            }
        }
    }

    /**
     * Clear search and bring back the list of nearest places
     */
    public void clearSearch() {
        mPlacePredictionRepository.setCurrentSearchQuery("");
        Location location = mLocationRepository.getLocationLiveData().getValue();
        if (location == null) return;
        mMapsPlacesRepository.fetchRestaurantPlaces(location);
    }

    /**
     * Tell if current user is already logged
     *
     * @return true if user is logged, false instead
     */
    public Boolean isCurrentUserLogged() {
        return mUserRepository.isCurrentUserLogged();
    }

    /**
     * Sign current user out.
     *
     * @param context context
     * @return resulting task
     */
    public Task<Void> signOut(Context context) {
        return mUserRepository.signOut(context);
    }

    public void sortByDistanceAsc(List<RestaurantStateItem> list) {
        List<RestaurantStateItem> sortedList = list.stream()
                .sorted(Comparator.comparing(RestaurantStateItem::getDistance))
                .collect(Collectors.toList());
       list.clear();
       list.addAll(sortedList);
    }
}
