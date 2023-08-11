package com.example.challapp.ui.group.landinggroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.domain.models.ApplicationGroup
import com.example.challapp.domain.models.InviteStatus
import com.example.challapp.domain.state.UiState
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
):ViewModel() {

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())
    val appGroupList: MutableStateFlow<MutableList<ApplicationGroup>>
        get() = _appGroupList

    private val _appGroupList: MutableStateFlow<MutableList<ApplicationGroup>> = MutableStateFlow(mutableListOf())

    private val _selectedGroup = MutableStateFlow<ApplicationGroup?>(null)
    val selectedGroup: StateFlow<ApplicationGroup?> = _selectedGroup

    private val _userIncludedGroupIds = MutableStateFlow<List<*>?>(null)

    private val _getSelectedGroupId = MutableStateFlow<String?>(null)
    val selectedGroupId: StateFlow<String?> = _getSelectedGroupId


    private val _getInvitationState: MutableStateFlow<UiState<String>> = MutableStateFlow(UiState.Empty)
    val invitationState: StateFlow<UiState<String>> get() = _getInvitationState

    init {
    viewModelScope.launch {
        fetchUserIncludedGroups()
     }
    }

    fun setSelectedGroup(group: ApplicationGroup) {
        _selectedGroup.value = group
    }
    private suspend fun fetchUserIncludedGroups() {
        val fetchedGroups = mutableListOf<ApplicationGroup>()

        _userIncludedGroupIds.value = currentUser.value?.let {
            userRepository.getUserIncludedGroupIds(
                it.uid)
        }

        _userIncludedGroupIds.value?.forEach { element ->
            userRepository.getGroupInformationByDocumentId(element)?.let {
                fetchedGroups.add(it)
            }
        }

        _appGroupList.value = fetchedGroups
    }

    fun setGroupDocumentId(position: Int){
        viewModelScope.launch {
            _getSelectedGroupId.value = _userIncludedGroupIds.value?.get(position) as String
        }
    }
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
}