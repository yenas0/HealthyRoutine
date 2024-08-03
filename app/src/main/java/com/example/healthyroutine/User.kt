package com.example.healthyroutine

data class User(
    var id: String = "",
    val name: String = "",  // This might be a display name or full name
    val points: Int = 0,
    val profileImageUrl: String = "",
    val username: String = "",  // Add this field for the user's username
    val nickname: String = ""   // Add this field for the user's nickname
)