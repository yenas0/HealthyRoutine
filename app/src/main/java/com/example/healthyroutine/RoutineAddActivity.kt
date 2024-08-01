package com.example.healthyroutine

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class RoutineAddActivity : AppCompatActivity() {

    private lateinit var routineTitle: EditText
    private lateinit var startDateTextView: TextView
    private lateinit var dayButtons: List<TextView>
    private var selectedDays: MutableList<String> = mutableListOf()
    private var notificationEnabled: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_add)

        routineTitle = findViewById(R.id.routine_title)
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

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val addButton: Button = findViewById(R.id.btn_add_routine)
        addButton.setOnClickListener {
            val routineName = routineTitle.text.toString()
            val startDate = startDateTextView.text.toString()

            if (routineName.isNotBlank() && selectedDays.isNotEmpty() && startDate.isNotBlank()) {
                val intent = Intent()
                intent.putExtra("routine_name", routineName)
                intent.putExtra("start_date", startDate)
                intent.putExtra("days", selectedDays.joinToString(","))
                intent.putExtra("notification_enabled", notificationEnabled)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Switch>(R.id.notification_switch).setOnCheckedChangeListener { _, isChecked ->
            notificationEnabled = isChecked
        }

        val routineName = intent.getStringExtra("routine")
        val routineDays = intent.getStringExtra("routine_days")

        routineName?.let {
            routineTitle.setText(it)
            Toast.makeText(this, "설정된 루틴: $it", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "새로운 루틴 추가", Toast.LENGTH_SHORT).show()
        }

        routineDays?.let {
            setSelectedDays(it)
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
            selectedDays.remove(button.text.toString())
        } else {
            button.background = resources.getDrawable(R.drawable.circle_background_active, null)
            button.tag = "active"
            selectedDays.add(button.text.toString())
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
