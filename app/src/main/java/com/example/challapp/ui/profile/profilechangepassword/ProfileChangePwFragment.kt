package com.example.challapp.ui.profile.profilechangepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.challapp.databinding.FragmentProfileChangePwBinding
import com.example.challapp.services.ImageUploadService
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
        displayName()
        onPressButtonValidation(binding)

        return binding.root
    }

    private suspend fun loadProfileImage() {
        profileChangePwViewModel.currentUser.collect{
            if (it != null) {
                it.email?.let { it1 -> ImageUploadService.loadProfileImage(it1,binding.shapeableImageView) }
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
                        Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun onPressButtonValidation(binding: FragmentProfileChangePwBinding) {
        binding.buttonApplychanges.setOnClickListener {
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
                                Toast.makeText(requireContext(), "Password did not match.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Please check your current password.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(requireContext(), "Password length should be longer than 6 characters.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all forms.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}