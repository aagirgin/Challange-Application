package com.example.challapp.repository

import com.example.challapp.domain.models.ApplicationDailyChallenge
import com.example.challapp.domain.models.ApplicationDailyQuestion
import com.example.challapp.domain.models.ApplicationGroup
import com.example.challapp.domain.models.InvitePermission
import com.example.challapp.domain.models.InviteStatus
import com.example.challapp.domain.models.UserNotification
import com.example.challapp.domain.state.InvitationState
import com.example.challapp.domain.state.UiState
import com.google.firebase.auth.FirebaseUser
interface FirestoreUserRepository {

    fun getCurrentUser(): FirebaseUser?
    fun getFirebaseErrorMessage(): String?
    suspend fun getGroupNameById(groupId: String): String?
    suspend fun getUserIncludedGroupIds(userId: String): List<*>?
    suspend fun getUsername(userId: String): String?
    suspend fun getUserNotications(userId: String): MutableList<UserNotification>?
    suspend fun getGroupInformationByDocumentId(documentId: Any?): ApplicationGroup?
    suspend fun getAllDailyChallangesForUser(userId: String): MutableList<ApplicationDailyChallenge>?
    suspend fun getInviteKey(userId: String): String
    suspend fun getDailyQuestionInformationByDocumentId(documentId: String): ApplicationDailyQuestion
    suspend fun getDailyQuestionInformation(): ApplicationDailyQuestion
    suspend fun getUserInviteNotificationCount(userId: String): Int
    suspend fun getStreak(userId: String): Int?
    suspend fun addDailyChallengeToAllUserIncludedGroups(groupIds: List<*>?, description: String, documentId: String, userId: String): Boolean
    suspend fun addDailyChallangeToUser(userId: String, description: String, documentId: String): Boolean
    suspend fun addUserToFirestore(userId: String, email: String, fullName: String): Boolean
    suspend fun addGroupToFirestore(appGroup:ApplicationGroup): String?
    suspend fun updateIncludedGroupsForUser(userId: String, groupId: String): Boolean
    suspend fun updateStreakBasedOnDailyQuestions(userId: String)
    suspend fun changeUsername(userId: String,newUsername: String) :Boolean
    suspend fun changeGroupInvitationStatus(groupId: String,changedPermission: InvitePermission) : UiState<String>
    suspend fun incrementStreakCountByOne(userId: String)
    suspend fun checkUserAlreadyHaveSubmission(userId: String) : Boolean
    suspend fun acceptToGroupInvitation(userId: String,groupId: String):Int?
    suspend fun deleteOnRejectInvitation(notification:UserNotification,userId: String):Int?
    suspend fun userLeaveGroup(userId: String,groupId: String): Boolean
    suspend fun userDeleteGroup(groupId: String): Boolean
    suspend fun sendUserInvitationWithInviteKey(inviteKey: String, fromGroup: String, sender: String ): InvitationState
    suspend fun sendPasswordResetEmail(email: String) : Boolean
    suspend fun sendNotificationsToUser(documentId: String, sender: String, fromGroup: String,notificationType: InviteStatus): Boolean
    suspend fun giveInviteKeyIfNull(userId: String)
    suspend fun signUp(email: String, password: String): FirebaseUser?
    suspend fun signIn(email: String, password: String): FirebaseUser?
    fun signOut()





}