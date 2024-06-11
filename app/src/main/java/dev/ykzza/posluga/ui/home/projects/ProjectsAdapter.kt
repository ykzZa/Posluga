package dev.ykzza.posluga.ui.home.projects

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Project
import dev.ykzza.posluga.databinding.ItemPostBinding
import dev.ykzza.posluga.util.convertTimestampToFormattedDateTime
import dev.ykzza.posluga.util.getStringArrayEnglish
import dev.ykzza.posluga.util.getSystemLanguage

class ProjectsAdapter(
    val onProjectClickListener: OnItemClickListener,
    val context: Context
) : ListAdapter<Project, ProjectsAdapter.ProjectViewHolder>(ProjectDiffCallback()) {

    private val cities = arrayOf(
        R.array.avtonomna_respublika_krym_cities,
        R.array.vinnytska_cities,
        R.array.volynska_cities,
        R.array.dnipropetrovska_cities,
        R.array.donetska_cities,
        R.array.zhytomyrska_cities,
        R.array.zakarpatska_cities,
        R.array.zaporizka_cities,
        R.array.ivano_frankivska_cities,
        R.array.kyivska_cities,
        R.array.kirovohradska_cities,
        R.array.luhanska_cities,
        R.array.lvivska_cities,
        R.array.mykolaivska_cities,
        R.array.odeska_cities,
        R.array.poltavska_cities,
        R.array.rivnenska_cities,
        R.array.sumska_cities,
        R.array.ternopilska_cities,
        R.array.kharkivska_cities,
        R.array.khersonska_cities,
        R.array.khmelnytska_cities,
        R.array.cherkaska_cities,
        R.array.chernivetska_cities,
        R.array.chernihivska_cities
    )

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
                } else {
                    Glide.with(root)
                        .load(R.drawable.baseline_image_24)
                        .into(previewImage)
                }
                titleTextView.text = project.title
                if(getSystemLanguage() == "en") {
                    geoTextView.text = project.city
                } else {
                    val stateIndex =
                        getStringArrayEnglish(
                            context,
                            R.array.states
                        ).indexOf(project.state)
                    val citiesArray = cities[stateIndex]
                    val cityIndex =
                        getStringArrayEnglish(
                            context,
                            citiesArray
                        ).indexOf(project.city)
                    val city = context.resources.getStringArray(citiesArray)[cityIndex]
                    geoTextView.text = city
                }

                dateTextView.text = convertTimestampToFormattedDateTime(
                    project.date.seconds
                ).dropLast(6)
                timeTextView.text = convertTimestampToFormattedDateTime(
                    project.date.seconds
                ).takeLast(5)
                if(project.price == 0) {
                    priceTextView.text =  context.getString(R.string.tradeble_price)
                } else {
                    if(getSystemLanguage() == "en") {
                        priceTextView.text = "${project.price} hrn"
                    } else {
                        priceTextView.text = "${project.price} грн"
                    }
                }
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