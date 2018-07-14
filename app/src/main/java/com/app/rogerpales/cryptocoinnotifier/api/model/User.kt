package com.app.rogerpales.cryptocoinnotifier.api.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
        @SerializedName("id") var id: Int?,
        @SerializedName("name") var name: String?,
        @SerializedName("email") var email: String?,
        @SerializedName("device_id") var deviceId: String?,
        @SerializedName("authentication_token") var authenticationToken: String?,
        @SerializedName("country_code") var countryCode: String?,
        @SerializedName("archived") var archived: Boolean?,
        @SerializedName("confirmed_email") var confirmedEmail: Boolean?,
        @SerializedName("alerts") var alerts: List<Alert>?
) : Serializable {
    fun toJson(gson: Gson): String? {
        return gson.toJson(this)
    }
}