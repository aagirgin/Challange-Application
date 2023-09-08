package com.challengerdaily.challenge.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.databinding.AdapterNotificationsBinding
import com.challengerdaily.challenge.domain.models.InviteStatus
import com.challengerdaily.challenge.domain.models.UserNotification
import com.challengerdaily.challenge.utils.DateUtils

class NotificationsAdapter(
    private val notificationList: MutableList<UserNotification>,
    private val senderNameMap: MutableMap<String, String>,
    private val context: Context
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
        private val notificationTypeImage: ImageView = binding.imageviewNotificationType
        private val date : TextView = binding.textviewDate
        private val card: CardView = binding.cardviewNotificationitem
        private val notificationText: TextView = binding.textviewMessage
        private val imageReply: ImageView = binding.imageviewReply
        private val cardConstraintLayout: ConstraintLayout = binding.cardConstraintLayout





        fun bind(data: UserNotification) {
            val invGroup = itemView.context.getString(R.string.user_sent_you_an_invitation_for_you_to_join_group)
            val deletedGroup = itemView.context.getString(R.string.deleted_group_string,data.notificationFromGroup)

            card.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedNotification = notificationList[position]
                    if (clickedNotification.notificationType != InviteStatus.DELETED_GROUP_INFO) {
                        itemClickListener?.onItemClick(clickedNotification)
                    }
                }
            }

            val sender = senderNameMap[data.notificationSenderUser]
            if (sender == "null"){
                userName.text = context.getString(R.string.deleted_user)
            }
            else{
                userName.text = senderNameMap[data.notificationSenderUser]
            }



            date.text = DateUtils.getCurrentFormattedDateAsDayMonth()

            if (data.notificationType == InviteStatus.INVITE){
                notificationText.text = invGroup
                cardConstraintLayout.setBackgroundResource(R.color.notificationColorInvite)
                notificationTypeImage.setImageResource(R.drawable.ic_invitation)
                imageReply.visibility = View.VISIBLE
            }
            else if(data.notificationType == InviteStatus.DELETED_GROUP_INFO){
                notificationText.text = deletedGroup
                notificationTypeImage.setImageResource(R.drawable.ic_info)
                imageReply.visibility = View.GONE
            }
        }
    }
}