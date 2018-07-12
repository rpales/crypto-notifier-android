package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.app.rogerpales.cryptocoinnotifier.api.model.Alert
import com.app.rogerpales.cryptocoinnotifier.api.model.User
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    var currentUser : User? = null
    var authToken : String? = null
    val gson : Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }


        var logoutButton = findViewById(R.id.main_logout_button) as Button
        logoutButton.setOnClickListener {
            val editor = getSharedPreferences(R.string.SHARED_PREFERENCES.toString(), Context.MODE_PRIVATE).edit()

            editor.remove("authToken")
            editor.remove("email")
            editor.remove("currentUser")
            editor.apply()

            goToLogin()
        }

        var addAlertButton = findViewById(R.id.main_add_alert_button) as android.support.design.widget.FloatingActionButton
        addAlertButton.setOnClickListener {
            goToAddAlert()
        }

    }

    private class AlertsListAdapter(context: Context, alertsArray: Array<Alert>?): BaseAdapter() {

        private val context : Context
        private val alertsArray : Array<Alert>?

        init {
            this.context = context
            this.alertsArray = alertsArray
        }

        // responsible for how many rows in my list
        override fun getCount(): Int {
            return alertsArray?.size ?: 0
        }

        //ignore
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        // ignore
        override fun getItem(position: Int): Any {
            return "something"
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            val view: View
            val vh: ViewHolder

            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.alert_list, viewGroup, false)
                vh = ViewHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.alertName.text = alertsArray?.get(position)?.name ?: "Alert"
            vh.alertDescription.text = "alert id is "+ alertsArray?.get(position)?.id.toString()
            vh.alertSwitch.isChecked = alertsArray?.get(position)?.active ?: false

            return view
        }
    }

    private class ViewHolder(view: View?) {
        val alertName: TextView
        val alertDescription: TextView
        val alertSwitch: Switch

        init {
            this.alertName = view?.findViewById(R.id.alertName) as TextView
            this.alertDescription= view.findViewById(R.id.alertDescription) as TextView
            this.alertSwitch= view.findViewById(R.id.alertSwitch) as Switch
        }

    }


    override fun onStart() {
        super.onStart()
        loadPreferences()
        if (authToken == null || authToken == "") {
            goToLogin()
        }

        val listView = findViewById<ListView>(R.id.main_alerts_list)

        listView.adapter = AlertsListAdapter(this, currentUser?.alerts)

    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE)
        authToken = prefs.getString("authToken", null)
        val currentUserJSON = prefs.getString("currentUser", "")
        currentUser = gson.fromJson<User>(currentUserJSON, User::class.java)
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun goToAddAlert() {
        val intent = Intent(this, AddAlertActivity::class.java)
        startActivity(intent)
    }
}
