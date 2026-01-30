package com.example.filmbox_front;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface ApiService {
    @GET("/api/categories")
    Call<List<Category>> getCategories();

    @GET("/api/movies")
    Call<List<FilmLite>> searchMovies(@Query("query") String query);

    @GET("/api/users")
    Call<List<UserLite>> searchUsers(@Query("query") String query);
}
