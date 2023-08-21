package com.example.challapp.ui.group.settingsgroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.domain.models.InvitePermission
import com.example.challapp.domain.state.UiState
import com.example.challapp.repository.FirestoreUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class GroupSettingsViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
): ViewModel() {

    private val _changeGroupState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val changedGroupState: StateFlow<UiState<Boolean>> get() = _changeGroupState
    fun changePermission(groupId:String,changedPermission: InvitePermission){
        viewModelScope.launch {
            _changeGroupState.value = UiState.Loading
            val isChanged = userRepository.changeGroupInvitationStatus(groupId,changedPermission)
            if(isChanged){
                _changeGroupState.value = UiState.Success(true)
            }else{
                _changeGroupState.value = UiState.Error("Error Occurred when changing state.")
            }
        }
    }
}