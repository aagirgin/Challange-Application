package com.example.challapp.domain.models

data class ApplicationUser(
    val username: String,
    val isAdmin: Boolean = false,
    val challangeStreak: Int = 0,
    val inviteKey: Int? = null,
    val includedGroups: MutableList<String> = mutableListOf(),
    val allDailyQuestions: MutableList<ApplicationDailyChallenge> = mutableListOf()
)
