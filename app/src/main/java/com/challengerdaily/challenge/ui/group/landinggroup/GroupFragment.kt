package com.challengerdaily.challenge.ui.group.landinggroup

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
import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.adapters.GroupAdapter
import com.challengerdaily.challenge.databinding.FragmentGroupBinding
import com.challengerdaily.challenge.domain.models.ApplicationGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupFragment : Fragment(), GroupAdapter.OnItemClickListener  {

    private lateinit var binding: FragmentGroupBinding

    private val groupViewModel: GroupViewModel by viewModels()

    private lateinit var groupAdapter: GroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupBinding.inflate(inflater, container, false)
        binding.viewModel =  groupViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        onClickNavigateCreateGroup()
        onClickNavigateNotifications()
        setupViewWithListener()


        return binding.root
    }

    private fun onClickNavigateCreateGroup(){
        binding.cardviewCreategroup.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_createGroupFragment)
        }
    }

    private fun onClickNavigateNotifications(){
        binding.imageviewNoticication.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_notificationFragment)
        }
    }

    private fun setupViewWithListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            groupViewModel.appGroupList.collect { appGroups ->
                val currentUser = groupViewModel.currentUser.value
                if (currentUser != null) {
                    groupAdapter = GroupAdapter(appGroups, currentUser.uid)
                    groupAdapter.setOnItemClickListener(this@GroupFragment)
                    setupRecyclerView()
                }
            }
        }
    }
    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerviewGroups
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = groupAdapter
    }


    override fun onGroupItemClick(group: ApplicationGroup,position: Int) {
        groupViewModel.userIncludedGroupIds.value?.get(position)?.let {
            val groupId = it.toString()
            val action = GroupFragmentDirections.actionGroupFragmentToSpecificGroupFragment(
                group, position, groupId
            )
            findNavController().navigate(action)
        }

    }
}