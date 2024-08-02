package com.example.healthyroutine

import java.util.Date

data class Post(
    var id: String = "",
    var title: String = "",
    var content: String = "",
    var likes: Int = 0,
    var routine: String? = null,
    var routineDays: String? = null,
    var userId: String = "",
    var createdAt: Date = Date()
)
