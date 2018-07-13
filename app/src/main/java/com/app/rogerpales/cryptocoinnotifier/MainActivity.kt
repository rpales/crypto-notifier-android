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
import com.app.rogerpales.cryptocoinnotifier.api.service.ApiClient
import com.app.rogerpales.cryptocoinnotifier.api.service.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    var currentUser : User?        = null
    var userAlerts  : List<Alert>? = null
    var authToken   : String?      = null
    val gson        : Gson         = Gson()
    val apiClient   : ApiClient    = RetrofitClient.getClient("http://206.189.19.242/")!!.create(ApiClient::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }


        val logoutButton = findViewById(R.id.main_logout_button) as Button
        logoutButton.setOnClickListener {
            val editor = getSharedPreferences(R.string.SHARED_PREFERENCES.toString(), Context.MODE_PRIVATE).edit()

            editor.remove("authToken")
            editor.remove("email")
            editor.remove("currentUser")
            editor.apply()

            goToLogin()
        }

        val addAlertButton = findViewById(R.id.main_add_alert_button) as android.support.design.widget.FloatingActionButton
        addAlertButton.setOnClickListener {
            goToAddAlert()
        }

    }

    override fun onStart() {
        super.onStart()
        loadPreferences()
        if (authToken == null || authToken == "") {
            goToLogin()
        }

        if (userAlerts == null) { userAlerts = currentUser?.alerts }

        populateAlertsList(false)
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE)
        authToken = prefs.getString("authToken", null)
        val currentUserJSON = prefs.getString("currentUser", "")
        currentUser = gson.fromJson<User>(currentUserJSON, User::class.java)
        val userAlertsJSON = prefs.getString("userAlerts", "")
        if (userAlertsJSON != "") {
            val listType = object : TypeToken<List<Alert>>() { }.type
            userAlerts = gson.fromJson<List<Alert>>(userAlertsJSON, listType)
        }
    }

    // -------------- Populate alerts list view --------------

    private fun populateAlertsList(apiFetch: Boolean) {
        if (apiFetch) {
            apiClient.getAlerts(authToken).enqueue(object : Callback<List<Alert>> {

                override fun onResponse(call: Call<List<Alert>>, response: Response<List<Alert>>) {
                    if (response.isSuccessful()) {
                        userAlerts = response.body()
                    } else {
                        Toast.makeText(this@MainActivity, "network error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Alert>>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "network error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val listView = findViewById<ListView>(R.id.main_alerts_list)

        listView.adapter = AlertsListAdapter(this, userAlerts)
    }

    private class AlertsListAdapter(context: Context, alertsArray: List<Alert>?): BaseAdapter() {

        private val context : Context
        private val alertsArray : List<Alert>?

        init {
            this.context = context
            this.alertsArray = alertsArray
        }

        override fun getCount(): Int { return alertsArray?.size ?: 0 }

        override fun getItemId(position: Int): Long { return position.toLong() }

        override fun getItem(position: Int): Any? { return alertsArray?.get(position) }

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
            vh.deleteButton.setOnClickListener {
//                it.
            }

            return view
        }
    }

    private class ViewHolder(view: View?) {
        val alertName: TextView
        val alertDescription: TextView
        val alertSwitch: Switch
        val deleteButton: ImageButton

        init {
            this.alertName = view?.findViewById(R.id.alertName) as TextView
            this.alertDescription= view.findViewById(R.id.alertDescription) as TextView
            this.alertSwitch= view.findViewById(R.id.alertSwitch) as Switch
            this.deleteButton= view.findViewById(R.id.alertDelete_button) as ImageButton
        }

    }

    // -------------- go to activities --------------

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun goToAddAlert() {
        val intent = Intent(this, AddAlertActivity::class.java)
        startActivity(intent)
    }
}
