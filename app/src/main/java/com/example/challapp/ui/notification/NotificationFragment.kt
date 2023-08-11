package com.example.challapp.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.adapters.NotificationsAdapter
import com.example.challapp.databinding.FragmentNotificationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val notificationViewModel: NotificationViewModel by viewModels()
    private lateinit var notificationsAdapter: NotificationsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)

        setupViewWithListener()

        return binding.root
    }

    private fun setupViewWithListener(){
        viewLifecycleOwner.lifecycleScope.launch {
            notificationViewModel.getUserNotifications()
            notificationViewModel.userNotificationList.collect { notifications ->
                println(notifications)
                notificationsAdapter = notifications?.let { NotificationsAdapter(it) }!!
                setupRecyclerView()
            }
        }
    }
    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerviewNotifications
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = notificationsAdapter
    }

}