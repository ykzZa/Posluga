package dev.ykzza.posluga.ui.home.services

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.databinding.ItemPostBinding
import dev.ykzza.posluga.util.convertTimestampToFormattedDateTime
import dev.ykzza.posluga.util.getStringArrayEnglish
import dev.ykzza.posluga.util.getSystemLanguage
import dev.ykzza.posluga.util.isEnglish
import javax.inject.Inject
import javax.inject.Named

class ServicesAdapter(
    val onServiceClickListener: OnItemClickListener,
    val context: Context
) : ListAdapter<Service, ServicesAdapter.ServiceViewHolder>(ServiceDiffCallback()) {

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
                if(getSystemLanguage() == "en") {
                    geoTextView.text = service.city
                } else {
                    val stateIndex =
                        getStringArrayEnglish(
                            context,
                            R.array.states
                        ).indexOf(service.state)
                    val citiesArray = cities[stateIndex]
                    val cityIndex =
                        getStringArrayEnglish(
                            context,
                            citiesArray
                        ).indexOf(service.city)
                    val city = context.resources.getStringArray(citiesArray)[cityIndex]
                    geoTextView.text = city
                }
                dateTextView.text = convertTimestampToFormattedDateTime(
                    service.date.seconds
                ).dropLast(6)
                timeTextView.text = convertTimestampToFormattedDateTime(
                    service.date.seconds
                ).takeLast(5)
                if(service.price == 0) {
                    if(getSystemLanguage() == "en") {
                        priceTextView.text = "Contract price"
                    } else {
                        priceTextView.text = "Договірна"
                    }
                } else {
                    if(getSystemLanguage() == "en") {
                        priceTextView.text = "${service.price} hrn"
                    } else {
                        priceTextView.text = "${service.price} грн"
                    }
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