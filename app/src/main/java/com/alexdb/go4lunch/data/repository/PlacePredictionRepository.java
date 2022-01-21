package com.alexdb.go4lunch.data.repository;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.model.maps.MapsPlacePrediction;
import com.alexdb.go4lunch.data.model.maps.MapsPlacePredictionsPage;
import com.alexdb.go4lunch.data.service.GoogleMapsApi;
import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacePredictionRepository {

    private final GoogleMapsApi mGoogleMapsApi;
    private final MutableLiveData<List<MapsPlacePrediction>> mRestaurantPredictions = new MutableLiveData<>();
    private String mCurrentSearchQuery="";

    public LiveData<List<MapsPlacePrediction>> getRestaurantPredictionsLiveData() {
        return mRestaurantPredictions;
    }

    public PlacePredictionRepository(GoogleMapsApi googleMapsApi) {
        mGoogleMapsApi = googleMapsApi;
    }

    public String getCurrentSearchQuery() { return mCurrentSearchQuery; }
    public void setCurrentSearchQuery(String currentSearchQuery) { mCurrentSearchQuery = currentSearchQuery; }

    /**
     * Request restaurant autocomplete predictions.
     * @param location current location
     * @param textInput user search input
     */

    public void requestRestaurantPredictions(Location location, int searchRadius, String textInput) {
        if (location == null || textInput == null || containsPrediction(textInput)) return;

        mGoogleMapsApi.getPlacesPredictions(location, searchRadius, textInput).enqueue(new Callback<MapsPlacePredictionsPage>() {
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

    /**
     * Tells if given textInput has an exact match with our current predictions list.
     * In this case, we won't need to request it as it will return only one match that we already have.
     * @param textInput the request input we want to check
     * @return true if we have an exact match, false instead
     */
    public boolean containsPrediction(String textInput) {
        List<MapsPlacePrediction> predictions = mRestaurantPredictions.getValue();
        if (predictions ==  null) return false;
        for( MapsPlacePrediction p : predictions) {
            if (p.getStructured_formatting().getMain_text().contentEquals(textInput)) return true;
        }
        return false;
    }
}
