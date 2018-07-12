package com.app.rogerpales.cryptocoinnotifier

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

class AddAlertActivity : AppCompatActivity() {

    lateinit var loadingSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alert)

    }
}
