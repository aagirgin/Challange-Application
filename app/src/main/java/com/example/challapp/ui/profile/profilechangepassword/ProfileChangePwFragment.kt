package com.example.challapp.ui.profile.profilechangepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentProfileChangePwBinding
import com.example.challapp.services.ImageUploadService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileChangePwFragment : Fragment() {
    private lateinit var binding: FragmentProfileChangePwBinding
    private val profileChangePwViewModel: ProfileChangePwViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileChangePwBinding.inflate(inflater, container, false)



        viewLifecycleOwner.lifecycleScope.launch {
            loadProfileImage()
        }
        onNavigateBack()
        displayName()
        onPressButtonValidation(binding)

        return binding.root
    }

    private fun loadProfileImage() {
        viewLifecycleOwner.lifecycleScope.launch {
            profileChangePwViewModel.currentUser.value?.email?.let { profileChangePwViewModel.loadProfileImage(it) }
            profileChangePwViewModel.profileImageUrl.collect { imageUrl ->
                if (imageUrl == "No Picture"){
                    binding.imageviewProfilePicture.setImageResource(R.drawable.baseline_person_24)
                }
                else {
                    if (imageUrl != null) {
                        ImageUploadService.loadImageIntoImageView(imageUrl, binding.imageviewProfilePicture)
                    }

                }
            }
        }
    }


    private fun displayName(){
        viewLifecycleOwner.lifecycleScope.launch {
            profileChangePwViewModel.usernameFlow.collect{   username ->
                binding.textviewUsername.text = username
            }
        }
    }

    private fun valideEmpty(currentPw: String, newPw: String, newPw2: String): Boolean {
        return currentPw.isNotEmpty() && newPw.isNotEmpty() && newPw2.isNotEmpty()
    }

    private fun validatePassword(newPw: String, newPw2: String):Boolean{
        return newPw == newPw2
    }

    private fun validatePasswordLength(newPw: String, newPw2: String):Boolean{
        return !(newPw.length < 6) && !(newPw2.length < 6)
    }

    private fun validateAccount(currentPw: String, callback: (Boolean) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentUser = profileChangePwViewModel.currentUser.value
            if (currentUser != null) {
                val credential = EmailAuthProvider.getCredential(currentUser.email!!, currentPw)
                currentUser.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    val isReauthenticationSuccessful = reauthTask.isSuccessful
                    callback.invoke(isReauthenticationSuccessful)
                }
            } else {
                callback.invoke(false)
            }
        }
    }

    private fun performPasswordChange(newPw: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentUser = profileChangePwViewModel.currentUser.value
            currentUser?.updatePassword(newPw)
                ?.addOnCompleteListener { passwordUpdateTask ->
                    if (passwordUpdateTask.isSuccessful) {
                        Snackbar.make(binding.root, getString(R.string.password_change_success), Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(binding.root, getString(R.string.password_change_fail), Snackbar.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun onNavigateBack(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_profileChangePwFragment_to_profileNavFragment)
        }
    }

    private fun onPressButtonValidation(binding: FragmentProfileChangePwBinding) {
        binding.buttonApplyChanges.setOnClickListener {
            val currentPw = binding.edittextCurrentPassword.text.toString()
            val newPw = binding.edittextNewPassword1.text.toString()
            val newPw2 = binding.edittextNewPassword2.text.toString()

            if (valideEmpty(currentPw, newPw, newPw2)) {
                if(validatePasswordLength(newPw, newPw2)){
                    validateAccount(currentPw){ isSuccessful ->
                        if (isSuccessful) {
                            if(validatePassword(newPw, newPw2)){
                                performPasswordChange(newPw)
                            }
                            else{
                                Snackbar.make(binding.root, getString(R.string.password_didnot_match), Snackbar.LENGTH_SHORT).show()
                            }
                        } else {
                            Snackbar.make(binding.root, getString(R.string.check_current_password), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Snackbar.make(binding.root, getString(R.string.check_password_length), Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(binding.root, getString(R.string.fill_forms_error), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

}