package com.example.soulmatchapp

import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var receiverId: String
    private lateinit var receiverName: String
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        receiverId = intent.getStringExtra("receiverId") ?: ""
        receiverName = intent.getStringExtra("receiverName") ?: "User"

        findViewById<TextView>(R.id.textReceiverName).text = receiverName

        recyclerView = findViewById(R.id.recyclerChat)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(messages, auth.currentUser?.uid ?: "")
        recyclerView.adapter = chatAdapter

        messageInput = findViewById(R.id.editMessage)
        sendButton = findViewById(R.id.buttonSend)

        sendButton.setOnClickListener { sendMessage() }

        listenForMessages()
    }

    private fun sendMessage() {
        val text = messageInput.text.toString().trim()
        val senderId = auth.currentUser?.uid ?: return
        if (text.isEmpty()) return

        val message = hashMapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("messages").add(message)
        messageInput.setText("")
    }

    private fun listenForMessages() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("messages")
            .whereIn("senderId", listOf(currentUserId, receiverId))
            .whereIn("receiverId", listOf(currentUserId, receiverId))
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        val msg = doc.toObject(ChatMessage::class.java)
                        if (msg != null &&
                            ((msg.senderId == currentUserId && msg.receiverId == receiverId) ||
                                    (msg.senderId == receiverId && msg.receiverId == currentUserId))
                        ) {
                            messages.add(msg)
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }
    }
}


