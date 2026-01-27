package com.example.filmbox_front;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiService {

    @GET("watched/")
    Call<List<FilmResponse>> getWatched(@Header("Authorization") String token);

    @GET("favorites/")
    Call<List<FilmResponse>> getFavorites(@Header("Authorization") String token);

    @GET("wishlist/")
    Call<List<FilmResponse>> getWishlist(@Header("Authorization") String token);

    @POST("register")
    Call<RegisterResponse> registerUser(@Body UserRegistration user);
}


