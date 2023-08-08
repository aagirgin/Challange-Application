package com.example.challapp.ui.profile.profilechangepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileChangePwViewModel @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository,
): ViewModel() {

    private val _getUsernameFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _getInviteKeyFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)

    init {
        _currentUser.value = firestoreUserRepository.getCurrentUser()
        val userId = _currentUser.value?.uid
        viewModelScope.launch {
            fetchUsernameAndInviteKey(userId!!)
        }
    }

    private suspend fun fetchUsernameAndInviteKey(userId: String) {
        viewModelScope.launch {
            _getUsernameFlow.value = firestoreUserRepository.getUsername(userId)
            _getInviteKeyFlow.value = firestoreUserRepository.getInviteKey(userId)
        }
    }

}