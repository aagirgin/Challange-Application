package com.example.challapp.ui.group.infogroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentGroupInfoBinding
import com.example.challapp.domain.state.UiState
import com.example.challapp.ui.group.landinggroup.GroupViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroupInfoFragment : Fragment() {
    private lateinit var binding: FragmentGroupInfoBinding
    private val sharedViewModel: GroupViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupInfoBinding.inflate(inflater, container, false)
        binding.viewModel =  sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        inviteUserToGroup()
        navigateBackSpecificGroupFeed()

        return binding.root
    }

    private fun navigateBackSpecificGroupFeed(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_groupInfoFragment_to_specificGroupFragment)
        }
    }

    private fun inviteUserToGroup(){
        binding.imageviewInviteButton.setOnClickListener {
            val invitationText = binding.textinputInviteKey.text.toString()
            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.selectedGroupId.value?.let { groupId ->
                    sharedViewModel.currentUser.value?.let { senderId ->
                        sharedViewModel.inviteToGroup(invitationText,senderId.uid, groupId)
                    }
                }
                sharedViewModel.invitationState.collect{state->
                    when(state){
                        is UiState.Success -> {
                            Snackbar.make(binding.root, state.data.toString() , Snackbar.LENGTH_SHORT).show()
                            sharedViewModel.resetState()
                        }
                        is UiState.Error -> {
                            Snackbar.make(binding.root, state.error , Snackbar.LENGTH_SHORT).show()
                            sharedViewModel.resetState()
                        }
                        else -> {}
                    }

                }
            }

        }
    }


}