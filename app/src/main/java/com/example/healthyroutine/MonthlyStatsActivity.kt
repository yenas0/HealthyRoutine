package com.example.healthyroutine

import android.app.Notification.DecoratedCustomViewStyle
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Locale

class MonthlyStatsActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var routineNameTextView: TextView
    private lateinit var dotTextView: TextView
    private lateinit var backButton: ImageView
    private val dbHelper = DatabaseHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_stats)

        calendarView = findViewById(R.id.calendar_view)
        routineNameTextView = findViewById(R.id.routine_name_text_view)
        dotTextView = findViewById(R.id.dottextview)
        backButton = findViewById(R.id.back_button)

        val routineId = intent.getIntExtra("routine_id", 0)
        val routineName = intent.getStringExtra("routine_name") ?: ""

        routineNameTextView.text = routineName

        backButton.setOnClickListener {
            finish()  // 현재 액티비티를 종료하고 이전 화면으로 돌아갑니다.
        }

        // 캘린더의 월 이름을 한국어로 설정
        calendarView.setTitleFormatter(TitleFormatter { day ->
            val dateFormat = SimpleDateFormat("yyyy년 MMMM", Locale("ko", "KR"))
            dateFormat.format(day.date)
        })

        // 캘린더의 요일 이름을 한국어로 설정
        calendarView.setWeekDayFormatter(WeekDayFormatter { dayOfWeek ->
            when (dayOfWeek) {
                Calendar.SUNDAY -> "일"
                Calendar.MONDAY -> "월"
                Calendar.TUESDAY -> "화"
                Calendar.WEDNESDAY -> "수"
                Calendar.THURSDAY -> "목"
                Calendar.FRIDAY -> "금"
                Calendar.SATURDAY -> "토"
                else -> ""
            }
        })

        loadRoutineStats(routineId)
    }

    private fun loadRoutineStats(routineId: Int) {
        val currentMonth = YearMonth.now()
        val startOfMonth = currentMonth.atDay(1)
        val endOfMonth = currentMonth.atEndOfMonth()

        val routineChecks = dbHelper.getRoutineChecksForMonth(routineId, startOfMonth, endOfMonth)
        val completedDates = routineChecks.filter { it.isChecked }.map { LocalDate.parse(it.date) }

        val eventDates = HashSet<CalendarDay>()

        completedDates.forEach { date ->
            eventDates.add(CalendarDay.from(date.year, date.monthValue-1, date.dayOfMonth))
        }

        val colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary)
        calendarView.addDecorator(EventDecorator(colorPrimary, eventDates))

        val completedCount = completedDates.size
        dotTextView.text = "이번 달 달성 횟수: $completedCount"
    }

    private fun generateDateRange(start: LocalDate, end: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var current = start
        while (!current.isAfter(end)) {
            dates.add(current)
            current = current.plusDays(1)
        }
        return dates
    }

}
