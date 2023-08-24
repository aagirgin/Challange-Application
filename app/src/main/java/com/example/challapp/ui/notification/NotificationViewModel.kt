package com.example.challapp.ui.notification

import androidx.lifecycle.ViewModel
import com.example.challapp.domain.models.UserNotification
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
):ViewModel() {

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())
    val userNotificationList: MutableStateFlow<MutableList<UserNotification>?>
        get() = _userNotificationList

    private val _userNotificationList: MutableStateFlow<MutableList<UserNotification>?> = MutableStateFlow(null)

    val indexOnDelete: MutableStateFlow<Int?>
        get() = _getIndexOnDelete

    private val _getIndexOnDelete: MutableStateFlow<Int?> = MutableStateFlow(null)
    val userNotificationDelete: MutableStateFlow<UserNotification?>
        get() = _getUserNotificationDelete

    private val _getUserNotificationDelete: MutableStateFlow<UserNotification?> = MutableStateFlow(null)

    private val _senderNamesMap: MutableMap<String, String> = mutableMapOf()
    val senderNamesMap: MutableMap<String, String>
        get() = _senderNamesMap



    suspend fun getGroupName(groupId: String): String? {
        return userRepository.getGroupNameById(groupId)
    }
    suspend fun getSenderNames(){
        val senderIds = _userNotificationList.value?.map { it.notificationSenderUser } ?: emptyList()

        for (senderId in senderIds) {
            val senderName = userRepository.getUsername(senderId).toString()
            _senderNamesMap[senderId] = senderName
        }
    }
    suspend fun getUserNotifications(){
        _userNotificationList.value = currentUser.value?.let { userRepository.getUserNotications(it.uid) }
    }

    suspend fun deleteOnRejection() {
        _getUserNotificationDelete.value?.let { _currentUser.value?.uid?.let { currentUser ->
            _getIndexOnDelete.value = userRepository.deleteOnRejectInvitation(it, currentUser) }
        }
    }

    suspend fun addToGroupOnAccept(groupId:String){
        _currentUser.value?.uid?.let { _getIndexOnDelete.value = userRepository.acceptToGroupInvitation(it,groupId) }
    }

    fun resetState(){
        _getUserNotificationDelete.value = null
    }
}