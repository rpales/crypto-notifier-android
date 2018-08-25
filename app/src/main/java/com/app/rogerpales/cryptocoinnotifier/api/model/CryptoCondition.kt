package com.app.rogerpales.cryptocoinnotifier.api.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class CryptoCondition(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("from_coin") var fromCoin: String? = null,
        @SerializedName("to_coin") var toCoin: String? = null,
        @SerializedName("unit") var unit: String? = null,
        @SerializedName("condition_type") var conditionType: String? = null,
        @SerializedName("value") var value: Float? = null,
        @SerializedName("period_time") var periodTime: Int? = null,
        @SerializedName("readings") var conditions: Array<Float>? = null,
        @SerializedName("readings_number") var readingsNumber: Int? = null,
        @SerializedName("created_at") var createdAt: Date? = null,
        @SerializedName("updated_at") var updatedAt: Date? = null,
        @SerializedName("alert_id") var alertId: Int? = null
) : Serializable, Deletable {
    @SerializedName("deleted") override var deleted : Boolean = false

    fun toJson(gson: Gson): String? {
        return gson.toJson(this)
    }

    fun description(): String {
        val type = when (conditionType) {
            "PRICE_ABOVE" -> "↑"
            "PRICE_BELOW" -> "↓"
            "PRICE_INCREMENT" -> "Δ"
            "VOLUME24HOUR_ABOVE" -> "v↑"
            "VOLUME24HOUR_BELOW" -> "v↓"
            "VOLUME24HOUR_INCREMENT" -> "vΔ"
            "SMA_ABOVE" -> "sma↑"
            "SMA_BELOW" -> "sma↓"
            else -> ""
        }
        
        return "$fromCoin/$toCoin $type ${value.toString()}"
    }
}