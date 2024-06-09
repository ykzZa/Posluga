package dev.ykzza.posluga.ui.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Chat
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.databinding.ItemChatBinding

class ChatAdapter(
    val onChatClick: OnItemClickListener
) : ListAdapter<Pair<Chat, User>, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatAdapter.ChatViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ChatViewHolder, position: Int) {
        val chat = getItem(position).first
        val user = getItem(position).second
        holder.bind(chat, user)
    }

    inner class ChatViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat, user: User) {
            binding.apply {
                userNameTextView.text = user.nickname
                Glide.with(root)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.ic_profile_grey)
                    .into(previewImage)
                root.setOnClickListener {
                    onChatClick.onChatClick(
                        chat.chatId
                    )
                }
            }
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<Pair<Chat, User>>() {

        override fun areItemsTheSame(
            oldItem: Pair<Chat, User>,
            newItem: Pair<Chat, User>
        ): Boolean {

            return oldItem.first.chatId == newItem.first.chatId
        }

        override fun areContentsTheSame(
            oldItem: Pair<Chat, User>,
            newItem: Pair<Chat, User>
        ): Boolean {

            return oldItem == newItem
        }
    }

    interface OnItemClickListener {

        fun onChatClick(chatId: String)
    }
}