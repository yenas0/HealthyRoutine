package com.example.healthyroutine

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class RoutineAddActivity : AppCompatActivity() {

    private lateinit var etRoutineName: EditText
    private lateinit var switchNotification: Switch
    private lateinit var startDateTextView: TextView
    private lateinit var dayButtons: List<TextView>
    private lateinit var btnSave: Button
    private lateinit var backButton: ImageView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_add)

        dbHelper = DatabaseHelper(this)

        etRoutineName = findViewById(R.id.routine_title)
        switchNotification = findViewById(R.id.notification_switch)
        btnSave = findViewById(R.id.btn_add_routine)
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

        startDateTextView.setOnClickListener {
            showDatePickerDialog()
        }

        dayButtons.forEach { dayButton ->
            dayButton.setOnClickListener {
                toggleDayButton(dayButton)
            }
        }

        val source = intent.getStringExtra("SOURCE")
        Log.d("RoutineAddActivity", "Source: $source")

        if (source == "BOARD") {
            val routineNameFromPost = intent.getStringExtra("routine_name")
            val routineDaysFromPost = intent.getStringExtra("routine_days")
            Log.d("RoutineAddActivity", "Routine Name from Post: $routineNameFromPost")
            Log.d("RoutineAddActivity", "Routine Days from Post: $routineDaysFromPost")
            etRoutineName.setText(routineNameFromPost)
            setSelectedDays(routineDaysFromPost ?: "")
        } else if (source == "RECOMMEND") {
            val routineNameFromButton = intent.getStringExtra("BUTTON_TEXT")
            Log.d("RoutineAddActivity", "Routine Name from Button: $routineNameFromButton")
            etRoutineName.setText(routineNameFromButton)
        }

        btnSave.setOnClickListener {
            val routineName = etRoutineName.text.toString()
            val notificationEnabled = switchNotification.isChecked
            val routineDays = dayButtons.filter { it.tag == "active" }.joinToString(",") { it.text.toString() }
            val startDate = startDateTextView.text.toString()

            val routine = Routine(
                id = 0, // 새 루틴이므로 ID는 0으로 설정
                name = routineName,
                notificationEnabled = notificationEnabled,
                days = routineDays,
                startDate = startDate
            )

            // 데이터베이스에 추가
            dbHelper.addRoutine(routine)

            // 결과 전달 및 액티비티 종료
            val resultIntent = Intent().apply {
                putExtra("routine_name", routineName)
                putExtra("notification_enabled", notificationEnabled)
                putExtra("routine_days", routineDays)
                putExtra("start_date", startDate)
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
