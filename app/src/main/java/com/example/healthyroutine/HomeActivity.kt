package com.example.healthyroutine

import ChecklistItem
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private lateinit var tvDate: TextView
    private lateinit var toggleButton: ImageView
    private lateinit var calendarContainer: LinearLayout
    private lateinit var calendarView: CalendarView

    private lateinit var checklistAdapter: ChecklistAdapter
    private var currentWeekStart: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    private lateinit var gestureDetector: GestureDetector

    private var selectedDate: LocalDate? = LocalDate.now() // 기본 선택 날짜를 오늘로 설정
    private val routines = mutableListOf<Routine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recycler_view)
        btnAddItem = findViewById(R.id.btn_add_item)
        btnPreviousWeek = findViewById(R.id.btn_previous_week)
        btnNextWeek = findViewById(R.id.btn_next_week)
        weekDatesContainer = findViewById(R.id.week_dates_container)
        horizontalScrollView = findViewById(R.id.horizontal_scroll_view)
        tvDate = findViewById(R.id.tv_date)
        toggleButton = findViewById(R.id.toggle_button)
        calendarContainer = findViewById(R.id.calendar_container)
        calendarView = findViewById(R.id.calendar_view)

        checklistAdapter = ChecklistAdapter(mutableListOf())
        recyclerView.adapter = checklistAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        updateWeekDates()
        updateTitleDate(selectedDate ?: LocalDate.now())

        btnPreviousWeek.setOnClickListener {
            currentWeekStart = currentWeekStart.minusWeeks(1)
            updateWeekDates()
        }

        btnNextWeek.setOnClickListener {
            currentWeekStart = currentWeekStart.plusWeeks(1)
            updateWeekDates()
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

        toggleButton.setOnClickListener {
            if (calendarContainer.visibility == View.GONE) {
                calendarContainer.visibility = View.VISIBLE
                toggleButton.setImageResource(R.drawable.ic_arrow_up)
            } else {
                calendarContainer.visibility = View.GONE
                toggleButton.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selected = LocalDate.of(year, month + 1, dayOfMonth)
            selectedDate = selected
            currentWeekStart = selected.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            updateWeekDates()
            updateTitleDate(selected)
        }

        btnAddItem.setOnClickListener {
            val intent = Intent(this, RoutineAddActivity::class.java)
            startActivityForResult(intent, ADD_ROUTINE_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        updateTitleDate(selectedDate ?: LocalDate.now())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ROUTINE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val routineName = it.getStringExtra("routine_name") ?: ""
                val notificationEnabled = it.getBooleanExtra("notification_enabled", true)

                Log.d("HomeActivity", "Routine Name: $routineName")
                Log.d("HomeActivity", "Notification Enabled: $notificationEnabled")

                val routine = Routine(routineName, notificationEnabled)
                routines.add(routine)
                updateWeekDates()
            }
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
                updateTitleDate(date)
            }

            weekDatesContainer.addView(dateView)

            // 루틴 체크리스트 업데이트
            updateChecklistForDate(date)
        }
    }

    private fun updateChecklistForDate(date: LocalDate) {
        val checklistItems = routines.map { routine ->
            ChecklistItem(routine.name, false)
        }

        checklistAdapter.updateChecklist(checklistItems)
    }

    private fun updateTitleDate(date: LocalDate) {
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val year = date.year
        tvDate.text = getString(R.string.date_format, year, month)
    }

    companion object {
        const val ADD_ROUTINE_REQUEST_CODE = 1
    }
}
