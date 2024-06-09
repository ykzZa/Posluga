package dev.ykzza.posluga.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import dev.ykzza.posluga.data.entities.Chat
import dev.ykzza.posluga.data.entities.Message
import dev.ykzza.posluga.util.Constants
import dev.ykzza.posluga.util.UiState

class ChatRepositoryImpl(
    private val db: FirebaseFirestore
) : ChatRepository {

    override fun sendMessage(
        message: Message,
        chatId: String,
        result: (UiState<String>) -> Unit
    ) {

        db.collection(Constants.CHATS_COLLECTION).document(chatId)
            .collection(Constants.MESSAGES_COLLECTION)
            .add(message)
    }

    override fun deleteChat(chatId: String, result: (UiState<String>) -> Unit) {
        db.collection(Constants.CHATS_COLLECTION).document(chatId)
            .delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        "Chat has been deleted"
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }

    override fun getUserChats(
        userId: String,
        result: (UiState<List<Chat>>) -> Unit
    ) {
        db.collection(Constants.CHATS_COLLECTION)
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val chats = querySnapshot.toObjects(Chat::class.java)
                result.invoke(
                    UiState.Success(
                        chats
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }

    override fun getChatById(chatId: String, result: (UiState<Chat>) -> Unit) {
        db.collection(Constants.CHATS_COLLECTION)
            .document(chatId)
            .get()
            .addOnSuccessListener {
                val chat = it.toObject(Chat::class.java)
                if (chat != null) {
                    result.invoke(
                        UiState.Success(
                            chat
                        )
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }

    override fun createChat(chat: Chat, result: (UiState<String>) -> Unit) {
        db.collection(Constants.CHATS_COLLECTION)
            .document(chat.chatId)
            .set(chat)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        "Chat created"
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }
}