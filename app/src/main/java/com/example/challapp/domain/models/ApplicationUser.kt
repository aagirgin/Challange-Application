package com.example.challapp.domain.models

import androidx.annotation.Keep

@Keep
data class ApplicationUser(
    val username: String = "None",
    val isAdmin: Boolean = false,
    val challangeStreak: Int = 0,
    val inviteKey: String? = null,
    val notifications: MutableList<UserNotification> = mutableListOf(),
    val includedGroups: MutableList<String> = mutableListOf(),
    val allDailyQuestions: MutableList<ApplicationDailyChallenge> = mutableListOf()
)

data class UserNotification(
    val notificationType: InviteStatus = InviteStatus.INFO,
    val notificationFromGroup: String = "None",
    val notificationSenderUser: String = "None"
)

enum class InviteStatus {
    INVITE,
    INFO,
    DELETED_GROUP_INFO
}