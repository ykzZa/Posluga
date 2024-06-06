package dev.ykzza.posluga.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ykzza.posluga.databinding.ItemImagePostBinding

class SliderAdapter(
    private val context: Context,
    private var imageList: List<String>
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(val binding: ItemImagePostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemImagePostBinding.inflate(LayoutInflater.from(context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val binding = holder.binding
        val currentImage = imageList[position]
        Glide.with(context)
            .load(currentImage)
            .centerInside()
            .into(binding.image)

    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}