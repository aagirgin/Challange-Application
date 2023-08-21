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
import com.example.challapp.extensions.toInvitePermission
import com.example.challapp.extensions.toStringValue
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class GroupSettingsFragment : Fragment() {
    private lateinit var binding: FragmentGroupSettingsBinding

    private val args: GroupSettingsFragmentArgs by navArgs()

    private val groupSettingsViewModel: GroupSettingsViewModel by viewModels()

    private lateinit var bundle: Bundle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupSettingsBinding.inflate(inflater, container, false)
        bundle = requireArguments()

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


        }
        binding.viewInviteStatus.setOnClickListener {
            val singleItems = arrayOf("ADMIN_ONLY", "USERS_ALL")
            var checkedItem = if (args.group.invitationPermission == InvitePermission.ADMIN_ONLY) 0 else 1

            context?.let { context ->
                MaterialAlertDialogBuilder(context)
                    .setTitle("Group Invite Status")
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                        if(singleItems[checkedItem] != args.group.invitationPermission.toStringValue()){
                            groupSettingsViewModel.changePermission(args.selectedGroupId,singleItems[checkedItem].toInvitePermission())
                            viewLifecycleOwner.lifecycleScope.launch {
                                groupSettingsViewModel.changedGroupState.collect{state ->
                                    when(state){
                                        is UiState.Success ->  {
                                            args.group.invitationPermission = singleItems[checkedItem].toInvitePermission()
                                            Snackbar.make(binding.root, getString(R.string.inv_status_change_message), Snackbar.LENGTH_SHORT).show()
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
}