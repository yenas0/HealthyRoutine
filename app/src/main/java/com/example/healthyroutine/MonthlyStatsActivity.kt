package com.example.healthyroutine

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class MonthlyStatsActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var routineNameTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        routineNameTextView = findViewById(R.id.routine_name_text_view)
        calendarView = findViewById(R.id.calendar_view)


        val routineDays = getRoutineDaysFromDB()

        val eventDates = HashSet<CalendarDay>()
        routineDays.forEach {
            eventDates.add(CalendarDay.from(it))
        }


        calendarView.addDecorator(EventDecorator(Color.RED, eventDates))
    }

    private fun getRoutineDaysFromDB(): List<Date> {

        val dates = mutableListOf<Date>()
        val calendar = Calendar.getInstance()


        return dates
    }
}
