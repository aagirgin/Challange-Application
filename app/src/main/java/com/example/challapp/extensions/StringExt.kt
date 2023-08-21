package com.example.challapp.extensions

import com.example.challapp.domain.models.InvitePermission

fun String.toInvitePermission(): InvitePermission {
    return when (this) {
        "ADMIN_ONLY" -> InvitePermission.ADMIN_ONLY
        else -> InvitePermission.USERS_ALL
    }
}