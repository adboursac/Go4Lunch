package com.alexdb.go4lunch.data.viewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;
import com.alexdb.go4lunch.data.service.PermissionHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

public class MapViewModel extends ViewModel {

    private final int DEFAULT_ZOOM = 18;
    @NonNull
    private final PermissionHelper mPermissionHelper;
    @NonNull
    private final LocationRepository mLocationRepository;
    @NonNull
    private final RestaurantPlacesRepository mMapsPlacesRepository;

    private GoogleMap mMap;

    public MapViewModel(
            @NonNull PermissionHelper permissionHelper,
            @NonNull LocationRepository locationRepository,
            @NonNull RestaurantPlacesRepository mapsPlacesRepository
    ) {
        mPermissionHelper = permissionHelper;
        mLocationRepository = locationRepository;
        mMapsPlacesRepository = mapsPlacesRepository;
    }

    public LiveData<Location> getLocationLiveData() {
        return mLocationRepository.getLocationLiveData();
    }

    public LiveData<List<MapsPlace>> getRestaurantsLiveData() {
        return mMapsPlacesRepository.getRestaurantPlacesLiveData();
    }


    @SuppressLint("MissingPermission")
    public void refreshLocation() {
        if (mPermissionHelper.hasLocationPermission()) {
            mLocationRepository.startLocationUpdatesLoop();
            mMap.setMyLocationEnabled(true);
        } else {
            mLocationRepository.stopLocationUpdatesLoop();
        }
    }

    public void fetchRestaurants(Location location) {
        mMapsPlacesRepository.fetchRestaurantPlaces(location);
    }

    public void initMap(GoogleMap map, Activity activity) {
        mMap = map;
        if (!mPermissionHelper.hasLocationPermission())
            mPermissionHelper.requestLocationPermission(activity);
        refreshLocation();
    }


    public void moveCamera(Location location) {
        if (location == null || mMap == null) return;
        LatLng cord = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cord, DEFAULT_ZOOM));
    }

    private void createRestaurantMarker(Location location, String title, boolean selected, String placeId) {
        LatLng cord = new LatLng(location.getLatitude(), location.getLongitude());
        float hue = selected ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_ORANGE;
        Objects.requireNonNull(mMap.addMarker(new MarkerOptions()
                .position(cord)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(hue))))
                .setTag(placeId);
    }

    public void addEveryRestaurantsMarkers() {
        for (MapsPlace restaurant : Objects.requireNonNull(getRestaurantsLiveData().getValue())) {
            createRestaurantMarker(
                    restaurant.getLocation(),
                    restaurant.getName(),
                    false,
                    restaurant.getPlaceId());
        }
    }
}
