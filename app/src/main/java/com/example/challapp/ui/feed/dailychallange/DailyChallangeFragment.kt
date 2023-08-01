package com.example.challapp.ui.feed.dailychallange

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.challapp.R
import com.example.challapp.adapters.DailyChallengeAdapter
import com.example.challapp.databinding.FragmentDailyChallangeBinding
import com.example.challapp.services.ImageUploadService
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DailyChallangeFragment : Fragment(), DailyChallengeAdapter.OnItemClickListener  {
    private lateinit var binding : FragmentDailyChallangeBinding
    private val dailyChallangeViewModel: DailyChallengeViewModel by viewModels()
    private lateinit var dailyChallengeAdapter: DailyChallengeAdapter
    private var downloadImageUrl: String? = null
    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                selectedImageUri?.let {
                    uploadImageToFirebaseStorage(it)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyChallangeBinding.inflate(inflater, container, false)

        onClickNavigateBack()
        dailyChallengeAdapter = DailyChallengeAdapter(downloadImageUrl)
        dailyChallengeAdapter.setOnItemClickListener(this)
        setupRecyclerView()

        return binding.root
    }

    private fun onClickNavigateBack(){
        binding.imageviewBacknavarrow.setOnClickListener {
            findNavController().navigate(R.id.action_dailyChallangeFragment_to_challangeFragment)
        }
    }

    private fun addChallangePicture() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentUserUid = dailyChallangeViewModel.currentUser.value?.uid

            if (currentUserUid != null) {
                ImageUploadService.uploadImagetoUserDir(
                    imageUri,
                    "1",
                    currentUserUid,
                    { downloadUrl ->
                        downloadImageUrl = downloadUrl
                        dailyChallengeAdapter.setDownloadImageUrl(downloadUrl)
                    },
                    { errorMessage ->
                        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }



    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerviewDailypage
        val layoutManager = LinearLayoutManager(requireContext())

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = dailyChallengeAdapter
    }

    override fun onCardClick() {
        addChallangePicture()
    }

    override fun onButtonClick() {
        println("Button Cliced")
    }

}