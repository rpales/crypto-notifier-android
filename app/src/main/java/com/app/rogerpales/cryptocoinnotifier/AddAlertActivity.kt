package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.app.rogerpales.cryptocoinnotifier.api.model.Alert
import com.app.rogerpales.cryptocoinnotifier.api.model.CryptoCondition
import com.app.rogerpales.cryptocoinnotifier.api.model.User
import com.app.rogerpales.cryptocoinnotifier.api.service.ApiClient
import com.app.rogerpales.cryptocoinnotifier.api.service.RetrofitClient
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAlertActivity : AppCompatActivity() {

    var authToken : String? = null
    var currentAlert : Alert? = null
    val gson : Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alert)

        var doneButton = findViewById(R.id.add_alert_done_button) as android.support.design.widget.FloatingActionButton
        doneButton.setOnClickListener {
            goToMain()
        }
    }

    private class ConditionsListAdapter(context: Context, conditionsArray: Array<CryptoCondition>?, addButton: Button): BaseAdapter() {

        private val context : Context
        private val conditionsArray : Array<CryptoCondition>?
        private val addButton : Button

        init {
            this.context = context
            this.conditionsArray = conditionsArray
            this.addButton = addButton
        }

        // responsible for how many rows in my list
        override fun getCount(): Int {
            val arraySize = conditionsArray?.size ?: 0
            return arraySize + 1
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
            if (conditionsArray?.size ?: 0 == position) {
                return addButton
            } else {
                val view: View
                val vh: ViewHolder

                if (convertView == null) {
                    view = LayoutInflater.from(context).inflate(R.layout.condition_list, viewGroup, false)
                    vh = ViewHolder(view)
                    view.tag = vh
                } else {
                    view = convertView
                    vh = view.tag as ViewHolder
                }

                vh.conditionDescription.text = "condition id is "+ conditionsArray?.get(position)?.id.toString()

                return view
            }
        }
    }

    private class ViewHolder(view: View?) {
        val conditionDescription: TextView

        init {
            this.conditionDescription = view?.findViewById(R.id.conditionDescription) as TextView
        }

    }


    override fun onStart() {
        super.onStart()

        loadPreferences()

        if (authToken == null || authToken == "") {
            goToMain()
        }

        if (intent.getBooleanExtra("NEW_ALERT", false)) {
            val activityTitle = findViewById(R.id.add_alert_title) as TextView
            val alertName = findViewById(R.id.alert_name) as EditText
            activityTitle.setText("Edit Alert")
            alertName.setText(currentAlert?.name ?: "")
        }

        val listView = findViewById<ListView>(R.id.alert_conditions_list)

        val addButton = Button(this)
        addButton.setText("add condition")
        addButton.setOnClickListener {
            Toast.makeText(this, "add condition tapped", Toast.LENGTH_SHORT).show()
        }

        listView.adapter = ConditionsListAdapter(this, currentAlert?.conditions, addButton)
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE)
        authToken = prefs.getString("authToken", null)
        val currentAlertJSON = prefs.getString("currentAlert", "")
        currentAlert = gson.fromJson<Alert>(currentAlertJSON, User::class.java)
    }

    private fun goToMain() {
        val apiClient : ApiClient = RetrofitClient.getClient(getString(R.string.API_BASE_URL))!!.create(ApiClient::class.java)

        apiClient.getAlertsRaw(authToken).enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful()) {
                    val prefsEditor = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE).edit()
                    prefsEditor.putString("userAlerts", response.body().toString())
                    prefsEditor.apply()
                    finish()
                } else {
                    Toast.makeText(this@AddAlertActivity, "network error", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@AddAlertActivity, "network error", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }
}
