package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class RoutineAddActivity : AppCompatActivity() {

    private lateinit var routineTitle: EditText
    private lateinit var notificationSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_add)

        routineTitle = findViewById(R.id.routine_title)
        notificationSwitch = findViewById(R.id.notification_switch)

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val addButton: Button = findViewById(R.id.btn_add_routine)
        addButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("routine_name", routineTitle.text.toString())
            intent.putExtra("notification_enabled", notificationSwitch.isChecked)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
