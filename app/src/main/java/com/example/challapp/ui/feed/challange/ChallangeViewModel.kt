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
    private val _getUserStreakCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val getUserStreakCount: StateFlow<Int> get() = _getUserStreakCount

    private val _getNotificationCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val notificationCount: StateFlow<Int> get() = _getNotificationCount

    val getCurrentUser: MutableStateFlow<FirebaseUser?>
        get() = _getCurrentUser

    private val _getCurrentUser: MutableStateFlow<FirebaseUser?> =
        MutableStateFlow(userRepository.getCurrentUser())

    init {
        viewModelScope.launch {
            val currentUser = _getCurrentUser.value
            if (currentUser != null) {
                _getUserStreakCount.value = userRepository.getStreak(currentUser.uid)
                _getNotificationCount.value = userRepository.getUserInviteNotificationCount(currentUser.uid)
            }
        }
    }
}