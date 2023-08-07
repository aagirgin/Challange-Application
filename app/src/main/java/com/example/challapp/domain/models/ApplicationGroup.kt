package com.example.challapp.domain.models

data class ApplicationGroup(
    val groupName: String,
    val creationDate: String,
    val groupDescription:String,
    val userCount: Int = 1,
    val groupMembers: MutableList<Any?>,
    val groupOwner: String
    )