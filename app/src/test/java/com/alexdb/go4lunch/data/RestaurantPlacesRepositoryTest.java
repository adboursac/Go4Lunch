package com.alexdb.go4lunch.data;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetailsPage;
import com.alexdb.go4lunch.data.model.maps.MapsPlacesPage;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;
import com.alexdb.go4lunch.data.service.GoogleMapsApi;
import com.alexdb.go4lunch.ui.LiveDataTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantPlacesRepositoryTest {

    MapsPlaceDetails expectedPlaceDetails = new MapsPlaceDetails(
            "placeId",
            null,
            "name",
            null,
            "website",
            "phone",
            "address",
            3f,
            new ArrayList<>());
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private GoogleMapsApi mGoogleMapsApi;

    @InjectMocks
    private RestaurantPlacesRepository mRestaurantPlacesRepository;

    @Before
    public void setUp() {
        //fetchRestaurantPlaces_test Mock
        given(mGoogleMapsApi.getRestaurantPlaces(mockedLocation)).willReturn(mockedCall);
        given(mockedResponse.body()).willReturn(mockedMapsPlacesPage);
        given(mockedMapsPlacesPage.getResults()).willReturn(mapsPlacesListDummy);
        mapsPlacesListDummy.add(mockedPlace);
        given(mockedPlace.hasSameId(anyList())).willReturn(false);

        //requestRestaurant_test Mock
        given(mGoogleMapsApi.getPlaceDetails("placeId")).willReturn(detailsMockedCall);
        given(detailsMockedResponse.body()).willReturn(mockedMapsPlaceDetailsPage);
        given(mockedMapsPlaceDetailsPage.getResult()).willReturn(expectedPlaceDetails);

        //getPictureUrl_test Mock
        given(mGoogleMapsApi.getPictureUrl("ref")).willReturn("url");
    }

    @Test
    public void fetchRestaurantPlaces_test() {
        LiveData<List<MapsPlace>> result = mRestaurantPlacesRepository.getRestaurantPlacesLiveData();

        // Let's call the repository method
        mRestaurantPlacesRepository.fetchRestaurantPlaces(mockedLocation);

        // Capture the callback waiting for data
        verify(mGoogleMapsApi.getRestaurantPlaces(mockedLocation)).enqueue(callbackArgumentCaptor.capture());

        // Trigger the response ourselves
        callbackArgumentCaptor.getValue().onResponse(mockedCall, mockedResponse);

        // Assert the result is posted to the LiveData
        // Asserts that requested mapPlace has been addedplace to our liveData
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals(mapsPlacesListDummy.get(0), liveData.getValue().get(0)));
    }

    @Test
    public void requestRestaurant_test() {
        LiveData<List<MapsPlace>> result = mRestaurantPlacesRepository.getRestaurantPlacesLiveData();

        // Let's call the repository method
        mRestaurantPlacesRepository.requestRestaurant("placeId");

        // Capture the callback waiting for data
        verify(mGoogleMapsApi.getPlaceDetails("placeId")).enqueue(detailsCallbackArgumentCaptor.capture());

        // Trigger the response ourselves
        detailsCallbackArgumentCaptor.getValue().onResponse(detailsMockedCall, detailsMockedResponse);

        // Assert the result is posted to the LiveData
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            // Assert the first element is requested restaurant
            assertNotNull(liveData.getValue());
            assertMapPlaceValuesEqualsDetailsValues(liveData.getValue().get(0), expectedPlaceDetails);
        });
    }

    @Test
    public void getPictureUrl_test() {
        String result = mRestaurantPlacesRepository.getPictureUrl("ref");
        assertEquals(result, "url");
    }

    @Test
    public void transformPlaceDetailsToMapPlace_test() {
        MapsPlace place = mRestaurantPlacesRepository.transformPlaceDetailsToMapPlace(expectedPlaceDetails);
        assertMapPlaceValuesEqualsDetailsValues(place, expectedPlaceDetails);
    }

    private void assertMapPlaceValuesEqualsDetailsValues(MapsPlace place, MapsPlaceDetails details) {
        assertEquals(details.getName(), place.getName());
        assertEquals(details.getFormatted_address(), place.getVicinity());
        assertEquals(details.getGeometry(), place.getGeometry());
        assertEquals(details.getOpening_hours(), place.getOpening_hours());
        assertEquals(details.getRating(), place.getRating());
        assertEquals(details.getPhotos(), place.getPhotos());
    }

    ///// fetchRestaurantPlaces_test Mocks
    // IN
    @Captor
    private ArgumentCaptor<Callback<MapsPlacesPage>> callbackArgumentCaptor;
    @Mock
    private Call<MapsPlacesPage> mockedCall;
    //< OUT
    @Mock
    private Response<MapsPlacesPage> mockedResponse;

    private List<MapsPlace> mapsPlacesListDummy = new ArrayList<>();

    @Mock
    private MapsPlacesPage mockedMapsPlacesPage;
    @Mock
    private Location mockedLocation;
    @Mock
    private MapsPlace mockedPlace;

    ///// requestRestaurant_test Mocks
    // IN
    @Captor
    private ArgumentCaptor<Callback<MapsPlaceDetailsPage>> detailsCallbackArgumentCaptor;
    @Mock
    private Call<MapsPlaceDetailsPage> detailsMockedCall;
    // OUT
    @Mock
    private Response<MapsPlaceDetailsPage> detailsMockedResponse;
    @Mock
    private MapsPlaceDetailsPage mockedMapsPlaceDetailsPage;
}
