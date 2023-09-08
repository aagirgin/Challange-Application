package com.challengerdaily.challenge.ui.profile.profileaccountinfo

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
import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.databinding.FragmentProfileBinding
import com.challengerdaily.challenge.extensions.load
import com.challengerdaily.challenge.utils.CopyClipboardUtils
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

        loadProfileImage()
        onNavigateBack()
        changeName()

        binding.imageviewClipboard.setOnClickListener {
            copyInviteKeyToClipboard()
        }

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

    private fun copyInviteKeyToClipboard(){
        val invKey = binding.textviewInvitationKey.text
        CopyClipboardUtils.copyTextToClipboard(invKey,requireContext())
        Snackbar.make(binding.root, getString(R.string.invkey_clipboard), Snackbar.LENGTH_SHORT).show()
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
                        binding.imageviewProfilePicture.load(imageUrl)
                    }
                }
            }
        }
    }


}