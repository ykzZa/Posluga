package dev.ykzza.posluga.ui.create_post

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import dev.ykzza.posluga.databinding.ItemImageBinding

class ImagesAdapter(
    val onCancelClickListener: OnItemClickListener
) : ListAdapter<Uri, ImagesAdapter.ImageViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = getItem(position)
        holder.bind(imageUrl)
    }

    inner class ImageViewHolder(private val binding: ItemImageBinding) : ViewHolder(binding.root) {

        fun bind(imageUrl: Uri) {
            binding.apply {
                Glide.with(root)
                    .load(imageUrl)
                    .into(image)

                cancel.setOnClickListener {
                    onCancelClickListener.onCancelClick(imageUrl)
                }
            }
        }
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {

        fun onCancelClick(image: Uri)
    }
}