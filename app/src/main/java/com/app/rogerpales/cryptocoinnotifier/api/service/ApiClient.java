package com.app.rogerpales.cryptocoinnotifier.api.service;


import com.app.rogerpales.cryptocoinnotifier.api.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiClient {

    @POST("register")
    Call<User> registerUser(@Body LoginRequest loginRequest);

    @POST("session")
    Call<User> createSession(@Body LoginRequest loginRequest);
}

