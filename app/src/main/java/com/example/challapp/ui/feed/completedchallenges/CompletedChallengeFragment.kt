package com.example.challapp.ui.feed.completedchallenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.adapters.CompletedChallengeAdapter
import com.example.challapp.databinding.FragmentCompletedChallengeBinding
import com.example.challapp.domain.models.ApplicationDailyChallenge
import com.example.challapp.domain.state.UiState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompletedChallengeFragment : Fragment(),CompletedChallengeAdapter.OnItemClickListener {
    private lateinit var binding: FragmentCompletedChallengeBinding
    private lateinit var completedChallengeAdapter: CompletedChallengeAdapter
    private val completedChallangeViewModel: CompletedChallengeViewModel by viewModels()

        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentCompletedChallengeBinding.inflate(inflater, container, false)

        completedChallengeAdapter = completedChallangeViewModel.getCurrentUser.value?.uid?.let {
            CompletedChallengeAdapter(mutableListOf(),
                it
            )
        }!!
        setupRecyclerView()
        onNavigateBackChallengePage()


        return binding.root
    }

    private fun onNavigateBackChallengePage(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_completedChallengeFragment_to_challangeFragment)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerviewCompletedChallenges
        val layoutManager = LinearLayoutManager(requireContext())

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = completedChallengeAdapter
        completedChallengeAdapter.setOnItemClickListener(this)

        viewLifecycleOwner.lifecycleScope.launch {
            completedChallangeViewModel.getDailyChallenges.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        val data = state.data as MutableList<ApplicationDailyChallenge>
                        completedChallengeAdapter.setCompletedChallenges(data)

                    }
                    is UiState.Error -> Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                    else -> { }
                }
            }
        }
    }

    override fun onItemClick(documentId: String) {
        val bottomSheet = QuestionTaskSheet(documentId)
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

}