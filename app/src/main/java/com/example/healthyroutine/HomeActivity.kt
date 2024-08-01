package com.example.healthyroutine

import android.graphics.Color
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
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

    private lateinit var checklistAdapter: ChecklistAdapter
    private var currentWeekStart: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    private lateinit var gestureDetector: GestureDetector
    private var selectedDateView: View? = null

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
                    tvDate.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
                date.isAfter(today) -> {
                    circleBackground.setBackgroundResource(R.drawable.circle_future_background)
                    tvDate.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
            }

            dateView.setOnClickListener {
                selectDate(dateView, date)
            }

            weekDatesContainer.addView(dateView)
        }
    }

    private fun selectDate(view: View, date: LocalDate) {
        selectedDateView?.let {
            val circleBackground: View = it.findViewById(R.id.circle_background)
            val tvDate: TextView = it.findViewById(R.id.tv_date)
            val tvDayOfWeek: TextView = it.findViewById(R.id.tv_day_of_week)

            // 기존에 선택된 날짜 스타일을 원래대로 돌려놓기
            circleBackground.setBackgroundResource(0)
            when {
                date.isEqual(LocalDate.now()) -> {
                    circleBackground.setBackgroundResource(R.drawable.circle_today_background)
                    tvDayOfWeek.setTextColor(ContextCompat.getColor(this, R.color.gray))
                    tvDate.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
                date.isBefore(LocalDate.now()) -> {
                    circleBackground.setBackgroundResource(R.drawable.circle_past_background)
                    tvDate.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
                date.isAfter(LocalDate.now()) -> {
                    circleBackground.setBackgroundResource(R.drawable.circle_future_background)
                    tvDate.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
            }
        }

        // 새로운 선택된 날짜 스타일 적용
        val circleBackground: View = view.findViewById(R.id.circle_background)
        circleBackground.setBackgroundResource(R.drawable.circle_selected_background)

        selectedDateView = view

        // 상단 바에 년도와 월 업데이트
        val yearMonth = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy년 M월"))
        tvDate.text = yearMonth
    }
}
