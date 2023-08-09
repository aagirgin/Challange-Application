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

    private val _selectedGroup = MutableStateFlow<ApplicationGroup?>(null)
    val selectedGroup: StateFlow<ApplicationGroup?> = _selectedGroup


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

        val userIncludedGroupIds = currentUser.value?.let {
            userRepository.getUserIncludedGroupIds(
                it.uid)
        }

        userIncludedGroupIds?.forEach { element ->
            userRepository.getGroupInformationByDocumentId(element)?.let { fetchedGroups.add(it) }
        }

        _appGroupList.value = fetchedGroups
    }

}