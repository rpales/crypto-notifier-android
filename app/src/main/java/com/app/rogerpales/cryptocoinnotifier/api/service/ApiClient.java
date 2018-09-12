package com.app.rogerpales.cryptocoinnotifier.api.service;


import com.app.rogerpales.cryptocoinnotifier.api.model.Alert;
import com.app.rogerpales.cryptocoinnotifier.api.model.CoinsContainer;
import com.app.rogerpales.cryptocoinnotifier.api.model.CryptoCondition;
import com.app.rogerpales.cryptocoinnotifier.api.model.CurrentData;
import com.app.rogerpales.cryptocoinnotifier.api.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiClient {

    // users endpoints

    @POST("register")
    Call<User> registerUser(@Body LoginRequest loginRequest);

    @POST("session")
    Call<User> createSession(@Body LoginRequest loginRequest);

    @PUT("me")
    Call<User> updateMe(@Header("X-Api-Key") String authToken, @Body LoginRequest loginRequest);

    // alerts endpoints

    @GET("alerts")
    Call<List<Alert>> getAlerts(@Header("X-Api-Key") String authToken);

    @GET("alerts")
    Call<String> getAlertsRaw(@Header("X-Api-Key") String authToken);

    @POST("alerts")
    Call<Alert> createAlert(@Header("X-Api-Key") String authToken);

    @GET("alerts/{alertId}")
    Call<Alert> getAlert(@Header("X-Api-Key") String authToken, @Path("alertId") Integer alertId);

    @PUT("alerts/{alertId}")
    Call<Alert> updateAlert(@Header("X-Api-Key") String authToken, @Path("alertId") Integer alertId, @Body Alert alert);

    @DELETE("alerts/{alertId}")
    Call<Alert> deleteAlert(@Header("X-Api-Key") String authToken, @Path("alertId") Integer alertId);

    // conditions endpoints

    @GET("alerts/{alertId}/conditions")
    Call<List<CryptoCondition>> getConditions(@Header("X-Api-Key") String authToken, @Path("alertId") Integer alertId);

    @GET("alerts/{alertId}/conditions")
    Call<String> getConditionsRaw(@Header("X-Api-Key") String authToken, @Path("alertId") Integer alertId);

    @POST("alerts/{alertId}/conditions")
    Call<CryptoCondition> createCondition(@Header("X-Api-Key") String authToken, @Path("alertId") Integer alertId, @Body CryptoCondition condition);

    @PUT("alerts/{alertId}/conditions/{conditionId}")
    Call<CryptoCondition> updateCondition(@Header("X-Api-Key") String authToken, @Path("alertId") Integer alertId, @Path("conditionId") Integer conditionId, @Body CryptoCondition condition);

    @DELETE("alerts/{alertId}/conditions/{conditionId}")
    Call<CryptoCondition> deleteCondition(@Header("X-Api-Key") String authToken, @Path("alertId") Integer alertId, @Path("conditionId") Integer conditionId);

    // coins endpoints

    @GET("from_coins")
    Call<CoinsContainer> getFromCoins(@Header("X-Api-Key") String authToken);

    @GET("to_coins")
    Call<CoinsContainer> getToCoins(@Header("X-Api-Key") String authToken, @Query("from_coin") String fromCoin);

    // current data endpoints
    @GET("current_data")
    Call<CurrentData> getCurrentData(@Header("X-Api-Key") String authToken, @Query("from_coin") String fromCoin, @Query("to_coin") String toCoin);
}

