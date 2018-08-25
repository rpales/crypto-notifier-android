package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.app.rogerpales.cryptocoinnotifier.api.model.Alert
import com.app.rogerpales.cryptocoinnotifier.api.model.Deletable
import com.app.rogerpales.cryptocoinnotifier.api.model.User
import com.app.rogerpales.cryptocoinnotifier.api.service.ApiClient
import com.app.rogerpales.cryptocoinnotifier.api.service.LoginRequest
import com.app.rogerpales.cryptocoinnotifier.api.service.RetrofitClient
import com.app.rogerpales.cryptocoinnotifier.lib.AppUtils
import com.google.gson.Gson
import com.onesignal.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class AppActivity : AppCompatActivity(), OSPermissionObserver, OSSubscriptionObserver {
    var currentUser : User?                     = null
    var userAlerts  : List<Alert>?              = null
    var authToken   : String?                   = null
    val gson        : Gson                      = Gson()
    val apiClient   : ApiClient                 = RetrofitClient.getClient("http://206.189.19.242/")!!.create(ApiClient::class.java)
    var prefs       : SharedPreferences?        = null
    var prefsEditor : SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init()
//        OneSignal.addSubscriptionObserver(this)
        prefs = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        prefsEditor = prefs!!.edit()
    }

    // ---- OneSignal ------------------------------------------------------------------------------
    override fun onOSPermissionChanged(stateChanges: OSPermissionStateChanges) {
        if (stateChanges.from.enabled && !stateChanges.to.enabled) {
            Toast.makeText(this@AppActivity, "Please enable notifications. Settings > Apps & notifications > Notifications", Toast.LENGTH_LONG).show()
        }

        Log.i("Debug", "onOSPermissionChanged: $stateChanges")
    }

    override fun onOSSubscriptionChanged(stateChanges: OSSubscriptionStateChanges) {
        if (!stateChanges.from.subscribed && stateChanges.to.subscribed) {
            Toast.makeText(this@AppActivity, "Updating device ID..", Toast.LENGTH_SHORT).show()
            // get player ID
            stateChanges.to.userId

            updateDeviceId(stateChanges.to.userId)
        }

        Log.i("Debug", "onOSPermissionChanged: $stateChanges")
    }

    private fun updateDeviceId(deviceId: String?) {
        val apiClient : ApiClient = RetrofitClient.getClient(getString(R.string.API_BASE_URL))!!.create(ApiClient::class.java)
        val loginRequest = LoginRequest(null, null, deviceId)
        apiClient.updateMe(authToken, loginRequest).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful()) {
                    Toast.makeText(this@AppActivity, "device ID has been updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AppActivity, "unknown error updating device_id", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@AppActivity, "unknown error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ---- end of OneSignal -----------------------------------------------------------------------

    override fun onStart() {
        super.onStart()
        loadPreferences()
        if (authToken == null || authToken == "") {
            goToLogin()
        }
        if (userAlerts == null) { userAlerts = currentUser?.alerts }
    }

    open fun loadPreferences() {
        authToken   = prefs!!.getString("authToken", null)
        currentUser = AppUtils.deserializeUser(prefs!!.getString("currentUser", ""))
        userAlerts  = AppUtils.deserializeAlertsList(prefs!!.getString("userAlerts", ""))
    }

    // -------------- go to activities --------------

    fun goToLogin() {
        currentUser = null
        userAlerts  = null
        authToken = null

        prefsEditor!!.remove("authToken")
        prefsEditor!!.remove("currentUser")
        prefsEditor!!.remove("userAlerts")
        prefsEditor!!.remove("currentAlert")
        prefsEditor!!.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    abstract fun showMessage(message: String?)

    fun sizeOf(list: List<Deletable>?): Int {
        var count = 0
        if (list != null) {
            for (item: Deletable in list) {
                if (!item.deleted) { count += 1 }
            }
        }
        return count
    }

    fun errorCallaback(rawResponse: String) {
        val err = AppUtils.deserializeApiError(rawResponse)
        if (err != null) {
            for(message in err.errorArray){
                showMessage(message)
            }
        } else {
            showMessage(rawResponse)
        }
    }
}