package com.example.soulmatchapp

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "SoulMatch 💞"
        val body = remoteMessage.notification?.body ?: "You have a new message!"

        NotificationHelper.showNotification(applicationContext, title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Log or send token to your backend later if needed
    }
}
