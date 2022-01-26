package com.alexdb.go4lunch.data.repository;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Looper;

import com.alexdb.go4lunch.data.service.PermissionHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Provide a location data with liveData
 */
public class LocationRepository {

    private static final int LOCATION_REQUEST_INTERVAL_MS = 10_000;
    private static final float SMALLEST_DISPLACEMENT_THRESHOLD_METER = 25;

    @NonNull
    private final FusedLocationProviderClient mFusedLocationProviderClient;
    @NonNull
    private final PermissionHelper mPermissionHelper;
    @NonNull
    private final MutableLiveData<Location> mLocationMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> mLocationPermissionMutableLiveData = new MutableLiveData<>();

    private LocationCallback mCallback;

    public LocationRepository(@NonNull PermissionHelper permissionHelper,
                              @NonNull FusedLocationProviderClient fusedLocationProviderClient) {
        mFusedLocationProviderClient = fusedLocationProviderClient;
        mPermissionHelper = permissionHelper;
        denyLocationPermission();
    }

    public LiveData<Location> getLocationLiveData() {
        return mLocationMutableLiveData;
    }
    public LiveData<Boolean> getLocationPermissionLiveData() { return mLocationPermissionMutableLiveData; }

    /**
     * Grant location permission and update live data
     */
    public void grantLocationPermission() {
        mLocationPermissionMutableLiveData.setValue(true);
        refreshLocation();
    }

    /**
     * Deny location permission and update live data
     */
    public void denyLocationPermission() {
        mLocationPermissionMutableLiveData.setValue(false);
        stopLocationUpdatesLoop();
    }

    /**
     * Tells if fine location is permitted.
     * @return true is fine location is permitted, false instead.
     */
    public boolean hasLocationPermission() {
        return mPermissionHelper.hasLocationPermission();
    }

    /**
     * Request fine location.
     * @param activity from which permission is requested
     */
    public void requestLocationPermission(Activity activity) {
        mPermissionHelper.requestLocationPermission(activity);
    }

    /**
     * Refresh location live data or stop getting it if permission is denied
     */
    @SuppressLint("MissingPermission")
    public void refreshLocation() {
        if (mPermissionHelper.hasLocationPermission()) {
            startLocationUpdatesLoop();
        } else {
            stopLocationUpdatesLoop();
        }
    }

    /**
     * Starts a periodic update of the location, in accordance with LOCATION_REQUEST_INTERVAL_MS interval
     * and SMALLEST_DISPLACEMENT_THRESHOLD_METER precision.
     */
    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    public void startLocationUpdatesLoop() {
        if (mCallback == null) {
            mCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    mLocationMutableLiveData.setValue(location);
                }
            };
        }
        //remove current update cycle
        mFusedLocationProviderClient.removeLocationUpdates(mCallback);
        //starts a new update cycle
        mFusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setSmallestDisplacement(SMALLEST_DISPLACEMENT_THRESHOLD_METER)
                        .setInterval(LOCATION_REQUEST_INTERVAL_MS),
                mCallback,
                Looper.getMainLooper()
        );
    }

    /**
     * Stops the periodic update of the location
     */
    public void stopLocationUpdatesLoop() {
        if (mCallback != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mCallback);
        }
    }
}
