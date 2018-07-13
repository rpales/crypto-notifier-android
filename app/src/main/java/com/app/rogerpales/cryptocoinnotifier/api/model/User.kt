package com.app.rogerpales.cryptocoinnotifier.api.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
        @SerializedName("id") val id: Integer?,
        @SerializedName("name") val name: String?,
        @SerializedName("email") val email: String?,
        @SerializedName("device_id") val deviceId: String?,
        @SerializedName("authentication_token") val authenticationToken: String?,
        @SerializedName("country_code") val countryCode: String?,
        @SerializedName("archived") val archived: Boolean?,
        @SerializedName("confirmed_email") val confirmedEmail: Boolean?,
        @SerializedName("alerts") val alerts: List<Alert>?
) : Serializable {
    fun toJson(gson: Gson): String? {
        return gson.toJson(this)
    }
}