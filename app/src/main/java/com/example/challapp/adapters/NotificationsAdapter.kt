package com.example.challapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.databinding.AdapterNotificationsBinding
import com.example.challapp.domain.models.UserNotification
import com.example.challapp.utils.DateUtils

class NotificationsAdapter(
    private val notificationList: MutableList<UserNotification>
): RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {




    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(notification: UserNotification)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
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

    fun removeNotification(index: Int) {
        if (index in 0 until notificationList.size) {
            notificationList.removeAt(index)
            notifyItemRemoved(index)
        }
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
        private val card: CardView = binding.cardviewNotificationitem

        init {
            card.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(notificationList[position])
                }
            }
        }
        fun bind(data: UserNotification) {
            userName.text = data.notificationSenderUser
            date.text = DateUtils.getCurrentFormattedDateAsDayMonth()
        }
    }
}