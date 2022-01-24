package com.alexdb.go4lunch.data;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.alexdb.go4lunch.data.model.maps.MapsPlacePrediction;
import com.alexdb.go4lunch.data.model.maps.MapsPlacePredictionsPage;
import com.alexdb.go4lunch.data.model.maps.MapsStructuredFormattingText;
import com.alexdb.go4lunch.data.repository.PlacePredictionRepository;
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

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

//@SuppressWarnings("unchecked")
//@SuppressWarnings("all")
@RunWith(MockitoJUnitRunner.class)
public class PlacePredictionRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private GoogleMapsApi mGoogleMapsApi;

    @InjectMocks
    private PlacePredictionRepository mPlacePredictionRepository;

    @Before
    public void setUp() {
        //requestRestaurantPredictions_test Mock
        given(mGoogleMapsApi.getPlacesPredictions(mockedLocation, 0, "search")).willReturn(mockedCall);
        given(mockedResponse.body()).willReturn(mockedMapsPlacePredictionsPage);
        given(mockedMapsPlacePredictionsPage.getPredictions()).willReturn(mockedMapsPlacePredictionsList);
    }

    @Test
    public void requestRestaurantPredictions_test() {
        LiveData<List<MapsPlacePrediction>> result = mPlacePredictionRepository.getRestaurantPredictionsLiveData();

        // Let's call the repository method
        mPlacePredictionRepository.requestRestaurantPredictions(mockedLocation, 0, "search");

        // Capture the callback waiting for data
        verify(mGoogleMapsApi.getPlacesPredictions(mockedLocation, 0, "search")).enqueue(callbackArgumentCaptor.capture());

        // Trigger the response ourselves
        callbackArgumentCaptor.getValue().onResponse(mockedCall, mockedResponse);

        // Assert the result is posted to the LiveData
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals(mockedMapsPlacePredictionsList, liveData.getValue()));
    }

    @Test
    public void containsPrediction_test() {
        // We define current prediction list
         List<MapsPlacePrediction> currentPredictionList = Arrays.asList(
                new MapsPlacePrediction("placeId1", new MapsStructuredFormattingText("search", "address")),
                new MapsPlacePrediction("placeId2", new MapsStructuredFormattingText("Burger", "address"))
        );

        // When request current prediction
        mPlacePredictionRepository.requestRestaurantPredictions(mockedLocation, 0, "search");

        // Capture the callback waiting for data
        verify(mGoogleMapsApi.getPlacesPredictions(mockedLocation, 0, "search")).enqueue(callbackArgumentCaptor.capture());

        // Mocks for response
        given(containsPredictionOnResponse.body()).willReturn(containsPredictionPage);
        given(containsPredictionPage.getPredictions()).willReturn(currentPredictionList);

        // Trigger the response ourselves
        callbackArgumentCaptor.getValue().onResponse(mockedCall, containsPredictionOnResponse);

        // assert containsPrediction return true only when prediction has an exact match
        assertTrue(mPlacePredictionRepository.containsPrediction("search"));
        assertFalse(mPlacePredictionRepository.containsPrediction("search and more"));
        assertFalse(mPlacePredictionRepository.containsPrediction("seArch"));
    }

    ///// requestRestaurantPredictions_test Mocks
    // IN
    @Mock
    private Location mockedLocation;
    @Captor
    private ArgumentCaptor<Callback<MapsPlacePredictionsPage>> callbackArgumentCaptor;
    @Mock
    private Call<MapsPlacePredictionsPage> mockedCall;
    // OUT
    @Mock
    private Response<MapsPlacePredictionsPage> mockedResponse;
    @Mock
    private MapsPlacePredictionsPage mockedMapsPlacePredictionsPage;
    @Mock
    private List<MapsPlacePrediction> mockedMapsPlacePredictionsList;

    ///// requestRestaurantPredictions_test Mocks
    // OUT
    @Mock
    private Response<MapsPlacePredictionsPage> containsPredictionOnResponse;
    @Mock
    private MapsPlacePredictionsPage containsPredictionPage;

}