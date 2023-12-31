package com.challengerdaily.challenge.ui.login.loginpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengerdaily.challenge.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class LoginState {
    object Loading : LoginState()
    data class Error(val errorMessage: String) : LoginState()
    object Success : LoginState()
}


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState?>(null)
    val loginState: StateFlow<LoginState?> get() = _loginState

    private val _loginGoogleState = MutableStateFlow<LoginState?>(null)
    val loginGoogleState: StateFlow<LoginState?> get() = _loginGoogleState

    private val _firstTimeGoogleLogin = MutableStateFlow<Boolean>(false)
    val firstTimeGoogleLogin: StateFlow<Boolean> get() = _firstTimeGoogleLogin

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val user = userRepository.signIn(email, password)
            if (user != null) {
                _loginState.value = LoginState.Success
                _currentUser.value = userRepository.getCurrentUser()
                userRepository.giveInviteKeyIfNull(user.uid)

            } else {
                _loginState.value = LoginState.Error("Invalid credentials. Please try again.")
            }
        }
    }

    fun signInWithGoogle(idToken: String?,email: String) {
        viewModelScope.launch {
            _loginGoogleState.value = LoginState.Loading
            val user = userRepository.loginWithGoogle(idToken)
            if (user != null) {
            if(!user.uid.let { uid -> userRepository.checkDocumentExistsForUser(uid) }){
                userRepository.addUserToFirestore(user.uid,email,null)
                _firstTimeGoogleLogin.value = true
            }
            _loginGoogleState.value = LoginState.Success
            _currentUser.value = userRepository.getCurrentUser()
             userRepository.giveInviteKeyIfNull(user.uid)
            }

        }
    }
    suspend fun updateStreakOnNavigate(){
        _currentUser.value?.let { userRepository.updateStreakBasedOnDailyQuestions(it.uid) }
    }
}