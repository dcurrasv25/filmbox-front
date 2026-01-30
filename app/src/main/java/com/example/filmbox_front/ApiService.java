package com.example.filmbox_front;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // Las rutas deben coincidir EXACTAMENTE con Django (sin barra final)
    @GET("movies")
    Call<List<Film>> getFilms();

    @GET("categories")
    Call<List<Category>> getCategories();

    // Método adicional por si lo necesitas: obtener películas de una categoría
    @GET("categories/{category_id}/movies")
    Call<List<Film>> getMoviesByCategory(@Path("category_id") int categoryId);

    @PUT("favorites/{movie_id")
    Call<Void> addFavorite(@Path("movie_id") int movieId);

    @DELETE("favorites/{movie_id")
    Call<Void> removeFavorite(@Path("movie_id") int movieId);
}