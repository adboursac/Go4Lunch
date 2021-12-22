package com.alexdb.go4lunch.data.service;

import com.alexdb.go4lunch.data.model.maps.RestaurantPlacesPage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsApiRequest {
    @GET("place/nearbysearch/json?rankby=distance")
    Call<RestaurantPlacesPage> getRestaurantPlaces(
            @Query("location") String location,
            @Query("keyword") String keyword,
            @Query("key") String key
    );

    @GET("place/nearbysearch/json")
    Call<RestaurantPlacesPage> getRestaurantPlacesPage(
            @Query("pagetoken") String pagetoken,
            @Query("key") String key
    );
}
