package com.example.challapp.ui.feed.dailychallange

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
class DailyChallengeViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
):ViewModel() {
    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())

    private val _dailyQuestionFlow: MutableStateFlow<UiState<ApplicationDailyQuestion?>> = MutableStateFlow(UiState.Empty)

    private val _dailyQuestionName: MutableStateFlow<String?> = MutableStateFlow(null)
    val dailyQuestionName: StateFlow<String?> get() = _dailyQuestionName

    private val _dailyQuestion: MutableStateFlow<String?> = MutableStateFlow(null)
    val dailyQuestion: StateFlow<String?> get() = _dailyQuestion

        private val _addDailyChallenge: MutableStateFlow<UiState<*>> = MutableStateFlow(UiState.Empty)
    val addDailyChallenge: StateFlow<UiState<*>> get() = _addDailyChallenge
    init {
        viewModelScope.launch {
            _dailyQuestionFlow.value = UiState.Loading
            val daily = userRepository.getDailyQuestionInformation()
            if (daily.dailyQuestion != null) {
                _dailyQuestionFlow.value = UiState.Success(daily)
                _dailyQuestion.value = daily.dailyQuestion.toString()
                _dailyQuestionName.value = daily.dailyQuestionName.toString()
            } else {
                _dailyQuestionFlow.value = UiState.Empty
            }
        }
    }

    suspend fun addItemToFirestore(description: String){
        viewModelScope.launch {
            _addDailyChallenge.value = UiState.Loading
            val isAdditionComplete = _currentUser.value?.let { userRepository.addDailyChallangeToUser(it.uid,description,"2023-07-30") }
            if (isAdditionComplete == true) {
                _addDailyChallenge.value = UiState.Success("Successfully added.")
            } else {
                _addDailyChallenge.value = UiState.Error("Error occurred while adding items to Firestore.")
            }
        }
    }

}