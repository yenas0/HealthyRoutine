package com.example.healthyroutine

import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.Calendar
import java.util.Date

class MonthlyStatsActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var routineNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_stats)

        routineNameTextView = findViewById(R.id.routine_name_text_view)
        calendarView = findViewById(R.id.calendar_view)

        val routineDays = getRoutineDaysFromDB()

        val routineEventDates = HashSet<CalendarDay>()
        val nonRoutineEventDates = HashSet<CalendarDay>()

        // Populate routine and non-routine dates
        routineDays.forEach {
            routineEventDates.add(CalendarDay.from(it))
        }

        // For demonstration, let's assume we have all dates in the current month for non-routine days
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        while (calendar.get(Calendar.MONTH) == month) {
            val day = CalendarDay.from(calendar)
            if (!routineEventDates.contains(day)) {
                nonRoutineEventDates.add(day)
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calendarView.setTitleFormatter(
            MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months))
        )
        calendarView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))
        calendarView.setSelectedDate(CalendarDay.today())

        calendarView.addDecorators(
            RoutineEventDecorator(Color.GREEN, routineEventDates),
            NonRoutineEventDecorator(Color.GRAY, nonRoutineEventDates),
            TodayDecorator()
        )
    }

    private fun getRoutineDaysFromDB(): List<Date> {
        val dates = mutableListOf<Date>()
        // Fetch actual routine days from database
        // Sample data:
        val calendar = Calendar.getInstance()
        calendar.set(2024, 7, 1)
        dates.add(calendar.time)
        calendar.set(2024, 7, 3)
        dates.add(calendar.time)
        // Add more sample data as needed
        return dates
    }

    class RoutineEventDecorator(private val color: Int, dates: Collection<CalendarDay>) : DayViewDecorator {
        private val dates: HashSet<CalendarDay> = HashSet(dates)

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(DotSpan(10F, color))
        }
    }

    class NonRoutineEventDecorator(private val color: Int, dates: Collection<CalendarDay>) : DayViewDecorator {
        private val dates: HashSet<CalendarDay> = HashSet(dates)

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(DotSpan(10F, color))
        }
    }

    class TodayDecorator : DayViewDecorator {
        private val today = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day == today
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(RoundBackgroundSpan(Color.CYAN, 20f))
            view?.addSpan(ForegroundColorSpan(Color.BLUE))
        }
    }
}

class RoundBackgroundSpan(cyan: Int, fl: Float) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("cyan"),
        TODO("fl")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoundBackgroundSpan> {
        override fun createFromParcel(parcel: Parcel): RoundBackgroundSpan {
            return RoundBackgroundSpan(parcel)
        }

        override fun newArray(size: Int): Array<RoundBackgroundSpan?> {
            return arrayOfNulls(size)
        }
    }

}
