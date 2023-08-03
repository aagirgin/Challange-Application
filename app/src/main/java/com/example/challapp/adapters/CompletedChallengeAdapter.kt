package com.example.challapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.domain.models.ApplicationDailyChallenge
import com.example.challapp.domain.models.ApplicationGroup

class CompletedChallengeAdapter(
    private var completedChallengesList: MutableList<ApplicationDailyChallenge>
):
    RecyclerView.Adapter<CompletedChallengeAdapter.CompletedChallengeViewHolder>() {

    inner class CompletedChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val creationDate: TextView = itemView.findViewById(R.id.textview_creationdate)
        val desctiption: TextView = itemView.findViewById(R.id.textview_description)
        val completedImage: ImageView = itemView.findViewById(R.id.imageview_completechallange)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedChallengeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_completedchallenge_item, parent, false)
        return CompletedChallengeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CompletedChallengeViewHolder, position: Int) {
        val currentChallenge = completedChallengesList[position]
        holder.creationDate.text = currentChallenge.questionDocumentId
        holder.desctiption.text = currentChallenge.description

    }

    override fun getItemCount(): Int {
        return completedChallengesList.size
    }

    fun setCompletedChallenges(challenges: MutableList<ApplicationDailyChallenge>) {
        completedChallengesList = challenges
        notifyDataSetChanged()
    }

}
