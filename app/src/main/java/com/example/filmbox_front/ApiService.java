package com.example.filmbox_front;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiService {

    @GET("watched/")
    Call<List<FilmResponse>> getWatched(@Header("Authorization") String token);

    @GET("favorites/")
    Call<List<FilmResponse>> getFavorites(@Header("Authorization") String token);

    @GET("wishlist/")
    Call<List<FilmResponse>> getWishlist(@Header("Authorization") String token);
}

