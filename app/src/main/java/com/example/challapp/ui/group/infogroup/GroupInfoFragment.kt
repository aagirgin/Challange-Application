package com.example.challapp.ui.group.infogroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentGroupInfoBinding
import com.example.challapp.domain.models.ApplicationGroup
import com.example.challapp.domain.state.UiState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class GroupInfoFragment : Fragment() {
    private lateinit var binding: FragmentGroupInfoBinding
    private val groupInfoViewModel: GroupInfoViewModel by viewModels()
    private lateinit var bundle: Bundle
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupInfoBinding.inflate(inflater, container, false)
        binding.viewModel =  groupInfoViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        bundle = requireArguments()
        val args = bundle.let { GroupInfoFragmentArgs.fromBundle(it) }
        dataSetGroup(args.group)
        inviteUserToGroup(args)
        navigateBackSpecificGroupFeed()

        return binding.root
    }

    private fun inviteUserToGroup(args:GroupInfoFragmentArgs){
        binding.imageviewInviteButton.setOnClickListener {
            val invitationText = binding.textinputInviteKey.text.toString()
            viewLifecycleOwner.lifecycleScope.launch {
               groupInfoViewModel.inviteToGroup(invitationText,args.selectedGroupId ,groupInfoViewModel.currentUser.value?.uid!!)

                groupInfoViewModel.invitationState.collect{state->
                    when(state){
                        is UiState.Success -> {
                            Snackbar.make(binding.root, state.data , Snackbar.LENGTH_SHORT).show()
                            groupInfoViewModel.resetState()
                        }
                        is UiState.Error -> {
                            Snackbar.make(binding.root, state.error , Snackbar.LENGTH_SHORT).show()
                            groupInfoViewModel.resetState()
                        }
                        else -> {}
                    }

                }

            }
        }

    }
    private fun navigateBackSpecificGroupFeed(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_groupInfoFragment_to_specificGroupFragment)
        }
    }

    private fun dataSetGroup(group:ApplicationGroup){
        groupInfoViewModel.setGroupData(group)
    }

}