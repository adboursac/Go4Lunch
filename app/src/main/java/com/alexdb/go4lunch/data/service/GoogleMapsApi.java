package com.alexdb.go4lunch.data.service;

import android.location.Location;

import com.alexdb.go4lunch.BuildConfig;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetailsPage;
import com.alexdb.go4lunch.data.model.maps.MapsPlacePredictionsPage;
import com.alexdb.go4lunch.data.model.maps.MapsPlacesPage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleMapsApi {

    private final Gson gson = new GsonBuilder().setLenient().create();
    private final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    private GoogleMapsApiRequest request() {
        return retrofit.create(GoogleMapsApiRequest.class);
    }

    public Call<MapsPlacesPage> getRestaurantPlaces(Location location) {
        return request().getPlaces(locationToString(location),
                "restaurant",
                BuildConfig.google_maps_api_key);
    }

    public Call<MapsPlacesPage> getPlacesPage(String pageToken) {
        return request().getPlacesPage(
                pageToken,
                BuildConfig.google_maps_api_key);
    }

    public String getPictureUrl(String mapsPhotoReference) {
        if (mapsPhotoReference == null) return null;
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                + mapsPhotoReference
                + "&key=" + BuildConfig.google_maps_api_key;
    }

    public Call<MapsPlaceDetailsPage> getPlaceDetails(String placeId) {
        return request().getPlaceDetails(placeId,
                "place_id,geometry,name,opening_hours,website,international_phone_number,formatted_address,rating,photos",
                BuildConfig.google_maps_api_key);
    }

    public Call<MapsPlacePredictionsPage> getPlacesPredictions(Location location, int radius, String input) {
        return request().getPlacesPredictions(locationToString(location),
                radius,
                input,
                "establishment",
                "true",
                BuildConfig.google_maps_api_key);
    }

    private static String locationToString(Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }
}