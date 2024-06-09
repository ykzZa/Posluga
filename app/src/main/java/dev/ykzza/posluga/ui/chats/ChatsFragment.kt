package dev.ykzza.posluga.ui.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Chat
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.databinding.FragmentChatsBinding
import dev.ykzza.posluga.ui.home.reviews.ReviewViewModel
import dev.ykzza.posluga.ui.home.reviews.ReviewsAdapter
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showView

@AndroidEntryPoint
class ChatsFragment : Fragment(), ChatAdapter.OnItemClickListener {

    private var _binding: FragmentChatsBinding? = null
    private val binding: FragmentChatsBinding
        get() = _binding ?: throw RuntimeException("FragmentChatsBinding can't be null")

    private lateinit var viewModel: ChatViewModel
    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val recyclerViewAdapter by lazy {
        ChatAdapter(
            this
        )
    }

    private lateinit var chats: List<Chat>
    private lateinit var users: List<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        binding.recyclerViewChats.adapter = recyclerViewAdapter
        observeViewModel()
        if (firebaseAuth.uid != null) {
            viewModel.getChats(
                firebaseAuth.uid ?: ""
            )
        } else {
            binding.apply {
                errorImageView.showView()
                errorTextView.text = "You need to login!"
                errorTextView.showView()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.chatsUserPairs.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                hideUi()
                binding.apply {
                    noDataImageView.showView()
                    noDataTextView.showView()
                }
            } else {
                showUi()
                recyclerViewAdapter.submitList(it)
            }
        }
    }

    private fun hideUi() {
        binding.apply {
            recyclerViewChats.hideView()
            errorImageView.hideView()
            errorTextView.hideView()
            noDataImageView.hideView()
            noDataTextView.hideView()
        }
    }

    private fun showUi() {
        binding.apply {
            recyclerViewChats.showView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onChatClick(chatId: String) {
        val action = ChatsFragmentDirections.actionChatsFragmentToOpenedChatFragment(
            chatId, firebaseAuth.uid ?: ""
        )
        findNavController().navigate(action)
    }
}