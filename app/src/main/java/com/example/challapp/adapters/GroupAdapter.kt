package com.example.challapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.databinding.AdapterGroupItemBinding
import com.example.challapp.domain.models.ApplicationGroup

class GroupAdapter(
    private val groupList: MutableList<ApplicationGroup>
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = AdapterGroupItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentGroup = groupList[position]
        holder.bind(currentGroup)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    inner class GroupViewHolder(private val binding: AdapterGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(group: ApplicationGroup) {
            binding.textviewGroupName.text = group.groupName
            val userCountText = itemView.context.getString(R.string.user_count_format, group.userCount)
            binding.textviewNumberofusersingroup.text = userCountText
        }
    }
}