// RoutineEditActivity.kt
package com.example.healthyroutine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class RoutineEditActivity : AppCompatActivity() {

    private lateinit var etRoutineName: EditText
    private lateinit var switchNotification: Switch
    private lateinit var btnSave: Button
    private lateinit var backButton: ImageView

    private var routineId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_edit)

        etRoutineName = findViewById(R.id.routine_name_edit_text)
        switchNotification = findViewById(R.id.et_notification_switch)
        btnSave = findViewById(R.id.save_button)
        backButton = findViewById(R.id.back_button)

        routineId = intent.getIntExtra("routine_id", 0)
        val routineName = intent.getStringExtra("routine_name") ?: ""
        val notificationEnabled = intent.getBooleanExtra("notification_enabled", true)

        etRoutineName.setText(routineName)
        switchNotification.isChecked = notificationEnabled

        btnSave.setOnClickListener {
            val updatedRoutineName = etRoutineName.text.toString()
            val updatedNotificationEnabled = switchNotification.isChecked

            val dbHelper = DatabaseHelper(this)
            val updatedRoutine = Routine(
                id = routineId,
                name = updatedRoutineName,
                notificationEnabled = updatedNotificationEnabled
            )

            dbHelper.updateRoutine(updatedRoutine)

            // 결과 반환
            val resultIntent = Intent().apply {
                putExtra("routine_id", routineId)
                putExtra("routine_name", updatedRoutineName)
                putExtra("notification_enabled", updatedNotificationEnabled)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
