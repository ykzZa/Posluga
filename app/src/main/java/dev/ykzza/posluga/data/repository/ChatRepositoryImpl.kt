package dev.ykzza.posluga.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
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
        result: (List<Chat>) -> Unit
    ) {
        db.collection(Constants.CHATS_COLLECTION)
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val chats = querySnapshot.toObjects(Chat::class.java)
                result.invoke(
                    chats
                )
            }
            .addOnFailureListener {
                result.invoke(
                    emptyList()
                )
            }
    }

    override fun getChatById(chatId: String, result: (Chat?) -> Unit) {
        db.collection(Constants.CHATS_COLLECTION)
            .document(chatId)
            .get()
            .addOnSuccessListener {
                val chat = it.toObject(Chat::class.java)
                result.invoke(chat)
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun createChat(chat: Chat, result: (Chat?) -> Unit) {
        chat.members = chat.members.sorted()
        val document = db.collection(Constants.CHATS_COLLECTION).document()
        chat.chatId = document.id
        document
            .set(chat)
            .addOnSuccessListener {
                result.invoke(
                    chat
                )
            }
            .addOnFailureListener {
                result.invoke(
                    null
                )
            }
    }

    override fun checkChatExist(members: List<String>, result: (UiState<Boolean>) -> Unit) {
        db.collection(Constants.CHATS_COLLECTION)
            .whereEqualTo("members", members.sorted())
            .get()
            .addOnSuccessListener { querySnapshot ->
                result.invoke(
                    UiState.Success(
                        !querySnapshot.isEmpty
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

    override fun getChatByMembers(members: List<String>, result: (Chat?) -> Unit) {
        db.collection(Constants.CHATS_COLLECTION)
            .whereEqualTo("members", members.sorted())
            .get()
            .addOnSuccessListener { querySnapshot ->
                val chat = querySnapshot.documents.firstOrNull()?.toObject(Chat::class.java)
                result.invoke(
                    chat
                )
            }
            .addOnFailureListener {
                result.invoke(
                    null
                )
            }
    }

    override fun loadMessages(chatId: String, result: (UiState<List<Message>>) -> Unit) {
        db.collection(Constants.CHATS_COLLECTION).document(chatId)
            .collection(Constants.MESSAGES_COLLECTION)
            .orderBy("date")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(
                        UiState.Error(
                            e.localizedMessage ?: "Oops, something went wrong"
                        )
                    )
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) }
                    ?: emptyList()
                result.invoke(
                    UiState.Success(
                        messages
                    )
                )
            }
    }
}