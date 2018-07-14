package com.app.rogerpales.cryptocoinnotifier.api.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CoinsContainer (
        @SerializedName("coins") val coinsList: List<String>?
) : Serializable {
    fun toJson(gson: Gson): String? {
        return gson.toJson(this)
    }
}