// HomeActivity.kt
package com.example.healthyroutine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var btnAddItem: ImageView
    private lateinit var btnPreviousWeek: ImageView
    private lateinit var btnNextWeek: ImageView
    private lateinit var weekDatesContainer: LinearLayout
    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var tvDate: TextView
    private lateinit var toggleButton: ImageView
    private lateinit var calendarContainer: LinearLayout
    private lateinit var calendarView: CalendarView
    private lateinit var checklistContainer: LinearLayout

    private var currentWeekStart: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    private lateinit var gestureDetector: GestureDetector

    private var selectedDate: LocalDate? = LocalDate.now() // 기본 선택 날짜를 오늘로 설정
    private lateinit var dbHelper: DatabaseHelper
    private val routines = mutableListOf<Routine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnAddItem = findViewById(R.id.btn_add_item)
        btnPreviousWeek = findViewById(R.id.btn_previous_week)
        btnNextWeek = findViewById(R.id.btn_next_week)
        weekDatesContainer = findViewById(R.id.week_dates_container)
        horizontalScrollView = findViewById(R.id.horizontal_scroll_view)
        tvDate = findViewById(R.id.tv_date)
        toggleButton = findViewById(R.id.toggle_button)
        calendarContainer = findViewById(R.id.calendar_container)
        calendarView = findViewById(R.id.calendar_view)
        checklistContainer = findViewById(R.id.checklist_container)

        dbHelper = DatabaseHelper(this)

        loadRoutinesFromDatabase()
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

                val routine = Routine(id = 0, name = routineName, notificationEnabled = notificationEnabled)
                dbHelper.addRoutine(routine)
                loadRoutinesFromDatabase()
            }
        } else if (requestCode == EDIT_ROUTINE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val routineId = it.getIntExtra("routine_id", 0)
                val routineName = it.getStringExtra("routine_name") ?: ""
                val notificationEnabled = it.getBooleanExtra("notification_enabled", true)

                Log.d("HomeActivity", "Updated Routine ID: $routineId")
                Log.d("HomeActivity", "Updated Routine Name: $routineName")
                Log.d("HomeActivity", "Updated Notification Enabled: $notificationEnabled")

                val updatedRoutine = Routine(id = routineId, name = routineName, notificationEnabled = notificationEnabled)
                dbHelper.updateRoutine(updatedRoutine)
                loadRoutinesFromDatabase()
            }
        }
    }

    private fun loadRoutinesFromDatabase() {
        routines.clear()
        routines.addAll(dbHelper.getAllRoutines())
        updateWeekDates()
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
        checklistContainer.removeAllViews()
        routines.forEach { routine ->
            val checklistItemView = layoutInflater.inflate(R.layout.item_checklist, checklistContainer, false)
            val routineName: TextView = checklistItemView.findViewById(R.id.routine_name)
            val checkBox: CheckBox = checklistItemView.findViewById(R.id.checkbox)
            val container: View = checklistItemView.findViewById(R.id.container)

            routineName.text = routine.name
            checkBox.isChecked = false

            container.setOnClickListener {
                showPopupMenu(ChecklistItem(routine.id, routine.name, checkBox.isChecked), container)
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checklistItemView.isSelected = false
                    container.setBackgroundResource(R.drawable.item_background_checked)
                } else {
                    checklistItemView.isSelected = false
                    container.setBackgroundResource(R.drawable.item_background)
                }
            }

            checklistContainer.addView(checklistItemView)
        }
    }

    private fun updateTitleDate(date: LocalDate) {
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val year = date.year
        tvDate.text = getString(R.string.date_format, year, month)
    }

    private fun showPopupMenu(item: ChecklistItem, anchorView: View) {
        val popupView = layoutInflater.inflate(R.layout.popup_menu, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        popupWindow.showAsDropDown(anchorView)

        val itemStatistics: LinearLayout = popupView.findViewById(R.id.item_statistics)
        val itemEdit: LinearLayout = popupView.findViewById(R.id.item_edit)
        val itemDelete: LinearLayout = popupView.findViewById(R.id.item_delete)

        // 팝업창이 열릴 때 검정 외곽선 추가
        anchorView.setBackgroundResource(R.drawable.item_border_selected)

        // 팝업창이 닫힐 때 검정 외곽선 제거
        popupWindow.setOnDismissListener {
            anchorView.setBackgroundResource(if (item.isCompleted) R.drawable.item_background_checked else R.drawable.item_background)
        }

        itemStatistics.setOnClickListener {
            Toast.makeText(this, "월간 통계 클릭", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        itemEdit.setOnClickListener {
            val intent = Intent(this, RoutineEditActivity::class.java).apply {
                putExtra("routine_id", item.id)
                putExtra("routine_name", item.name)
                putExtra("notification_enabled", item.isCompleted)
            }
            startActivityForResult(intent, EDIT_ROUTINE_REQUEST_CODE)
            popupWindow.dismiss()
        }

        itemDelete.setOnClickListener {
            dbHelper.deleteRoutine(item.id)
            loadRoutinesFromDatabase()
            popupWindow.dismiss()
        }
    }

    companion object {
        const val ADD_ROUTINE_REQUEST_CODE = 1
        const val EDIT_ROUTINE_REQUEST_CODE = 2
    }
}
