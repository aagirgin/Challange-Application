package com.example.challapp.ui.group.infogroup

import androidx.lifecycle.ViewModel
import com.example.challapp.domain.models.ApplicationGroup
import com.example.challapp.domain.models.ApplicationUser
import com.example.challapp.domain.state.InvitationState
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.RawValue
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

    private val _getAllMembers= MutableStateFlow<MutableList<ApplicationUser>?>(null)
    val allMembers: StateFlow<MutableList<ApplicationUser>?> = _getAllMembers


    suspend fun getAllUserSorted(userIdList: MutableList<@RawValue String?>){
        val appUserList = mutableListOf<ApplicationUser>()
        userIdList.forEach { userId ->
            if (userId != null) {
                userRepository.getApplicationUserById(userId)?.let { userInfo -> appUserList.add(userInfo) }
            }
        }
        val sortedAppUserList = appUserList.sortedByDescending { listUsers -> listUsers.allDailyQuestions.size }
        _getAllMembers.value = sortedAppUserList.toMutableList()
    }

    suspend fun inviteToGroup(inviteKey: String, message: String, from: String) {
        val invitationState = userRepository.sendUserInvitationWithInviteKey(inviteKey, message, from)
        _invitationState.value = invitationState
        println(_invitationState.value)
    }

    fun setGroupData(group:ApplicationGroup) {
        _selectedGroup.value = group
    }

}