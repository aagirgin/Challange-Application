package com.challengerdaily.challenge.ui.feed.dailychallange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengerdaily.challenge.domain.models.ApplicationDailyQuestion
import com.challengerdaily.challenge.domain.state.UiState
import com.challengerdaily.challenge.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DailyChallengeViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository,
):ViewModel() {
    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())

    private val _dailyQuestionFlow: MutableStateFlow<UiState<ApplicationDailyQuestion?>> = MutableStateFlow(UiState.Empty)

    private val _getDailyQuestionName: MutableStateFlow<String?> = MutableStateFlow(null)
    val getDailyQuestionName: StateFlow<String?> get() = _getDailyQuestionName

    private val _getDailyQuestion: MutableStateFlow<String?> = MutableStateFlow(null)
    val getDailyQuestion: StateFlow<String?> get() = _getDailyQuestion

        private val _addDailyChallenge: MutableStateFlow<UiState<String?>> = MutableStateFlow(UiState.Empty)
    val addDailyChallenge: StateFlow<UiState<*>> get() = _addDailyChallenge

    private val _submissionExists: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val submissionExists: StateFlow<Boolean?> get() = _submissionExists



    private val currentDate: LocalDate = LocalDate.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val formattedDate = currentDate.format(formatter)
    init {

        viewModelScope.launch {
            _dailyQuestionFlow.value = UiState.Loading
            _submissionExists.value =  checkIfSubmissionAlreadyExists()
            val daily = userRepository.getDailyQuestionInformation()
            if (daily.dailyQuestion != null) {
                _dailyQuestionFlow.value = UiState.Success(daily)
                _getDailyQuestion.value = daily.dailyQuestion.toString()
                _getDailyQuestionName.value = daily.dailyQuestionName.toString()
            } else {
                _dailyQuestionFlow.value = UiState.Empty
            }
        }
    }

    suspend fun addItemToFirestore(description: String){

        viewModelScope.launch {
            _addDailyChallenge.value = UiState.Loading
            val isAdditionComplete = _currentUser.value?.let { userRepository.addDailyChallangeToUser(it.uid,description,formattedDate) }
            if (isAdditionComplete == true) {
                //Increment streak count
                _currentUser.value?.let { userRepository.incrementStreakCountByOne(it.uid) }
                _addDailyChallenge.value = UiState.Success("Successfully added.")
            } else {
                _addDailyChallenge.value = UiState.Error("Error occurred while adding items to Firestore.")
            }
        }
    }

    private suspend fun checkIfSubmissionAlreadyExists(): Boolean? {
        return _currentUser.value?.let { userRepository.checkUserAlreadyHaveSubmission(it.uid) }
    }

}