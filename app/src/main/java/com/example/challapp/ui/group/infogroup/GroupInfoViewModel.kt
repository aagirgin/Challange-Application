package com.example.challapp.ui.group.infogroup

import androidx.lifecycle.ViewModel
import com.example.challapp.domain.models.ApplicationGroup
import com.example.challapp.domain.state.InvitationState
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

    private val _invitationState: MutableStateFlow<InvitationState> = MutableStateFlow(InvitationState.EmptyState)
    val invitationState: StateFlow<InvitationState> get() = _invitationState

    private val _selectedGroup = MutableStateFlow<ApplicationGroup?>(null)
    val selectedGroup: StateFlow<ApplicationGroup?> = _selectedGroup



    suspend fun inviteToGroup(inviteKey: String, message: String, from: String) {
        val invitationState = userRepository.sendUserInvitationWithInviteKey(inviteKey, message, from)
        _invitationState.value = invitationState
        println(_invitationState.value)
    }

    fun setGroupData(group:ApplicationGroup) {
        _selectedGroup.value = group
    }

}