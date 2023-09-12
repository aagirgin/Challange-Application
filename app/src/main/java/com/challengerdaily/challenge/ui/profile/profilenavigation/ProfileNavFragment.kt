package com.challengerdaily.challenge.ui.profile.profilenavigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.databinding.FragmentProfileNavBinding
import com.challengerdaily.challenge.extensions.load
import com.challengerdaily.challenge.utils.CopyClipboardUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileNavFragment : Fragment() {
    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                selectedImageUri?.let {
                    profileNavViewModel.insertImageForUserAvatar(it, profileNavViewModel.currentUser.value?.email ?: "")
                }
            }
        }

    private val profileNavViewModel: ProfileNavViewModel by viewModels()
    private lateinit var binding : FragmentProfileNavBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentProfileNavBinding.inflate(inflater,container,false)
        binding.viewModel = profileNavViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        displayImage()
        addProfilePicture()
        onNavigateInProfile()

        return binding.root
    }


    private fun addProfilePicture() {

        binding.imageviewProfilePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            launcher.launch(intent)
        }
    }




    private fun displayImage() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.shimmerShapableimage.startShimmer()
            profileNavViewModel.currentUser.value?.email?.let { email ->
                profileNavViewModel.loadProfileImage(email)
                profileNavViewModel.profileImageUrl.collect { imageUrl ->
                    binding.shimmerShapableimage.stopShimmer()
                    if (imageUrl.isNullOrBlank()) {
                        binding.shimmerShapableimage.startShimmer()
                    }
                    else if (imageUrl == "No Picture"){
                        binding.imageviewProfilePicture.setImageResource(R.drawable.baseline_person_24)
                        binding.shimmerShapableimage.hideShimmer()
                    }
                    else {
                        binding.imageviewProfilePicture.load(imageUrl)
                        binding.shimmerShapableimage.hideShimmer()
                    }
                }
            }
        }
    }




    private fun onNavigateInProfile(){
        binding.viewNavProfileInfo.setOnClickListener {
            findNavController().navigate(R.id.action_profileNavFragment_to_profileFragment)
        }

        binding.viewNavChangePwInfo.setOnClickListener {
            findNavController().navigate(R.id.action_profileNavFragment_to_profileChangePwFragment)
        }
        binding.viewDeleteAccInfo.setOnClickListener {
            showDeleteAccountDialog()
        }
        binding.viewSignout.setOnClickListener {
            showSignOutAccountDialog()
        }
        binding.imageviewClipboard.setOnClickListener {
            copyInviteKeyToClipboard()

        }
    }





    private fun showDeleteAccountDialog() {
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        alertDialogBuilder.apply {
            setTitle(getString(R.string.delete_account))
            setMessage(getString(R.string.delete_account_text))
            setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    profileNavViewModel.deleteAccount()
                    profileNavViewModel.signOutUser()
                    findNavController().navigate(R.id.action_profileNavFragment_to_loginFragment)
                }
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()

    }

    private fun showSignOutAccountDialog() {
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        alertDialogBuilder.apply {
            setTitle(getString(R.string.sign_out))
            setMessage(getString(R.string.signout_account_text))
            setPositiveButton(getString(R.string.sign_out)) { _, _ ->
                profileNavViewModel.signOutUser()
                findNavController().navigate(R.id.action_profileNavFragment_to_loginFragment)
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }

    private fun copyInviteKeyToClipboard(){
        val invKey = binding.textviewInvitationKey.text
        CopyClipboardUtils.copyTextToClipboard(invKey,requireContext())
        Snackbar.make(binding.root, getString(R.string.invkey_clipboard), Snackbar.LENGTH_SHORT).show()
    }
}