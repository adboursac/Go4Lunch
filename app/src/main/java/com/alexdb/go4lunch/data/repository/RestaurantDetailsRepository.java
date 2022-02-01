package com.alexdb.go4lunch.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetailsPage;
import com.alexdb.go4lunch.data.service.GoogleMapsApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailsRepository {

    private final GoogleMapsApi mGoogleMapsApi;
    private final MutableLiveData<Map<String, MapsPlaceDetails>> mRestaurantsDetailsLiveData = new MutableLiveData<>(new HashMap<>());

    public RestaurantDetailsRepository(GoogleMapsApi googleMapsApi) {
        mGoogleMapsApi = googleMapsApi;
    }

    public LiveData<Map<String, MapsPlaceDetails>> getRestaurantDetailsLiveData() {
        return mRestaurantsDetailsLiveData;
    }

    /**
     * Fetch restaurant details from its place id.
     *
     * @param placeId place id of the restaurant
     */
    public void fetchRestaurantDetails(String placeId) {
        Map<String, MapsPlaceDetails> currentDetails = mRestaurantsDetailsLiveData.getValue();
        if (currentDetails == null || placeId == null) return;

        // If we already fetched this result we ignore this fetch request
        if (currentDetails.get(placeId) != null) return;

        mGoogleMapsApi.getPlaceDetails(placeId).enqueue(new Callback<MapsPlaceDetailsPage>() {
            @Override
            public void onResponse(@NonNull Call<MapsPlaceDetailsPage> call, @NonNull Response<MapsPlaceDetailsPage> response) {
                MapsPlaceDetailsPage detailsPage = response.body();
                if (detailsPage != null) {
                    currentDetails.put(placeId, detailsPage.getResult());
                    mRestaurantsDetailsLiveData.setValue(currentDetails);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapsPlaceDetailsPage> call, @NonNull Throwable t) {
                Log.d("RestaurantDetailsRepo--", "fetchRestaurantDetails failure" + t);
            }
        });
    }

    public String getPictureUrl(String mapsPhotoReference) {
        return mGoogleMapsApi.getPictureUrl(mapsPhotoReference);
    }
}
