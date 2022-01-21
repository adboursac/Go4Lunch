package com.alexdb.go4lunch.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetailsPage;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantDetailsRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private GoogleMapsApi mGoogleMapsApi;

    @InjectMocks
    private RestaurantDetailsRepository restaurantDetailsRepository;

    @Before
    public void setUp() {
        //fetchRestaurantDetails_test Mock
        given(mGoogleMapsApi.getPlaceDetails("placeId")).willReturn(mockedCall);
        given(mockedResponse.body()).willReturn(mockedMapsPlaceDetailsPage);
        given(mockedMapsPlaceDetailsPage.getResult()).willReturn(mockedMapsPlaceDetails);

        //getPictureUrl_test Mock
        given(mGoogleMapsApi.getPictureUrl("ref")).willReturn("url");
    }

    @Test
    public void fetchRestaurantDetails_test() {
        LiveData<MapsPlaceDetails> result = restaurantDetailsRepository.getRestaurantDetailsLiveData();

        // Let's call the repository method
        restaurantDetailsRepository.fetchRestaurantDetails("placeId");

        // Capture the callback waiting for data
        verify(mGoogleMapsApi.getPlaceDetails("placeId")).enqueue(callbackArgumentCaptor.capture());

        // Trigger the response ourselves
        callbackArgumentCaptor.getValue().onResponse(mockedCall, mockedResponse);

        // Assert the result is posted to the LiveData
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals(mockedMapsPlaceDetails, liveData.getValue()));
    }

    @Test
    public void getPictureUrl_test() {
        String result = restaurantDetailsRepository.getPictureUrl("ref");
        assertEquals(result, "url");
    }

    ///// fetchRestaurantDetails_test Mocks
    // IN
    @Captor
    private ArgumentCaptor<Callback<MapsPlaceDetailsPage>> callbackArgumentCaptor;
    @Mock
    private Call<MapsPlaceDetailsPage> mockedCall;
    // OUT
    @Mock
    private Response<MapsPlaceDetailsPage> mockedResponse;
    @Mock
    private MapsPlaceDetailsPage mockedMapsPlaceDetailsPage;
    @Mock
    private MapsPlaceDetails mockedMapsPlaceDetails;
}
