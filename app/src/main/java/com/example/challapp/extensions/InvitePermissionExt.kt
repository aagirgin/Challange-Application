package com.example.challapp.extensions

import com.example.challapp.domain.models.InvitePermission

fun InvitePermission.toStringValue(): String {
    return when (this) {
        InvitePermission.ADMIN_ONLY -> "ADMIN_ONLY"
        InvitePermission.USERS_ALL -> "USERS_ALL"
    }
}