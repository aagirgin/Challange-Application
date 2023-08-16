package com.example.challapp.ui.group.infogroup

import androidx.lifecycle.ViewModel
import com.example.challapp.domain.models.ApplicationGroup
import com.example.challapp.domain.state.UiState
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GroupInfoViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
): ViewModel() {
    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())

    private val _getInvitationState: MutableStateFlow<UiState<String>> = MutableStateFlow(UiState.Empty)
    val invitationState: StateFlow<UiState<String>> get() = _getInvitationState

    private val _selectedGroup = MutableStateFlow<ApplicationGroup?>(null)
    val selectedGroup: StateFlow<ApplicationGroup?> = _selectedGroup


    suspend fun inviteToGroup(inviteKey: String,message:String,from:String){
        val invitation = userRepository.sendUserInvitationWithInviteKey(inviteKey,message,from)
        _getInvitationState.value = UiState.Loading
        if (invitation.isNullOrBlank()){
            _getInvitationState.value = UiState.Error("User with key not found.")
        }
        else{
            _getInvitationState.value = UiState.Success(invitation)
        }
    }


    fun resetState(){
        _getInvitationState.value = UiState.Empty
    }

    fun setGroupData(group:ApplicationGroup) {
        _selectedGroup.value = group
    }

}