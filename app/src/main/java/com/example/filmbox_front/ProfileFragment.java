package com.example.filmbox_front;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {

    private ApiService api;
    private String sessionToken = "";
    private String username = "Usuario";

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener argumentos de forma segura
        if(getArguments() != null) {
            sessionToken = getArguments().getString("SESSION_TOKEN", "");
            username = getArguments().getString("USERNAME", "Usuario");
        }

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);

        // Foto de perfil predeterminada
        ImageView profileImage = view.findViewById(R.id.profile_image);
        profileImage.setImageResource(R.drawable.ic_profile_grey);

        // Nombre de usuario
        TextView usernameText = view.findViewById(R.id.username_text);
        usernameText.setText(username);

        // Cargar películas solo si hay token
        if(!sessionToken.isEmpty()) {
            loadWatchedMovies(view);
            loadFavoriteMovies(view);
            loadWishlistMovies(view);
        }
    }

    private void loadWatchedMovies(View view) {
        api.getWatched("Bearer " + sessionToken).enqueue(new Callback<List<FilmResponse>>() {
            @Override
            public void onResponse(Call<List<FilmResponse>> call, Response<List<FilmResponse>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<String> urls = new ArrayList<>();
                    for(FilmResponse f : response.body()) urls.add(f.image_url);

                    RecyclerView watchedRecycler = view.findViewById(R.id.watched_recycler);
                    watchedRecycler.setLayoutManager(
                            new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    watchedRecycler.setAdapter(new MovieAdapter(getContext(), urls, position -> {
                        // click en la película
                    }));
                }
            }

            @Override
            public void onFailure(Call<List<FilmResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loadFavoriteMovies(View view) {
        api.getFavorites("Bearer " + sessionToken)
                .enqueue(new Callback<List<FilmResponse>>() {

                    @Override
                    public void onResponse(Call<List<FilmResponse>> call,
                                           Response<List<FilmResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<String> urls = new ArrayList<>();
                            for (FilmResponse f : response.body()) {
                                urls.add(f.image_url);
                            }

                            RecyclerView favoritesRecycler =
                                    view.findViewById(R.id.favorites_recycler);

                            favoritesRecycler.setLayoutManager(
                                    new LinearLayoutManager(
                                            requireContext(),
                                            LinearLayoutManager.HORIZONTAL,
                                            false
                                    )
                            );

                            favoritesRecycler.setAdapter(
                                    new MovieAdapter(requireContext(), urls, position -> {
                                        // click en favorita
                                    })
                            );
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FilmResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void loadWishlistMovies(View view) {
        api.getWishlist("Bearer " + sessionToken).enqueue(new Callback<List<FilmResponse>>() {
            @Override
            public void onResponse(Call<List<FilmResponse>> call, Response<List<FilmResponse>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<String> urls = new ArrayList<>();
                    for(FilmResponse f : response.body()) urls.add(f.image_url);

                    RecyclerView wishlistRecycler = view.findViewById(R.id.wishlist_recycler);
                    wishlistRecycler.setLayoutManager(
                            new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    wishlistRecycler.setAdapter(new MovieAdapter(getContext(), urls, position -> {
                        // click en la película
                    }));
                }
            }

            @Override
            public void onFailure(Call<List<FilmResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
