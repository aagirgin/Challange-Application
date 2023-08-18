package com.example.challapp.ui.group.settingsgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.challapp.databinding.FragmentGroupSettingsBinding

class GroupSettingsFragment : Fragment() {
    private lateinit var binding: FragmentGroupSettingsBinding
    private val args: GroupSettingsFragmentArgs by navArgs()
    private lateinit var bundle: Bundle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupSettingsBinding.inflate(inflater, container, false)
        bundle = requireArguments()

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
}