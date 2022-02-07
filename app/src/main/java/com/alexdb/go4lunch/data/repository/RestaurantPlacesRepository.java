package com.alexdb.go4lunch.data.repository;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetailsPage;
import com.alexdb.go4lunch.data.model.maps.MapsPlacesPage;
import com.alexdb.go4lunch.data.service.GoogleMapsApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Provide places list with liveData
 * List of places is updated according to location criteria as MapsPlace model objects
 */
public class RestaurantPlacesRepository {

    public static final int RETRY_DELAY = 5000;

    private final GoogleMapsApi mGoogleMapsApi;
    private final Executor mExecutor;
    private final MutableLiveData<List<MapsPlace>> mRestaurantPlacesMutableLiveData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<MapsPlace>> getRestaurantPlacesLiveData() {
        return mRestaurantPlacesMutableLiveData;
    }

    public RestaurantPlacesRepository(GoogleMapsApi googleMapsApi) {
        mGoogleMapsApi = googleMapsApi;
        mExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Fetch restaurants places for given location.
     *
     * @param location last location of the device.
     */
    public void fetchRestaurantPlaces(Location location) {
        if (location == null) return;

        mGoogleMapsApi.getRestaurantPlaces(location).enqueue(new Callback<MapsPlacesPage>() {
            @Override
            public void onResponse(@NonNull Call<MapsPlacesPage> call, @NonNull Response<MapsPlacesPage> response) {
                MapsPlacesPage placesPage = response.body();
                if (placesPage != null) {
                    addPlacesAvoidDuplicates(placesPage.getResults());

                        /*//request for more results if possible
                        if (placesPage.getNext_page_token() != null) {
                            //fetchRestaurantPlacesPage(placesPage.getNext_page_token(), true);
                        }
                        */
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapsPlacesPage> call, @NonNull Throwable t) {
                Log.d("RestaurantRepository", "fetchRestaurantPlaces failure" + t);
            }
        });
    }

    /*
     * Fetch places page from given page token.
     * If allowRetry is set to true, the request will be retried in a background thread if request receives INVALID_REQUEST status.
     *
     * @param pageToken  page token
     * @param allowRetry true if retry is allowed, false instead
     */
    /*
    private void fetchRestaurantPlacesPage(String pageToken, boolean allowRetry) {

        mGoogleMapsApi.getPlacesPage(pageToken).enqueue(new Callback<MapsPlacesPage>() {
            @Override
            public void onResponse(@NonNull Call<MapsPlacesPage> call, @NonNull Response<MapsPlacesPage> response) {
                if (response.isSuccessful()) {
                    MapsPlacesPage placesPage = response.body();
                    if (placesPage != null) {
                        if (handlePageTokenInvalidRequest(pageToken, placesPage.getStatus(), allowRetry))
                            return;
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
    */

    /*
     * Check page request status. If status is INVALID_REQUEST and allowRetry set to true,
     * it retries the request in a background thread after waiting for RETRY_DELAY milliseconds.
     *
     * @param pageToken  requested page token
     * @param status     the status of the request
     * @param allowRetry true if retry in background is allowed.
     * @return true if status is INVALID_REQUEST
     */
    /*
    public boolean handlePageTokenInvalidRequest(String pageToken, String status, boolean allowRetry) {
        if (status.contentEquals("INVALID_REQUEST")) {
            if (!allowRetry) return true;
            Log.d("RestaurantRepository", "getRestaurantsPage status: INVALID_REQUEST, retrying in background thread");
            mExecutor.execute(() -> {
                try {
                    Thread.sleep(RETRY_DELAY);
                    fetchRestaurantPlacesPage(pageToken, false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }
    */

    /**
     * get restaurant by its place id and add it to the current list
     *
     * @param placeId place id of the restaurant
     */
    public void requestRestaurant(String placeId) {
        if (placeId == null) return;

        // if we already have this placeId in our list, we won't fetch it again
        // we just place it at index 0
        if (findPlaceAndMoveItAtFirstIndex(placeId)) return;

        mGoogleMapsApi.getPlaceDetails(placeId).enqueue(new Callback<MapsPlaceDetailsPage>() {
            @Override
            public void onResponse(@NonNull Call<MapsPlaceDetailsPage> call, @NonNull Response<MapsPlaceDetailsPage> response) {
                MapsPlaceDetailsPage detailsPage = response.body();
                if (detailsPage != null) {
                    List<MapsPlace> currentPlaceList = mRestaurantPlacesMutableLiveData.getValue();
                    MapsPlaceDetails placeDetails = detailsPage.getResult();
                    if (placeDetails != null && currentPlaceList != null) {
                        MapsPlace place = transformPlaceDetailsToMapPlace(placeDetails);
                        currentPlaceList.add(0, place);
                        mRestaurantPlacesMutableLiveData.setValue(currentPlaceList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapsPlaceDetailsPage> call, @NonNull Throwable t) {
                Log.d("RestaurantDetailsRepo--", "requestRestaurant failure" + t);
            }
        });
    }

    public String getPictureUrl(String mapsPhotoReference) {
        return mGoogleMapsApi.getPictureUrl(mapsPhotoReference);
    }

    public MapsPlace transformPlaceDetailsToMapPlace(MapsPlaceDetails p) {
        return new MapsPlace(p.getPlace_id(),
                p.getName(),
                p.getFormatted_address(),
                p.getGeometry(),
                p.getOpening_hours(),
                p.getRating(),
                p.getPhotos());
    }

    /**
     * Find place with given id and move it at first index.
     * Returns true if element has been found, false instead.
     *
     * @param placeId id of place to move at first index/
     * @return true if place has been found, false instead.
     */
    public boolean findPlaceAndMoveItAtFirstIndex(String placeId) {
        List<MapsPlace> currentPlaceList = mRestaurantPlacesMutableLiveData.getValue();
        if (currentPlaceList == null) return false;
        for (MapsPlace p : currentPlaceList) {
            if (p.getPlaceId().contentEquals(placeId)) {
                currentPlaceList.remove(p);
                currentPlaceList.set(0, p);
                mRestaurantPlacesMutableLiveData.setValue(currentPlaceList);
                return true;
            }
        }
        return false;
    }

    /**
     * Add new places list to liveData avoiding duplicates
     *
     * @param newList list of places to add
     */
    public void addPlacesAvoidDuplicates(List<MapsPlace> newList) {
        List<MapsPlace> currentList = mRestaurantPlacesMutableLiveData.getValue();
        if (currentList == null) return;
        List<MapsPlace> checkedList = new ArrayList<>();

        for (MapsPlace newPlace : newList) {
            if (! newPlace.hasSameId(currentList)) checkedList.add(newPlace);
        }

        currentList.addAll(checkedList);
        mRestaurantPlacesMutableLiveData.setValue(currentList);
    }
}