package com.example.challapp.ui.group.settingsgroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.domain.models.InvitePermission
import com.example.challapp.domain.state.UiState
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class GroupSettingsViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
): ViewModel() {

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())

    private val _changeGroupState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val changedGroupState: StateFlow<UiState<Boolean>> get() = _changeGroupState

    private val _leaveGroupState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val leaveGroupState: StateFlow<UiState<Boolean>> get() = _leaveGroupState

    private val _deleteGroupState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val deleteGroupState: StateFlow<UiState<Boolean>> get() = _deleteGroupState

    fun changePermission(groupId:String,changedPermission: InvitePermission){
        viewModelScope.launch {
            _changeGroupState.value = UiState.Loading
            val isChanged = userRepository.changeGroupInvitationStatus(groupId,changedPermission)
            if(isChanged){
                _changeGroupState.value = UiState.Success(true)
            }else{
                _changeGroupState.value = UiState.Error("Error occurred when changing group state.")
            }
        }
    }
    suspend fun leaveGroup(userId: String,groupId: String){
        viewModelScope.launch{
            _leaveGroupState.value = UiState.Loading
            val canLeaveGroup = userRepository.userLeaveGroup(userId,groupId)
            if(canLeaveGroup){
                _leaveGroupState.value = UiState.Success(true)
            }else{
                _leaveGroupState.value = UiState.Error("Error occurred while leaving.")
            }
        }
    }


    suspend fun deleteGroup(groupId: String){
        viewModelScope.launch {
            _deleteGroupState.value = UiState.Loading
            val canDeleteGroup = userRepository.userDeleteGroup(groupId)
            if(canDeleteGroup){
                _deleteGroupState.value = UiState.Success(true)
            }else{
                _deleteGroupState.value = UiState.Error("Error occurred while deleting.")
            }
        }
    }
}