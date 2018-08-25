package com.app.rogerpales.cryptocoinnotifier.api.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CurrentData (
        @SerializedName("from_coin") var fromCoin: String? = null,
        @SerializedName("to_coin") var toCoin: String? = null,
        @SerializedName("price")  val price: Float?,
        @SerializedName("volume") val volume: Float?
) : Serializable {
    fun toJson(gson: Gson): String? {
        return gson.toJson(this)
    }
}