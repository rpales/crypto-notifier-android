package com.app.rogerpales.cryptocoinnotifier.api.model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Alert (
        @SerializedName("id") var id: Int?,
        @SerializedName("active") var active: Boolean?,
        @SerializedName("name") var name: String?,
        @SerializedName("frequency") var frequency: String?,
        @SerializedName("last_notification_at") var lastNotificationAt: Date?,
        @SerializedName("created_at") var createdAt: Date?,
        @SerializedName("updated_at") var updatedAt: Date?,
        @SerializedName("user_id") var userId: Int?,
        @SerializedName("conditions") var conditions: List<CryptoCondition>?
) : Serializable, Deletable {
    @SerializedName("deleted") override var deleted : Boolean = false

    fun toJson(gson: Gson): String? {
        return gson.toJson(this)
    }

    fun getConditions(includeDeleted: Boolean = false) : List<CryptoCondition> {
        if (conditions != null) {
            for (condition: CryptoCondition in conditions!!) {
            }
            var conditionsList : MutableList<CryptoCondition> = conditions!!.toMutableList()
            if (includeDeleted) {
                return conditions!!
            }
            return conditionsList.filter { !it.deleted }.toList()
        } else {
            return listOf()
        }
    }
}