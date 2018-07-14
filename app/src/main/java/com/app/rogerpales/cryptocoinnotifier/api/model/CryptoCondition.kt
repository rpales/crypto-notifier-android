package com.app.rogerpales.cryptocoinnotifier.api.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class CryptoCondition(
        @SerializedName("id") var id: Integer?,
        @SerializedName("from_coin") var fromCoin: String?,
        @SerializedName("to_coin") var toCoin: String?,
        @SerializedName("unit") var unit: String?,
        @SerializedName("condition_type") var conditionType: String?,
        @SerializedName("value") var value: Float?,
        @SerializedName("period_time") var periodTime: Integer?,
        @SerializedName("readings") var conditions: Array<Float>?,
        @SerializedName("readings_number") var readingsNumber: Integer?,
        @SerializedName("created_at") var createdAt: Date?,
        @SerializedName("updated_at") var updatedAt: Date?,
        @SerializedName("alert_id") var alertId: Integer?
) : Serializable, Deletable {
    @SerializedName("deleted") override var deleted : Boolean = false

    fun toJson(gson: Gson): String? {
        return gson.toJson(this)
    }
}