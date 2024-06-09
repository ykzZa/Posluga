package dev.ykzza.posluga.ui.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Message
import dev.ykzza.posluga.databinding.ItemMessage2Binding
import dev.ykzza.posluga.databinding.ItemMessageBinding
import dev.ykzza.posluga.util.convertTimestampToFormattedDateTime
import java.util.Date

class MessageAdapter(
    private val userId: String
) :
    ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER_MAIN = 1
        private const val VIEW_TYPE_USER_SECOND = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if(getItem(position).senderId == userId) {
            VIEW_TYPE_USER_MAIN
        } else {
            VIEW_TYPE_USER_SECOND
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER_SECOND) {
            val binding = ItemMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            MainUserMessageViewHolder(binding)
        } else {
            val binding = ItemMessage2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            SecondUserMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        if (holder is MainUserMessageViewHolder) {
            holder.bind(message)
        } else if (holder is SecondUserMessageViewHolder) {
            holder.bind(message)
        }
    }

    inner class MainUserMessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.messageTextView.text = message.content
            binding.dateTextView.text = convertTimestampToFormattedDateTime(message.date.seconds)
        }
    }


    inner class SecondUserMessageViewHolder(private val binding: ItemMessage2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.messageTextView.text = message.content
            binding.dateTextView.text = convertTimestampToFormattedDateTime(message.date.seconds)
        }
    }

    private class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}