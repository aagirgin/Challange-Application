package com.example.challapp.ui.group.landinggroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.domain.models.ApplicationGroup
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

    private val _userIncludedGroupIds = MutableStateFlow<List<*>?>(null)
    val userIncludedGroupIds: MutableStateFlow<List<*>?>
        get() = _userIncludedGroupIds

    private val _getNotificationCount: MutableStateFlow<Int?> = MutableStateFlow(null)
    val notificationCount: StateFlow<Int?> get() = _getNotificationCount

    init {
        viewModelScope.launch {
            fetchUserIncludedGroups()
            _getNotificationCount.value = _currentUser.value?.let { user -> userRepository.getUserInviteNotificationCount(user.uid) }
         }
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

}