package com.example.soulmatchapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        db = FirebaseFirestore.getInstance()

        val userId = intent.getStringExtra("uid") ?: return
        val image = findViewById<ImageView>(R.id.imageProfile)
        val name = findViewById<TextView>(R.id.textName)
        val gender = findViewById<TextView>(R.id.textGender)
        val interests = findViewById<TextView>(R.id.textInterests)
        val matchPercent = findViewById<TextView>(R.id.textMatchPercent)
        val chatButton = findViewById<Button>(R.id.buttonStartChat)

        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)
                    if (user != null) {
                        name.text = user.name
                        gender.text = user.gender
                        interests.text = "Interests: ${user.interests.joinToString(", ")}"

                        // Simple fake “match” percentage
                        val randomMatch = (70..98).random()
                        matchPercent.text = "Match Score: $randomMatch%"

                        user.profileImage?.let {
                            try {
                                val bytes = Base64.decode(it, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                image.setImageBitmap(bitmap)
                            } catch (_: Exception) { }
                        }

                        chatButton.setOnClickListener {
                            val intent = Intent(this, ChatActivity::class.java)
                            intent.putExtra("receiverId", user.uid)
                            intent.putExtra("receiverName", user.name)
                            startActivity(intent)
                        }
                    }
                }
            }
    }
}
