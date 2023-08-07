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
    private val _getUserStreakCount: MutableStateFlow<Long> = MutableStateFlow(0)
    val getUserStreakCount: StateFlow<Long> get() = _getUserStreakCount

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())

    init {
        viewModelScope.launch {
            _getUserStreakCount.value = _currentUser.value?.let { userRepository.getStreak(it.uid) }!!
        }


    }

}