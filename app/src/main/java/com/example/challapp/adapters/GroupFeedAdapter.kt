package com.example.challapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challapp.R
import com.example.challapp.databinding.AdapterGroupfeedItemBinding
import com.example.challapp.domain.models.GroupFeed
import com.example.challapp.extensions.load
import com.example.challapp.services.ImageUploadService
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupFeedAdapter(private var itemList: MutableList<GroupFeed>,
                       private val memberNames: Map<String, String>?
) : RecyclerView.Adapter<GroupFeedAdapter.ViewHolder>() {

    inner class ViewHolder(binding: AdapterGroupfeedItemBinding) :
        RecyclerView.ViewHolder(binding.root)  {

        private val userName: TextView = binding.textviewUsername
        private val creationDate: TextView = binding.textviewCreationdate
        private val description: TextView = binding.textviewDescription
        private val completedImage: ImageView = binding.imageviewCompletechallange
        private val shimmerLayout: ShimmerFrameLayout = binding.shimmerImage

        fun bind(data: GroupFeed) {
            userName.text = memberNames?.get(data.userId)
            creationDate.text = data.questionDocumentId
            description.text = data.description

            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmer()

            if (data.questionDocumentId.isNotBlank()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val imageUrl = ImageUploadService.getImageWithDocumentId(
                        data.userId,
                        data.questionDocumentId
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
    ): GroupFeedAdapter.ViewHolder {
        val binding = AdapterGroupfeedItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentData = itemList[position]
        holder.bind(currentData)
    }

    fun setCompletedChallenges(challenges: MutableList<GroupFeed>) {
        itemList = challenges
        notifyDataSetChanged()
    }
}