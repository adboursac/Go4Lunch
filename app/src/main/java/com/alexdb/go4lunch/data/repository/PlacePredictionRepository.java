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
    private int mDefaultSearchRadius=200;
    private String mCurrentSearchQuery="";

    public LiveData<List<MapsPlacePrediction>> getRestaurantPredictionsLiveData() {
        return mRestaurantPredictions;
    }

    public String getCurrentSearchQuery() { return mCurrentSearchQuery; }
    public void setCurrentSearchQuery(String currentSearchQuery) { mCurrentSearchQuery = currentSearchQuery; }
    public void setDefaultSearchRadius(int defaultSearchRadius) { mDefaultSearchRadius = defaultSearchRadius; }

    /**
     * Request restaurant autocomplete predictions.
     * @param location current location
     * @param textInput user search input
     */

    public void requestRestaurantPredictions(Location location, String textInput) {
        if (textInput == null || containsPrediction(textInput)) return;

        GoogleMapsApiClient.getPlacesPredictions(location, mDefaultSearchRadius, textInput).enqueue(new Callback<MapsPlacePredictionsPage>() {
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
