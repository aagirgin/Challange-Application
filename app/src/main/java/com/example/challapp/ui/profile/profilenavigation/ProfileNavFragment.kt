package com.example.challapp.ui.profile.profilenavigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.challapp.R
import com.example.challapp.databinding.FragmentProfileNavBinding
import com.example.challapp.repository.FirestoreUserRepository
import com.example.challapp.repository.FirestoreUserRepositoryImpl
import com.example.challapp.services.AuthService
import com.example.challapp.services.ImageUploadService
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileNavFragment : Fragment() {
    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the selected image URI
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                selectedImageUri?.let {
                    uploadImageToFirebaseStorage(it)
                }
            }
        }

    private val profileNavViewModel: ProfileNavViewModel by viewModels()
    private var downloadImageUrl: String? = null
    private lateinit var binding : FragmentProfileNavBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileNavBinding.inflate(inflater,container,false)

        displayName()

        val imageUrl = "${AuthService.getCurrentUser()?.email}_avatar.jpg"
        loadProfileImage(imageUrl)


        addProfilePicture()
        onPressedDeleteAccount()
        onNavigateInProfile()

        return binding.root
    }


    private fun addProfilePicture() {

        binding.shapeableImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            launcher.launch(intent)
        }
    }

    private fun displayName(){
        viewLifecycleOwner.lifecycleScope.launch {
            profileNavViewModel.usernameFlow.collect{   username ->
                binding.textviewUsername.text = username
            }
        }
    }



    private fun loadProfileImage(imageUrl: String) {
        val storageRef: StorageReference =
            FirebaseStorage.getInstance().reference.child("profileAvatars").child(imageUrl)

        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->

            ImageUploadService.loadImageIntoImageView(downloadUri.toString(), binding.shapeableImageView)
        }.addOnFailureListener { exception ->

        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val email = AuthService.getCurrentUser()?.email ?: return

        ImageUploadService.uploadImage(
            imageUri,
            email,
            { downloadUrl ->
                // Image upload success
                downloadImageUrl = downloadUrl
                ImageUploadService.loadImageIntoImageView(downloadImageUrl!!, binding.shapeableImageView)
            },
            { errorMessage ->
                // Image upload failure
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun onNavigateInProfile(){
        binding.navProfileInfo.setOnClickListener {
            findNavController().navigate(R.id.action_profileNavFragment_to_profileFragment)
        }

        binding.navChangePwInfo.setOnClickListener {
            findNavController().navigate(R.id.action_profileNavFragment_to_profileChangePwFragment)
        }
    }


    private fun onPressedDeleteAccount(){
        binding.deleteAccInfo.setOnClickListener {
            showDeleteAccountDialog()
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

}