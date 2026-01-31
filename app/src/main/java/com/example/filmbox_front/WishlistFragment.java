package com.example.filmbox_front;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WishlistFragment extends Fragment {

    private ApiService api;
    private String sessionToken = "";

    private static final String PREFS_NAME = "FilmBoxPrefs";
    private static final String TOKEN_KEY = "SESSION_TOKEN";
    /** Base del servidor para convertir URLs relativas de imágenes en absolutas */
    private static final String BASE_URL = "http://10.0.2.2:8000";

    public WishlistFragment() {
        super(R.layout.fragment_wishlist);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Token: primero el que pasó Profile/Main (arguments), si no el de SharedPreferences
        if (getArguments() != null) {
            String fromArgs = getArguments().getString("SESSION_TOKEN", "");
            if (fromArgs != null && !fromArgs.isEmpty()) {
                sessionToken = fromArgs;
            }
        }
        if (sessionToken == null || sessionToken.isEmpty()) {
            SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            sessionToken = prefs.getString(TOKEN_KEY, "");
        }

        Log.d("WishlistFragment", "Token presente: " + (sessionToken != null && !sessionToken.isEmpty()));

        // Gson que acepta lista como array [ {...} ] o como objeto { "data"/"results"/"films": [...] }
        Type listType = new TypeToken<List<FilmResponse>>() {}.getType();
        GsonConverterFactory gsonFactory = GsonConverterFactory.create(
                new GsonBuilder()
                        .registerTypeAdapter(listType, new FilmListDeserializer())
                        .create()
        );

        api = RetrofitClient.getApiService();

        // Configurar RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.wishlist_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Adaptador vacío inicial
        MovieAdapter adapter = new MovieAdapter(requireContext(), new ArrayList<>(), pos -> {});
        recyclerView.setAdapter(adapter);

        // Cargar wishlist
        loadWishlist(adapter);

        // Flecha de volver
        ImageView backArrow = view.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void loadWishlist(MovieAdapter adapter) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            Log.e("WishlistFragment", "Token vacío, no se puede cargar wishlist");
            return;
        }

        api.getWishlist("Bearer " + sessionToken).enqueue(new Callback<List<FilmResponse>>() {
            @Override
            public void onResponse(Call<List<FilmResponse>> call, Response<List<FilmResponse>> response) {
                Log.d("WishlistFragment", "Respuesta: code=" + response.code() + ", body!=null=" + (response.body() != null));
                if (response.isSuccessful() && response.body() != null) {
                    List<FilmResponse> body = response.body();
                    List<String> urls = new ArrayList<>();
                    for (FilmResponse film : body) {
                        if (film != null && film.image_url != null && !film.image_url.isEmpty()) {
                            urls.add(buildFullImageUrl(film.image_url));
                        }
                    }
                    Log.d("WishlistFragment", "Películas recibidas: " + body.size() + ", URLs de imagen: " + urls.size());

                    final List<String> urlsToSet = urls;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            adapter.updateData(urlsToSet);
                            Log.d("WishlistFragment", "Adapter actualizado con " + urlsToSet.size() + " imágenes");
                        });
                    } else {
                        adapter.updateData(urlsToSet);
                    }
                } else {
                    if (response.isSuccessful() && response.body() == null) {
                        Log.e("WishlistFragment", "Respuesta 200 pero body null: fallo al parsear JSON");
                    } else {
                        Log.e("WishlistFragment", "Error wishlist: " + response.code() + " " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FilmResponse>> call, Throwable t) {
                Log.e("WishlistFragment", "Fallo red: " + t.getMessage(), t);
            }
        });
    }

    /**
     * Convierte una URL de imagen relativa (ej. /media/posters/x.jpg) en absoluta
     * para que Picasso pueda cargarla desde el emulador.
     */
    private static String buildFullImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return imageUrl;
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) return imageUrl;
        String path = imageUrl.startsWith("/") ? imageUrl : "/" + imageUrl;
        return BASE_URL + path;
    }
}
