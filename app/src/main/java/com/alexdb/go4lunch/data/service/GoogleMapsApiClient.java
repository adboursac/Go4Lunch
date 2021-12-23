package com.alexdb.go4lunch.data.service;

import android.location.Location;

import com.alexdb.go4lunch.BuildConfig;
import com.alexdb.go4lunch.data.model.maps.RestaurantPlacesPage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleMapsApiClient {

    private static final Gson gson = new GsonBuilder().setLenient().create();
    private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    private static GoogleMapsApiRequest getApi() {
        return retrofit.create(GoogleMapsApiRequest.class);
    }

    public static Call<RestaurantPlacesPage> getRestaurantPlaces(Location location) {
        String locationString = location.getLatitude() + "," + location.getLongitude();
        return getApi().getRestaurantPlaces(locationString,
                "restaurant",
                BuildConfig.google_maps_api_key);
    }

    public static Call<RestaurantPlacesPage> getRestaurantPlacesPage(String pageToken) {
        return getApi().getRestaurantPlacesPage(
                pageToken,
                BuildConfig.google_maps_api_key);
    }
}
