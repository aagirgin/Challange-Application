package com.example.challapp.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ApplicationGroup(
    val groupName: String = "",
    val creationDate: String = "",
    val groupDescription:String = "",
    val groupFeed: MutableList<@RawValue GroupFeed>? = mutableListOf(),
    val groupMembers: MutableList<@RawValue String?> = mutableListOf(),
    val invitationInvitePermission: InvitePermission = InvitePermission.ADMIN_ONLY,
    val groupOwner: String = ""
    ): Parcelable

data class GroupFeed(
    val userId: String = "",
    val questionDocumentId: String = "",
    val description: String? = ""
)

enum class InvitePermission {
    USERS_ALL,
    ADMIN_ONLY
}