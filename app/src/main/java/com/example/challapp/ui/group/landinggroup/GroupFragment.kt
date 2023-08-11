package com.example.challapp.ui.group.landinggroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.adapters.GroupAdapter
import com.example.challapp.databinding.FragmentGroupBinding
import com.example.challapp.domain.models.ApplicationGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupFragment : Fragment(), GroupAdapter.OnItemClickListener  {
    private lateinit var binding: FragmentGroupBinding
    private val groupViewModel: GroupViewModel by activityViewModels()
    private lateinit var groupAdapter: GroupAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupBinding.inflate(inflater, container, false)
        binding.viewModel =  groupViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        onClickNavigateCreateGroup()
        setupViewWithListener()


        return binding.root
    }

    private fun onClickNavigateCreateGroup(){
        binding.cardviewCreategroup.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_createGroupFragment)
        }
    }

    private fun setupViewWithListener(){
        viewLifecycleOwner.lifecycleScope.launch {
            groupViewModel.appGroupList.collect { appGroups ->
                groupAdapter = GroupAdapter(appGroups)
                groupAdapter.setOnItemClickListener(this@GroupFragment)
                setupRecyclerView()
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
        groupViewModel.setSelectedGroup(group)
        groupViewModel.setGroupDocumentId(position)
        findNavController().navigate(R.id.action_groupFragment_to_specificGroupFragment)
    }
}