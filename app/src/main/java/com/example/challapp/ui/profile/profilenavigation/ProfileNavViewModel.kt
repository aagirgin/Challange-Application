package com.example.challapp.ui.profile.profilenavigation

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.R
import com.example.challapp.domain.state.UiState
import com.example.challapp.repository.FirestoreUserRepository
import com.example.challapp.repository.StorageRepository
import com.example.challapp.repository.StorageRepositoryImpl
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("SuspiciousIndentation")
@HiltViewModel
class ProfileNavViewModel @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository,
    private val storageRepository: StorageRepository
):ViewModel() {

    private val _usernameFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    val usernameFlow: Flow<String?>
        get() = _usernameFlow

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)

    private val _insertIntoStorageFlow: MutableStateFlow<UiState<*>> = MutableStateFlow(UiState.Empty)
    val insertIntoStorageFlow: StateFlow<UiState<*>> get() = _insertIntoStorageFlow

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: MutableStateFlow<String?>
        get() = _profileImageUrl



    init {
    _currentUser.value = firestoreUserRepository.getCurrentUser()
        if (_currentUser.value != null){
            val userId = _currentUser.value?.uid
            viewModelScope.launch {
                fetchUsername(userId!!)
            }
        }
    }

    fun loadProfileImage(email: String) {
        viewModelScope.launch {
            val imageUrl = storageRepository.loadProfileImage(email)
            _profileImageUrl.value = imageUrl
        }
    }
    private suspend fun fetchUsername(userId: String) {
            viewModelScope.launch {
                _usernameFlow.value = firestoreUserRepository.getUsername(userId)
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
            } catch (e: StorageRepository.StorageException) {
                _insertIntoStorageFlow.value = e.message?.let { UiState.Error(it) }!!
            }
        }
    }




    fun signoutUser(){
        firestoreUserRepository.signOut()
    }
}