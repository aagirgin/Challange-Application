package com.example.challapp.ui.group.settingsgroup

import androidx.lifecycle.ViewModel
import com.example.challapp.repository.FirestoreUserRepository
import javax.inject.Inject

class GroupSettingsViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
): ViewModel() {

}