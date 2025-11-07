package com.example.soulmatchapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yourapp.model.AppDatabase
import com.example.yourapp.model.Profile
import kotlinx.coroutines.launch

class SavedProfilesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedProfilesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_profiles)

        recyclerView = findViewById(R.id.recyclerViewProfiles)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SavedProfilesAdapter()
        recyclerView.adapter = adapter

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            val profiles = db.profileDao().getAllProfiles()
            adapter.submitList(profiles)
        }
    }
}
