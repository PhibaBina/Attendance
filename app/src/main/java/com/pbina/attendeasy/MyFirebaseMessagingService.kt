package com.pbina.attendeasy

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if the message contains a notification payload
        remoteMessage.notification?.let {
            // Handle the notification message here
            Log.d("FCM", "Notification message: ${it.body}")
            showNotification(it.body ?: "New message!")
        }

        // Check if the message contains data payload
        remoteMessage.data.isNotEmpty().let {
            // Handle data message here (if necessary)
            Log.d("FCM", "Data message: ${remoteMessage.data}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle token refresh here
        Log.d("FCM", "New token: $token")
        // Save the new token if needed (e.g., for sending notifications to specific devices)
    }

    private fun showNotification(messageBody: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "default")
            .setContentTitle("New Notification")
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.notification)  // Replace with your own icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(0, notification)
    }
}
