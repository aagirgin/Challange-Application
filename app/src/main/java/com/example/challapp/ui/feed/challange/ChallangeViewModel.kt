package com.example.challapp.ui.feed.challange

import androidx.lifecycle.ViewModel
import com.example.challapp.repository.FirestoreUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChallangeViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
):ViewModel() {
    fun signOutFromSession(){
        userRepository.signOut()
    }
}