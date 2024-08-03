package com.example.healthyroutine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    lateinit var bottom_navigation: BottomNavigationView

    private var currentWeekStart: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    private lateinit var gestureDetector: GestureDetector

    private var selectedDate: LocalDate? = LocalDate.now()
    private val dbHelper = DatabaseHelper(this)
    private val firestoreHelper = FirestoreHelper()
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private val routines = mutableListOf<Routine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

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

        bottom_navigation = findViewById(R.id.bottom_navigation)
        bottom_navigation.selectedItemId = R.id.navigation_home

        // BottomNavigationView 설정
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // 홈 화면으로 이동
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_home).isChecked = true
                    true
                }
                R.id.navigation_recommend -> {
                    // 추천 화면으로 이동
                    val intent = Intent(this, RecommendActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_recommend).isChecked = true
                    true
                }
                R.id.navigation_board -> {
                    // 게시판 화면으로 이동
                    val intent = Intent(this, BoardActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_board).isChecked = true
                    true
                }
                R.id.navigation_ranking -> {
                    // 랭킹 화면으로 이동
                    val intent = Intent(this, RankingActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_ranking).isChecked = true
                    true
                }
                R.id.navigation_profile -> {
                    // 마이페이지 화면으로 이동
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_profile).isChecked = true
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateTitleDate(selectedDate ?: LocalDate.now())
        loadRoutinesFromDatabase()
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
        updateChecklistForDate(selectedDate ?: LocalDate.now())
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
                else -> {
                    val checkCount = dbHelper.getCheckCountForDate(date)
                    if (checkCount >= 3) {
                        circleBackground.setBackgroundResource(R.drawable.circle_past_background)
                    } else {
                        circleBackground.setBackgroundResource(R.drawable.circle_future_background)
                    }
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
                updateChecklistForDate(date)
            }

            weekDatesContainer.addView(dateView)
        }
    }

    private fun updateChecklistForDate(date: LocalDate) {
        checklistContainer.removeAllViews()
        var checkedCount = dbHelper.getCheckCountForDate(date)
        routines.forEach { routine ->
            val checklistItemView = layoutInflater.inflate(R.layout.item_checklist, checklistContainer, false)
            val routineName: TextView = checklistItemView.findViewById(R.id.routine_name)
            val checkBox: CheckBox = checklistItemView.findViewById(R.id.checkbox)
            val container: View = checklistItemView.findViewById(R.id.container)

            routineName.text = routine.name
            val routineCheck = dbHelper.getRoutineCheck(routine.id, date.toString())
            checkBox.isChecked = routineCheck?.isChecked ?: false

            if (checkBox.isChecked) {
                container.setBackgroundResource(R.drawable.item_background_checked)
            } else {
                container.setBackgroundResource(R.drawable.item_background)
            }

            container.setOnClickListener {
                showPopupMenu(ChecklistItem(routine.id, routine.name, checkBox.isChecked), container)
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                routineCheck?.isChecked = isChecked

                if (isChecked) {
                    container.setBackgroundResource(R.drawable.item_background_checked)
                    checkedCount++
                    if (checkedCount == 3) {
                        updatePoints(10) // 첫 10포인트 추가
                        updatePoints(10) // 보너스 10포인트 추가
                        showToast("3개 달성! 보너스 10 포인트 획득!")
                    } else {
                        updatePoints(10)
                        showToast("10 포인트를 얻었어요!")
                    }
                } else {
                    container.setBackgroundResource(R.drawable.item_background)
                    checkedCount--
                    updatePoints(-10)
                }

                if (routineCheck == null) {
                    dbHelper.addRoutineCheck(RoutineCheck(routine.id, date.toString(), isChecked))
                } else {
                    dbHelper.updateRoutineCheck(routineCheck)
                }

                updateWeekDates()
            }

            checklistContainer.addView(checklistItemView)
        }
    }

    private fun updatePoints(pointsChange: Int) {
        currentUser?.let { user ->
            val userId = user.uid
            firestoreHelper.updateUserPoints(userId, pointsChange) { success ->
                if (success) {
                    println("Points updated successfully")
                } else {
                    println("Failed to update points")
                }
            }
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

        val width = resources.getDimensionPixelSize(R.dimen.popup_width)  // dimen에서 값을 불러옴
        popupWindow.width = width

        // anchorView의 위치
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val x = location[0] + anchorView.width
        val y = location[1] + anchorView.height

        // 팝업 창을 화면의 특정 위치에 표시
        popupWindow.showAtLocation(anchorView, 0, x, y)

        val itemStatistics: LinearLayout = popupView.findViewById(R.id.item_statistics)
        val itemEdit: LinearLayout = popupView.findViewById(R.id.item_edit)
        val itemDelete: LinearLayout = popupView.findViewById(R.id.item_delete)

        if (item.isCompleted) {
            anchorView.setBackgroundResource(R.drawable.item_background_checked_selected)
        } else {
            anchorView.setBackgroundResource(R.drawable.item_border_selected)
        }

        popupWindow.setOnDismissListener {
            if (item.isCompleted) {
                anchorView.setBackgroundResource(R.drawable.item_background_checked)
            } else {
                anchorView.setBackgroundResource(R.drawable.item_background)
            }
        }

        itemStatistics.setOnClickListener {
            val intent = Intent(this, MonthlyStatsActivity::class.java)
            Toast.makeText(this, "월간 통계 클릭", Toast.LENGTH_SHORT).show()
            startActivity(intent)
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ADD_ROUTINE_REQUEST_CODE = 1
        const val EDIT_ROUTINE_REQUEST_CODE = 2
    }
}
