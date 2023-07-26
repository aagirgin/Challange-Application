package com.example.challapp.ui.profile.profileaccountinfo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.challapp.R
import com.example.challapp.databinding.FragmentProfileBinding
import com.example.challapp.services.AuthService
import com.example.challapp.services.ImageUploadService
import com.example.challapp.ui.profile.profilenavigation.ProfileNavViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var binding : FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadProfileImage()
        displayMail()
        displayName()
        changeName()

        return binding.root
    }

    private fun validationField(): Boolean{
        return binding.edittextName.text.isNotBlank() && binding.edittextName.text.toString() != "Here is My Name"
    }

    private fun changeName() {
        binding.iconChangename.setOnClickListener {
            binding.edittextName.isEnabled = true
            binding.edittextName.text = null
            binding.edittextName.requestFocus()
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.edittextName, InputMethodManager.SHOW_IMPLICIT)
        }
        binding.buttonApplychanges.setOnClickListener {
            if (validationField()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    AuthService.getCurrentUser()?.let { currentUser ->
                        profileViewModel.changeUsername(currentUser.uid, binding.edittextName.text.toString())
                        profileViewModel.changeUsernameFlow.collect { changeSuccess ->
                            changeSuccess?.let {
                                if (it) {
                                    println("Change Completed.")
                                    binding.edittextName.isEnabled = false
                                    profileViewModel.fetchUsername(currentUser.uid)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadProfileImage() {
        ImageUploadService.loadProfileImage(binding.shapeableImageView)
    }

    private fun displayMail(){
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.mailFlow.collect{   mail ->
                binding.edittextMail.setText(mail)
            }
        }
    }

    private fun displayName(){
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.usernameFlow.collect{   username ->
                binding.textviewUsername.text = username
                binding.edittextName.setText(username)
            }
        }
    }
}