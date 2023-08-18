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
import com.example.challapp.utils.CopyClipboardUtils
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
        binding.viewModel = profileViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        onClickCopyTextToClipboard()
        loadProfileImage()
        onNavigateBack()
        changeName()

        return binding.root
    }

    private fun validationField(): Boolean{
        return binding.edittextName.text.isNotBlank()
    }

    private fun onNavigateBack(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileNavFragment)
        }
    }

    private fun onClickCopyTextToClipboard() {
        binding.imageviewClipboard.setOnClickListener {
            val copyText = binding.textviewInvitationKey.text.toString()
            CopyClipboardUtils.copyTextToClipboard(copyText,requireContext())
            Snackbar.make(binding.root, getString(R.string.copy_clipboard_text), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun changeName() {
        binding.iconChangeName.setOnClickListener {
            binding.edittextName.isEnabled = true
            binding.edittextName.text = null
            binding.edittextName.requestFocus()
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.edittextName, InputMethodManager.SHOW_IMPLICIT)
        }
        binding.buttonApplyChanges.setOnClickListener {
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
                                        profileViewModel.fetchUsernameAndInviteKey(currentUser.uid)
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


}