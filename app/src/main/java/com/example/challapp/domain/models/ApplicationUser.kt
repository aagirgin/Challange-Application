package com.example.challapp.domain.models

data class ApplicationUser(
    val username: String,
    val isAdmin: Boolean = false,
    val challangeStreak: Int = 0,
    val avatarUrl: String? = null
)
