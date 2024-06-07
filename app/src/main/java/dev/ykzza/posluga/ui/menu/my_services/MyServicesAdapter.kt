package dev.ykzza.posluga.ui.menu.my_services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.databinding.ItemPostWithMenuBinding

class MyServicesAdapter(
    val onItemClickListener: OnItemClickListener,
): ListAdapter<Service, MyServicesAdapter.MyServiceViewHolder>(MyServiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyServicesAdapter.MyServiceViewHolder {
        val binding = ItemPostWithMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyServicesAdapter.MyServiceViewHolder, position: Int) {
        val service = getItem(position)
        holder.bind(service)
    }

    inner class MyServiceViewHolder(
        private val binding: ItemPostWithMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

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
                    onItemClickListener.onServiceClick(
                        service.serviceId
                    )
                }
                binding.menuImageView.setOnClickListener {
                    showPopupMenu(it, service.serviceId)
                }
            }
        }
    }

    private fun showPopupMenu(view: View, serviceId: String) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.post_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    onItemClickListener.onEditClick(serviceId)
                    true
                }
                R.id.delete -> {
                    onItemClickListener.onDeleteClick(serviceId)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    class MyServiceDiffCallback : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem.serviceId == newItem.serviceId
        }

        override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem == newItem
        }
    }


    interface OnItemClickListener {

        fun onServiceClick(serviceId: String)
        fun onEditClick(serviceId: String)
        fun onDeleteClick(serviceId: String)
    }
}