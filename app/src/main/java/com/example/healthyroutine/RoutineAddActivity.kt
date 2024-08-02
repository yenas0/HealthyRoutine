// RoutineAddActivity.kt
package com.example.healthyroutine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class RoutineAddActivity : AppCompatActivity() {

    private lateinit var etRoutineName: EditText
    private lateinit var switchNotification: Switch
    private lateinit var btnSave: Button
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_add)

        etRoutineName = findViewById(R.id.routine_title)
        switchNotification = findViewById(R.id.notification_switch)
        btnSave = findViewById(R.id.btn_add_routine)
        backButton = findViewById(R.id.back_button)

        // 루틴 추천 이름 그대로 들어가도록
        val routineName = intent.getStringExtra("BUTTON_TEXT")
        val healthRoutineName = findViewById<EditText>(R.id.routine_title)
        healthRoutineName.setText(routineName)

        btnSave.setOnClickListener {
            val routineName = etRoutineName.text.toString()
            val notificationEnabled = switchNotification.isChecked

            val resultIntent = Intent().apply {
                putExtra("routine_name", routineName)
                putExtra("notification_enabled", notificationEnabled)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
