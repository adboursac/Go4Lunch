package com.alexdb.go4lunch.data.service;

import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetailsPage;
import com.alexdb.go4lunch.data.model.maps.MapsPlacePredictionsList;
import com.alexdb.go4lunch.data.model.maps.MapsPlacesPage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsApiRequest {
    @GET("place/nearbysearch/json?rankby=distance")
    Call<MapsPlacesPage> getPlaces(
            @Query("location") String location,
            @Query("keyword") String keyword,
            @Query("key") String key
    );

    @GET("place/nearbysearch/json")
    Call<MapsPlacesPage> getPlacesPage(
            @Query("pagetoken") String pagetoken,
            @Query("key") String key
    );

    @GET("place/details/json")
    Call<MapsPlaceDetailsPage> getPlaceDetails(
            @Query("place_id") String place_id,
            @Query("fields") String fields,
            @Query("key") String key
    );

    @GET("place/autocomplete/json")
    Call<MapsPlacePredictionsList> getPlacesPredictions(
            @Query("location") String location,
            @Query("radius") Integer radius,
            @Query("input") String input,
            @Query("types") String types,
            @Query("key") String key
    );
}
