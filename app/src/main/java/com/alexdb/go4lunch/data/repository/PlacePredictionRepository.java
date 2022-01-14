package com.alexdb.go4lunch.data.repository;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.maps.MapsPlacePrediction;
import com.alexdb.go4lunch.data.model.maps.MapsPlacePredictionsPage;
import com.alexdb.go4lunch.data.service.GoogleMapsApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacePredictionRepository {

    private final MutableLiveData<List<MapsPlacePrediction>> mRestaurantPredictions = new MutableLiveData<>();
    private String mCurrentSearchQuery;

    public LiveData<List<MapsPlacePrediction>> getRestaurantPredictionsLiveData() {
        return mRestaurantPredictions;
    }

    public String getCurrentSearchQuery() { return mCurrentSearchQuery; }
    public void setCurrentSearchQuery(String currentSearchQuery) { mCurrentSearchQuery = currentSearchQuery; }

    /**
     * Request restaurant autocomplete predictions.
     * @param location current location
     * @param radius search radius
     * @param textInput user search input
     */

    public void requestRestaurantPredictions(Location location, int radius, String textInput) {
        if (textInput == null) return;

        GoogleMapsApiClient.getPlacesPredictions(location, radius, textInput).enqueue(new Callback<MapsPlacePredictionsPage>() {
            @Override
            public void onResponse(@NonNull Call<MapsPlacePredictionsPage> call, @NonNull Response<MapsPlacePredictionsPage> response) {
                if (response.isSuccessful()) {
                    MapsPlacePredictionsPage predictionsPage = response.body();
                    if (predictionsPage != null) {
                        mRestaurantPredictions.setValue(predictionsPage.getPredictions());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapsPlacePredictionsPage> call, @NonNull Throwable t) {
                Log.d("PlacePredictionRepo--", "requestRestaurantPredictions failure" + t);
            }
        });
    }
}
