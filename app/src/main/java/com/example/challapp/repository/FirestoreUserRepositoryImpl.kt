package com.example.challapp.repository

import android.util.Log
import com.example.challapp.domain.models.ApplicationDailyChallenge
import com.example.challapp.domain.models.ApplicationDailyQuestion
import com.example.challapp.domain.models.ApplicationGroup
import com.example.challapp.domain.models.ApplicationUser
import com.example.challapp.domain.models.GroupFeed
import com.example.challapp.domain.models.InviteStatus
import com.example.challapp.domain.models.UserNotification
import com.example.challapp.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.threeten.bp.LocalDate
import javax.inject.Inject

class FirestoreUserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
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

    override suspend fun getGroupNameById(groupId: String): String? {
        return try {
            val userDocRef = firestore.collection("Groups").document(groupId).get().await()
            val userData = userDocRef.data
            userData?.get("groupName") as? String
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addUserToFirestore(userId: String, email: String, fullName: String): Boolean {
        val userCollection = firestore.collection("Users")
        val userDocument = userCollection.document(userId)
        return try {
            val applicationUser = ApplicationUser(
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


        return try {
            val document = groupDocumentRef.get().await()
            if (document.exists()) {
                val groupData = document.data

                val groupName = groupData?.get("groupName") as? String ?: ""
                val creationDate = groupData?.get("creationDate") as? String ?: ""
                val groupDescription = groupData?.get("groupDescription") as? String ?: ""
                val groupMembers = (groupData?.get("groupMembers") as? List<*>)?.toMutableList()
                    ?: mutableListOf()
                val groupFeedList = (groupData?.get("groupFeed") as? MutableList<*>)?.mapNotNull { item ->
                    if (item is Map<*, *>) {
                        val description = item["description"] as? String
                        val questionDocumentId = item["questionDocumentId"] as? String
                        val user = item["user"] as? String
                        val userName = user?.let { getUsername(it) }
                        GroupFeed(user!!, userName!! ,description,questionDocumentId!!)
                        } else {
                            null
                        }
                    }?.toMutableList()

                val groupOwner = groupData?.get("groupOwner") as? String ?: ""

                ApplicationGroup(
                    groupName = groupName,
                    creationDate = creationDate,
                    groupDescription = groupDescription,
                    groupMembers = groupMembers,
                    groupFeed = groupFeedList,
                    groupOwner = groupOwner
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getStreak(userId: String): Long {
        val userDocRef = firestore.collection("Users").document(userId).get().await()
        val userData = userDocRef.data
        return userData?.get("challangeStreak") as Long
    }

    override suspend fun incrementStreakCountByOne(userId: String) {
        val groupDocumentRefStreak = firestore.collection("Users").document(userId)
        val documentSnapshot = groupDocumentRefStreak.get().await()
        val currentStreak = documentSnapshot.getLong("challangeStreak") ?: 0
        val newStreak = currentStreak + 1
        groupDocumentRefStreak.update("challangeStreak", newStreak).await()
    }

    override suspend fun sendUserInvitationWithInviteKey(inviteKey: String, fromGroup: String, sender: String): String? {
        val userCollection = firestore.collection("Users")
        val query = userCollection.whereEqualTo("inviteKey", inviteKey)
        val querySnapshot = query.get().await()
        if (!querySnapshot.isEmpty) {
            for (documentSnapshot in querySnapshot.documents) {
                val notificationsList = documentSnapshot.toObject(ApplicationUser::class.java)?.notifications

                notificationsList?.forEach {
                    println( it.notificationFromGroup)
                    println(fromGroup)
                    println(it.notificationFromGroup == fromGroup)
                    println(it.notificationType)
                    println(InviteStatus.INVITE)
                    println(it.notificationType == InviteStatus.INVITE)
                    if(it.notificationType == InviteStatus.INVITE && it.notificationFromGroup == fromGroup){
                        return "This group is already sent request to this user. Please wait for users response."
                    }
                }
                sendNotificationsToUser(documentSnapshot.id, sender, fromGroup, InviteStatus.INVITE)
                return "Invitation sent successfully to user."
            }
        }
        return null
    }

    override suspend fun sendNotificationsToUser(documentId: String, sender: String, fromGroup: String, notificationType: InviteStatus): Boolean {
        val userDocumentRef = firestore.collection("Users").document(documentId)
        val document = userDocumentRef.get().await()
        if (document.exists()) {
            val notificationsList = document.toObject(ApplicationUser::class.java)?.notifications
            val newNotification = UserNotification(notificationType = InviteStatus.INVITE,
                                                    notificationFromGroup =  fromGroup,
                                                    notificationSenderUser = sender)
            notificationsList?.add(newNotification)
            userDocumentRef.update("notifications", notificationsList)
            return true
        }
        return false
    }
    override suspend fun updateStreakBasedOnDailyQuestions(userId: String) {
        val groupDocumentRef = firestore.collection("Users").document(userId).get().await()
        val userData = groupDocumentRef.data
        val allDailyQuestions = userData?.get("allDailyQuestions") as? List<*>

        val groupDocumentRefStreak = firestore.collection("Users").document(userId)

        val currentDate = DateUtils.getCurrentFormattedDate()
        val previousDate = DateUtils.getPreviousFormattedDate()

        var consecutiveStreakCounter = 0L
        var lastDate: LocalDate? = null

        if (!allDailyQuestions.isNullOrEmpty()) {
            if ((allDailyQuestions.last() as? HashMap<*, *>)?.get("questionDocumentId") == currentDate || (allDailyQuestions.last() as? HashMap<*, *>)?.get("questionDocumentId") == previousDate) {
                for (i in allDailyQuestions.size - 1 downTo 0) {
                    val questionMap = allDailyQuestions[i] as? HashMap<*, *>
                    val questionDateStr = questionMap?.get("questionDocumentId") as? String
                    val questionDate = LocalDate.parse(questionDateStr)
                    println("Last Date: $lastDate , Question Date $questionDate")
                    if (lastDate == null || lastDate.minusDays(1) == questionDate) {
                        consecutiveStreakCounter++
                        lastDate = questionDate
                    } else {
                        break
                    }
                }
                groupDocumentRefStreak.update("challangeStreak", consecutiveStreakCounter).await()
            }
            else{
                groupDocumentRefStreak.update("challangeStreak", 0).await()
            }
        } else {
            return
        }
    }
    override suspend fun checkUserAlreadyHaveSubmission(userId: String): Boolean {
        val currentDate = DateUtils.getCurrentFormattedDate()
        val groupDocumentRef = firestore.collection("Users").document(userId).get().await()
        val userData = groupDocumentRef.data
        val allDailyQuestions = userData?.get("allDailyQuestions") as? List<*>

        if (allDailyQuestions != null) {
            return allDailyQuestions.any { dailyQuestion ->
                if (dailyQuestion is HashMap<*, *>) {
                    val questionDocumentId = dailyQuestion["questionDocumentId"] as? String
                    questionDocumentId == currentDate
                } else {
                    false
                }
            }
        }
        return false
    }

    override suspend fun getAllDailyChallangesForUser(userId: String): MutableList<ApplicationDailyChallenge>? {
        val groupDocumentRef = firestore.collection("Users").document(userId).get().await()
        val data = groupDocumentRef.data?.get("allDailyQuestions") as? List<*>
        return data?.filterIsInstance<HashMap<String, Any>>()?.mapTo(mutableListOf()) { it.toApplicationDailyChallenge() }
    }

    override suspend fun getInviteKey(userId: String): String {
        val groupDocumentRef = firestore.collection("Users").document(userId).get().await()
        val data = groupDocumentRef.data
        return data?.get("inviteKey") as String
    }

    override suspend fun getUserIncludedGroupIds(userId: String): List<*> {
        val userDocument = firestore.collection("Users").document(userId).get().await()
        return userDocument.get("includedGroups") as List<*>
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

    override suspend fun addDailyChallengeToAllUserIncludedGroups(groupIds: List<*>?, description: String, documentId: String, userId: String) : Boolean{
        val groupDocRef = firestore.collection("Groups")
        return try{
            groupIds?.forEach { includedGroup ->
                groupDocRef.document(includedGroup.toString()).update("groupFeed", FieldValue.arrayUnion(
                    mapOf(
                        "user" to userId,
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
                    // Generate a new invite key and check if it's unique
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

fun HashMap<String, Any>.toApplicationDailyChallenge(): ApplicationDailyChallenge {
    val questionDocumentId = this["questionDocumentId"] as String
    val description = this["description"] as String
    return ApplicationDailyChallenge(questionDocumentId, description)
}