package com.alexdb.go4lunch.data;

import android.location.Location;
import android.os.Looper;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.service.PermissionHelper;
import com.alexdb.go4lunch.ui.LiveDataTestUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LocationRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    FusedLocationProviderClient mFusedLocationProviderClient;

    @Mock
    PermissionHelper mPermissionHelper;

    @InjectMocks
    LocationRepository mLocationRepository;

    @Before
    public void setUp() {
        // First time we call refreshLocation, location is permitted
        //then is forbidden
        given(mPermissionHelper.hasLocationPermission())
                .willReturn(true)
                .willReturn(false);
        // First time we get mockedLocation1
        // Second time mockedLocation2
        given(mockedLocationResult.getLastLocation())
                .willReturn(mockedLocation1)
                .willReturn(mockedLocation2);
    }

    @Test
    public void refreshLocation_test() {

        LiveData<Location> result = mLocationRepository.getLocationLiveData();

        // When location is permitted
        mLocationRepository.refreshLocation();

        //Capture the callback
        verify(mFusedLocationProviderClient).requestLocationUpdates(any(LocationRequest.class),
                callbackArgumentCaptor.capture(),
                any());

        // Trigger the onLocationResult
        callbackArgumentCaptor.getValue().onLocationResult(mockedLocationResult);

        // Assert the result is posted to the LiveData
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals(mockedLocation1, liveData.getValue()));

        // When location is not permitted, we do not trigger onLocationResult
        mLocationRepository.refreshLocation();

        // Assert the location didn't change.
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals(mockedLocation1, liveData.getValue()));
    }

    ///// requestRestaurantPredictions_test Mocks
    // IN
    @Captor
    private ArgumentCaptor<LocationCallback> callbackArgumentCaptor;
    // OUT
    @Mock
    LocationResult mockedLocationResult;
    @Mock
    private Location mockedLocation1;
    @Mock
    private Location mockedLocation2;
}
