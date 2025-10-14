package com.example.soulmatchapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: UserAdapter
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        adapter = UserAdapter(userList) { selectedUser ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("receiverId", selectedUser.uid)
            intent.putExtra("receiverName", selectedUser.name)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        loadUsers()
    }

    private fun loadUsers() {
        val currentUserId = auth.currentUser?.uid
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                userList.clear()
                for (doc in documents) {
                    if (doc.id != currentUserId) {
                        val user = doc.toObject(User::class.java).copy(uid = doc.id)
                        userList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e -> e.printStackTrace() }
    }
}
