package com.example.challapp.ui.profile.profilenavigation

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.domain.state.StorageException
import com.example.challapp.domain.state.UiState
import com.example.challapp.repository.FirestoreUserRepository
import com.example.challapp.repository.StorageRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("SuspiciousIndentation")
@HiltViewModel
class ProfileNavViewModel @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository,
    private val storageRepository: StorageRepository
):ViewModel() {

    private val _getUsernameFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    val getUsername: MutableStateFlow<String?>
        get() = _getUsernameFlow

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)

    private val _insertIntoStorageFlow: MutableStateFlow<UiState<*>> = MutableStateFlow(UiState.Empty)

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: MutableStateFlow<String?>
        get() = _profileImageUrl

    private val _getInviteKeyFlow = MutableStateFlow<String?>(null)
    val getInviteKey: MutableStateFlow<String?>
        get() = _getInviteKeyFlow

    init {
    _currentUser.value = firestoreUserRepository.getCurrentUser()
        if (_currentUser.value != null){
            val userId = _currentUser.value?.uid
            viewModelScope.launch {
                if (userId != null) {
                    fetchUsernameAndInviteKey(userId)
                }
            }
        }
    }

    fun loadProfileImage(email: String) {
        viewModelScope.launch {
            val imageUrl = storageRepository.loadProfileImage(email)
            _profileImageUrl.value = imageUrl
        }
    }
    private suspend fun fetchUsernameAndInviteKey(userId: String) {
            viewModelScope.launch {
                _getUsernameFlow.value = firestoreUserRepository.getUsername(userId)
                _getInviteKeyFlow.value = firestoreUserRepository.getInviteKey(userId)
            }
    }


    fun insertImageForUserAvatar(imageUri: Uri, email: String) {
        viewModelScope.launch {
            _insertIntoStorageFlow.value = UiState.Loading
            try {
                val imageUrl = storageRepository.insertImageForUserAvatar(imageUri, email)
                if (imageUrl.isNotBlank()){
                    _insertIntoStorageFlow.value = UiState.Success(true)
                }
                else{
                    _insertIntoStorageFlow.value = UiState.Error("Error occurred while inserting profile picture.")
                }
            } catch (e: StorageException) {
                _insertIntoStorageFlow.value = e.message?.let { UiState.Error(it) }!!
            }
        }
    }


    fun signoutUser(){
        firestoreUserRepository.signOut()
    }
}