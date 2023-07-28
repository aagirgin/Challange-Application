package com.example.challapp.ui.group.creategroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentCreateGroupBinding

class CreateGroupFragment : Fragment() {


    private lateinit var binding: FragmentCreateGroupBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateGroupBinding.inflate(inflater, container, false)

        onNavigateBack()

        return binding.root
    }

    private fun onNavigateBack(){
        binding.imageviewBacknavarrow.setOnClickListener {
            findNavController().navigate(R.id.action_createGroupFragment_to_groupFragment)
        }
    }

    private fun fillOwner(){
        binding.textviewGroupowner.text = "A"
    }
}