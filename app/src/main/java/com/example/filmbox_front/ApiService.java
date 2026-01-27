package com.example.filmbox_front;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("register")
    Call<RegisterResponse> registerUser(@Body UserRegistration user);

}
