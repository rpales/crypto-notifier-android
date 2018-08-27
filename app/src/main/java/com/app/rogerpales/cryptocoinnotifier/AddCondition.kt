package com.app.rogerpales.cryptocoinnotifier

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.ColorInt
import android.util.Log
import android.view.View
import android.widget.*
import com.app.rogerpales.cryptocoinnotifier.api.model.Alert
import com.app.rogerpales.cryptocoinnotifier.api.model.CoinsContainer
import com.app.rogerpales.cryptocoinnotifier.api.model.CryptoCondition
import com.app.rogerpales.cryptocoinnotifier.api.model.CurrentData
import com.app.rogerpales.cryptocoinnotifier.api.service.ApiClient
import com.app.rogerpales.cryptocoinnotifier.api.service.RetrofitClient
import com.app.rogerpales.cryptocoinnotifier.lib.AppUtils
import com.google.gson.Gson
import com.onesignal.OneSignal
import retrofit2.Call
import retrofit2.Response

class AddCondition : AppActivity() {

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

    var currentCondition : CryptoCondition? = null
    var currentAlert : Alert? = null
    var fromInput : AutoCompleteTextView? = null
    var toInput : AutoCompleteTextView? = null
    var typeSpinner : Spinner? = null
    var valueInput : EditText? = null
    var periodSpinner : Spinner? = null
    var numReadingsInput : EditText? = null
    var unitsLabel : TextView? = null

    // keep track if inputs change
    var previousFromInput : String? = null
    var previousToInput : String? = null
    var previousType : String? = null

    var availablefromCoins : List<String>? = listOf<String>()
    var availableToCoins   : List<String>? = listOf<String>()
    var context : Context = this

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

        fromInput!!.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (availablefromCoins!!.contains(fromInput!!.text.toString().toUpperCase())) {
                    fromInput!!.setText(fromInput!!.text.toString().toUpperCase())
                    updateToCoins()
                    if (fromInput!!.text.toString() != previousFromInput) {
                        fillValueInput()
                        previousFromInput = fromInput!!.text.toString()
                    }
                } else {
                    showMessage("FROM coin not available")
                }
            }
        }

        toInput!!.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (availableToCoins!!.contains(toInput!!.text.toString().toUpperCase())) {
                    toInput!!.setText(toInput!!.text.toString().toUpperCase())
                    updateToCoins()
                    if (toInput!!.text.toString() != previousToInput) {
                        fillValueInput()
                        previousToInput = toInput!!.text.toString()
                    }
                } else {
                    showMessage("TO coin not available")
                }
                if (!typeSpinner!!.selectedItem.toString().contains("increment")) {
                    unitsLabel!!.text = toInput!!.text.toString()
                }

            }
        }

        typeSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                changeTypeCallback()
            }
        }

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
            activityTitle.text = "New Condition"
        } else {
            activityTitle.text = "Edit Condition"
            fromInput!!.setText(currentCondition?.fromCoin ?: "BTC")
            toInput!!.setText(currentCondition?.toCoin ?: "USD")
            val conditionType = currentCondition?.conditionType ?: "PRICE_ABOVE"
            typeSpinner!!.setSelection(typeSpinnerAdapter.getPosition(typesToMap[conditionType]))
            val period = currentCondition?.periodTime ?: 0
            periodSpinner!!.setSelection(periodSpinnerAdapter.getPosition(periodsToMap[period]))
            numReadingsInput!!.setText((currentCondition?.readingsNumber ?: 0).toString())
        }
        if (typeSpinner!!.selectedItem!!.toString().contains("increment")) {
            unitsLabel!!.text = "%"
            var value = (currentCondition?.value ?: 0.0f) * 100
            valueInput!!.setText(value.toString())
        } else {
            unitsLabel!!.text = toInput!!.text.toString()
            valueInput!!.setText((currentCondition?.value ?: 0.0f).toString())
        }

        updateFromCoins()
        updateToCoins()
        fillValueInput()
    }

    override fun loadPreferences() {
        super.loadPreferences()
        currentCondition = AppUtils.deserializeCondition(prefs!!.getString("currentCondition", ""))
        currentAlert = AppUtils.deserializeAlert(prefs!!.getString("currentAlert", ""))
    }

    private fun saveAndFinish() {
        // call API with POST or PUT condition
        currentCondition?.fromCoin = fromInput!!.text.toString()
        currentCondition?.toCoin = toInput!!.text.toString()
        currentCondition?.conditionType = typeSpinner!!.selectedItem.toString()
        if (currentCondition?.conditionType.toString().contains("increment")) {
            currentCondition?.value = valueInput!!.text.toString().toFloat()/100
        } else {
            currentCondition?.value = valueInput!!.text.toString().toFloat()
        }
        val type = typeSpinner!!.selectedItem.toString()
        currentCondition?.conditionType = typesFromMap[type]
        val period = periodSpinner!!.selectedItem.toString()
        if ((type.toLowerCase().contains("inc") || type.toLowerCase().contains("sma")) && period.contains("none")) {
            currentCondition?.periodTime = 60
        } else {
            currentCondition?.periodTime = periodsFromMap[period]
        }
        if (numReadingsInput!!.text.toString() != "") {
            currentCondition?.readingsNumber = numReadingsInput!!.text.toString().toInt()
        }
        if (currentCondition?.id != null) {
            apiClient.updateCondition(authToken, currentCondition?.alertId, currentCondition?.id, currentCondition).enqueue(object : retrofit2.Callback<CryptoCondition> {
                override fun onResponse(call: Call<CryptoCondition>, response: Response<CryptoCondition>) {
                    if (response.isSuccessful) {
                        getAlertAndFinish()
                    } else {
                        when (response.code()) {
                            401  -> {
                                prefsEditor!!.remove("authToken")
                                prefsEditor!!.apply()
                                finish()
                            }
                            else -> {
                                errorCallaback(response.errorBody()!!.string())
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CryptoCondition>, t: Throwable?) {
                    showMessage("unknown error")
                }
            })
        } else {
            apiClient.createCondition(authToken, currentCondition?.alertId, currentCondition).enqueue(object : retrofit2.Callback<CryptoCondition> {
                override fun onResponse(call: Call<CryptoCondition>, response: Response<CryptoCondition>) {
                    if (response.isSuccessful) {
                        getAlertAndFinish()
                    } else {
                        when (response.code()) {
                            401  -> {
                                prefsEditor!!.remove("authToken")
                                prefsEditor!!.apply()
                                finish()
                            }
                            else -> {
                                errorCallaback(response.errorBody()!!.string())
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CryptoCondition>, t: Throwable?) {
                    showMessage("unknown error")
                }
            })
        }
    }

    private fun getAlertAndFinish() {
        apiClient.getAlert(authToken, currentCondition?.alertId).enqueue(object : retrofit2.Callback<Alert> {
            override fun onResponse(call: Call<Alert>, response: Response<Alert>) {
                if (response.isSuccessful) {
                    prefsEditor!!.putString("currentAlert", response.body()?.toJson(Gson()) ?: "")
                    prefsEditor!!.apply()
                    finish()
                } else {
                    when (response.code()) {
                        401  -> {
                            prefsEditor!!.remove("authToken")
                            prefsEditor!!.apply()
                            finish()
                        }
                        else -> {
                            errorCallaback(response.errorBody()!!.string())
                            finish()
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Alert>, t: Throwable?) {
                Toast.makeText(this@AddCondition, "network error", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun changeTypeCallback() {
        val type = typeSpinner!!.selectedItem.toString()
        numReadingsInput!!.isEnabled = type.contains("SMA")
        periodSpinner!!.isEnabled = type.contains("SMA") || type.contains("increment")
        if (type.contains("increment")) {
            unitsLabel!!.text = "%"
        } else {
            unitsLabel!!.text = toInput!!.text.toString()
        }
        if (previousType != typeSpinner!!.selectedItem.toString()) {
            fillValueInput()
            previousType = typeSpinner!!.selectedItem.toString()
        }
    }

    private fun changeToCoinCallback() {
        if (!typeSpinner!!.selectedItem.toString().contains("increment")) {
            unitsLabel!!.text = toInput!!.text.toString()
        }
    }

    fun updateFromCoins() {
        fromInput!!.isEnabled = false
        apiClient.getFromCoins(authToken).enqueue(object : retrofit2.Callback<CoinsContainer> {
            override fun onResponse(call: Call<CoinsContainer>, response: Response<CoinsContainer>) {
                if (response.isSuccessful) {
                    availablefromCoins = response.body()?.coinsList
                    prefsEditor!!.putString("availablefromCoins", response.body()?.toJson(Gson()) ?: "")
                    prefsEditor!!.apply()
                    val fromInputAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, availablefromCoins)
                    fromInputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    fromInput!!.setAdapter(fromInputAdapter)
                    fromInput!!.isEnabled = true
                } else {
                    errorCallaback(response.errorBody()!!.string())
                    fromInput!!.isEnabled = true
                }
            }

            override fun onFailure(call: Call<CoinsContainer>, t: Throwable?) {
                fromInput!!.isEnabled = true
                showMessage("unkown error")
            }

        })
    }

    fun updateToCoins() {
        toInput!!.isEnabled = false
        apiClient.getToCoins(authToken, fromInput!!.text.toString()).enqueue(object : retrofit2.Callback<CoinsContainer> {
            override fun onResponse(call: Call<CoinsContainer>, response: Response<CoinsContainer>) {
                if (response.isSuccessful) {
                    availableToCoins = response.body()?.coinsList
                    val toInputAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, availableToCoins)
                    toInputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    toInput!!.setAdapter(toInputAdapter)
                    toInput!!.isEnabled = true
                    Log.d("update to coins", "success")
                    Log.d("new to coins list", response.body()?.coinsList.toString())
                } else {
                    toInput!!.isEnabled = true
                    errorCallaback(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<CoinsContainer>, t: Throwable?) {
                toInput!!.isEnabled = true
                showMessage("unkown error")
            }
        })
    }

    fun fillValueInput() {
        if (!typeSpinner!!.selectedItem.toString().toLowerCase().contains("increment")) {
            valueInput!!.isEnabled = false
            apiClient.getCurrentData(authToken, fromInput!!.text.toString(), toInput!!.text.toString()).enqueue(object : retrofit2.Callback<CurrentData> {
                override fun onResponse(call: Call<CurrentData>, response: Response<CurrentData>) {
                    if (response.isSuccessful) {
                        if (typeSpinner!!.selectedItem.toString().toLowerCase().contains("volume")) {
                            valueInput!!.setText(response.body()?.volume.toString())
                        } else {
                            valueInput!!.setText(response.body()?.price.toString())
                        }
                        valueInput!!.isEnabled = true
                    } else {
                        valueInput!!.isEnabled = true
                        errorCallaback(response.errorBody()!!.string())
                    }
                }

                override fun onFailure(call: Call<CurrentData>, t: Throwable?) {
                    valueInput!!.isEnabled = true
                    showMessage("unkown error")
                }
            })
        } else {
            if (valueInput!!.text.toString().toFloat() > 1000.00) {
                valueInput!!.setText("0")
            }
        }
    }

    override fun showMessage(message: String?) {
        if (message != null) {
            Toast.makeText(this@AddCondition, message, Toast.LENGTH_SHORT).show()
        }
    }
}