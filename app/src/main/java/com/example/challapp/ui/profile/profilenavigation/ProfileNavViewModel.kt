package com.example.challapp.ui.profile.profilenavigation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.R
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("SuspiciousIndentation")
@HiltViewModel
class ProfileNavViewModel @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository
):ViewModel() {

    private val _usernameFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    val usernameFlow: Flow<String?>
        get() = _usernameFlow

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)



    init {
    _currentUser.value = firestoreUserRepository.getCurrentUser()
        if (_currentUser.value != null){
            val userId = _currentUser.value?.uid
            viewModelScope.launch {
                fetchUsername(userId!!)
            }
        }
    }

    private suspend fun fetchUsername(userId: String) {
            viewModelScope.launch {
                _usernameFlow.value = firestoreUserRepository.getUsername(userId)
            }
    }

    fun signoutUser(){
        firestoreUserRepository.signOut()
    }
}