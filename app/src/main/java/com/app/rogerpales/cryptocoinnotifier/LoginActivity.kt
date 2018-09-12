package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.content.Intent
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.app.rogerpales.cryptocoinnotifier.api.model.User
import com.app.rogerpales.cryptocoinnotifier.api.service.ApiClient
import com.app.rogerpales.cryptocoinnotifier.api.service.LoginRequest
import com.app.rogerpales.cryptocoinnotifier.api.service.RetrofitClient
import com.app.rogerpales.cryptocoinnotifier.lib.AppUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson
import com.onesignal.*
import com.onesignal.OSSubscriptionStateChanges
import com.onesignal.OneSignal
import com.onesignal.OSPermissionSubscriptionState



class LoginActivity : AppCompatActivity(), OSPermissionObserver {
    val gson : Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        val apiClient : ApiClient = RetrofitClient.getClient(getString(R.string.API_BASE_URL))!!.create(ApiClient::class.java)
        var register = false

        super.onCreate(savedInstanceState)
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init()

        setContentView(R.layout.activity_login)

        loadPreferences()

        var submitButton = findViewById(R.id.submit_button) as Button
        var emailField = findViewById(R.id.email_field) as EditText
        var passwordField = findViewById(R.id.password_field) as EditText
        var actionSwitcher = findViewById(R.id.action_switcher) as TextView

        val prefsEditor = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE).edit()

        // remove authToken key (session)
        prefsEditor!!.remove("authToken")
        prefsEditor!!.apply()

        actionSwitcher.setOnClickListener {
            if (register) {
                register = false
                submitButton.setText("login")
                actionSwitcher.setText("register")
            } else {
                register = true
                submitButton.setText("register")
                actionSwitcher.setText("login")
            }
        }

        submitButton.setOnClickListener {

            val loginRequest = LoginRequest(emailField.text.toString(),
                                            passwordField.text.toString(),
                                            getDeviceId())
            if (register) {
                registerUser(loginRequest, apiClient)
            } else {
                createSession(loginRequest, apiClient)
            }
        }

    }

    // ---- OneSignal ------------------------------------------------------------------------------
    override fun onOSPermissionChanged(stateChanges: OSPermissionStateChanges) {
        if (stateChanges.from.enabled && !stateChanges.to.enabled) {
            Toast.makeText(this@LoginActivity, "Please enable notifications. Settings > Apps & notifications > Notifications", Toast.LENGTH_LONG).show()
        }

        Log.i("Debug", "onOSPermissionChanged: $stateChanges")
    }
    // ---- end of OneSignal -----------------------------------------------------------------------


    override fun onStart() {
        super.onStart()
        val prefsEditor = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE).edit()
        // remove authToken key (session)
        prefsEditor.remove("authToken")
        prefsEditor.apply()
    }

    // close app on back button
    override fun onBackPressed() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("EXIT", true)
        startActivity(intent)
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE)

        val emailField = findViewById(R.id.email_field) as TextView

        emailField.setText(prefs.getString("lastEmail", "") ?: "")
    }

    private fun createSession(loginRequest: LoginRequest, apiClient: ApiClient) {
        apiClient.createSession(loginRequest).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful()) {
                    successCallback(response)
                } else {
                    Toast.makeText(this@LoginActivity, "invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "unknown error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun registerUser(loginRequest: LoginRequest, apiClient: ApiClient) {
        apiClient.registerUser(loginRequest).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful()) {
                    successCallback(response)
                } else {
                    errorCallaback(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "unknown error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun successCallback(response: Response<User>) {
        val user : User? = response.body()
        val prefsEditor = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE).edit()
        prefsEditor.putString("authToken", user?.authenticationToken ?: "")
        prefsEditor.putString("lastEmail", user?.email ?: "")
        prefsEditor.putString("currentUser", user?.toJson(gson) ?: "")
        prefsEditor.apply()
        finish()
    }

    private fun errorCallaback(rawResponse: String) {
        Log.d("error response", rawResponse)
        val err = AppUtils.deserializeApiError(rawResponse)
        if (err != null) {
            for(message in err.errorArray){
                showMessage(message)
            }
        }
    }

    private fun showMessage(message: String?) {
        if (message != null) {
            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDeviceId(): String? {
        val status = OneSignal.getPermissionSubscriptionState()
        if (status.permissionStatus.enabled && status.subscriptionStatus.subscribed) {
            return status.subscriptionStatus.userId
        }
        return null
    }

}