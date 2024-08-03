package com.example.healthyroutine

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.text.style.ReplacementSpan
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.LocalDate

class MonthlyStatsActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var routineNameTextView: TextView
    private lateinit var checkCountTextView: TextView
    private lateinit var backButton: ImageButton
    private val eventDates = mutableListOf<LocalDate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_stats)

        calendarView = findViewById(R.id.calendar_view)
        routineNameTextView = findViewById(R.id.routine_name_text_view)
        checkCountTextView = findViewById(R.id.dottextview)
        backButton = findViewById(R.id.imageButton2)

        val routineId = intent.getIntExtra("ROUTINE_ID", -1)
        loadRoutineData(routineId)

        calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedDate = LocalDate.of(date.year, date.month + 1, date.day)
            if (eventDates.contains(selectedDate)) {
                checkCountTextView.text = "Good"
            } else {
                checkCountTextView.text = "No  on this day."
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadRoutineData(routineId: Int) {
        // 이벤트 날짜를 불러와 캘린더에 점을 표시
        loadEventDates()
        val decorator = EventDecorator(getColor(R.color.colorAccent), eventDates)
        calendarView.addDecorator(decorator)
    }

    private fun loadEventDates() {
        // 데이터베이스나 SharedPreferences에서 이벤트 날짜를 로드
        val checkedDates = getCheckedDatesFromDatabase()
        eventDates.addAll(checkedDates.map { LocalDate.parse(it) })
    }

    private fun getCheckedDatesFromDatabase(): List<String> {
        // 데이터베이스에서 체크된 날짜를 가져오는 로직
        return emptyList()
    }

    class CustomDotSpan(private val radius: Float, private val color: Int) : ReplacementSpan() {
        override fun getSize(
            paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?
        ): Int {
            return (2 * radius).toInt()
        }

        override fun draw(
            canvas: Canvas, text: CharSequence?, start: Int, end: Int,
            x: Float, top: Int, y: Int, bottom: Int, paint: Paint
        ) {
            val oldColor = paint.color
            paint.color = color
            canvas.drawCircle(x + radius, (top + bottom) / 2f, radius, paint)
            paint.color = oldColor
        }
    }
}

class EventDecorator(private val color: Int, private val dates: List<LocalDate>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        // 날짜가 이벤트 날짜 목록에 있는지 여부를 확인
        return dates.contains(LocalDate.of(day.year, day.month + 1, day.day))
    }

    override fun decorate(view: DayViewFacade) {
        // 날짜에 점을 추가
        view.addSpan(DotSpan(10f, color))  // 10f는 점의 반지름입니다. 필요에 따라 조절 가능
    }

    // 점을 그리는 Span 클래스
    class DotSpan(private val radius: Float, private val color: Int) : android.text.style.ReplacementSpan() {
        override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
            return (2 * radius).toInt()
        }

        override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
            val oldColor = paint.color
            paint.color = color
            canvas.drawCircle(x + radius, (top + bottom) / 2f, radius, paint)
            paint.color = oldColor
        }
    }
}
