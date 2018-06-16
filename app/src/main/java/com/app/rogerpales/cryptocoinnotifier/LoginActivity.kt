package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.content.Intent



class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadPreferences()

        val prefsEditor = getSharedPreferences(R.string.SHARED_PREFERENCES.toString(), Context.MODE_PRIVATE).edit()

        var loginButton = findViewById(R.id.login_button) as Button
        var emailField = findViewById(R.id.email_field) as TextView
        var passwordField = findViewById(R.id.password_field) as TextView

        // remove authToken key (session)
        prefsEditor.remove("authToken")
        prefsEditor.apply()

        loginButton.setOnClickListener {
            // set userEmail to email field value
            prefsEditor.putString("userEmail", emailField.text.toString())
            prefsEditor.apply()


            prefsEditor.putString("authToken", passwordField.text.toString())
            prefsEditor.apply()

            // TODO: LOGIN LOGIC HERE
            // 1. get password from password field
            // 2. rest POST /session -d '{email: email, password: pwd}'
            // if (authToken)
            //      3. set "authToken"
            //      4. move to Main Activity
            // else
            //      5. display feedback
            finish()
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
        val prefs = getSharedPreferences(R.string.SHARED_PREFERENCES.toString(), Context.MODE_PRIVATE)

        var emailField = findViewById(R.id.email_field) as TextView
        emailField.setText(prefs.getString("userEmail", ""))
    }
}
