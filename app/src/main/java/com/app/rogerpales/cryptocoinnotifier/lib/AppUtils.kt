package com.app.rogerpales.cryptocoinnotifier.lib

import android.util.Log
import com.app.rogerpales.cryptocoinnotifier.api.model.Alert
import com.app.rogerpales.cryptocoinnotifier.api.model.ApiError
import com.app.rogerpales.cryptocoinnotifier.api.model.CryptoCondition
import com.app.rogerpales.cryptocoinnotifier.api.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.RoundingMode
import java.text.DecimalFormat

class AppUtils {
    companion object {
        // deserialize functions

        fun deserializeUser(JSONstring: String, throwError: Boolean = false): User? {
            var user : User? = null

            if (throwError) {
                user = Gson().fromJson<User>(JSONstring, User::class.java)
            } else {
                try {
                    user = Gson().fromJson<User>(JSONstring, User::class.java)
                } catch (t: Throwable) {
                    Log.e("deserialize error", "string: $JSONstring")
                }
            }

            return user
        }

        fun deserializeAlert(JSONstring: String, throwError: Boolean = false): Alert? {
            var alert : Alert? = null

            if (throwError) {
                alert = Gson().fromJson<Alert>(JSONstring, Alert::class.java)
            } else {
                try {
                    alert = Gson().fromJson<Alert>(JSONstring, Alert::class.java)
                } catch (t: Throwable) {
                    Log.e("deserialize error", "string: $JSONstring")
                }
            }

            return alert
        }

        fun deserializeAlertsList(JSONstring: String, throwError: Boolean = false): List<Alert>? {
            var alertsList : List<Alert>? = null
            val listType = object : TypeToken<List<Alert>>() { }.type

            if (throwError) {
                alertsList = Gson().fromJson<List<Alert>>(JSONstring, listType)
            } else {
                try {
                    alertsList = Gson().fromJson<List<Alert>>(JSONstring, listType)
                } catch (t: Throwable) {
                    Log.e("deserialize error", "string: $JSONstring")
                }
            }

            return alertsList
        }

        fun deserializeCondition(JSONstring: String, throwError: Boolean = false): CryptoCondition? {
            var condition : CryptoCondition? = null

            if (throwError) {
                condition = Gson().fromJson<CryptoCondition>(JSONstring, CryptoCondition::class.java)
            } else {
                try {
                    condition = Gson().fromJson<CryptoCondition>(JSONstring, CryptoCondition::class.java)
                } catch (t: Throwable) {
                    Log.e("deserialize error", "string: $JSONstring")
                }
            }

            return condition
        }

        fun deserializeConditionsList(JSONstring: String, throwError: Boolean = false): List<CryptoCondition>? {
            var conditionsList : List<CryptoCondition>? = null
            val listType = object : TypeToken<List<CryptoCondition>>() { }.type

            if (throwError) {
                conditionsList = Gson().fromJson<List<CryptoCondition>>(JSONstring, listType)
            } else {
                try {
                    conditionsList = Gson().fromJson<List<CryptoCondition>>(JSONstring, listType)
                } catch (t: Throwable) {
                    Log.e("deserialize error", "string: $JSONstring")
                }
            }

            return conditionsList
        }

        fun deserializeApiError(JSONstring : String, throwError: Boolean = false): ApiError? {
            var apiError : ApiError? = null

            if (throwError) {
                apiError = Gson().fromJson<ApiError>(JSONstring, ApiError::class.java)
            } else {
                try {
                    apiError = Gson().fromJson<ApiError>(JSONstring, ApiError::class.java)
                } catch (t: Throwable) {
                    Log.e("deserialize error", "string: $JSONstring")
                }
            }

            return apiError
        }

        fun floatToDecimalString(number: Number?): String {
            if (number != null) {
                val decimalFormat = DecimalFormat("#,##0.00")
                return decimalFormat.format(number)
            }
            return "0.00"
        }

        fun stringToFloat(number: String?): Float {
            if (number != null) {
                return number.replace(",", "").toFloat()
            }
            return 0.00f
        }
    }
}
