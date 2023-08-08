package com.example.challapp.repository

import com.example.challapp.domain.models.ApplicationDailyChallenge
import com.example.challapp.domain.models.ApplicationDailyQuestion
import com.example.challapp.domain.models.ApplicationGroup
import com.google.firebase.auth.FirebaseUser

interface FirestoreUserRepository {
    suspend fun incrementStreakCountByOne(userId: String)
    suspend fun updateStreakBasedOnDailyQuestions(userId: String)
    suspend fun getStreak(userId: String): Long
    suspend fun checkUserAlreadyHaveSubmission(userId: String) : Boolean
    suspend fun sendPasswordResetEmail(email: String) : Boolean
    suspend fun getUserIncludedGroupIds(userId: String): List<*>?
    suspend fun addUserToFirestore(userId: String, email: String, fullName: String): Boolean
    suspend fun addGroupToFirestore(appGroup:ApplicationGroup): String?
    suspend fun getUsername(userId: String): String?
    suspend fun changeUsername(userId: String,newUsername: String) :Boolean
    suspend fun giveInviteKeyIfNull(userId: String)
    fun getFirebaseErrorMessage(): String?
    suspend fun signUp(email: String, password: String): FirebaseUser?
    suspend fun signIn(email: String, password: String): FirebaseUser?
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
    suspend fun updateIncludedGroupsForUser(userId: String, groupId: String): Boolean
    suspend fun getGroupInformationByDocumentId(documentId: Any?): ApplicationGroup?
    suspend fun getDailyQuestionInformationByDocumentId(documentId: String): ApplicationDailyQuestion
    suspend fun getDailyQuestionInformation(): ApplicationDailyQuestion
    suspend fun addDailyChallangeToUser(userId: String, description: String, documentId: String): Boolean
    suspend fun getAllDailyChallangesForUser(userId: String): MutableList<ApplicationDailyChallenge>?
}