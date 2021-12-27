package com.alexdb.go4lunch.data.repository;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.model.maps.MapsPlacesPage;
import com.alexdb.go4lunch.data.service.GoogleMapsApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantPlacesRepository {

    private final Executor mExecutor;
    private final MutableLiveData<List<MapsPlace>> mRestaurantPlacesMutableLiveData = new MutableLiveData<>();

    public LiveData<List<MapsPlace>> getRestaurantPlacesLiveData() {
        return mRestaurantPlacesMutableLiveData;
    }

    public RestaurantPlacesRepository(Executor executor) {
        mExecutor = executor;
    }

    /**
     * Fetch restaurants places for given location.
     * @param location last location of the device.
     */
    public void fetchRestaurantPlaces(Location location) {
        if (location == null) return;

        GoogleMapsApiClient.getRestaurantPlaces(location).enqueue(new Callback<MapsPlacesPage>() {
            @Override
            public void onResponse(@NonNull Call<MapsPlacesPage> call, @NonNull Response<MapsPlacesPage> response) {
                if (response.isSuccessful()) {
                    MapsPlacesPage placesPage = response.body();
                    if (placesPage != null) {
                        mRestaurantPlacesMutableLiveData.setValue(placesPage.getResults());

                        /*
                        //request for more results if possible
                        if (placesPage.getNext_page_token() != null) {
                            fetchRestaurantPlacesPage(placesPage.getNext_page_token(), true);
                        }
                         */
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapsPlacesPage> call, @NonNull Throwable t) {
                Log.d("RestaurantRepository", "fetchRestaurantPlaces failure" + t);
            }
        });
    }

    /**
     * Fetch places page from given page token.
     * If allowRetry is set to true, the request will be retried in a background thread if request receives INVALID_REQUEST status.
     * @param pageToken page token
     * @param allowRetry true if retry is allowed, false instead
     */
    private void fetchRestaurantPlacesPage(String pageToken, boolean allowRetry) {

        GoogleMapsApiClient.getPlacesPage(pageToken).enqueue(new Callback<MapsPlacesPage>() {
            @Override
            public void onResponse(@NonNull Call<MapsPlacesPage> call, @NonNull Response<MapsPlacesPage> response) {
                if (response.isSuccessful()) {
                    MapsPlacesPage placesPage = response.body();
                    if (placesPage != null) {
                        if (handlePageTokenInvalidRequest(pageToken, placesPage.getStatus(), allowRetry)) return;
                        List<MapsPlace> combinedList = new ArrayList<>();
                        combinedList.addAll(Objects.requireNonNull(mRestaurantPlacesMutableLiveData.getValue()));
                        combinedList.addAll(placesPage.getResults());
                        mRestaurantPlacesMutableLiveData.setValue(combinedList);

                        //request for more results if possible
                        if (placesPage.getNext_page_token() != null) {
                            fetchRestaurantPlacesPage(placesPage.getNext_page_token(), true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapsPlacesPage> call, @NonNull Throwable t) {
                Log.d("RestaurantRepository", "getRestaurantsPage failure" + t);
            }
        });
    }

    /**
     * Check page request status. If status is INVALID_REQUEST and allowRetry set to true,
     * it retries the request in a background thread after waiting for 2 seconds.
     * @param pageToken requested page token
     * @param status the status of the request
     * @param allowRetry true if retry in background is allowed.
     * @return true if status is INVALID_REQUEST
     */
    public boolean handlePageTokenInvalidRequest(String pageToken, String status, boolean allowRetry) {
        if (status.contentEquals("INVALID_REQUEST")) {
            if (!allowRetry) return true;
            Log.d("RestaurantRepository", "getRestaurantsPage status: INVALID_REQUEST, retrying in background thread");
            mExecutor.execute(() -> {
                try {
                    Thread.sleep(2000);
                    fetchRestaurantPlacesPage(pageToken, false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }
}
