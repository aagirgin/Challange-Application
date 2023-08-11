package com.example.challapp.repository

import android.util.Log
import com.example.challapp.domain.models.ApplicationDailyChallenge
import com.example.challapp.domain.models.ApplicationDailyQuestion
import com.example.challapp.domain.models.ApplicationGroup
import com.example.challapp.domain.models.ApplicationUser
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
            val userData = userDocRef.toObject(ApplicationUser::class.java)
            userData?.username
        } catch (e: Exception) {
            null
        }
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
        val documentSnapshot = groupDocumentRef.get().await()
        println(documentSnapshot.toObject(ApplicationGroup::class.java))
        return documentSnapshot.toObject(ApplicationGroup::class.java)
    }


    override suspend fun getStreak(userId: String): Int? {
        val userDocRef = firestore.collection("Users").document(userId).get().await()
        val userData = userDocRef.toObject(ApplicationUser::class.java)
        return userData?.challangeStreak
    }

    override suspend fun incrementStreakCountByOne(userId: String) {
        val userDocumentRef = firestore.collection("Users").document(userId)
        val documentSnapshot = userDocumentRef.get().await()
        val userData = documentSnapshot.toObject(ApplicationUser::class.java)
        val currentStreak = userData?.challangeStreak
        val newStreak = currentStreak?.plus(1)
        userDocumentRef.update("challangeStreak", newStreak).await()
    }

    override suspend fun sendUserInvitationWithInviteKey(inviteKey: String, fromGroup: String, sender: String): String? {
        val userCollection = firestore.collection("Users")
        val query = userCollection.whereEqualTo("inviteKey", inviteKey)
        val querySnapshot = query.get().await()
        if (!querySnapshot.isEmpty) {
            for (documentSnapshot in querySnapshot.documents) {
                val notificationsList = documentSnapshot.toObject(ApplicationUser::class.java)?.notifications

                notificationsList?.forEach {
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