package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.app.rogerpales.cryptocoinnotifier.api.model.Alert
import com.app.rogerpales.cryptocoinnotifier.api.model.Deletable
import com.app.rogerpales.cryptocoinnotifier.api.model.User
import com.app.rogerpales.cryptocoinnotifier.api.service.ApiClient
import com.app.rogerpales.cryptocoinnotifier.api.service.LoginRequest
import com.app.rogerpales.cryptocoinnotifier.api.service.RetrofitClient
import com.app.rogerpales.cryptocoinnotifier.lib.AppUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.onesignal.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // allow exit app (back button LoginActivity)
        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }

        setContentView(R.layout.activity_main)

        val logoutButton = findViewById(R.id.main_logout_button) as Button
        var logoutText = "Logout"
        if (currentUser?.email != null) { logoutButton.setText(logoutText + " " + currentUser?.email) }
        logoutButton.setOnClickListener {
            goToLogin()
        }

        val addAlertButton = findViewById(R.id.main_add_alert_button) as android.support.design.widget.FloatingActionButton
        addAlertButton.setOnClickListener {
            goToAddAlert(true, null)
        }

    }

    override fun onStart() {
        super.onStart()

        populateAlertsList(true)
        if (intent.getStringExtra("ALERT_FROM_NOTIFICATION") != null && intent.getStringExtra("ALERT_FROM_NOTIFICATION") != "") {
            if (intent.getStringExtra("AUTH_TOKEN_FROM_NOTIFICATION") != this.authToken) {
                goToLogin()
            } else {
                val alert = AppUtils.deserializeAlert(intent.getStringExtra("ALERT_FROM_NOTIFICATION"))
                intent.removeExtra("ALERT_FROM_NOTIFICATION")
                goToAddAlert(false, alert)
            }
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
                        errorCallaback(response.errorBody()!!.string())
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

    inner class AlertsListAdapter(context: Context, alertsArray: List<Alert>?): BaseAdapter() {

        private val context : Context
        private val alertsArray : List<Alert>?

        init {
            this.context = context
            this.alertsArray = alertsArray?.filter { it.deleted == false }
        }

        override fun getCount(): Int { return sizeOf(alertsArray) }

        override fun getItemId(position: Int): Long { return position.toLong() }

        override fun getItem(position: Int): Any { return "alertsArray?.get(position)" }

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
            val alert = alertsArray?.get(position)
            if (alert?.name == null || alert.name == "") {
                vh.alertName.text = "(no label)"
            } else {
                vh.alertName.text = alert.name
            }
            vh.alertName.setOnClickListener {
                goToAddAlert(false, alert)
            }
            if (sizeOf(alert!!.conditions) > 1) {
                val conditionsCount = sizeOf(alert.conditions).toString()
                vh.alertDescription.text = "$conditionsCount conditions"
            } else if (sizeOf(alert!!.conditions) == 1) {
                vh.alertDescription.text = "condition: ${alert.conditions!!.get(0).description()}"
            } else {
                vh.alertDescription.text = "no conditions"
            }
            vh.alertDescription.setOnClickListener {
                goToAddAlert(false, alert)
            }
            vh.alertSwitch.isChecked = alert?.active ?: false
            vh.alertSwitch.setOnClickListener {
                alert?.active = vh.alertSwitch.isChecked
                apiClient.updateAlert(authToken, alert?.id?.toInt(), alert).enqueue(object : Callback<Alert> {

                    override fun onResponse(call: Call<Alert>, response: Response<Alert>) {
                        if (!response.isSuccessful()) {
                            when (response.code()) {
                                401  -> goToLogin()
                                else -> errorCallaback(response.errorBody()!!.string())
                            }
                        }
                    }

                    override fun onFailure(call: Call<Alert>, t: Throwable) {
                        showMessage("network error")
                    }
                })
            }
            vh.deleteButton.setOnClickListener {
                apiClient.deleteAlert(authToken, alert?.id?.toInt()).enqueue(object : Callback<Alert> {

                    override fun onResponse(call: Call<Alert>, response: Response<Alert>) {
                        if (response.isSuccessful()) {
                            alert?.deleted = true
                            vh.hide()
                            view.visibility = View.GONE
                        } else {
                            when (response.code()) {
                                401  -> goToLogin()
                                else -> errorCallaback(response.errorBody()!!.string())
                            }
                        }
                    }

                    override fun onFailure(call: Call<Alert>, t: Throwable) {
                        showMessage("network error")
                    }
                })
            }

            return view
        }
    }

    inner class ViewHolder(view: View?) {
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

        fun hide() {
            this.alertName.visibility = View.GONE
            this.alertDescription.visibility = View.GONE
            this.alertSwitch.visibility = View.GONE
            this.deleteButton.visibility = View.GONE
        }
    }

    // -------------- go to activities --------------

    fun goToAddAlert(newAlert: Boolean, alertParameter: Alert?) {
        var alert : Alert? = alertParameter
        val intent = Intent(this, AddAlertActivity::class.java)
        intent.putExtra("NEW_ALERT", newAlert)
        if (newAlert) {
            apiClient.createAlert(authToken).enqueue(object : Callback<Alert> {

                override fun onResponse(call: Call<Alert>, response: Response<Alert>) {
                    if (response.isSuccessful()) {
                        alert = response.body()
                        prefsEditor!!.putString("currentAlert", alert?.toJson(gson) ?: "")
                        prefsEditor!!.apply()
                        startActivity(intent)
                    } else {
                        when (response.code()) {
                            401  -> goToLogin()
                            else -> errorCallaback(response.errorBody()!!.string())
                        }
                    }
                }

                override fun onFailure(call: Call<Alert>, t: Throwable) {
                    showMessage("network error")
                }
            })
        } else {
            prefsEditor!!.putString("currentAlert", alert?.toJson(gson) ?: "")
            prefsEditor!!.apply()
            startActivity(intent)
        }
    }

    override fun showMessage(message: String?) {
        if (message != null) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}