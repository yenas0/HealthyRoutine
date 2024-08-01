package com.example.healthyroutine

import java.time.LocalDate

data class RoutineData(
    val id: Int,
    val name: String,
    val date: LocalDate,
    val isCompleted: Boolean
)
