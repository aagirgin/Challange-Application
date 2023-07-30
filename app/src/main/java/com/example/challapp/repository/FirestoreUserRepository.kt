package com.example.challapp.repository

import com.example.challapp.domain.models.ApplicationGroup
import com.google.firebase.auth.FirebaseUser

interface FirestoreUserRepository {

    suspend fun addGroupToFirestore(appGroup:ApplicationGroup)
    suspend fun getUsername(userId: String): String?
    suspend fun changeUsername(userId: String,newUsername: String) :Boolean
    suspend fun giveInviteKeyIfNull(userId: String)
    suspend fun signUp(email: String, password: String): FirebaseUser?
    suspend fun signIn(email: String, password: String): FirebaseUser?
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
}