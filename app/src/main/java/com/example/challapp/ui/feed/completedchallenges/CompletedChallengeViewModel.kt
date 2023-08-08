package com.example.challapp.ui.feed.completedchallenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.domain.models.ApplicationDailyQuestion
import com.example.challapp.domain.state.UiState
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompletedChallengeViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
):ViewModel() {

    val getCurrentUser: MutableStateFlow<FirebaseUser?>
        get() = _getCurrentUser

    private val _getCurrentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())

    private val _getCurrentUserName: MutableStateFlow<String> = MutableStateFlow("")

    private val _getDailyChallenges: MutableStateFlow<UiState<*>> = MutableStateFlow(UiState.Empty)
    val getDailyChallenges: StateFlow<UiState<*>> get() = _getDailyChallenges

    val getSpecificDailyQuestion: MutableStateFlow<ApplicationDailyQuestion?>
        get() = _getSpecificDailyQuestion

    private val _getSpecificDailyQuestion: MutableStateFlow<ApplicationDailyQuestion?> = MutableStateFlow(null)

    suspend fun getDailyQuestion(documentId: String) {
        _getSpecificDailyQuestion.value = userRepository.getDailyQuestionInformationByDocumentId(documentId)
    }
    init {
        viewModelScope.launch {
            _getDailyChallenges.value = UiState.Loading
            val getAllInfo = _getCurrentUser.value?.let { userRepository.getAllDailyChallangesForUser(it.uid) }
            if(getAllInfo!= null){
                _getCurrentUserName.value =
                    _getCurrentUser.value?.let { userRepository.getUsername(it.uid).toString() }.toString()
                _getDailyChallenges.value = UiState.Success(getAllInfo)
            }else{
                _getDailyChallenges.value = UiState.Error("Error occured while retrieving the previous challenges.")
            }
        }
    }
}