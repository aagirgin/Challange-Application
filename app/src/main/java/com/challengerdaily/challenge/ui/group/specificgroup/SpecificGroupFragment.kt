package com.challengerdaily.challenge.ui.group.specificgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.adapters.GroupFeedAdapter
import com.challengerdaily.challenge.databinding.FragmentSpecificGroupBinding
import com.challengerdaily.challenge.domain.models.ApplicationGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SpecificGroupFragment : Fragment() {
    private lateinit var binding: FragmentSpecificGroupBinding

    private val specificGroupViewModel: SpecificGroupViewModel by viewModels()

    private lateinit var groupFeedAdapter: GroupFeedAdapter

    private val args: SpecificGroupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpecificGroupBinding.inflate(inflater, container, false)
        dataSetGroup(args.selectedGroup,args.groupPosition,args.selectedGroupId)
        navigateBackLandingGroup()
        navigateGroupInformation()
        recyclerViewData()

        return binding.root
    }

    private fun navigateBackLandingGroup(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_specificGroupFragment_to_groupFragment)
        }
    }

    private fun navigateGroupInformation(){
        binding.cardviewSeeGroupInfo.setOnClickListener {
            val appGroup = specificGroupViewModel.selectedGroup.value
            val positionGroup = specificGroupViewModel.groupPosition.value
            val groupId = specificGroupViewModel.selectedGroupId.value

            if (positionGroup != null && appGroup != null) {
                val directions = groupId?.let { id ->
                    SpecificGroupFragmentDirections.actionSpecificGroupFragmentToGroupInfoFragment(positionGroup,
                        id,appGroup)
                }
                if (directions != null) {
                    findNavController().navigate(directions)
                }
            }
        }
    }

    private fun recyclerViewData() {
        viewLifecycleOwner.lifecycleScope.launch {
            specificGroupViewModel.fetchUserMap(args.selectedGroup.groupMembers)
            specificGroupViewModel.selectedGroup.collect { selectedGroup ->
                val groupFeed = selectedGroup?.groupFeed ?: return@collect
                val usersMap = specificGroupViewModel.usersMap.value
                groupFeedAdapter = GroupFeedAdapter(groupFeed,usersMap)
                groupFeedAdapter.setCompletedChallenges(groupFeed)
                val recyclerView: RecyclerView = binding.recyclerviewGroupFeed
                val layoutManager = LinearLayoutManager(requireContext())
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = groupFeedAdapter
            }
        }
    }

    private fun dataSetGroup(group: ApplicationGroup,position:Int,groupId:String){
        specificGroupViewModel.setGroupData(group,position,groupId)
    }
}