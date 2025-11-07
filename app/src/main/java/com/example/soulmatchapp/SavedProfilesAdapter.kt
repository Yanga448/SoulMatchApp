package com.example.soulmatchapp



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yourapp.model.Profile

class SavedProfilesAdapter : RecyclerView.Adapter<SavedProfilesAdapter.ProfileViewHolder>() {

    private var profiles = listOf<Profile>()

    fun submitList(list: List<Profile>) {
        profiles = list
        notifyDataSetChanged()
    }

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.txtName)
        val age: TextView = itemView.findViewById(R.id.txtAge)
        val quote: TextView = itemView.findViewById(R.id.txtQuote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = profiles[position]
        holder.name.text = profile.name
        holder.age.text = "Age: ${profile.age}"
        holder.quote.text = "\"${profile.quote}\""
    }

    override fun getItemCount(): Int = profiles.size
}
