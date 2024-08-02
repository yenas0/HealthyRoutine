package com.example.healthyroutine

import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.DayOfWeek

class MonthlyStatsActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var routineNameTextView: TextView
    private lateinit var checkCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_stats)

//        calendarView = findViewById(R.id.calendar_view)
        routineNameTextView = findViewById(R.id.routine_name_text_view)

        val routineId = intent.getIntExtra("ROUTINE_ID", -1)
        loadRoutineData(routineId)
    }

    private fun loadRoutineData(routineId: Int) {
        // 루틴 이름과 체크 횟수를 불러오는 로직
        val routine = getRoutineById(routineId)
        routineNameTextView.text = routine?.name ?: "Unknown"

        // 예시로 체크 횟수를 5로 설정 (실제로는 DB에서 가져와야 함)
        val checkCount = getCheckCountForMonth(routineId)
        checkCountTextView.text = "체크 횟수: $checkCount"
    }

    private fun getRoutineById(id: Int): Routine? {
        // 데이터베이스에서 루틴을 가져오는 로직
        val routines = listOf(
            Routine(1, "Morning Stretch", false),
            Routine(2, "Evening Run", false)
            // 다른 루틴들...
        )
        return routines.find { id == id }
    }

    private fun getCheckCountForMonth(routineId: Int): Int {
        // 월간 체크 횟수를 계산하는 로직
        return 5
    }
}
