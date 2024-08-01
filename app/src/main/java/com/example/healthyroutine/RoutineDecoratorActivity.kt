package com.example.healthyroutine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.LocalDate

class RoutineDecoratorActivity : AppCompatActivity {
    private lateinit var calendarView: MaterialCalendarView
    private val routineList = mutableListOf<Routine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView)

        // Dummy data for testing
        routineList.add(Routine(LocalDate.of(2024, 7, 10), true))
        routineList.add(Routine(LocalDate.of(2024, 7, 11), false))
        routineList.add(Routine(LocalDate.of(2024, 7, 15), true))
        routineList.add(Routine(LocalDate.of(2024, 7, 20), false))

        val completedDates = routineList.filter { it.isCompleted }.map { CalendarDay.from(it.date) }.toSet()
        val notCompletedDates = routineList.filter { !it.isCompleted }.map { CalendarDay.from(it.date) }.toSet()

        calendarView.addDecorator(RoutineDecorator(completedDates, Color.GREEN))
        calendarView.addDecorator(RoutineDecorator(notCompletedDates, Color.GRAY))
    }
}
}