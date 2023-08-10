package com.example.challapp.ui.group.infogroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentGroupInfoBinding
import com.example.challapp.ui.group.landinggroup.GroupViewModel

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

        navigateBackSpecificGroupFeed()

        return binding.root
    }

    private fun navigateBackSpecificGroupFeed(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_groupInfoFragment_to_specificGroupFragment)
        }
    }


}