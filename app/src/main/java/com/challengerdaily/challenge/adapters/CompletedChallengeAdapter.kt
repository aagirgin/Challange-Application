package com.challengerdaily.challenge.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.challengerdaily.challenge.R
import com.challengerdaily.challenge.databinding.AdapterCompletedchallengeItemBinding
import com.challengerdaily.challenge.domain.models.ApplicationDailyChallenge
import com.challengerdaily.challenge.extensions.load
import com.challengerdaily.challenge.services.ImageUploadService
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CompletedChallengeAdapter(
    private var completedChallengesList: MutableList<ApplicationDailyChallenge>,
    private val currentUser: String
) : RecyclerView.Adapter<CompletedChallengeAdapter.CompletedChallengeViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(documentId: String)
    }

    private var listener: OnItemClickListener? = null
    private lateinit var binding: AdapterCompletedchallengeItemBinding

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class CompletedChallengeViewHolder(binding: AdapterCompletedchallengeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentChallenge = completedChallengesList[position]
                    val documentId = currentChallenge.questionDocumentId
                    listener?.onItemClick(documentId)
                }
            }
        }

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
                        completedImage.load(imageUrl)
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
        binding = AdapterCompletedchallengeItemBinding.inflate(
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