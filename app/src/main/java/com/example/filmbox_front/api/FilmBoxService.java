package com.example.filmbox_front.api;

import com.example.filmbox_front.models.Film;
import com.example.filmbox_front.models.ReviewResponse;
import com.example.filmbox_front.models.ReviewRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FilmBoxService {

    @GET("movies/{movie_id}/")
    Call<Film> getMovieDetail(@Path("movie_id") int movieId);

    @GET("reviews/{id}/")
    Call<ReviewResponse> getMovieReviews(@Path("id") int movieId, @Query("all") boolean all);

    @PUT("reviews/{id}/")
    Call<ReviewResponse.Review> postMovieReview(@Path("id") int movieId, @Body ReviewRequest review);

    @PUT("watched/{movie_id}/")
    Call<Void> markFilmAsWatched(@Path("movie_id") int movieId);

    @DELETE("watched/{movie_id}/")
    Call<Void> unmarkFilmAsWatched(@Path("movie_id") int movieId);

    @PUT("favorites/{movie_id}/")
    Call<Void> addFilmToFavorites(@Path("movie_id") int movieId);

    @DELETE("favorites/{movie_id}/")
    Call<Void> removeFilmFromFavorites(@Path("movie_id") int movieId);

    @PUT("wishlist/{movie_id}/")
    Call<Void> addFilmToWishlist(@Path("movie_id") int movieId);

    @DELETE("wishlist/{movie_id}/")
    Call<Void> removeFilmFromWishlist(@Path("movie_id") int movieId);

    @GET("search/movies/")
    Call<List<Film>> searchMovies(@Query("query") String query);

    // Otros endpoints si son necesarios
}

