package com.example.challapp.ui.feed.dailychallange

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.challapp.R
import com.example.challapp.adapters.DailyChallengeAdapter
import com.example.challapp.databinding.FragmentDailyChallangeBinding
import com.example.challapp.domain.models.ApplicationDailyQuestion
import com.example.challapp.domain.state.UiState
import com.example.challapp.services.ImageUploadService
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@AndroidEntryPoint
class DailyChallangeFragment : Fragment(), DailyChallengeAdapter.OnItemClickListener, DailyChallengeAdapter.OnDescriptionChangeListener  {
    private lateinit var binding : FragmentDailyChallangeBinding
    private var descriptionField: String = ""
    private val dailyChallangeViewModel: DailyChallengeViewModel by viewModels()
    private lateinit var dailyChallengeAdapter: DailyChallengeAdapter
    private var downloadImageUrl: String? = null
    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                selectedImageUri?.let {
                    downloadImageUrl = it.toString()
                    dailyChallengeAdapter.setDownloadImageUrl(downloadImageUrl!!)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyChallangeBinding.inflate(inflater, container, false)
        binding.viewModel = dailyChallangeViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        onClickNavigateBack()


        dailyChallengeAdapter = DailyChallengeAdapter(downloadImageUrl)
        dailyChallengeAdapter.setOnItemClickListener(this)
        dailyChallengeAdapter.setDescriptionChangeListener(this)
        submissionExistsVisibilityRecyclerView()


        return binding.root
    }


    private fun onClickNavigateBack(){
        binding.imageviewBacknavarrow.setOnClickListener {
            findNavController().navigate(R.id.action_dailyChallangeFragment_to_challangeFragment)
        }
    }

    override fun onDescriptionChanged(description: String) {
        descriptionField = description
    }


    private fun addChallangePicture() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcher.launch(intent)
    }


    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentUserUid = dailyChallangeViewModel.currentUser.value?.uid

            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = currentDate.format(formatter)

            if (currentUserUid != null) {
                ImageUploadService.uploadImagetoUserDir(
                    imageUri,
                    formattedDate,
                    currentUserUid,
                ) { errorMessage ->
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerviewDailypage
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = dailyChallengeAdapter
    }

    private fun submissionExistsVisibilityRecyclerView(){
        viewLifecycleOwner.lifecycleScope.launch {
            dailyChallangeViewModel.submissionExists.collect{submissionExists->
                when(submissionExists){
                    true -> {
                        binding.recyclerviewDailypage.visibility = View.GONE
                        binding.viewNoSubmissionView.visibility = View.VISIBLE
                    }
                    false -> {
                        setupRecyclerView()
                        binding.recyclerviewDailypage.visibility = View.VISIBLE
                        binding.viewNoSubmissionView.visibility = View.GONE
                    }
                    else -> {
                        binding.viewNoSubmissionView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun validate(): Boolean {
        return descriptionField.isNotBlank() || !(downloadImageUrl.isNullOrBlank())
    }
    override fun onCardClick() {
        addChallangePicture()
    }

    override fun onButtonClick() {
        if(validate()){
            viewLifecycleOwner.lifecycleScope.launch {
                downloadImageUrl?.let { uploadImageToFirebaseStorage(it.toUri()) }
                dailyChallangeViewModel.addItemToFirestore(descriptionField)
                dailyChallangeViewModel.addDailyChallenge.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            // Show loading UI if needed
                        }
                        is UiState.Success -> {
                            Snackbar.make(binding.root, state.data.toString(), Snackbar.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_dailyChallangeFragment_to_challangeFragment)
                        }
                        is UiState.Error -> {
                            // Show error UI and display the error message
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }

            }
        }else{
            Snackbar.make(binding.root, getString(R.string.picture_or_description), Snackbar.LENGTH_SHORT).show()
        }
    }

}