package com.example.challapp.ui.group.creategroup

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.challapp.R
import com.example.challapp.databinding.FragmentCreateGroupBinding
import com.example.challapp.ui.login.register.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateGroupFragment : Fragment() {

    private val createGroupViewModel: CreateGroupViewModel by viewModels()
    private lateinit var binding: FragmentCreateGroupBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        binding.viewModel = createGroupViewModel

        lifecycleScope.launch {
            println(addGroupToDatabase())
        }
        onNavigateBack()

        return binding.root
    }

    private fun onNavigateBack(){
        binding.imageviewBacknavarrow.setOnClickListener {
            findNavController().navigate(R.id.action_createGroupFragment_to_groupFragment)
        }
    }

    private fun validationFields(groupName: String, groupDesc: String) : Boolean{
        return groupName.isNotBlank() && groupDesc.isNotBlank()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun addGroupToDatabase(){
        binding.buttonCreategroup.setOnClickListener {
            lifecycleScope.launch {
                val groupName = binding.edittextGroupname.text.toString()
                val groupDescription = binding.edittextGroupdescription.text.toString()
                println(groupName)
                if(validationFields(groupName,groupDescription)){
                    createGroupViewModel.descriptionFlow.value = groupDescription
                    createGroupViewModel.groupnameFlow.value = groupName
                    createGroupViewModel.addGroup()
                } else{
                    Toast.makeText(requireContext() ,"Please fill all the fields.",Toast.LENGTH_SHORT).show()
                }
            } }
        }
}