package com.example.challapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.domain.models.ApplicationGroup

class GroupAdapter(
    private val groupList: MutableList<ApplicationGroup>,
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_group_item, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentGroup = groupList[position]
        holder.bind(currentGroup)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val groupNameTextView: TextView = itemView.findViewById(R.id.textview_groupName)
        private val userCountTextView: TextView = itemView.findViewById(R.id.textview_numberofusersingroup)

        @SuppressLint("SetTextI18n")
        fun bind(group: ApplicationGroup) {
            groupNameTextView.text = group.groupName
            userCountTextView.text = "${group.userCount} Users"
        }
    }

}