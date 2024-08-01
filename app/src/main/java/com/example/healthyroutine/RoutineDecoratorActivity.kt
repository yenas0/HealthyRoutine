package com.example.healthyroutine

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

class RoutineDecoratorActivity : AppCompatActivity() {
    private lateinit var calendarView: MaterialCalendarView
    private val routineList = mutableListOf<Routine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_decorator)

        calendarView = findViewById(R.id.calendarView)

        // Dummy data for testing
        routineList.add(Routine(LocalDate.of(2024, 7, 10), true))
        routineList.add(Routine(LocalDate.of(2024, 7, 11), false))
        routineList.add(Routine(LocalDate.of(2024, 7, 15), true))
        routineList.add(Routine(LocalDate.of(2024, 7, 20), false))

        val completedDates = routineList.filter { it.isCompleted }.map { CalendarDay.from(it.date.toDate()) }.toSet()
        val notCompletedDates = routineList.filter { !it.isCompleted }.map { CalendarDay.from(it.date.toDate()) }.toSet()

        calendarView.addDecorator(RoutineDecorator(completedDates, Color.GREEN))
        calendarView.addDecorator(RoutineDecorator(notCompletedDates, Color.GRAY))
    }
}
