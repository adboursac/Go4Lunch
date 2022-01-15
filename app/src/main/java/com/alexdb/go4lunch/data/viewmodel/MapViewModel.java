package com.alexdb.go4lunch.data.viewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.RestaurantStateItem;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.model.maps.MapsOpeningHours;
import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.model.maps.MapsPlacePrediction;
import com.alexdb.go4lunch.data.model.PredictionStateItem;
import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.PlacePredictionRepository;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.GoogleMapsApiClient;
import com.alexdb.go4lunch.data.service.PermissionHelper;
import com.alexdb.go4lunch.ui.MainApplication;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    private int mDefaultZoom = 18;
    @NonNull
    private final PermissionHelper mPermissionHelper;
    @NonNull
    private final LocationRepository mLocationRepository;
    @NonNull
    private final RestaurantPlacesRepository mMapsPlacesRepository;
    @NonNull
    private final UserRepository mUserRepository;
    @NonNull
    private final PlacePredictionRepository mPlacePredictionRepository;

    private MediatorLiveData<List<RestaurantStateItem>> mRestaurantsLiveData;

    private GoogleMap mMap;

    public MapViewModel(
            @NonNull PermissionHelper permissionHelper,
            @NonNull LocationRepository locationRepository,
            @NonNull RestaurantPlacesRepository mapsPlacesRepository,
            @NonNull UserRepository userRepository,
            @NonNull PlacePredictionRepository placePredictionRepository
    ) {
        mPermissionHelper = permissionHelper;
        mLocationRepository = locationRepository;
        mMapsPlacesRepository = mapsPlacesRepository;
        mUserRepository = userRepository;
        mPlacePredictionRepository = placePredictionRepository;
        initRestaurantsLiveData();
    }

    public LiveData<Location> getLocationLiveData() {
        return mLocationRepository.getLocationLiveData();
    }

    public LiveData<List<RestaurantStateItem>> getRestaurantsLiveData() {
        return mRestaurantsLiveData;
    }

    public String getCurrentSearchQuery() { return mPlacePredictionRepository.getCurrentSearchQuery(); }

    public void setDefaultZoom(int defaultZoom) { mDefaultZoom = defaultZoom; }

    /**
     * Merge restaurant places, location and workmates live data from repositories into a single observable live data
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
     * Map restaurant places, location, and workmates data from repositories to view data as a RestaurantStateItem instance,
     * and store it in the Mediator Live Data mRestaurantsLiveData
     *
     * @param places       data from restaurant places repository
     * @param userLocation current user location
     * @param workmates    workmates data from User repository
     */
    private void mapDataToViewState(List<MapsPlace> places, Location userLocation, List<User> workmates) {
        if ((places == null) || (userLocation == null) || (workmates == null)) return;
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

    @SuppressLint("MissingPermission")
    public void refreshLocation() {
        if (mPermissionHelper.hasLocationPermission()) {
            mLocationRepository.startLocationUpdatesLoop();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        } else {
            mLocationRepository.stopLocationUpdatesLoop();
        }
    }

    public void fetchRestaurants(Location location) {
        if (location == null) {
            refreshLocation();
            return;
        }
        mMapsPlacesRepository.fetchRestaurantPlaces(location);
        mUserRepository.fetchWorkmates();
    }

    public void initMap(GoogleMap map, Activity activity) {
        mMap = map;
        if (!mPermissionHelper.hasLocationPermission())
            mPermissionHelper.requestLocationPermission(activity);
    }

    public void moveCamera(Location location) {
        if (location == null || mMap == null) return;
        LatLng cord = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cord, mDefaultZoom));
    }

    private void createRestaurantMarker(Location location, String title, boolean selected, String placeId) {
        if (location == null || mMap == null) return;
        LatLng cord = new LatLng(location.getLatitude(), location.getLongitude());
        float hue = selected ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_ORANGE;
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(cord)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(hue)));
        if (marker != null) marker.setTag(placeId);
    }

    public void updateEveryRestaurantsMarkers(List<RestaurantStateItem> restaurants) {
        if (mMap == null) return;
        mMap.clear();
        for (RestaurantStateItem restaurant : restaurants) {
            createRestaurantMarker(
                    restaurant.getLocation(),
                    restaurant.getName(),
                    restaurant.getWorkmatesAmount() > 0,
                    restaurant.getPlaceId());
        }
    }

    private String mapOpeningStatus(MapsOpeningHours openingHours) {
        Resources resources = MainApplication.getApplication().getResources();
        if ( openingHours == null || openingHours.getOpen_now() == null ) return resources.getString(R.string.restaurant_no_schedule);
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

    public void requestRestaurantPredictions(String textInput) {
        mPlacePredictionRepository.requestRestaurantPredictions(mLocationRepository.getLocationLiveData().getValue(), textInput);
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

    public void applySearch(String query) {
        mPlacePredictionRepository.setCurrentSearchQuery(query);
        List<MapsPlacePrediction> currentPredictions = mPlacePredictionRepository.getRestaurantPredictionsLiveData().getValue();
        if (currentPredictions == null) return;
        for (MapsPlacePrediction p : currentPredictions) {
            if (query.contentEquals(p.getStructured_formatting().getMain_text())) {
                mMapsPlacesRepository.requestRestaurant(p.getPlace_id());
            }
        }
    }

    public void clearSearch() {
        mPlacePredictionRepository.setCurrentSearchQuery("");
        Location location = mLocationRepository.getLocationLiveData().getValue();
        if (location == null) return;
        mMapsPlacesRepository.fetchRestaurantPlaces(location);
    }
}
