package com.example.challapp.ui.group.specificgroup

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
import com.example.challapp.adapters.GroupFeedAdapter
import com.example.challapp.databinding.FragmentSpecificGroupBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SpecificGroupFragment : Fragment() {
    private lateinit var binding: FragmentSpecificGroupBinding

    private val specificGroupViewModel: SpecificGroupViewModel by viewModels()

    private lateinit var groupFeedAdapter: GroupFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpecificGroupBinding.inflate(inflater, container, false)

        val bundle = findNavController().previousBackStackEntry?.savedStateHandle?.get<Bundle>("groupBundle")
        specificGroupViewModel.setBundleValues(bundle)

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
            viewLifecycleOwner.lifecycleScope.launch {
                val appGroup = specificGroupViewModel.selectedGroup.value
                val positionGroup = specificGroupViewModel.groupPosition.value
                val groupId = specificGroupViewModel.selectedGroupId.value

                if (positionGroup != null && appGroup != null) {
                    val directions = groupId?.let { it1 ->
                        SpecificGroupFragmentDirections.actionSpecificGroupFragmentToGroupInfoFragment(positionGroup,
                            it1,appGroup)
                    }
                    findNavController().navigate(directions!!)
                }
            }
        }
    }

    private fun recyclerViewData(){

        viewLifecycleOwner.lifecycleScope.launch {
            specificGroupViewModel.selectedGroup.collect {selectedGroup ->
                groupFeedAdapter = selectedGroup?.groupFeed?.let { GroupFeedAdapter(it) }!!
                groupFeedAdapter.setCompletedChallenges(selectedGroup.groupFeed)
            }
        }

    }
    private fun setupRecyclerView() {
        recyclerViewData()
        val recyclerView: RecyclerView = binding.recyclerviewGroupFeed
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = groupFeedAdapter
    }

}