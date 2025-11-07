package com.example.soulmatchapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.soulmatchapp.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🎯 Send test notification
        binding.btnSendNotification.setOnClickListener {
            NotificationHelper.showNotification(
                context = this,
                title = "💖 SoulMatch Notification",
                message = "Someone liked your profile! 💌"
            )
        }
    }
}
