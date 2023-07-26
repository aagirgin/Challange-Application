package com.example.challapp.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreUserRepository {


    override suspend fun getUsername(userId: String): String? {
        return try {
            val userDocRef = firestore.collection("Users").document(userId).get().await()
            val userData = userDocRef.data
            userData?.get("username") as? String
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun changeUsername(userId: String, newUsername: String): Boolean {
        return try {
            val userDocRef = firestore.collection("Users").document(userId)
            userDocRef.update("username", newUsername).await()
            true

        } catch (e: Exception) {
            Log.e("FirestoreUserRepo", "Error updating username: ${e.message}")
            false
        }
    }
}