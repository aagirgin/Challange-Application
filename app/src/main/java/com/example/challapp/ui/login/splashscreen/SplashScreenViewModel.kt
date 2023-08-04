package com.example.challapp.ui.login.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.repository.FirestoreUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
) : ViewModel() {
    private val _currUserState = MutableStateFlow(false)
    val currUserState: StateFlow<Boolean> get() = _currUserState

    init {
        getCurrentUserStatus()
    }

    private fun getCurrentUserStatus() {
        viewModelScope.launch {
            val currentUser = userRepository.getCurrentUser()
            _currUserState.value = currentUser != null
        }
    }
}