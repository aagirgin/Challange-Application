package com.challengerdaily.challenge.ui.group.creategroup


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengerdaily.challenge.domain.models.ApplicationGroup
import com.challengerdaily.challenge.domain.state.UiState
import com.challengerdaily.challenge.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
): ViewModel() {

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())

    val currentUserName: MutableStateFlow<String?>
        get() = _currentUserName

    private val _currentUserName: MutableStateFlow<String?> = MutableStateFlow(null)

    val descriptionFlow: MutableStateFlow<String?>
        get() = _descriptionFlow

    private val _descriptionFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    val groupnameFlow: MutableStateFlow<String?>
        get() = _groupnameFlow

    private val _groupnameFlow: MutableStateFlow<String?> = MutableStateFlow(null)


    private val _createGroupState = MutableStateFlow<UiState<*>>(UiState.Empty)
    val createGroupState: StateFlow<UiState<*>> get() = _createGroupState


    init {
        viewModelScope.launch {
            _currentUserName.value = _currentUser.value?.uid?.let { userRepository.getUsername(it) }
        }
    }


    suspend fun addGroup(){
        val currentUser = _currentUser.value
        val groupflow = _groupnameFlow.value
        val descriptionflow = _descriptionFlow.value

        _createGroupState.value = UiState.Loading
        try{
            currentUser?.uid?.let { uid ->
                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formattedDate = currentDate.format(formatter)

                val group = ApplicationGroup(
                    groupName = groupflow!!,
                    creationDate = formattedDate,
                    groupDescription = descriptionflow!!,
                    groupMembers = mutableListOf(uid),
                    groupOwner = uid
                )
                val isAdded = userRepository.addGroupToFirestore(group)
                val isUpdated = isAdded?.let { userRepository.updateIncludedGroupsForUser(uid, it) }
                if (isAdded != null && isUpdated == true){
                    _createGroupState.value = UiState.Success(true )
                }
                else{
                    _createGroupState.value = UiState.Error("Error occured while adding group.")
                }
            }
        }catch (e:Exception){
            _createGroupState.value = UiState.Error("Error occured while adding group.")
        }
    }
}
