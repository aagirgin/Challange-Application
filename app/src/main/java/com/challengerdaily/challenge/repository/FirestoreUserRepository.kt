package com.challengerdaily.challenge.repository

import com.challengerdaily.challenge.domain.models.ApplicationDailyChallenge
import com.challengerdaily.challenge.domain.models.ApplicationDailyQuestion
import com.challengerdaily.challenge.domain.models.ApplicationGroup
import com.challengerdaily.challenge.domain.models.ApplicationUser
import com.challengerdaily.challenge.domain.models.InvitePermission
import com.challengerdaily.challenge.domain.models.InviteStatus
import com.challengerdaily.challenge.domain.models.UserNotification
import com.challengerdaily.challenge.domain.state.InvitationState
import com.challengerdaily.challenge.domain.state.UiState
import com.google.firebase.auth.FirebaseUser
import kotlinx.parcelize.RawValue

interface FirestoreUserRepository {

    fun getCurrentUser(): FirebaseUser?
    fun getFirebaseErrorMessage(): String?
    suspend fun getGroupNameById(groupId: String): String?
    suspend fun getUserIncludedGroupIds(userId: String): List<*>?
    suspend fun getUsername(userId: String): String?
    suspend fun getUserNotifications(userId: String): MutableList<UserNotification>?
    suspend fun getUsersNameAsMap(userList: MutableList<@RawValue String?>): Map<String,String>
    suspend fun getGroupInformationByDocumentId(documentId: Any?): ApplicationGroup?
    suspend fun getAllDailyChallangesForUser(userId: String): MutableList<ApplicationDailyChallenge>?
    suspend fun getInviteKey(userId: String): String
    suspend fun getDailyQuestionInformationByDocumentId(documentId: String): ApplicationDailyQuestion
    suspend fun getDailyQuestionInformation(): ApplicationDailyQuestion
    suspend fun getUserInviteNotificationCount(userId: String): Int
    suspend fun getApplicationUserById(userId: String): ApplicationUser?
    suspend fun getStreak(userId: String): Int
    suspend fun addDailyChallengeToAllUserIncludedGroups(groupIds: List<*>?, description: String, documentId: String, userId: String): Boolean
    suspend fun addDailyChallangeToUser(userId: String, description: String, documentId: String): Boolean
    suspend fun addUserToFirestore(userId: String, email: String, fullName: String?): Boolean
    suspend fun addGroupToFirestore(appGroup:ApplicationGroup): String?
    suspend fun updateIncludedGroupsForUser(userId: String, groupId: String): Boolean
    suspend fun updateStreakBasedOnDailyQuestions(userId: String)
    suspend fun changeUsername(userId: String,newUsername: String) :Boolean
    suspend fun changeGroupInvitationStatus(groupId: String,changedPermission: InvitePermission) : UiState<String>
    suspend fun incrementStreakCountByOne(userId: String)
    suspend fun checkDocumentExistsForUser(userId: String): Boolean
    suspend fun checkUserAlreadyHaveSubmission(userId: String) : Boolean
    suspend fun acceptToGroupInvitation(userId: String,groupId: String):Int?
    suspend fun deleteOnRejectInvitation(notification:UserNotification,userId: String):Int?
    suspend fun userLeaveGroup(userId: String,groupId: String): Boolean
    suspend fun userDeleteGroup(groupId: String): Boolean
    suspend fun sendUserInvitationWithInviteKey(inviteKey: String, fromGroup: String, sender: String ): InvitationState
    suspend fun sendPasswordResetEmail(email: String) : Boolean
    suspend fun sendNotificationsToUser(documentId: String, sender: String, fromGroup: String,notificationType: InviteStatus): Boolean
    suspend fun giveInviteKeyIfNull(userId: String)
    suspend fun loginWithGoogle(idToken: String?): FirebaseUser?
    suspend fun signUp(email: String, password: String): FirebaseUser?
    suspend fun signIn(email: String, password: String): FirebaseUser?

    suspend fun deleteAccount(userId: String): Boolean
    fun signOut()





}