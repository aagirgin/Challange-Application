package com.example.challapp.ui.group.specificgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.adapters.GroupFeedAdapter
import com.example.challapp.databinding.FragmentSpecificGroupBinding
import com.example.challapp.ui.group.landinggroup.GroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpecificGroupFragment : Fragment() {
    private lateinit var binding: FragmentSpecificGroupBinding
    private val sharedViewModel: GroupViewModel by activityViewModels()
    private lateinit var groupFeedAdapter: GroupFeedAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpecificGroupBinding.inflate(inflater, container, false)
        navigateBackLandingGroup()
        navigateGroupInformation()
        setupRecyclerView()

        return binding.root
    }

    private fun navigateBackLandingGroup(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_specificGroupFragment_to_groupFragment)
        }
    }

    private fun navigateGroupInformation(){
        binding.cardviewSeeGroupInfo.setOnClickListener {
            findNavController().navigate(R.id.action_specificGroupFragment_to_groupInfoFragment)
        }
    }

    private fun recyclerViewData(){
        val groupFeedData = sharedViewModel.selectedGroup.value?.groupFeed
        groupFeedAdapter = groupFeedData?.let { GroupFeedAdapter(it) }!!
        groupFeedAdapter.setCompletedChallenges(groupFeedData)
    }
    private fun setupRecyclerView() {
        recyclerViewData()
        val recyclerView: RecyclerView = binding.recyclerviewGroupFeed
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = groupFeedAdapter
    }

}