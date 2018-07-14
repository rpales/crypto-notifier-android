package com.app.rogerpales.cryptocoinnotifier

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class AddCondition : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_condition)

        val doneButton = findViewById(R.id.add_condition_done_button) as android.support.design.widget.FloatingActionButton
        doneButton.setOnClickListener {
            finish()
        }
    }
}
