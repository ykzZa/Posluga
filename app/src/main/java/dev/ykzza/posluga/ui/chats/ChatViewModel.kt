package dev.ykzza.posluga.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.Chat
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.data.repository.ChatRepository
import dev.ykzza.posluga.data.repository.UserRepository
import dev.ykzza.posluga.util.UiState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _chats = MutableLiveData<List<Chat>>()

    private val _users = MutableLiveData<List<User>>()

    private val _chatsUserPairs = MutableLiveData<List<Pair<Chat, User>>>()
    val chatsUserPairs: LiveData<List<Pair<Chat, User>>>
        get() = _chatsUserPairs

    fun getChats(
        userId: String
    ) {
        repository.getUserChats(
            userId
        ) {
            _chats.value = it
            if(it.isNotEmpty()) {
                getUsers(
                    it.flatMap {  membersIt ->
                        membersIt.members
                    }.filter {  filterIt ->
                        filterIt != userId
                    }
                ) {
                    val zipped = _chats.value?.zip(_users.value!!)
                    _chatsUserPairs.value = zipped ?: emptyList()
                }
            } else {
                _chatsUserPairs.value = emptyList()
            }
        }
    }

    private fun getUsers(
        usersId: List<String>,
        onEnd: () -> Unit
    ) {
        viewModelScope.launch {
            userRepository.getUsersListByIds(
                usersId
            ) {
                _users.value = it
                onEnd.invoke()
            }
        }
    }
}