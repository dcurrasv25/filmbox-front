package com.example.filmbox_front.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/api/"; // Usa 10.0.2.2 para el emulador de Android
    private static Retrofit retrofit = null;

    public static FilmBoxService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(FilmBoxService.class);
    }
}

