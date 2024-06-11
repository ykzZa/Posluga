package dev.ykzza.posluga.ui.menu.my_projects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Project
import dev.ykzza.posluga.databinding.ItemPostWithMenuBinding
import dev.ykzza.posluga.util.convertTimestampToFormattedDateTime

class MyProjectsAdapter(
    val onItemClickListener: OnItemClickListener,
): ListAdapter<Project, MyProjectsAdapter.MyProjectsViewHolder>(MyProjectsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProjectsAdapter.MyProjectsViewHolder {
        val binding = ItemPostWithMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyProjectsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyProjectsViewHolder, position: Int) {
        val project = getItem(position)
        holder.bind(project)
    }

    inner class MyProjectsViewHolder(
        private val binding: ItemPostWithMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(project: Project) {
            binding.apply {
                if(project.images.isNotEmpty()) {
                    Glide.with(root)
                        .load(project.images[0])
                        .centerCrop()
                        .into(previewImage)
                } else {
                    Glide.with(root)
                        .load(R.drawable.baseline_image_24)
                        .into(previewImage)
                }
                titleTextView.text = project.title
                geoTextView.text = project.city
                dateTextView.text = convertTimestampToFormattedDateTime(
                    project.date.seconds
                ).dropLast(6)
                timeTextView.text = convertTimestampToFormattedDateTime(
                    project.date.seconds
                ).takeLast(5)
                if(project.price == 0) {
                    priceTextView.text = "Договірна"
                } else {
                    priceTextView.text = "${project.price} hrn"
                }
                root.setOnClickListener {
                    onItemClickListener.onProjectClick(
                        project.projectId
                    )
                }
                binding.menuImageView.setOnClickListener {
                    showPopupMenu(it, project.projectId)
                }
            }
        }
    }

    private fun showPopupMenu(view: View, projectId: String) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.post_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    onItemClickListener.onEditClick(projectId)
                    true
                }
                R.id.delete -> {
                    onItemClickListener.onDeleteClick(projectId)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    class MyProjectsDiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.projectId == newItem.projectId
        }

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem == newItem
        }
    }


    interface OnItemClickListener {

        fun onProjectClick(projectId: String)
        fun onEditClick(projectId: String)
        fun onDeleteClick(projectId: String)
    }
}