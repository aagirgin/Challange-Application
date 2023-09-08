package com.challengerdaily.challenge.ui.notification

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
import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.adapters.NotificationsAdapter
import com.challengerdaily.challenge.databinding.AlertdialogInvitenotificationBinding
import com.challengerdaily.challenge.databinding.FragmentNotificationBinding
import com.challengerdaily.challenge.domain.models.UserNotification
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

    private fun setupViewWithListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            notificationViewModel.getUserNotifications()
            notificationViewModel.getSenderNames()

            val senderNamesMap = notificationViewModel.senderNamesMap
            val currentNotifications = notificationViewModel.userNotificationList.value

            if (currentNotifications != null) {
                notificationsAdapter = NotificationsAdapter(currentNotifications, senderNamesMap, requireContext())
                notificationsAdapter.setOnItemClickListener(this@NotificationFragment)
                setupRecyclerView()
            }
        }
    }

    private fun onClickNavigateBack(){
        binding.imageviewBackNavArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerviewNotifications
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = notificationsAdapter
    }


    private fun showCustomDialog(notification: UserNotification) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val customView = LayoutInflater.from(requireContext()).inflate(R.layout.alertdialog_invitenotification, null)

        val bindingCustomDialog = AlertdialogInvitenotificationBinding.bind(customView)
        viewLifecycleOwner.lifecycleScope.launch {
            bindingCustomDialog.textviewGroupname.text = notificationViewModel.getGroupName(notification.notificationFromGroup)
        }
        builder.setView(customView)
        val dialog = builder.create()

        bindingCustomDialog.buttonAccept.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                notificationViewModel.userNotificationDelete.value = notification
                notificationViewModel.addToGroupOnAccept(notification.notificationFromGroup)
                notificationViewModel.resetState()
                notificationViewModel.indexOnDelete.value?.let { index ->
                    if (index >= 0) {
                        notificationsAdapter.removeNotification(index)
                    }
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
                notificationViewModel.indexOnDelete.value?.let { index ->
                    if (index >= 0) {
                        notificationsAdapter.removeNotification(index)
                    }
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