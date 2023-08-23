package com.example.challapp.ui.group.settingsgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.challapp.R
import com.example.challapp.databinding.FragmentGroupSettingsBinding
import com.example.challapp.domain.models.InvitePermission
import com.example.challapp.domain.state.UiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class GroupSettingsFragment : Fragment() {
    private lateinit var binding: FragmentGroupSettingsBinding

    private val args: GroupSettingsFragmentArgs by navArgs()

    private val groupSettingsViewModel: GroupSettingsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupSettingsBinding.inflate(inflater, container, false)

        onClickOpenAlertDialog()
        onClickNavigateBack()

        return binding.root
    }

    private fun onClickNavigateBack(){
        binding.imageviewBackNavArrow.setOnClickListener {
            val direction = GroupSettingsFragmentDirections.actionGroupSettinsFragmentToGroupInfoFragment(
                args.position,
                args.group,
                args.selectedGroupId
            )
            findNavController().navigate(direction)
        }
    }

    private fun onClickOpenAlertDialog(){
        binding.viewLeave.setOnClickListener {
            if (args.group.groupOwner == groupSettingsViewModel.currentUser.value?.uid){
                onClickGroupOwnerDelete()
            }
            else {
                onClickMemberLeave()
            }
        }
        binding.viewInviteStatus.setOnClickListener {
                if(args.group.groupOwner == groupSettingsViewModel.currentUser.value?.uid){
                    onClickGroupInviteStatusChange()
                }
            }
        }

    private fun onClickGroupOwnerDelete(){
        context?.let {context->
            MaterialAlertDialogBuilder(context)
                .setTitle(resources.getString(R.string.delete_group_title))
                .setMessage(resources.getString(R.string.delete_group_title_text))
                .setNegativeButton(resources.getString(R.string.stay)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.delete)) { dialog, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        groupSettingsViewModel.deleteGroup(args.selectedGroupId)
                        groupSettingsViewModel.deleteGroupState.collect{state->
                            when(state){
                                is UiState.Success -> {
                                    Snackbar.make(binding.root, getString(R.string.successfully_delete_group), Snackbar.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_groupSettinsFragment_to_groupFragment)
                                }
                                is UiState.Error -> Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                                else -> {}
                            }
                        }
                    }

                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun onClickMemberLeave(){
        context?.let {context->
            MaterialAlertDialogBuilder(context)
                .setTitle(resources.getString(R.string.leave_group))
                .setMessage(resources.getString(R.string.leave_group_title_text))
                .setNegativeButton(resources.getString(R.string.stay)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.leave)) { dialog, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        groupSettingsViewModel.currentUser.value?.uid?.let {user-> groupSettingsViewModel.leaveGroup(user,args.selectedGroupId) }
                        groupSettingsViewModel.leaveGroupState.collect{state->
                            when(state){
                                is UiState.Success -> {
                                    Snackbar.make(binding.root, getString(R.string.successfully_left_group), Snackbar.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_groupSettinsFragment_to_groupFragment)
                                }
                                is UiState.Error -> Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                                else -> {}
                            }
                        }
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun onClickGroupInviteStatusChange(){
        val singleItems = arrayOf("ADMIN_ONLY", "USERS_ALL")
        var checkedItem = if (args.group.invitationPermission == InvitePermission.ADMIN_ONLY) 0 else 1

        context?.let { context ->
            MaterialAlertDialogBuilder(context)
                .setTitle(getString(R.string.group_invitation_status))
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                    if(InvitePermission.getByValue(singleItems[checkedItem]) != args.group.invitationPermission){
                        groupSettingsViewModel.changePermission(args.selectedGroupId,InvitePermission.getByValue(singleItems[checkedItem]))
                        viewLifecycleOwner.lifecycleScope.launch {
                            groupSettingsViewModel.changedGroupState.collect{state ->
                                when(state){
                                    is UiState.Success ->  {
                                        args.group.invitationPermission = InvitePermission.getByValue(singleItems[checkedItem])
                                        Snackbar.make(binding.root, state.data, Snackbar.LENGTH_SHORT).show()
                                    }
                                    is UiState.Error -> Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                                    else -> {}
                                }
                            }
                        }
                    }
                    dialog.dismiss()
                }
                .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                    checkedItem = when (which) {
                        0 -> { 0 }
                        else -> { 1 }
                    }
                }.show()
    }
}
}