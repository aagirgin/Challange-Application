package com.challengerdaily.challenge.ui.group.specificgroup

import androidx.lifecycle.ViewModel
import com.challengerdaily.challenge.domain.models.ApplicationGroup
import com.challengerdaily.challenge.repository.FirestoreUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.RawValue
import javax.inject.Inject
@HiltViewModel
class SpecificGroupViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
):ViewModel() {


    private val _selectedGroup = MutableStateFlow<ApplicationGroup?>(null)
    val selectedGroup: StateFlow<ApplicationGroup?> = _selectedGroup

    private val _selectedGroupId = MutableStateFlow<String?>(null)
    val selectedGroupId: StateFlow<String?> = _selectedGroupId


    private val _groupPosition = MutableStateFlow<Int?>(null)
    val groupPosition: StateFlow<Int?> = _groupPosition


    private val _getUsersMap = MutableStateFlow<Map<String,String>?>(null)
    val usersMap: StateFlow<Map<String,String>?> = _getUsersMap


    fun setGroupData(group:ApplicationGroup,position:Int, groupId: String) {
        _selectedGroup.value = group
        _groupPosition.value = position
        _selectedGroupId.value = groupId
    }
    suspend fun fetchUserMap(groupMemberIds: MutableList<@RawValue String?>){
        _getUsersMap.value = userRepository.getUsersNameAsMap(groupMemberIds)
    }
}