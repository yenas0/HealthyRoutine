package com.example.healthyroutine

import ChecklistAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthyroutine.R

class HomeActivity : AppCompatActivity() {
    private lateinit var checklistAdapter: ChecklistAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val editText: EditText = findViewById(R.id.et_new_item)
        val button: Button = findViewById(R.id.btn_add_item)

        checklistAdapter = ChecklistAdapter(mutableListOf())
        recyclerView.adapter = checklistAdapter
        recyclerView.layoutManager = LinearLayoutManager(this) }


        }

