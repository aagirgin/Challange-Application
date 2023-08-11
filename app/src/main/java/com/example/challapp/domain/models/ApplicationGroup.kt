package com.example.challapp.domain.models

data class ApplicationGroup(
    val groupName: String = "",
    val creationDate: String = "",
    val groupDescription:String = "",
    val groupFeed: MutableList<GroupFeed>? = mutableListOf(),
    val groupMembers: MutableList<Any?> = mutableListOf(),
    val groupOwner: String = ""
    )

data class GroupFeed(
    val userId: String = "",
    val questionDocumentId: String = "",
    val description: String? = ""
)