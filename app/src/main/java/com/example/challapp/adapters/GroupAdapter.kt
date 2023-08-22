package com.example.challapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.databinding.AdapterGroupItemBinding
import com.example.challapp.domain.models.ApplicationGroup

class GroupAdapter(
    private val groupList: MutableList<ApplicationGroup>,
    private val currentUserUid: String
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onGroupItemClick(group: ApplicationGroup, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
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
        private val groupItem: ConstraintLayout = binding.constraintLayoutGroupItem
        init {
            groupItem.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onGroupItemClick(groupList[position],position)
                }
            }
        }
        fun bind(group: ApplicationGroup) {
            val userCountText = itemView.context.getString(R.string.user_count_format, group.groupMembers.size)
            val ownerText = itemView.context.getString(R.string.owner)
            val memberText = itemView.context.getString(R.string.member)

            binding.textviewGroupName.text = group.groupName
            binding.textviewNumberofusersingroup.text = userCountText

            if (currentUserUid == group.groupOwner){
                binding.textviewMember.text = ownerText
                binding.imageviewStar.setImageResource(R.drawable.baseline_star_rate_24_orange)
            }else{
                binding.textviewMember.text = memberText
                binding.imageviewStar.setImageResource(R.drawable.baseline_star_rate_24)
            }
        }
    }
}