package com.app.rogerpales.cryptocoinnotifier.api.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Alert(
        @SerializedName("id") var id: Integer?,
        @SerializedName("active") var active: Boolean?,
        @SerializedName("name") var name: String?,
        @SerializedName("frequency") var frequency: String?,
        @SerializedName("last_notification_at") var lastNotificationAt: Date?,
        @SerializedName("created_at") var createdAt: Date?,
        @SerializedName("updated_at") var updatedAt: Date?,
        @SerializedName("user_id") var userId: Integer?,
        @SerializedName("conditions") var conditions: Array<CryptoCondition>?
) : Serializable {
    fun toJson(gson: Gson): String? {
        return gson.toJson(this)
    }
}