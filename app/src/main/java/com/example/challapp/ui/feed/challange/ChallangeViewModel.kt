package com.example.challapp.ui.feed.challange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallangeViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
):ViewModel() {
    private val _getUserStreakCount: MutableStateFlow<Int?> = MutableStateFlow(null)
    val getUserStreakCount: StateFlow<Int?> get() = _getUserStreakCount

    private val _getNotificationCount: MutableStateFlow<Int?> = MutableStateFlow(null)
    val notificationCount: StateFlow<Int?> get() = _getNotificationCount

    val getCurrentUser: MutableStateFlow<FirebaseUser?>
        get() = _getCurrentUser

    private val _getCurrentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())

    init {
        viewModelScope.launch {
            _getUserStreakCount.value = _getCurrentUser.value?.let { user -> userRepository.getStreak(user.uid) }
            _getNotificationCount.value = _getCurrentUser.value?.let { user -> userRepository.getUserInviteNotificationCount(user.uid) }
        }
    }

}