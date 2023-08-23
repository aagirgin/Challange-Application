package com.example.challapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.databinding.AdapterGroupmemberItemBinding
import com.example.challapp.domain.models.ApplicationUser

class GroupMembersAdapter(private val memberList: MutableList<ApplicationUser>) :
    RecyclerView.Adapter<GroupMembersAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: AdapterGroupmemberItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val userNameText: TextView = binding.textviewName
        private val challengeCount: TextView = binding.textviewNumberOfChallenge
        fun bind(member: ApplicationUser) {
            val countChallengeString = itemView.context.getString(R.string.challenge_count_format, member.allDailyQuestions.size)
            userNameText.text = member.username
            challengeCount.text = countChallengeString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterGroupmemberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(memberList[position])
    }

    override fun getItemCount(): Int {
        return memberList.size
    }
}