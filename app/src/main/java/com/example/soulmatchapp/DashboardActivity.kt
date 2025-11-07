package com.example.soulmatchapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.soulmatchapp.api.RetrofitClient
import com.example.soulmatchapp.databinding.ActivityDashboardBinding
import com.example.soulmatchapp.model.Quote
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth

    private val NOTIFICATION_PERMISSION_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // ✅ Ask user for notification permission (Android 13+)
        requestNotificationPermission()

        // 🔁 Load quote
        loadLoveQuote()

        binding.btnFindSoulmate.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnApiDemo.setOnClickListener {
            loadLoveQuote()
        }

        binding.btnOffline.setOnClickListener {
            startActivity(Intent(this, SavedProfilesActivity::class.java))
        }

        binding.btnNotifications.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnRefreshQuote.setOnClickListener {
            loadLoveQuote()
        }
    }

    // ✅ Ask for notification permission only when needed
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST
                )
            }
        }
    }

    // 🧠 Fetch love quote from API
    private fun loadLoveQuote() {
        binding.quoteText.animate().alpha(0f).setDuration(300).start()
        binding.quoteAuthor.animate().alpha(0f).setDuration(300).start()

        binding.quoteText.text = "Loading love quote..."
        binding.quoteAuthor.text = ""

        RetrofitClient.instance.getRandomLoveQuote().enqueue(object : Callback<Quote> {
            override fun onResponse(call: Call<Quote>, response: Response<Quote>) {
                if (response.isSuccessful) {
                    val quote = response.body()
                    binding.quoteText.text = "\"${quote?.content ?: "No quote available"}\""
                    binding.quoteAuthor.text = "- ${quote?.author ?: "Unknown"}"
                } else {
                    binding.quoteText.text = "Could not load love quote 😔"
                    binding.quoteAuthor.text = ""
                }

                binding.quoteText.animate().alpha(1f).setDuration(500).start()
                binding.quoteAuthor.animate().alpha(1f).setDuration(500).start()
            }

            override fun onFailure(call: Call<Quote>, t: Throwable) {
                binding.quoteText.text = "Network error 💔"
                binding.quoteAuthor.text = ""
                binding.quoteText.animate().alpha(1f).setDuration(500).start()
                binding.quoteAuthor.animate().alpha(1f).setDuration(500).start()
            }
        })
    }
}
