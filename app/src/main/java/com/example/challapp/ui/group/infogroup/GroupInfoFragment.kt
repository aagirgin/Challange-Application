package com.example.challapp.ui.group.infogroup

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
import com.example.challapp.adapters.GroupMembersAdapter
import com.example.challapp.databinding.FragmentGroupInfoBinding
import com.example.challapp.domain.models.ApplicationGroup
import com.example.challapp.domain.state.InvitationState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class GroupInfoFragment : Fragment() {

    private lateinit var binding: FragmentGroupInfoBinding

    private val groupInfoViewModel: GroupInfoViewModel by viewModels()

    private val args: GroupInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupInfoBinding.inflate(inflater, container, false)
        binding.viewModel =  groupInfoViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        dataSetGroup(args.group)
        navigateBackSpecificGroupFeed()
        onClickNavigateSettings()

        viewLifecycleOwner.lifecycleScope.launch {
            bindViewModel()
        }


        return binding.root
    }

    private suspend fun bindViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            groupInfoViewModel.allMembers.collect{userGroups->
                val recyclerView: RecyclerView = binding.recyclerviewGroupMembers
                val layoutManager = LinearLayoutManager(requireContext())
                recyclerView.layoutManager = layoutManager
                groupInfoViewModel.getAllUserSorted(args.group.groupMembers)
                val membersAdapter = userGroups?.let { allGroups -> GroupMembersAdapter(allGroups) }
                recyclerView.adapter = membersAdapter

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            groupInfoViewModel.currentUser.collect{ currentUser->
                if (currentUser != null) {
                    inviteUserToGroup(currentUser.uid)
                }
            }
        }
    }

    private fun inviteUserToGroup(currentUser: String){
        binding.imageviewInviteButton.setOnClickListener {
            val invitationText = binding.textinputInviteKey.text.toString()
            viewLifecycleOwner.lifecycleScope.launch {
                groupInfoViewModel.inviteToGroup(invitationText, args.selectedGroupId, currentUser)
                when(val state = groupInfoViewModel.invitationState.value){
                    is InvitationState.Success -> {
                        Snackbar.make(binding.root, state.message , Snackbar.LENGTH_SHORT).show()
                    }
                    else -> {
                        Snackbar.make(binding.root, state.message , Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun navigateBackSpecificGroupFeed(){
        binding.imageviewBackNavArrow.setOnClickListener {
            val direction = GroupInfoFragmentDirections.actionGroupInfoFragmentToSpecificGroupFragment(
                args.group,
                args.position,
                args.selectedGroupId
            )
            findNavController().navigate(direction)
        }
    }

    private fun onClickNavigateSettings(){
        binding.imageviewSettings.setOnClickListener {
            val direction = GroupInfoFragmentDirections.actionGroupInfoFragmentToGroupSettinsFragment(
                args.group,
                args.position,
                args.selectedGroupId
            )
            findNavController().navigate(direction)
        }
    }
    private fun dataSetGroup(group:ApplicationGroup){
        groupInfoViewModel.setGroupData(group)
    }
}