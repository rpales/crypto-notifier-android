package com.app.rogerpales.cryptocoinnotifier.api.model

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("id") val id: Integer,
        @SerializedName("name") val name: String,
        @SerializedName("email") val image: String,
        @SerializedName("device_id") val deviceId: String,
        @SerializedName("authentication_token") val authenticationToken: String,
        @SerializedName("country_code") val countryCode: String,
        @SerializedName("archived") val archived: Boolean,
        @SerializedName("confirmed_email") val confirmedEmail: Boolean
        )