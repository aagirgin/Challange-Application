package com.example.challapp.adapters
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.challapp.databinding.AdapterDailychallangeItemBinding
import com.example.challapp.ui.feed.dailychallange.DailyChallangeFragment

class DailyChallengeAdapter(
    private var downloadImageUrl: String?
) :
    RecyclerView.Adapter<DailyChallengeAdapter.ChallengeViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null
    private var descriptionChangeListener: OnDescriptionChangeListener? = null
    interface OnItemClickListener {
        fun onCardClick()
        fun onButtonClick()
    }
    interface OnDescriptionChangeListener {
        fun onDescriptionChanged(description: String)
    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun setDescriptionChangeListener(listener: DailyChallangeFragment) {
        descriptionChangeListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = AdapterDailychallangeItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChallengeViewHolder(binding)
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

    inner class ChallengeViewHolder(private val binding: AdapterDailychallangeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val cardView: CardView = binding.cardviewAddphotocard
        private val imagePhoto: ImageView = binding.imageviewAddphoto
        private val imagePhotoText: TextView = binding.textviewAddphotoprompt
        private val button: Button = binding.button
        private val constaintL: ConstraintLayout = binding.constraintlayoutBackgroundChange
        private val edittextGroupDescription: EditText = binding.edittextGroupdescription

        init {
            cardView.setOnClickListener {
                itemClickListener?.onCardClick()
            }

            button.setOnClickListener {
                itemClickListener?.onButtonClick()
            }

            edittextGroupDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    descriptionChangeListener?.onDescriptionChanged(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })
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