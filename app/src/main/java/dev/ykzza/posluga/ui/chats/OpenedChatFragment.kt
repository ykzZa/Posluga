package dev.ykzza.posluga.ui.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.data.entities.Message
import dev.ykzza.posluga.databinding.FragmentOpenedChatBinding
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.hideView
import dev.ykzza.posluga.util.showToast
import dev.ykzza.posluga.util.showView


@AndroidEntryPoint
class OpenedChatFragment : Fragment() {

    private var _binding: FragmentOpenedChatBinding? = null
    private val binding: FragmentOpenedChatBinding
        get() = _binding ?: throw RuntimeException("FragmentOpenedChatBinding can't be null")

    private lateinit var viewModel: OpenedChatViewModel
    private val args: OpenedChatFragmentArgs by navArgs()

    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val recyclerViewAdapter by lazy {
        MessageAdapter(
            firebaseAuth.uid ?: ""
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpenedChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[OpenedChatViewModel::class.java]
        binding.recyclerViewMessages.adapter = recyclerViewAdapter
        recyclerViewAdapter.registerAdapterDataObserver(
            ScrollToBottomObserver(
                binding.recyclerViewMessages,
                recyclerViewAdapter,
                LinearLayoutManager(requireContext())
            )
        )
        observeViewModel()
        setOnClickListeners()
        if (args.chatId == null) {
            viewModel.checkChatExist(
                listOf(args.userId, firebaseAuth.uid ?: "")
            )
        } else {
            Log.d("OpenedChatFragment", "getChatCalled")
            viewModel.getChat(
                args.chatId ?: ""
            )
        }
    }

    private fun observeViewModel() {
        viewModel.chatExist.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    Log.d("OpenedChatFragment", "chatExistError")
                    binding.apply {
                        progressBar.hideView()
                        errorImageView.showView()
                        errorTextView.showView()
                    }
                }

                UiState.Loading -> {
                    Log.d("OpenedChatFragment", "chatExistLoading")
                    hideUi()
                    binding.progressBar.showView()
                }

                is UiState.Success -> {
                    Log.d("OpenedChatFragment", "chatExistSuccess")
                    val chatExist = uiState.data
                    if (chatExist) {
                        viewModel.getChatByMembers(
                            listOf(args.userId, firebaseAuth.uid ?: "")
                        )
                    } else {
                        binding.apply {
                            progressBar.hideView()
                            noDataTextView.showView()
                            noDataImageView.showView()
                            editTextMessage.showView()
                            buttonSendMessage.showView()
                        }
                    }
                }
            }
        }
        viewModel.chat.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d("OpenedChatFragment", "loadCalled")
                viewModel.loadMessages(
                    it.chatId
                )
            }
        }
        viewModel.messages.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is UiState.Loading -> {
                    Log.d("OpenedChatFragment", "messagesLoading")
                    hideUi()
                    binding.progressBar.showView()
                }
                is UiState.Success -> {
                    Log.d("OpenedChatFragment", "messagesSuccess")
                    binding.apply {
                        progressBar.hideView()
                        editTextMessage.showView()
                        buttonSendMessage.showView()
                        recyclerViewMessages.showView()
                    }
                    recyclerViewAdapter.submitList(uiState.data)
                    binding.recyclerViewMessages.scrollToPosition(
                        recyclerViewAdapter.itemCount - 1
                    )
                    if(uiState.data.isEmpty()) {
                        binding.apply {
                            noDataImageView.showView()
                            noDataTextView.showView()
                        }
                    }
                }
                else -> {
                    Log.d("OpenedChatFragment", "messagesError")
                    binding.apply {
                        hideUi()
                        errorTextView.showView()
                        errorImageView.showView()
                    }
                }
            }
        }
    }

    fun setOnClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonSendMessage.setOnClickListener {
                buttonSendMessageClick()
            }
        }
    }

    fun hideUi() {
        binding.apply {
            progressBar.hideView()
            recyclerViewMessages.hideView()
            errorImageView.hideView()
            noDataImageView.hideView()
            errorTextView.hideView()
            noDataTextView.hideView()
            editTextMessage.hideView()
            buttonSendMessage.hideView()
        }
    }

    private fun buttonSendMessageClick() {
        if (binding.editTextMessage.text.isNotBlank()) {
            val message = Message(
                firebaseAuth.uid ?: "",
                binding.editTextMessage.text.toString(),
                Timestamp.now()
            )
            viewModel.sendMessage(
                message,
                listOf(args.userId, firebaseAuth.uid ?: "")
            )
            binding.editTextMessage.setText("")
        } else {
            showToast("Type a message!")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ScrollToBottomObserver(
        private val recycler: RecyclerView,
        private val adapter: MessageAdapter,
        private val manager: LinearLayoutManager
    ) : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            val count = adapter.itemCount
            val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
            val loading = lastVisiblePosition == -1
            val atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
            if (loading || atBottom) {
                recycler.scrollToPosition(positionStart)
            }
        }
    }
}