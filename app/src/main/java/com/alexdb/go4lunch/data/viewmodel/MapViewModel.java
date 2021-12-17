package com.alexdb.go4lunch.data.viewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.service.PermissionHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewModel extends ViewModel {

    private final int DEFAULT_ZOOM = 18;
    @NonNull
    private final PermissionHelper mPermissionHelper;
    @NonNull
    private final LocationRepository mLocationRepository;

    private GoogleMap mMap;

    public MapViewModel (
            @NonNull PermissionHelper permissionHelper,
            @NonNull LocationRepository locationRepository
    ) {
        mPermissionHelper = permissionHelper;
        mLocationRepository = locationRepository;
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

    public void initMap(GoogleMap map, Activity activity) {
        mMap = map;
        if (!mPermissionHelper.hasLocationPermission()) mPermissionHelper.requestLocationPermission(activity);
        refreshLocation();
    }

    public LiveData<Location> getLocationLiveData() {
        return mLocationRepository.getLocationLiveData();
    }

    public void moveCamera(Location location) {
        if (location == null || mMap == null) return;
        LatLng cord = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cord, DEFAULT_ZOOM));
    }

    private void addMarker(Location location, String title) {
        LatLng cord = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(cord).title(title));
    }
}
