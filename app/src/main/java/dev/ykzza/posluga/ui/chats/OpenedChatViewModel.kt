package dev.ykzza.posluga.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.Chat
import dev.ykzza.posluga.data.entities.Message
import dev.ykzza.posluga.data.repository.ChatRepository
import dev.ykzza.posluga.util.UiState
import javax.inject.Inject

@HiltViewModel
class OpenedChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chat = MutableLiveData<Chat?>()
    val chat: LiveData<Chat?>
        get() = _chat

    private val _chatExist = MutableLiveData<UiState<Boolean>>()
    val chatExist: LiveData<UiState<Boolean>>
        get() = _chatExist

    private val _messages = MutableLiveData<UiState<List<Message>>>()
    val messages: LiveData<UiState<List<Message>>>
        get() = _messages



    fun getChat(
        chatId: String
    ) {
        chatRepository.getChatById(
            chatId
        ) {
            _chat.value = it
        }
    }

    fun getChatByMembers(
        members: List<String>
    ) {
        chatRepository.getChatByMembers(
            members
        ) {
            _chat.value = it
        }
    }

    fun checkChatExist(
        members: List<String>
    ) {
        _chatExist.value = UiState.Loading
        chatRepository.checkChatExist(
            members
        ) {
            _chatExist.value = it
        }
    }

    fun sendMessage(
        message: Message,
        members: List<String>,
    ) {
        if(_chat.value == null) {
            val chat = Chat(
                "",
                members
            )
            chatRepository.createChat(
                chat
            ) {
                _chat.value = it
                if(it != null) {
                    chatRepository.sendMessage(
                        message,
                        it.chatId ,
                    ) {}
                }
            }
        } else {
            chatRepository.sendMessage(
                message,
                _chat.value!!.chatId ,
            ) {}
        }
    }

    fun loadMessages(
        chatId: String
    ) {
        _messages.value = UiState.Loading
        chatRepository.loadMessages(
            chatId
        ) {
            _messages.value = it
        }
    }
}