package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.content.Intent
import android.widget.Toast
import com.app.rogerpales.cryptocoinnotifier.api.model.User
import com.app.rogerpales.cryptocoinnotifier.api.service.ApiClient
import com.app.rogerpales.cryptocoinnotifier.api.service.LoginRequest
import com.app.rogerpales.cryptocoinnotifier.api.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val apiClient : ApiClient = RetrofitClient.getClient(getString(R.string.API_BASE_URL))!!.create(ApiClient::class.java)
        var register = false

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadPreferences()

        val prefsEditor = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE).edit()

        var submitButton = findViewById(R.id.submit_button) as Button
        var emailField = findViewById(R.id.email_field) as TextView
        var passwordField = findViewById(R.id.password_field) as TextView
        var actionSwitcher = findViewById(R.id.action_switcher) as TextView

        // remove authToken key (session)
        prefsEditor.remove("authToken")
        prefsEditor.apply()

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
            prefsEditor.putString("userEmail", emailField.text.toString())
            prefsEditor.apply()


            prefsEditor.putString("authToken", passwordField.text.toString())
            prefsEditor.apply()


            val loginRequest = LoginRequest(emailField.text.toString(),
                                            passwordField.text.toString(),
                                            "device")
            if (register) {
                registerUser(loginRequest, apiClient)
            } else {
                createSession(loginRequest, apiClient)
            }
        }
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

        var emailField = findViewById(R.id.email_field) as TextView
        emailField.setText(prefs.getString("userEmail", ""))
    }

    private fun createSession(loginRequest: LoginRequest, apiClient: ApiClient) {
        apiClient.createSession(loginRequest).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful()) {
                    Toast.makeText(this@LoginActivity, response.body().toString(), Toast.LENGTH_SHORT).show()
                    finish()
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
                    Toast.makeText(this@LoginActivity, response.body().toString(), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "email already taken", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "unknown error", Toast.LENGTH_SHORT).show()
            }
        })
    }

}