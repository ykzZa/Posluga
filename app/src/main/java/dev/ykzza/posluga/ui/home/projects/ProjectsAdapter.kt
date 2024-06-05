package dev.ykzza.posluga.ui.home.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ykzza.posluga.data.entities.Project
import dev.ykzza.posluga.databinding.ItemPostBinding

class ProjectsAdapter(
    val onProjectClickListener: OnItemClickListener
) : ListAdapter<Project, ProjectsAdapter.ProjectViewHolder>(ProjectDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = getItem(position)
        holder.bind(project)
    }

    inner class ProjectViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(project: Project) {
            binding.apply {
                if(project.images.isNotEmpty()) {
                    Glide.with(root)
                        .load(project.images[0])
                        .centerCrop()
                        .into(previewImage)
                }
                titleTextView.text = project.title
                geoTextView.text = project.city
                dateTextView.text = project.date.dropLast(6)
                timeTextView.text = project.date.takeLast(5)
                priceTextView.text = "${project.price} hrn"

                root.setOnClickListener {
                    onProjectClickListener.onItemClick(
                        project.projectId
                    )
                }
            }
        }
    }

    class ProjectDiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.projectId == newItem.projectId
        }

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {

        fun onItemClick(projectId: String)
    }
}