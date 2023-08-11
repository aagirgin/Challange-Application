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


    suspend fun getUserNotifications(){
        _userNotificationList.value = currentUser.value?.let { userRepository.getUserNotications(it.uid) }
    }
}