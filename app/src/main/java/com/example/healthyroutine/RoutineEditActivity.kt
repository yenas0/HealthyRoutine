// RoutineEditActivity.kt
package com.example.healthyroutine

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class RoutineEditActivity : AppCompatActivity() {

    private lateinit var etRoutineName: EditText
    private lateinit var switchNotification: Switch
    private lateinit var startDateTextView: TextView
    private lateinit var dayButtons: List<TextView>
    private lateinit var btnSave: Button
    private lateinit var backButton: ImageView
    private lateinit var dbHelper: DatabaseHelper

    private var routineId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_edit)

        etRoutineName = findViewById(R.id.routine_name_edit_text)
        switchNotification = findViewById(R.id.et_notification_switch)
        btnSave = findViewById(R.id.save_button)
        backButton = findViewById(R.id.back_button)

        startDateTextView = findViewById(R.id.tv_start_date)
        dayButtons = listOf(
            findViewById(R.id.day_mon),
            findViewById(R.id.day_tue),
            findViewById(R.id.day_wed),
            findViewById(R.id.day_thu),
            findViewById(R.id.day_fri),
            findViewById(R.id.day_sat),
            findViewById(R.id.day_sun)
        )

        dbHelper = DatabaseHelper(this) // dbHelper 초기화

        startDateTextView.setOnClickListener {
            showDatePickerDialog()
        }

        dayButtons.forEach { dayButton ->
            dayButton.setOnClickListener {
                toggleDayButton(dayButton)
            }
        }

        routineId = intent.getIntExtra("routine_id", 0)
        val routineName = intent.getStringExtra("routine_name") ?: ""
        val notificationEnabled = intent.getBooleanExtra("notification_enabled", true)
        val routineDays = intent.getStringExtra("routine_days")

        etRoutineName.setText(routineName)
        switchNotification.isChecked = notificationEnabled
        setSelectedDays(routineDays ?: "")

        btnSave.setOnClickListener {
            val updatedRoutineName = etRoutineName.text.toString()
            val updatedNotificationEnabled = switchNotification.isChecked
            val updatedDays = dayButtons.filter { it.tag == "active" }.joinToString(",") { it.text.toString() }

            val updatedRoutine = Routine(
                id = routineId, // 기존 루틴의 ID를 사용
                name = updatedRoutineName,
                days = updatedDays, // days 매개변수를 추가
                notificationEnabled = updatedNotificationEnabled
            )

            // 데이터베이스에 업데이트
            dbHelper.updateRoutine(updatedRoutine)

            // 결과 전달 및 액티비티 종료
            val resultIntent = Intent().apply {
                putExtra("routine_id", routineId)
                putExtra("routine_name", updatedRoutineName)
                putExtra("notification_enabled", updatedNotificationEnabled)
                putExtra("routine_days", updatedDays)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear. ${selectedMonth + 1}. $selectedDay."
                startDateTextView.text = selectedDate
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun toggleDayButton(button: TextView) {
        val isActive = button.tag == "active"
        if (isActive) {
            button.background = resources.getDrawable(R.drawable.circle_background_inactive, null)
            button.tag = "inactive"
        } else {
            button.background = resources.getDrawable(R.drawable.circle_background_active, null)
            button.tag = "active"
        }
    }

    private fun setSelectedDays(days: String) {
        val selectedDays = days.split(",")
        dayButtons.forEach { dayButton ->
            if (selectedDays.contains(dayButton.text.toString())) {
                dayButton.background = resources.getDrawable(R.drawable.circle_background_active, null)
                dayButton.tag = "active"
            } else {
                dayButton.background = resources.getDrawable(R.drawable.circle_background_inactive, null)
                dayButton.tag = "inactive"
            }
        }
    }
}
