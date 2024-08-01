package com.example.healthyroutine

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.CalendarView
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddItem: ImageView
    private lateinit var btnPreviousWeek: ImageView
    private lateinit var btnNextWeek: ImageView
    private lateinit var weekDatesContainer: LinearLayout
    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var tvDate: TextView // 상단바 제목 텍스트뷰
    private lateinit var toggleButton: ImageView // 상단바 토글 버튼
    private lateinit var calendarContainer: LinearLayout // 캘린더 컨테이너
    private lateinit var calendarView: CalendarView // 캘린더 뷰

    private lateinit var checklistAdapter: ChecklistAdapter
    private var currentWeekStart: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    private lateinit var gestureDetector: GestureDetector

    private var selectedDate: LocalDate? = LocalDate.now() // 기본 선택 날짜를 오늘로 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recycler_view)
        btnAddItem = findViewById(R.id.btn_add_item)
        btnPreviousWeek = findViewById(R.id.btn_previous_week)
        btnNextWeek = findViewById(R.id.btn_next_week)
        weekDatesContainer = findViewById(R.id.week_dates_container)
        horizontalScrollView = findViewById(R.id.horizontal_scroll_view)
        tvDate = findViewById(R.id.tv_date) // 상단바 제목 텍스트뷰
        toggleButton = findViewById(R.id.toggle_button) // 상단바 토글 버튼
        calendarContainer = findViewById(R.id.calendar_container) // 캘린더 컨테이너
        calendarView = findViewById(R.id.calendar_view) // 캘린더 뷰

        checklistAdapter = ChecklistAdapter(mutableListOf())
        recyclerView.adapter = checklistAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        updateWeekDates()

        btnPreviousWeek.setOnClickListener {
            currentWeekStart = currentWeekStart.minusWeeks(1)
            updateWeekDates()
        }

        btnNextWeek.setOnClickListener {
            currentWeekStart = currentWeekStart.plusWeeks(1)
            updateWeekDates()
        }

        toggleButton.setOnClickListener {
            if (calendarContainer.visibility == View.VISIBLE) {
                calendarContainer.visibility = View.GONE
                toggleButton.setImageResource(R.drawable.ic_arrow_down)
            } else {
                calendarContainer.visibility = View.VISIBLE
                toggleButton.setImageResource(R.drawable.ic_arrow_up)
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedCalendarDate = LocalDate.of(year, month + 1, dayOfMonth)
            selectedDate = selectedCalendarDate
            currentWeekStart = selectedCalendarDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            updateWeekDates()
            updateTopBarDate(selectedCalendarDate)
            calendarContainer.visibility = View.GONE
            toggleButton.setImageResource(R.drawable.ic_arrow_down)
        }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null && e2 != null) {
                    if (e1.x - e2.x > 100) {
                        currentWeekStart = currentWeekStart.plusWeeks(1)
                        updateWeekDates()
                        return true
                    } else if (e2.x - e1.x > 100) {
                        currentWeekStart = currentWeekStart.minusWeeks(1)
                        updateWeekDates()
                        return true
                    }
                }
                return false
            }
        })

        horizontalScrollView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun updateWeekDates() {
        weekDatesContainer.removeAllViews()
        val today = LocalDate.now()

        for (i in 0..6) {
            val date = currentWeekStart.plusDays(i.toLong())
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val dayOfMonth = date.dayOfMonth.toString()

            val dateView = layoutInflater.inflate(R.layout.date_button, weekDatesContainer, false)
            val tvDayOfWeek: TextView = dateView.findViewById(R.id.tv_day_of_week)
            val tvDate: TextView = dateView.findViewById(R.id.tv_date)
            val circleBackground: View = dateView.findViewById(R.id.circle_background)

            tvDayOfWeek.text = dayOfWeek
            tvDate.text = dayOfMonth

            when {
                date.isEqual(today) -> {
                    circleBackground.setBackgroundResource(R.drawable.circle_today_background)
                    tvDayOfWeek.setTextColor(ContextCompat.getColor(this, R.color.gray))
                    tvDate.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
                date.isBefore(today) -> {
                    circleBackground.setBackgroundResource(R.drawable.circle_past_background)
                }
                date.isAfter(today) -> {
                    circleBackground.setBackgroundResource(R.drawable.circle_future_background)
                }
            }

            if (date == selectedDate) {
                dateView.setBackgroundResource(R.drawable.circle_border)
            } else {
                dateView.setBackgroundResource(android.R.color.transparent)
            }

            dateView.setOnClickListener {
                selectedDate = date
                updateWeekDates()
                updateTopBarDate(date) // 상단바 제목 업데이트
            }

            weekDatesContainer.addView(dateView)
        }

        // 기본 선택 날짜의 상단바 제목 업데이트
        selectedDate?.let { updateTopBarDate(it) }
    }

    private fun updateTopBarDate(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.getDefault())
        tvDate.text = date.format(formatter)
    }
}
