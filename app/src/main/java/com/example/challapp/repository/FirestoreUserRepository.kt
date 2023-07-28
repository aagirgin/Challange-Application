package com.example.challapp.repository

interface FirestoreUserRepository {
    suspend fun getUsername(userId: String): String?
    suspend fun changeUsername(userId: String,newUsername: String) :Boolean
    suspend fun giveInviteKeyIfNull(userId: String)
}