package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.app.rogerpales.cryptocoinnotifier.api.model.Alert
import com.app.rogerpales.cryptocoinnotifier.api.model.Deletable
import com.app.rogerpales.cryptocoinnotifier.api.model.User
import com.app.rogerpales.cryptocoinnotifier.api.service.ApiClient
import com.app.rogerpales.cryptocoinnotifier.api.service.RetrofitClient
import com.app.rogerpales.cryptocoinnotifier.lib.AppUtils
import com.google.gson.Gson

abstract class AppActivity : AppCompatActivity() {
    var currentUser : User?                     = null
    var userAlerts  : List<Alert>?              = null
    var authToken   : String?                   = null
    val gson        : Gson                      = Gson()
    val apiClient   : ApiClient                 = RetrofitClient.getClient("http://206.189.19.242/")!!.create(ApiClient::class.java)
    var prefs       : SharedPreferences?        = null
    var prefsEditor : SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        prefsEditor = prefs!!.edit()
    }

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