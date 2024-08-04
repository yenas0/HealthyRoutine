// RoutineEditActivity.kt
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

        // 루틴 ID 가져오기
        routineId = intent.getIntExtra("routine_id", 0)

        // 가져온 ID로 DB 읽어오기
        var routine: Routine? = null
        routine = dbHelper.getRoutine(routineId)

        // 루틴 값 적용
        val routineName = routine!!.name ?: ""
        val notificationEnabled = routine.notificationEnabled
        val routineDays = routine.days
        val startDate = routine.startDate

        etRoutineName.setText(routineName)
        switchNotification.isChecked = notificationEnabled
        setSelectedDays(routineDays ?: "")
        startDateTextView.text = startDate

        btnSave.setOnClickListener {
            val updatedRoutineName = etRoutineName.text.toString()
            val updatedNotificationEnabled = switchNotification.isChecked
            val updatedDays = dayButtons.filter { it.tag == "active" }.joinToString(",") { it.text.toString() }
            val updatedStartDate = startDateTextView.text.toString()

            val updatedRoutine = Routine(
                id = routineId,
                name = updatedRoutineName,
                days = updatedDays,
                notificationEnabled = updatedNotificationEnabled,
                startDate = updatedStartDate
            )

            // 데이터베이스에 업데이트
            dbHelper.updateRoutine(updatedRoutine)
            Log.d("updatedRoutine", "현재 값은 $updatedRoutine 입니다.")

            // 결과 전달 및 액티비티 종료
            val resultIntent = Intent().apply {
                putExtra("routine_id", routineId)
                putExtra("routine_name", updatedRoutineName)
                putExtra("notification_enabled", updatedNotificationEnabled)
                putExtra("routine_days", updatedDays)
                putExtra("start_date", updatedStartDate) // 시작일 추가
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
