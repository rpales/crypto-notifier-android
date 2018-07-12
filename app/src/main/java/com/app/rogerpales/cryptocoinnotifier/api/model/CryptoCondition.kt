package com.app.rogerpales.cryptocoinnotifier.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class CryptoCondition(
        @SerializedName("id") val id: Integer?,
        @SerializedName("from_coin") val fromCoin: String?,
        @SerializedName("to_coin") val toCoin: String?,
        @SerializedName("unit") val unit: String?,
        @SerializedName("condition_type") val conditionType: String?,
        @SerializedName("value") val value: Float?,
        @SerializedName("period_time") val periodTime: Integer?,
        @SerializedName("readings") val conditions: Array<Float>?,
        @SerializedName("readings_number") val readingsNumber: Integer?,
        @SerializedName("created_at") val createdAt: Date?,
        @SerializedName("updated_at") val updatedAt: Date?,
        @SerializedName("alert_id") val alertId: Integer?
) : Serializable