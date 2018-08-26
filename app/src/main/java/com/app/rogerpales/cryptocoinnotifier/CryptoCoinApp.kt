package com.app.rogerpales.cryptocoinnotifier

import android.app.Application
import android.content.Context
import android.util.Log
import com.app.rogerpales.cryptocoinnotifier.lib.CryptoNotificiationOpenedHandler
import com.onesignal.*

class CryptoCoinApp : Application(), OSPermissionObserver, OSSubscriptionObserver {

    companion object {
        lateinit var instance: Context private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.WARN)

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(CryptoNotificiationOpenedHandler(instance))
                .autoPromptLocation(true)
                .init()
    }

    // ---- OneSignal ------------------------------------------------------------------------------
    override fun onOSPermissionChanged(stateChanges: OSPermissionStateChanges) {
        if (stateChanges.from.enabled && !stateChanges.to.enabled) {
//            Toast.makeText(this@AppActivity, "Please enable notifications. Settings > Apps & notifications > Notifications", Toast.LENGTH_LONG).show()
        }

        Log.i("Debug", "onOSPermissionChanged: $stateChanges")
    }

    override fun onOSSubscriptionChanged(stateChanges: OSSubscriptionStateChanges) {
        if (!stateChanges.from.subscribed && stateChanges.to.subscribed) {
            val deviceId = stateChanges.to.userId
            updateDeviceId(deviceId)
            Log.d("onOSSubscriptionChanged", "update deviceId: $deviceId")
        } else if (stateChanges.from.subscribed && !stateChanges.to.subscribed) {
            updateDeviceId("")
            Log.d("onOSSubscriptionChanged", "update deviceId: \"\"")
        } else {
            Log.d("onOSSubscriptionChanged", "$stateChanges")
        }
    }

    private fun updateDeviceId(deviceId: String?) {
//        val apiClient : ApiClient = RetrofitClient.getClient(getString(R.string.API_BASE_URL))!!.create(ApiClient::class.java)
//        val loginRequest = LoginRequest(null, null, deviceId)
//        apiClient.updateMe(authToken, loginRequest).enqueue(object : Callback<User> {
//
//            override fun onResponse(call: Call<User>, response: Response<User>) {
//                if (response.isSuccessful()) {
//                    Log.d("udpateDeviceId", "success, deviceId: $deviceId")
//                } else {
//                    Log.d("udpateDeviceId", "API error: ${response.errorBody()!!.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<User>, t: Throwable) {
//                Toast.makeText(this@AppActivity, "unknown error", Toast.LENGTH_SHORT).show()
//            }
//        })
        Log.d("updateDeviceId", "to implement")
    }

}