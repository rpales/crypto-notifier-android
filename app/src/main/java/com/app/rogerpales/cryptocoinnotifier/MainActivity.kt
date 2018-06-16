package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var userEmail: String? = null
    var authToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }


        var logoutButton = findViewById(R.id.logout_button) as Button
        logoutButton.setOnClickListener {
            val editor = getSharedPreferences(R.string.SHARED_PREFERENCES.toString(), Context.MODE_PRIVATE).edit()

            editor.remove("authToken")
            editor.apply()

            goToLogin()
        }
    }

    override fun onStart() {
        super.onStart()

        loadPreferences()

        if (authToken == null || authToken == "") {
            goToLogin()
        }

        // set greeting message
        val email = userEmail
        if (email != null) {
            var greatingView = findViewById(R.id.greating_message) as TextView
            greatingView.setText("Hello $email")
        }

        val token = authToken
        if (token != null) {
            var tokenView = findViewById(R.id.auth_token_view) as TextView
            tokenView.setText("authToken: $token")
        }
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(R.string.SHARED_PREFERENCES.toString(), Context.MODE_PRIVATE)

        userEmail = prefs.getString("userEmail", null)
        authToken = prefs.getString("authToken", null)
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
