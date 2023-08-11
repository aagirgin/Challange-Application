package com.example.challapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.databinding.AdapterNotificationsBinding
import com.example.challapp.domain.models.UserNotification
import com.example.challapp.utils.DateUtils

class NotificationsAdapter(
    private val notificationList: MutableList<UserNotification>
): RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationsAdapter.ViewHolder {
        val binding = AdapterNotificationsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationsAdapter.ViewHolder, position: Int) {
        val currentData = notificationList[position]
        holder.bind(currentData)

    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class ViewHolder(binding: AdapterNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val userName: TextView = binding.textviewUserNameNotifications
        //private val notificationTypeImage: ImageView = binding.imageviewNotificationType
        private val date : TextView = binding.textviewDate

        fun bind(data: UserNotification) {
            userName.text = data.notificationSenderUser
            date.text = DateUtils.getCurrentFormattedDateAsDayMonth()
        }
    }
}