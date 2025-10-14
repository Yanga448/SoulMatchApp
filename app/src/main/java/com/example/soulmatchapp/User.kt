package com.example.soulmatchapp

data class User(
    val uid: String = "",
    val name: String = "",
    val gender: String = "",
    val interests: List<String> = listOf(),
    val profileImage: String? = null
)
