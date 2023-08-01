package com.example.challapp.adapters
import android.graphics.drawable.Drawable
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.challapp.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.challapp.services.ImageUploadService
import org.w3c.dom.Text


class DailyChallengeAdapter(
    private var downloadImageUrl: String?
) :
    RecyclerView.Adapter<DailyChallengeAdapter.ChallengeViewHolder>() {
    interface OnItemClickListener {
        fun onCardClick()
        fun onButtonClick()
    }
    private var itemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.adapter_dailychallange_item, parent, false)
        return ChallengeViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.loadImageToCard(downloadImageUrl)
    }

    override fun getItemCount(): Int {
        return 1
    }

    fun setDownloadImageUrl(downloadUrl: String) {
        downloadImageUrl = downloadUrl
        notifyDataSetChanged()
    }

    inner class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardview_addphotocard)
        private val imagePhoto : ImageView = itemView.findViewById(R.id.imageview_addphoto)
        private val imagePhotoText : TextView = itemView.findViewById(R.id.textview_addphotoprompt)
        private val button: Button = itemView.findViewById(R.id.button)
        private val constaintL: ConstraintLayout = itemView.findViewById(R.id.constraintlayout_backgroundChange)

        init {

            cardView.setOnClickListener {
                itemClickListener?.onCardClick()
            }

            // Set click listener for the Button
            button.setOnClickListener {
                itemClickListener?.onButtonClick()
            }
        }
        private var downloadImageUrl: String? = null

        fun loadImageToCard(imageUrl: String?) {
            if (imageUrl != null) {
                downloadImageUrl = imageUrl
                Glide.with(constaintL)
                    .load(imageUrl)
                    .centerCrop()
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            constaintL.background = resource
                            imagePhoto.visibility = View.INVISIBLE
                            imagePhotoText.visibility = View.INVISIBLE
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
        }
    }
}