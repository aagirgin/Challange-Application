package com.example.challapp.domain.models

import com.google.firebase.auth.FirebaseUser

data class ApplicationGroup(
    val groupName: String,
    val creationDate: String,
    val groupDescription:String,
    val userCount: Int = 1,
    val groupMembers: MutableList<String>,
    val groupOwner: String
    )