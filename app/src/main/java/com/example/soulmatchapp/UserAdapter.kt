package com.example.soulmatchapp

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val users: List<User>,
    private val onChatClicked: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.userImage)
        val name: TextView = view.findViewById(R.id.userName)
        val details: TextView = view.findViewById(R.id.userDetails)
        val chatBtn: Button = view.findViewById(R.id.chatButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_card, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.name ?: "Unknown"
        holder.details.text = "${user.gender ?: "N/A"} • ${user.interests?.joinToString(", ") ?: ""}"

        user.profileImage?.let {
            try {
                val bytes = Base64.decode(it, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.image.setImageBitmap(bitmap)
            } catch (_: Exception) { }
        }

        holder.chatBtn.setOnClickListener { onChatClicked(user) }
    }
}
