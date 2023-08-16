package com.example.challapp.ui.group.specificgroup

import androidx.lifecycle.ViewModel
import com.example.challapp.domain.models.ApplicationGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
@HiltViewModel
class SpecificGroupViewModel @Inject constructor(

):ViewModel() {


    private val _selectedGroup = MutableStateFlow<ApplicationGroup?>(null)
    val selectedGroup: StateFlow<ApplicationGroup?> = _selectedGroup

    private val _selectedGroupId = MutableStateFlow<String?>(null)
    val selectedGroupId: StateFlow<String?> = _selectedGroupId


    private val _groupPosition = MutableStateFlow<Int?>(null)
    val groupPosition: StateFlow<Int?> = _groupPosition

    fun setGroupData(group:ApplicationGroup,position:Int, groupId: String) {
        _selectedGroup.value = group
        _groupPosition.value = position
        _selectedGroupId.value = groupId
    }
}