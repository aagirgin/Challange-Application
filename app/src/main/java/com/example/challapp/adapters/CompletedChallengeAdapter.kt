package com.example.challapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.domain.models.ApplicationDailyChallenge
import com.example.challapp.services.ImageUploadService
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CompletedChallengeAdapter(
    private var completedChallengesList: MutableList<ApplicationDailyChallenge>,
    private val currentUser: String
):
    RecyclerView.Adapter<CompletedChallengeAdapter.CompletedChallengeViewHolder>() {

    inner class CompletedChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val creationDate: TextView = itemView.findViewById(R.id.textview_creationdate)
        val desctiption: TextView = itemView.findViewById(R.id.textview_description)
        val completedImage: ImageView = itemView.findViewById(R.id.imageview_completechallange)
        val shimmerLayout: ShimmerFrameLayout = itemView.findViewById(R.id.shimmer_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedChallengeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_completedchallenge_item, parent, false)
        return CompletedChallengeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CompletedChallengeViewHolder, position: Int) {
        val currentChallenge = completedChallengesList[position]
        holder.creationDate.text = currentChallenge.questionDocumentId
        holder.desctiption.text = currentChallenge.description

        holder.shimmerLayout.visibility = View.VISIBLE
        holder.shimmerLayout.startShimmer()

        if (currentChallenge.questionDocumentId.isNotBlank()) {
            CoroutineScope(Dispatchers.Main).launch {
                val imageUrl = ImageUploadService.getImageWithDocumentId(currentUser, currentChallenge.questionDocumentId)
                if (imageUrl != "No Image") {
                    ImageUploadService.loadImageIntoImageView(imageUrl!!, holder.completedImage)
                    holder.shimmerLayout.hideShimmer()
                }
                else{
                    holder.completedImage.setBackgroundColor(Color.WHITE)
                    holder.completedImage.setImageResource(R.drawable.baseline_group_24)
                    holder.shimmerLayout.hideShimmer()
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return completedChallengesList.size
    }

    fun setCompletedChallenges(challenges: MutableList<ApplicationDailyChallenge>) {
        completedChallengesList = challenges
        notifyDataSetChanged()
    }

}
