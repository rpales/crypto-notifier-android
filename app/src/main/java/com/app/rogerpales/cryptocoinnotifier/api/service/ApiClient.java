package com.app.rogerpales.cryptocoinnotifier.api.service;


import com.app.rogerpales.cryptocoinnotifier.api.model.Alert;
import com.app.rogerpales.cryptocoinnotifier.api.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiClient {

    // users endpoints

    @POST("register")
    Call<User> registerUser(@Body LoginRequest loginRequest);

    @POST("session")
    Call<User> createSession(@Body LoginRequest loginRequest);

    // alerts endpoints

    @GET("alerts")
    Call<List<Alert>> getAlerts(@Header("X-Api-Key") String authToken);

    @POST("alerts")
    Call<Alert> createAlert(@Header("X-Api-Key") String authToken, @Body Alert alert);

    @PUT("alerts/{alertId}")
    Call<Alert> updateAlert(@Header("X-Api-Key") String authToken, @Path("alertId") Integer alertId, @Body Alert alert);

    @DELETE("alerts/{alertId}")
    Call<Alert> deleteAlert(@Header("X-Api-Key") String authToken, @Path("alertId") Integer alertId);

    // conditions endpoints
}

