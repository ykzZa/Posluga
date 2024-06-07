package dev.ykzza.posluga.ui.home.reviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Review
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.databinding.ItemReviewBinding

class ReviewsAdapter(
    val onAuthorClickListener: OnItemClickListener
) : ListAdapter<Pair<Review, User>, ReviewsAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewsAdapter.ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewsAdapter.ReviewViewHolder, position: Int) {
        val review = getItem(position).first
        val user = getItem(position).second
        holder.bind(review, user)
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review, user: User) {
            binding.apply {
                titleTextView.text = review.title
                ratingTextView.text = "${review.rating}/5"
                reviewTextView.text = review.text
                authorNameTextView.text = user.nickname
                Glide.with(root)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.ic_profile_grey)
                    .into(previewImage)
                root.setOnClickListener {
                    onAuthorClickListener.onAuthorClick(
                        review.authorId
                    )
                }
            }
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<Pair<Review, User>>() {

        override fun areItemsTheSame(
            oldItem: Pair<Review, User>,
            newItem: Pair<Review, User>
        ): Boolean {

            return oldItem.first.reviewId == newItem.first.reviewId
        }

        override fun areContentsTheSame(
            oldItem: Pair<Review, User>,
            newItem: Pair<Review, User>
        ): Boolean {

            return oldItem == newItem
        }
    }

    interface OnItemClickListener {

        fun onAuthorClick(authorId: String)
    }
}