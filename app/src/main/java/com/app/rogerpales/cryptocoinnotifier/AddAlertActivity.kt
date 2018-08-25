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
import com.app.rogerpales.cryptocoinnotifier.api.model.CryptoCondition
import com.app.rogerpales.cryptocoinnotifier.api.model.Deletable
import com.app.rogerpales.cryptocoinnotifier.api.service.ApiClient
import com.app.rogerpales.cryptocoinnotifier.api.service.RetrofitClient
import com.app.rogerpales.cryptocoinnotifier.lib.AppUtils
import com.google.gson.Gson
import com.onesignal.OneSignal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAlertActivity : AppActivity() {

    var currentAlert : Alert? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_alert)

        val doneButton = findViewById(R.id.add_alert_done_button) as android.support.design.widget.FloatingActionButton
        doneButton.setOnClickListener {
            updateCurrentAlert(true)
        }
        val alertName = findViewById(R.id.alert_name) as EditText
        alertName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateCurrentAlert(false)
            }
        }
        val alertSwitch = findViewById(R.id.alert_active) as Switch
        alertSwitch.setOnClickListener {
            updateCurrentAlert(false)
        }
    }

    inner class ConditionsListAdapter(context: Context, conditionsArray: List<CryptoCondition>?, addButton: Button): BaseAdapter() {

        private val context : Context
        private val conditionsArray : List<CryptoCondition>?
        private val addButton : Button

        init {
            this.context = context
            this.conditionsArray = conditionsArray
            this.addButton = addButton
        }

        // responsible for how many rows in my list
        override fun getCount(): Int {
            return sizeOf(conditionsArray) + 1
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
            val size = sizeOf(conditionsArray)
            if (size == position) {
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
                var condition = conditionsArray?.get(position)
                vh.conditionDescription.text = condition!!.description()
                vh.conditionDescription.setOnClickListener {
                    goToAddCondition(conditionsArray?.get(position))
                }
                vh.deleteButton.setOnClickListener {
                    condition.deleted = true
                    apiClient.deleteCondition(authToken, condition.alertId, condition.id).enqueue(object : Callback<CryptoCondition> {
                        override fun onResponse(call: Call<CryptoCondition>, response: Response<CryptoCondition>) {
                            if (response.isSuccessful()) {
                                populateConditionsList()
                            } else {
                                when (response.code()) {
                                    401  -> {
                                        prefsEditor!!.remove("authToken")
                                        prefsEditor!!.apply()
                                        goToMain()
                                    }
                                    else -> errorCallaback(response.errorBody()!!.string())
                                }
                            }
                        }

                        override fun onFailure(call: Call<CryptoCondition>, t: Throwable) {
                            showMessage("network error")
                        }
                    })
                }

                return view
            }
        }
    }

    inner class ViewHolder(view: View?) {
        val conditionDescription: TextView
        val deleteButton : ImageButton

        init {
            this.conditionDescription = view?.findViewById(R.id.conditionDescription) as TextView
            this.deleteButton = view?.findViewById(R.id.conditionDelete_button) as ImageButton
        }
        fun hide() {
            this.conditionDescription.visibility = View.GONE
            this.deleteButton.visibility = View.GONE
        }
    }


    override fun onStart() {
        super.onStart()

        loadPreferences()

        if (authToken == null || authToken == "") {
            goToMain()
        }

        val activityTitle = findViewById(R.id.add_alert_title) as TextView
        if (intent.getBooleanExtra("NEW_ALERT", false)) {
            activityTitle.setText("New Alert")
        } else {
            activityTitle.setText("Edit Alert")
            val alertName = findViewById(R.id.alert_name) as EditText
            alertName.setText(currentAlert?.name ?: "")
        }

        val alertSwitch = findViewById(R.id.alert_active) as Switch
        alertSwitch.isChecked = currentAlert?.active ?: true

        populateConditionsList()
    }

    private fun populateConditionsList() {
        Log.d("populate list", "populating list...")
        val listView = findViewById<ListView>(R.id.alert_conditions_list)

        val addButton = Button(this)
        addButton.setText("add condition")
        addButton.setOnClickListener {
            goToAddCondition( null)
        }
        val list = currentAlert?.getConditions()
        listView.adapter = ConditionsListAdapter(this, list, addButton)
    }

    override fun onBackPressed() {
        updateCurrentAlert(true)
    }

    override fun loadPreferences() {
        super.loadPreferences()
        currentAlert = AppUtils.deserializeAlert(prefs!!.getString("currentAlert", ""))
    }

    private fun goToMain() {
        apiClient.getAlertsRaw(authToken).enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful()) {
                    prefsEditor!!.putString("userAlerts", response.body().toString())
                    prefsEditor!!.apply()
                    finish()
                } else {
                    errorCallaback(response.errorBody()!!.string())
                    finish()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@AddAlertActivity, "network error", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun updateCurrentAlert(goToMain: Boolean) {
        if (currentAlert != null) {
            val alertName   = findViewById(R.id.alert_name) as EditText
            currentAlert?.name = alertName.text.toString()
            val alertSwitch = findViewById(R.id.alert_active) as Switch
            currentAlert?.active = alertSwitch.isChecked
            apiClient.updateAlert(authToken, currentAlert?.id?.toInt(), currentAlert).enqueue(object : Callback<Alert> {

                override fun onResponse(call: Call<Alert>, response: Response<Alert>) {
                    if (!response.isSuccessful()) {
                        when (response.code()) {
                            401  -> {
                                prefsEditor!!.remove("authToken")
                                prefsEditor!!.apply()
                                goToMain()
                            }
                            else -> errorCallaback(response.errorBody()!!.string())
                        }
                    }
                    if (goToMain) { goToMain() }
                }

                override fun onFailure(call: Call<Alert>, t: Throwable) {
                    showMessage("network error")
                    if (goToMain) { goToMain() }
                }
            })
        } else {
            showMessage("error: no current alert")
        }
    }

    override fun showMessage(message: String?) {
        if (message != null) {
            Toast.makeText(this@AddAlertActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToAddCondition(conditionParameter: CryptoCondition?) {
        var condition : CryptoCondition? = conditionParameter
        val intent = Intent(this, AddCondition::class.java)
        intent.putExtra("NEW_CONDITION", condition == null)
        if (condition == null) {
            condition = CryptoCondition(alertId = currentAlert?.id!!)
        }
        prefsEditor!!.putString("currentCondition", condition?.toJson(Gson()) ?: "")
        prefsEditor!!.apply()
        startActivity(intent)
    }
}
