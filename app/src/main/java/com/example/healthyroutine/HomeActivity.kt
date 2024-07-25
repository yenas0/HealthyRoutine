package com.example.healthyroutine

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var todoList: RecyclerView
    private lateinit var adapter: TodoListAdapter
    private lateinit var todoHeader: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        calendarView = findViewById(R.id.calendarView)
        todoList = findViewById(R.id.todo_list)
        todoHeader = findViewById(R.id.todo_header)

        val btnMonthly: Button = findViewById(R.id.btn_monthly)
        val btnWeekly: Button = findViewById(R.id.btn_weekly)
        val btnDaily: Button = findViewById(R.id.btn_daily)
        val btnHome: Button = findViewById(R.id.btn_home)
        val btnMenu: Button = findViewById(R.id.btn_menu)
        val btnMyPage: Button = findViewById(R.id.btn_my_page)

        todoList.layoutManager = LinearLayoutManager(this)
        adapter = TodoListAdapter(mutableListOf())
        todoList.adapter = adapter

        btnMonthly.setOnClickListener { showMonthlyView() }
        btnWeekly.setOnClickListener { showWeeklyView() }
        btnDaily.setOnClickListener { showDailyView() }

        // Set default view
        showMonthlyView()
    }

    private fun showMonthlyView() {
        calendarView.visibility = View.VISIBLE
        todoHeader.text = "월간 할 일"
        adapter.updateTodoList(getMonthlyTodoList())
    }

    private fun showWeeklyView() {
        calendarView.visibility = View.VISIBLE
        todoHeader.text = "주간 할 일"
        adapter.updateTodoList(getWeeklyTodoList())
    }

    private fun showDailyView() {
        calendarView.visibility = View.GONE
        todoHeader.text = "오늘의 할 일"
        adapter.updateTodoList(getDailyTodoList())
    }

    private fun getMonthlyTodoList(): List<String> {
        // Fetch and return monthly todo list
        return listOf("월간 할 일 1", "월간 할 일 2", "월간 할 일 3")
    }

    private fun getWeeklyTodoList(): List<String> {
        // Fetch and return weekly todo list
        return listOf("주간 할 일 1", "주간 할 일 2", "주간 할 일 3")
    }

    private fun getDailyTodoList(): List<String> {
        // Fetch and return daily todo list
        return listOf("오늘의 할 일 1", "오늘의 할 일 2", "오늘의 할 일 3")
    }
}
