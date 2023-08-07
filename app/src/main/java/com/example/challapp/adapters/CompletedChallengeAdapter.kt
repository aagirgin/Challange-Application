package com.example.challapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.databinding.AdapterCompletedchallengeItemBinding
import com.example.challapp.domain.models.ApplicationDailyChallenge
import com.example.challapp.services.ImageUploadService
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CompletedChallengeAdapter(
    private var completedChallengesList: MutableList<ApplicationDailyChallenge>,
    private val currentUser: String
) : RecyclerView.Adapter<CompletedChallengeAdapter.CompletedChallengeViewHolder>() {

    inner class CompletedChallengeViewHolder(private val binding: AdapterCompletedchallengeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val creationDate: TextView = binding.textviewCreationdate
        private val description: TextView = binding.textviewDescription
        private val completedImage: ImageView = binding.imageviewCompletechallange
        private val shimmerLayout: ShimmerFrameLayout = binding.shimmerImage

        fun bind(currentChallenge: ApplicationDailyChallenge) {
            creationDate.text = currentChallenge.questionDocumentId
            description.text = currentChallenge.description
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmer()

            if (currentChallenge.questionDocumentId.isNotBlank()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val imageUrl = ImageUploadService.getImageWithDocumentId(
                        currentUser,
                        currentChallenge.questionDocumentId
                    )
                    if (imageUrl != "No Image") {
                        ImageUploadService.loadImageIntoImageView(imageUrl!!, completedImage)
                        shimmerLayout.hideShimmer()
                    } else {
                        completedImage.setBackgroundColor(Color.WHITE)
                        completedImage.setImageResource(R.drawable.baseline_group_24)
                        shimmerLayout.hideShimmer()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompletedChallengeViewHolder {
        val binding = AdapterCompletedchallengeItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CompletedChallengeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompletedChallengeViewHolder, position: Int) {
        val currentChallenge = completedChallengesList[position]
        holder.bind(currentChallenge)
    }

    override fun getItemCount(): Int {
        return completedChallengesList.size
    }

    fun setCompletedChallenges(challenges: MutableList<ApplicationDailyChallenge>) {
        completedChallengesList = challenges
        notifyDataSetChanged()
    }
}