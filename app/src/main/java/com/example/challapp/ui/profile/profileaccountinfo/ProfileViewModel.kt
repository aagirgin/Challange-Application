package com.example.challapp.ui.profile.profileaccountinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.repository.FirestoreUserRepository
import com.example.challapp.repository.StorageRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository,
    private val storageRepository: StorageRepository
):ViewModel() {

    private val _usernameFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    val usernameFlow: Flow<String?>
        get() = _usernameFlow

    private val _mailFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    val mailFlow: Flow<String?>
        get() = _mailFlow

    private val _changeUsernameFlow: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val changeUsernameFlow: Flow<Boolean?> get() = _changeUsernameFlow.asStateFlow()

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: MutableStateFlow<String?>
        get() = _profileImageUrl

    init {
        _currentUser.value = firestoreUserRepository.getCurrentUser()
        val userId = _currentUser.value?.uid
        viewModelScope.launch {
            fetchUsername(userId!!)
            _mailFlow.value = currentUser.value?.email
        }
    }




    fun loadProfileImage(email: String) {
        viewModelScope.launch {
            val imageUrl = storageRepository.loadProfileImage(email)
            _profileImageUrl.value = imageUrl
        }
    }

    suspend fun fetchUsername(userId: String) {
        viewModelScope.launch {
            _usernameFlow.value = firestoreUserRepository.getUsername(userId)
        }
    }

    suspend fun changeUsername(userId: String,newUsername: String){
        viewModelScope.launch {
            _changeUsernameFlow.value = firestoreUserRepository.changeUsername(userId,newUsername)
        }
    }
}