package com.laurentdarl.letschatapplication.domain.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.laurentdarl.letschatapplication.R
import com.laurentdarl.letschatapplication.presentation.activities.MainActivity
import kotlin.random.Random

class FirebaseService: FirebaseMessagingService() {
    private val CHANNEL_ID = "my_notification_channel"
    companion object {
        var sharedPref: SharedPreferences? = null
        var token: String?
        get() {
            return sharedPref!!.getString("token", "")
        }
        set(value) {
            sharedPref!!.edit()!!.putString("token", value)!!.apply()
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

//        val intent = Intent(this, MainActivity::class.java)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(p0.data["title"])
            .setContentText(p0.data["message"])
            .setSmallIcon(R.drawable.ic_chat)
            .setColor(getColor(R.color.blue))
            .setSubText(p0.data["message"])
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.ic_chat
                )
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("New messages in " + p0.senderId.toString())
                    .setSummaryText(p0.data["message"])
            )
            .setNumber(1)
            .setContentIntent(pendingIntent(this))
            .build()

        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "Let's Chat"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description="My Chat app trial"
            enableLights(true)
            lightColor = Color.WHITE
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun pendingIntent(context: Context): PendingIntent {
        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.chatsFragment)
            .createPendingIntent()
    }
}