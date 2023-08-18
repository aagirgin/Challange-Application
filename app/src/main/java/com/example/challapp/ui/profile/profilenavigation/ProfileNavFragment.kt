package com.example.challapp.ui.profile.profilenavigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentProfileNavBinding
import com.example.challapp.extensions.load
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
    }





    private fun showDeleteAccountDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setTitle("Delete Account")
            setMessage("Are you sure you want to delete your account?")
            setPositiveButton("Delete") { _, _ ->
                // Handle account deletion here
                // You can call a function to delete the account or perform any other actions
                // Example: profileViewModel.deleteAccount()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }

    private fun showSignOutAccountDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setTitle("Sign Out")
            setMessage("Are you sure you want to sign out your account?")
            setPositiveButton("Sign out") { _, _ ->
                profileNavViewModel.signoutUser()
                findNavController().navigate(R.id.action_profileNavFragment_to_loginFragment)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }

}