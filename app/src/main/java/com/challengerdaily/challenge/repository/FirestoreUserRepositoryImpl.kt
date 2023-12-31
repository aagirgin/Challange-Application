package com.challengerdaily.challenge.repository

import android.util.Log
import com.challengerdaily.challenge.domain.models.ApplicationDailyChallenge
import com.challengerdaily.challenge.domain.models.ApplicationDailyQuestion
import com.challengerdaily.challenge.domain.models.ApplicationGroup
import com.challengerdaily.challenge.domain.models.ApplicationUser
import com.challengerdaily.challenge.domain.models.InviteStatus
import com.challengerdaily.challenge.domain.models.InvitePermission
import com.challengerdaily.challenge.domain.models.UserNotification
import com.challengerdaily.challenge.domain.state.InvitationState
import com.challengerdaily.challenge.domain.state.UiState
import com.challengerdaily.challenge.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.parcelize.RawValue
import org.threeten.bp.LocalDate
import javax.inject.Inject

class FirestoreUserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FirestoreUserRepository {



    override suspend fun getUsername(userId: String): String? {
        return try {
            val userDocRef = firestore.collection("Users").document(userId).get().await()
            val userData = userDocRef.toObject(ApplicationUser::class.java)
            userData?.username
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getUserNotifications(userId: String): MutableList<UserNotification>? {
        val userDocRef = firestore.collection("Users").document(userId).get().await()
        val userData = userDocRef.toObject(ApplicationUser::class.java)
        return userData?.notifications
    }

    override suspend fun getUsersNameAsMap(userList: MutableList<@RawValue String?>): Map<String, String> {
        val resultMap = mutableMapOf<String, String>()

        userList.forEach { userId ->
            val userName = userId?.let { uid -> getUsername(uid) }
            if (userName != null) {
                resultMap[userId] = userName
            }
        }
        return resultMap
    }

    override suspend fun getGroupNameById(groupId: String): String? {
        return try {
            val userDocRef = firestore.collection("Groups").document(groupId).get().await()
            val userData = userDocRef.toObject(ApplicationGroup::class.java)
            userData?.groupName
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addUserToFirestore(userId: String, email: String, fullName: String?): Boolean {
        val userCollection = firestore.collection("Users")
        val userDocument = userCollection.document(userId)
        return try {
            val applicationUser =
                ApplicationUser(
                    username = fullName,
                )
            userDocument.set(applicationUser).await()

            true
        } catch (e: Exception) {
            Log.e("FirestoreUserRepo", "Error adding user to Firestore: ${e.message}")
            false
        }
    }

    override suspend fun addGroupToFirestore(appGroup: ApplicationGroup):  String?  {
        val groupCollection = firestore.collection("Groups")
        return try {
            val documentReference = groupCollection.add(appGroup).await()

            documentReference.id
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getGroupInformationByDocumentId(documentId: Any?): ApplicationGroup? {
        val groupDocumentRef = firestore.collection("Groups").document(documentId.toString())
        val documentSnapshot = groupDocumentRef.get().await()

        return documentSnapshot.toObject(ApplicationGroup::class.java)
    }


    override suspend fun getStreak(userId: String): Int {
        val userDocRef = firestore.collection("Users").document(userId).get().await()
        val userData = userDocRef.toObject(ApplicationUser::class.java)
        if (userData != null) {
            return userData.challangeStreak
        }
        return 0
    }

    override suspend fun incrementStreakCountByOne(userId: String) {
        val userDocumentRef = firestore.collection("Users").document(userId)
        val documentSnapshot = userDocumentRef.get().await()
        val userData = documentSnapshot.toObject(ApplicationUser::class.java)
        val currentStreak = userData?.challangeStreak
        val newStreak = currentStreak?.plus(1)
        userDocumentRef.update("challangeStreak", newStreak).await()
    }

    override suspend fun checkDocumentExistsForUser(userId: String): Boolean {
        val userDocRef = firestore.collection("Users").document(userId)
        val userDoc = userDocRef.get().await()

        if (!userDoc.exists()) {
            return false
        }
        return true
    }

    override suspend fun sendUserInvitationWithInviteKey(inviteKey: String, fromGroup: String, sender: String): InvitationState {
        val userCollection = firestore.collection("Users")
        val groupClass = firestore.collection("Groups").document(fromGroup).get().await().toObject(ApplicationGroup::class.java)
        val groupInvitePermission =  groupClass?.invitationPermission
        val groupOwner = groupClass?.groupOwner
        val query = userCollection.whereEqualTo("inviteKey", inviteKey)
        val querySnapshot = query.get().await()

        if (!querySnapshot.isEmpty) {
            for (documentSnapshot in querySnapshot.documents) {
                val docAsAppUser = documentSnapshot.toObject(ApplicationUser::class.java)
                val notificationsList = docAsAppUser?.notifications
                val userGroups = docAsAppUser?.includedGroups

                if (groupInvitePermission != InvitePermission.USERS_ALL){
                        if(sender != groupOwner){
                            return InvitationState.NoInvitationPermission
                        }
                }

                if (userGroups?.contains(fromGroup) == true){
                    return InvitationState.UserAlreadyMember
                }

                val requestAlreadySent = notificationsList?.any {
                    it.notificationType == InviteStatus.INVITE && it.notificationFromGroup == fromGroup
                } ?: false

                if (requestAlreadySent) {
                    return InvitationState.RequestAlreadySent
                }

                sendNotificationsToUser(documentSnapshot.id, sender, fromGroup, InviteStatus.INVITE)
                return InvitationState.Success
            }
        }

        return InvitationState.UserNotFound
    }

    override suspend fun sendNotificationsToUser(documentId: String, sender: String, fromGroup: String, notificationType: InviteStatus): Boolean {
        val userDocumentRef = firestore.collection("Users").document(documentId)
        val document = userDocumentRef.get().await()
        if (document.exists()) {
            val notificationsList = document.toObject(ApplicationUser::class.java)?.notifications
            val newNotification = UserNotification(notificationType = notificationType,
                                                    notificationFromGroup =  fromGroup,
                                                    notificationSenderUser = sender)
            notificationsList?.add(newNotification)
            userDocumentRef.update("notifications", notificationsList)
            return true
        }
        return false
    }


    override suspend fun updateStreakBasedOnDailyQuestions(userId: String) {
        val groupDocumentRef = firestore.collection("Users").document(userId)
        //val userData = groupDocumentRef.data
        val userData = groupDocumentRef.get().await().toObject(ApplicationUser::class.java)
        val allDailyQuestions = userData?.allDailyQuestions

        val currentDate = DateUtils.getCurrentFormattedDate()
        val previousDate = DateUtils.getPreviousFormattedDate()

        var consecutiveStreakCounter = 0L
        var lastDate: LocalDate? = null

        if (!allDailyQuestions.isNullOrEmpty()) {
            if (allDailyQuestions.last().questionDocumentId == currentDate || allDailyQuestions.last().questionDocumentId == previousDate) {
                for (i in allDailyQuestions.size - 1 downTo 0) {
                    val questionMap = allDailyQuestions[i]
                    val questionDateStr = questionMap.questionDocumentId
                    val questionDate = LocalDate.parse(questionDateStr)
                    if (lastDate == null || lastDate.minusDays(1) == questionDate) {
                        consecutiveStreakCounter++
                        lastDate = questionDate
                    } else {
                        break
                    }
                }
                groupDocumentRef.update("challangeStreak", consecutiveStreakCounter).await()
            }
            else{
                groupDocumentRef.update("challangeStreak", 0).await()
            }
        } else {
            return
        }
    }
    override suspend fun checkUserAlreadyHaveSubmission(userId: String): Boolean {
        val currentDate = DateUtils.getCurrentFormattedDate()
        val groupDocumentRef = firestore.collection("Users").document(userId).get().await()
        val userData = groupDocumentRef.toObject(ApplicationUser::class.java)
        val allDailyQuestions = userData?.allDailyQuestions

        allDailyQuestions?.forEach {
            if(it.questionDocumentId == currentDate){
                return true
            }
        }
        return false
    }

    override suspend fun acceptToGroupInvitation(userId: String,groupId: String): Int?{
        val groupDocumentRef = firestore.collection("Groups").document(groupId)
        val groupDocument = groupDocumentRef.get().await().toObject(ApplicationGroup::class.java)
        val groupMembers = groupDocument?.groupMembers

        val userDocumentRef = firestore.collection("Users").document(userId)
        val userDocument = userDocumentRef.get().await().toObject(ApplicationUser::class.java)
        val notifications = userDocument?.notifications
        val includedGroups = userDocument?.includedGroups

        if (groupMembers != null) {
            groupMembers.add(userId)
            groupDocumentRef.update("groupMembers", groupMembers).await()
        }

        includedGroups?.add(groupId)
        val updatedUserGroups = includedGroups?.let { userDocument.copy(includedGroups = it) }
        if (updatedUserGroups != null) {
            userDocumentRef.set(updatedUserGroups).await()
        }

        if (notifications != null) {
            val matchingIndex = notifications.indexOfFirst {
                it.notificationFromGroup == groupId && it.notificationType == InviteStatus.INVITE}
            if (matchingIndex >= 0) {
                notifications.removeAt(matchingIndex)

                val updatedUser = userDocument.copy(notifications = notifications)
                userDocumentRef.set(updatedUser).await()
                return matchingIndex
            }
        }
        return null
    }

    override suspend fun deleteOnRejectInvitation(
        notification: UserNotification,
        userId: String
    ): Int? {
        val userDocumentRef = firestore.collection("Users").document(userId)
        val userDocument = userDocumentRef.get().await().toObject(ApplicationUser::class.java)
        val notifications = userDocument?.notifications
        if (notifications != null) {
            val matchingIndex = notifications.indexOfFirst { it == notification }

            if (matchingIndex >= 0) {
                notifications.removeAt(matchingIndex)

                val updatedUser = userDocument.copy(notifications = notifications)
                userDocumentRef.set(updatedUser).await()
                return matchingIndex
            }
        }
        return null
    }

    override suspend fun userLeaveGroup(userId: String, groupId: String): Boolean {
        val userDocumentRef = firestore.collection("Users").document(userId)
        val groupDocumentRef = firestore.collection("Groups").document(groupId)
        return try {
            val userDocument = userDocumentRef.get().await().toObject(ApplicationUser::class.java)
            userDocument?.includedGroups?.remove(groupId)
            userDocumentRef.update("includedGroups", userDocument?.includedGroups).await()
            val groupDocument = groupDocumentRef.get().await().toObject(ApplicationGroup::class.java)
            groupDocument?.groupMembers?.remove(userId)
            groupDocumentRef.update("groupMembers", groupDocument?.groupMembers).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun userDeleteGroup(groupId: String): Boolean {
        val userDocumentRef = firestore.collection("Users")
        val groupDocumentRef = firestore.collection("Groups").document(groupId)

        return try {
            val groupDocument = groupDocumentRef.get().await().toObject(ApplicationGroup::class.java)
            groupDocument?.groupMembers?.forEach { user ->
                if (user != null) {
                    val userDoc = userDocumentRef.document(user)
                    val userGroups = userDoc.get().await().toObject(ApplicationUser::class.java)?.includedGroups
                    userGroups?.remove(groupId)
                    userDoc.update("includedGroups", userGroups).await()
                    sendNotificationsToUser(user,groupDocument.groupOwner,groupDocument.groupName,InviteStatus.DELETED_GROUP_INFO)
                }
            }
            groupDocumentRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getUserInviteNotificationCount(userId: String): Int {
        val notificationList = firestore.collection("Users").document(userId).get().await().toObject(ApplicationUser::class.java)?.notifications
        var count = 0
        notificationList?.forEach {
            if(it.notificationType == InviteStatus.INVITE){
                count++
            }
        }
        return count
    }

    override suspend fun getApplicationUserById(userId: String): ApplicationUser? {
        return firestore.collection("Users").document(userId).get().await().toObject(ApplicationUser::class.java)
    }

    override suspend fun getAllDailyChallangesForUser(userId: String): MutableList<ApplicationDailyChallenge>? {
        val groupDocumentRef = firestore.collection("Users").document(userId).get().await()
        return groupDocumentRef.toObject(ApplicationUser::class.java)?.allDailyQuestions
    }

    override suspend fun getInviteKey(userId: String): String {
        val groupDocumentRef = firestore.collection("Users").document(userId).get().await()
        val data = groupDocumentRef.toObject(ApplicationUser::class.java)
        return data?.inviteKey!!
    }

    override suspend fun getUserIncludedGroupIds(userId: String): List<*> {
        val userDocument = firestore.collection("Users").document(userId).get().await().toObject(ApplicationUser::class.java)
        return userDocument?.includedGroups!!
    }
    override suspend fun updateIncludedGroupsForUser(userId: String, groupId: String): Boolean {
        val userCollection = firestore.collection("Users")
        val userDocument = userCollection.document(userId)
        return try {
            userDocument.update("includedGroups", FieldValue.arrayUnion(groupId)).await()
            true
        } catch (e: Exception) {
            false
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

    override suspend fun changeGroupInvitationStatus(groupId: String,changedPermission: InvitePermission): UiState<String> {
        val groupDocRef = firestore.collection("Groups").document(groupId)

        return try {
            groupDocRef.update("invitationPermission", changedPermission).await()
            UiState.Success("Successfully changed the group invitation status.")
        } catch (e: Exception) {
            UiState.Error("Error occurred while changing invitation state.")
        }
    }

    override suspend fun addDailyChallengeToAllUserIncludedGroups(groupIds: List<*>?, description: String, documentId: String, userId: String) : Boolean{
        val groupDocRef = firestore.collection("Groups")
        return try{
            groupIds?.forEach { includedGroup ->
                groupDocRef.document(includedGroup.toString()).update("groupFeed", FieldValue.arrayUnion(
                    mapOf(
                        "userId" to userId,
                        "description" to description,
                        "questionDocumentId" to documentId
                    )
                )).await()
            }
            true
        }catch (e: Exception) {
            Log.e("FirestoreUserRepo", "Error updating daily questions: ${e.message}")
            false
        }
    }
    override suspend fun addDailyChallangeToUser(userId: String, description: String, documentId: String):Boolean {
        val userDocRef = firestore.collection("Users").document(userId)
        return try {
            userDocRef.update("allDailyQuestions", FieldValue.arrayUnion(
                mapOf(
                    "description" to description,
                    "questionDocumentId" to documentId
                )
            )).await()
            addDailyChallengeToAllUserIncludedGroups(getUserIncludedGroupIds(userId),description,documentId,userId)
            true
        }catch (e: Exception) {
            Log.e("FirestoreUserRepo", "Error updating daily questions: ${e.message}")
            false
        }
    }
    override suspend fun getDailyQuestionInformation(): ApplicationDailyQuestion{
        val currentDate = DateUtils.getCurrentFormattedDate()
        val userDocRef = firestore.collection("Questions").document(currentDate).get().await()
        val dailyQuestion = userDocRef.get("dailyQuestion") as? String
        val dailyQuestionName = userDocRef.get("dailyQuestionName") as? String
        return ApplicationDailyQuestion(
            dailyQuestion = dailyQuestion,
            dailyQuestionName = dailyQuestionName
        )
    }

    override suspend fun getDailyQuestionInformationByDocumentId(documentId: String): ApplicationDailyQuestion{

        val userDocRef = firestore.collection("Questions").document(documentId).get().await()
        val dailyQuestion = userDocRef.get("dailyQuestion") as? String
        val dailyQuestionName = userDocRef.get("dailyQuestionName") as? String
        return ApplicationDailyQuestion(
            dailyQuestion = dailyQuestion,
            dailyQuestionName = dailyQuestionName
        )
    }

    private fun generateInviteKey(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = java.util.Random()
        val sb = StringBuilder(8)
        for (i in 0 until 8) {
            val index = random.nextInt(chars.length)
            sb.append(chars[index])
        }
        return sb.toString()
    }
    override suspend fun giveInviteKeyIfNull(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("Users").document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val inviteKey = documentSnapshot.getString("inviteKey")

                CoroutineScope(Dispatchers.IO).launch {
                if (inviteKey == null) {
                    var newInviteKey: String
                    do {
                        newInviteKey = generateInviteKey()

                    }
                    while (!isInviteKeyUnique(newInviteKey))

                    userDocumentRef.update("inviteKey", newInviteKey)
                        .addOnSuccessListener {
                            println("New invite key $newInviteKey generated and saved for user $userId")
                        }
                        .addOnFailureListener { e ->
                            println("Failed to update invite key: $e")
                        }
                } else {
                    // Invite key already exists for the user
                    println("Invite key already exists for user $userId: $inviteKey")
                }}
            } else {
                // Document for the user doesn't exist in the database
                println("User document not found for user $userId")
            }
        }.addOnFailureListener { e ->
            // Handle the error if failed to fetch the document
            println("Failed to fetch user document: $e")
        }
    }

    private suspend fun isInviteKeyUnique(inviteKey: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        val usersCollectionRef = db.collection("users")

        return try {
            val querySnapshot = usersCollectionRef.whereEqualTo("inviteKey", inviteKey).get().await()
            querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    private var _firebaseErrorMessage: String? = null
    override suspend fun signUp(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: Exception) {
            _firebaseErrorMessage = e.message ?: "Unknown error occurred."
            null
        }
    }

    override suspend fun loginWithGoogle(idToken: String?): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            user
        } catch (e: Exception) {
            null
        }
    }
    override fun getFirebaseErrorMessage(): String? {
        return _firebaseErrorMessage
    }

    override suspend fun signIn(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: Exception) {
            Log.e("FirestoreUserRepo", "Error signing in: ${e.message}")
            null
        }
    }

    override suspend fun deleteAccount(userId: String): Boolean {
        val userDocRef = firestore.collection("Users").document(userId)
        val userGroups = userDocRef.get().await().toObject(ApplicationUser::class.java)?.includedGroups
        val groupDocumentRef = firestore.collection("Groups")

        try {
            userGroups?.forEach { groupId ->
                val groupDoc = groupDocumentRef.document(groupId)
                val group = groupDoc.get().await().toObject(ApplicationGroup::class.java)
                val groupMembers = group?.groupMembers
                val groupOwner = group?.groupOwner

                if (userId == groupOwner) {
                    userDeleteGroup(groupId)
                } else {
                    groupMembers?.remove(userId)
                    groupDoc.update("groupMembers", groupMembers).await()
                }
            }
            userDocRef.delete().await()
            getCurrentUser()?.delete()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreUserRepo", "Error sending password reset email: ${e.message}")
            false
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}