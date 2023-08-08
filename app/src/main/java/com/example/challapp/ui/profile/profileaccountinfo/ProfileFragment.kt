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
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentProfileBinding
import com.example.challapp.services.ImageUploadService
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
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
        onNavigateBack()
        displayMail()
        displayName()
        changeName()

        return binding.root
    }

    private fun validationField(): Boolean{
        return binding.edittextName.text.isNotBlank() && binding.edittextName.text.toString() != "Here is My Name"
    }

    private fun onNavigateBack(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileNavFragment)
        }
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
                    profileViewModel.currentUser.collect { currentUser ->
                        if (currentUser != null) {
                            val newUsername = binding.edittextName.text.toString()
                            profileViewModel.changeUsername(currentUser.uid, newUsername)
                            profileViewModel.changeUsernameFlow.collect { changeSuccess ->
                                changeSuccess?.let {
                                    if (it) {
                                        binding.edittextName.isEnabled = false
                                        profileViewModel.fetchUsername(currentUser.uid)
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                Snackbar.make(binding.root, getString(R.string.fill_namefield), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProfileImage() {
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.currentUser.value?.email?.let { profileViewModel.loadProfileImage(it) }
            profileViewModel.profileImageUrl.collect { imageUrl ->
                if (imageUrl == "No Picture"){
                    binding.shapeableImageView.setImageResource(R.drawable.baseline_person_24)
                }
                else {
                    if (imageUrl != null) {
                        ImageUploadService.loadImageIntoImageView(imageUrl, binding.shapeableImageView)
                    }
                }
            }
        }
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