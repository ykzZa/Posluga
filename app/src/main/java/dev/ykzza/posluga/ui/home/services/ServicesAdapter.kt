package dev.ykzza.posluga.ui.home.services

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.databinding.ItemPostBinding

class ServicesAdapter(
    val onServiceClickListener: OnItemClickListener
) : ListAdapter<Service, ServicesAdapter.ServiceViewHolder>(ServiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = getItem(position)
        holder.bind(service)
    }

    inner class ServiceViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(service: Service) {
            binding.apply {
                if(service.images.isNotEmpty()) {
                    Glide.with(root)
                        .load(service.images[0])
                        .centerCrop()
                        .into(previewImage)
                } else {
                    Glide.with(root)
                        .load(R.drawable.baseline_image_24)
                        .into(previewImage)
                }
                titleTextView.text = service.title
                geoTextView.text = service.city
                dateTextView.text = service.date.dropLast(6)
                timeTextView.text = service.date.takeLast(5)
                if(service.price == 0) {
                    priceTextView.text = "Договірна"
                } else {
                    priceTextView.text = "${service.price} hrn"
                }


                root.setOnClickListener {
                    onServiceClickListener.onItemClick(
                        service.serviceId
                    )
                }
            }
        }
    }

    class ServiceDiffCallback : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem.serviceId == newItem.serviceId
        }

        override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {

        fun onItemClick(serviceId: String)
    }
}