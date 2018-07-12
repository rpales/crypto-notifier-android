package com.app.rogerpales.cryptocoinnotifier.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Alert(
        @SerializedName("id") val id: Integer?,
        @SerializedName("active") val active: Boolean?,
        @SerializedName("name") val name: String?,
        @SerializedName("frequency") val frequency: String?,
        @SerializedName("last_notification_at") val lastNotificationAt: Date?,
        @SerializedName("created_at") val createdAt: Date?,
        @SerializedName("updated_at") val updatedAt: Date?,
        @SerializedName("user_id") val userId: Integer?,
        @SerializedName("conditions") val conditions: Array<CryptoCondition>?
) : Serializable