package com.challengerdaily.challenge.ui.login.providename

import androidx.lifecycle.ViewModel
import com.challengerdaily.challenge.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ProvideNameViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
):ViewModel() {

    val getCurrentUser: MutableStateFlow<FirebaseUser?>
        get() = _getCurrentUser

    private val _getCurrentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(userRepository.getCurrentUser())




    suspend fun addUserName(newUserName: String): Boolean? {
        return _getCurrentUser.value?.uid?.let { currentUser -> userRepository.changeUsername(currentUser,newUserName) }
    }
}