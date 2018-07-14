package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.app.rogerpales.cryptocoinnotifier.api.model.CryptoCondition
import com.app.rogerpales.cryptocoinnotifier.api.service.ApiClient
import com.app.rogerpales.cryptocoinnotifier.api.service.RetrofitClient
import com.app.rogerpales.cryptocoinnotifier.lib.AppUtils
import org.w3c.dom.Text

class AddCondition : AppCompatActivity() {

    val periodsFromMap : Map<String, Int> = mapOf(
            "none" to 0,
            "1 min" to 1,
            "5 min" to 5,
            "15 min" to 15,
            "30 min" to 30,
            "1 hour" to 60,
            "12 hours" to 720,
            "24 hours" to 1440
            )

    val periodsToMap : Map<Int, String> = mapOf(
            0 to "none",
            1 to "1 min",
            5 to "5 min",
            15 to "15 min",
            30 to "30 min",
            60 to "1 hour",
            720 to "12 hours",
            1440 to "24 hours"
    )

    val typesFromMap : Map<String, String> = mapOf(
            "Price above" to "PRICE_ABOVE",
            "Price below" to "PRICE_BELOW",
            "Price increment" to "PRICE_INCREMENT",
            "Volume above" to "VOLUME24HOUR_ABOVE",
            "Volume below" to "VOLUME24HOUR_BELOW",
            "Volume increment" to "VOLUME24HOUR_INCREMENT",
            "SMA above" to "SMA_ABOVE",
            "SMA below" to "SMA_BELOW"
    )

    val typesToMap : Map<String, String> = mapOf(
            "PRICE_ABOVE" to "Price above",
            "PRICE_BELOW" to "Price below",
            "PRICE_INCREMENT" to "Price increment",
            "VOLUME24HOUR_ABOVE" to "Volume above",
            "VOLUME24HOUR_BELOW" to "Volume below",
            "VOLUME24HOUR_INCREMENT" to "Volume increment",
            "SMA_ABOVE" to "SMA above",
            "SMA_BELOW" to "SMA below"
    )

    var authToken : String? = null
    var currentCondition : CryptoCondition? = null
    val apiClient : ApiClient = RetrofitClient.getClient("http://206.189.19.242/")!!.create(ApiClient::class.java)
    var fromInput : AutoCompleteTextView? = null
    var toInput : AutoCompleteTextView? = null
    var typeSpinner : Spinner? = null
    var valueInput : EditText? = null
    var periodSpinner : Spinner? = null
    var numReadingsInput : EditText? = null
    var unitsLabel : TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_condition)

        val doneButton = findViewById(R.id.add_condition_done_button) as android.support.design.widget.FloatingActionButton
        doneButton.setOnClickListener {
            saveAndFinish()
        }

        // assing condition view inputs
        fromInput = findViewById(R.id.add_conditioin_from_coin) as AutoCompleteTextView
        toInput = findViewById(R.id.add_conditioin_to_coin) as AutoCompleteTextView
        typeSpinner = findViewById(R.id.add_conditioin_type) as Spinner
        valueInput = findViewById(R.id.add_conditioin_value) as EditText
        periodSpinner = findViewById(R.id.add_conditioin_period) as Spinner
        numReadingsInput = findViewById(R.id.add_conditioin_readings_number) as EditText
        unitsLabel = findViewById(R.id.add_conditioin_unit_label) as TextView
    }

    override fun onStart() {
        super.onStart()

        loadPreferences()

        if (authToken == null || authToken == "") { finish() }

        val typeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.condition_types, android.R.layout.simple_spinner_item)
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner!!.adapter = typeSpinnerAdapter
        val periodSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.condition_periods, android.R.layout.simple_spinner_item)
        periodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        periodSpinner!!.adapter = periodSpinnerAdapter

        val activityTitle = findViewById(R.id.add_condition_title) as TextView
        if (intent.getBooleanExtra("NEW_CONDITION", false)) {
            activityTitle.setText("New Condition")
        } else {
            activityTitle.setText("Edit Condition")
            fromInput!!.setText(currentCondition?.fromCoin ?: "BTC")
            toInput!!.setText(currentCondition?.toCoin ?: "USD")
            val conditionType = currentCondition?.conditionType ?: "PRICE_ABOVE"
            typeSpinner!!.setSelection(typeSpinnerAdapter.getPosition(typesToMap[conditionType]))
            valueInput!!.setText((currentCondition?.value ?: 0).toString())
            val period = currentCondition?.periodTime ?: 0
            periodSpinner!!.setSelection(periodSpinnerAdapter.getPosition(periodsToMap[period]))
            numReadingsInput!!.setText((currentCondition?.readingsNumber ?: 0).toString())
        }
        if (typeSpinner!!.selectedItem!!.contains(""))
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE)
        authToken = prefs.getString("authToken", null)
        currentCondition = AppUtils.deserializeCondition(prefs.getString("currentCondition", ""))
        Log.d("currentCondition", currentCondition.toString())
    }

    private fun saveAndFinish() {
        // call API with POST or PUT condition
        finish()
    }
}


//        if (createCondition) {
//            apiClient.createCondition(authToken, currentAlert?.id).enqueue(object : Callback<CryptoCondition> {
//
//                override fun onResponse(call: Call<CryptoCondition>, response: Response<CryptoCondition>) {
//                    if (response.isSuccessful()) {
//                        condition = response.body()
//                        prefsEditor.putString("currentAlert", condition?.toJson(Gson()) ?: "")
//                        prefsEditor.apply()
//                        startActivity(intent)
//                    } else {
//                        when (response.code()) {
//                            401  -> goToLogin()
//                            else -> showMessage(response.errorBody()?.string())
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<CryptoCondition>, t: Throwable) {
//                    showMessage("network error")
//                }
//            })
//        } else {
//            prefsEditor.putString("currentCondition", condition?.toJson(Gson()) ?: "")
//            prefsEditor.apply()
//            startActivity(intent)
//        }
//