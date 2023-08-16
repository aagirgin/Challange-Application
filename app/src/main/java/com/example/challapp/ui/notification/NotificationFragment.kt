package com.example.challapp.ui.notification

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.adapters.NotificationsAdapter
import com.example.challapp.databinding.AlertdialogInvitenotificationBinding
import com.example.challapp.databinding.FragmentNotificationBinding
import com.example.challapp.domain.models.UserNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : Fragment(), NotificationsAdapter.OnItemClickListener {
    private lateinit var binding: FragmentNotificationBinding
    private val notificationViewModel: NotificationViewModel by viewModels()
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var bindingCustomDialog: AlertdialogInvitenotificationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        bindingCustomDialog = AlertdialogInvitenotificationBinding.inflate(inflater,container,false)

        onClickNavigateBack()
        setupViewWithListener()

        return binding.root
    }

    private fun setupViewWithListener(){
        viewLifecycleOwner.lifecycleScope.launch {
            notificationViewModel.getUserNotifications()
            notificationViewModel.userNotificationList.collect { notifications ->
                notificationsAdapter = notifications?.let { NotificationsAdapter(it) }!!
                notificationsAdapter.setOnItemClickListener(this@NotificationFragment)
                setupRecyclerView()
            }
        }
    }

    private fun onClickNavigateBack(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment_to_challangeFragment)
        }
    }
    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerviewNotifications
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = notificationsAdapter
    }

    @SuppressLint("InflateParams")
    private fun showCustomDialog(notification: UserNotification){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val customView = LayoutInflater.from(requireContext()).inflate(R.layout.alertdialog_invitenotification, null)

        val bindingCustomDialog = AlertdialogInvitenotificationBinding.bind(customView)
        bindingCustomDialog.textviewGroupname.text =  notification.notificationFromGroup
        builder.setView(customView)
        val dialog = builder.create()

        bindingCustomDialog.buttonAccept.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                notificationViewModel.userNotificationDelete.value = notification
                notificationViewModel.addToGroupOnAccept(notification.notificationFromGroup)
                notificationViewModel.resetState()
                if (notificationViewModel.indexOnDelete.value!! >= 0) {
                    notificationsAdapter.removeNotification(notificationViewModel.indexOnDelete.value!!)
                }
                notificationViewModel.getUserNotifications()
            }

            dialog.dismiss()
        }

        bindingCustomDialog.buttonDecline.setOnClickListener {
            notificationViewModel.userNotificationDelete.value = notification
            viewLifecycleOwner.lifecycleScope.launch {
                notificationViewModel.deleteOnRejection()
                notificationViewModel.resetState()
                notificationViewModel.getUserNotifications()
                if (notificationViewModel.indexOnDelete.value!! >= 0) {
                    notificationsAdapter.removeNotification(notificationViewModel.indexOnDelete.value!!)
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onItemClick(notification: UserNotification) {
        showCustomDialog(notification)
    }
}