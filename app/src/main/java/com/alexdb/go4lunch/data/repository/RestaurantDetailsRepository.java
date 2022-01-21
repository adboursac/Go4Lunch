package com.alexdb.go4lunch.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetailsPage;
import com.alexdb.go4lunch.data.service.GoogleMapsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailsRepository {

    private final GoogleMapsApi mGoogleMapsApi;
    private MutableLiveData<MapsPlaceDetails> mRestaurantDetailsMutableLiveData = new MutableLiveData<>();

    public RestaurantDetailsRepository(GoogleMapsApi googleMapsApi) {
        mGoogleMapsApi = googleMapsApi;
    }

    public LiveData<MapsPlaceDetails> getRestaurantDetailsLiveData() {
        return mRestaurantDetailsMutableLiveData;
    }

    /**
     * Fetch restaurant details from its place id.
     *
     * @param placeId place id of the restaurant
     */
    public void fetchRestaurantDetails(String placeId) {
        if (placeId == null) return;

        mGoogleMapsApi.getPlaceDetails(placeId).enqueue(new Callback<MapsPlaceDetailsPage>() {
            @Override
            public void onResponse(@NonNull Call<MapsPlaceDetailsPage> call, @NonNull Response<MapsPlaceDetailsPage> response) {
                if (response.isSuccessful()) {
                    MapsPlaceDetailsPage detailsPage = response.body();
                    if (detailsPage != null) {
                        mRestaurantDetailsMutableLiveData.setValue(detailsPage.getResult());
                    }
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
