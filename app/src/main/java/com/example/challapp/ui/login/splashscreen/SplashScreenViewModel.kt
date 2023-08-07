package com.example.challapp.ui.login.splashscreen

import androidx.lifecycle.ViewModel
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
) : ViewModel() {
    val getCurrentUser: MutableStateFlow<FirebaseUser?>
        get() = _getCurrentUser

    private val _getCurrentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())

    suspend fun updateStreakOnNavigate(){
        _getCurrentUser.value?.let { userRepository.updateStreakBasedOnDailyQuestions(it.uid) }
    }
}