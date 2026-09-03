package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object FocusNotificationHelper {

    const val CHANNEL_ID_ALERTS = "focus_alerts_channel"
    const val CHANNEL_ID_TIMER = "focus_timer_channel"
    const val CHANNEL_ID_SECURITY = "focus_security_channel"
    const val CHANNEL_ID_REMINDERS = "focus_reminders_channel"

    private const val NOTIFICATION_ID_TEST = 1001
    private const val NOTIFICATION_ID_BLOCK = 1002
    private const val NOTIFICATION_ID_TIMER = 1003
    private const val NOTIFICATION_ID_SECURITY = 1004
    private const val NOTIFICATION_ID_REMINDER = 1005

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // 1. Blocking Alerts Channel
            val alertsChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "নিরাপত্তা ও ব্লকিং সতর্কতা",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "অ্যাপ ও ক্ষতিকারক সাইট ব্লকিংয়ের সাথে সাথে অ্যালার্ট প্রদান করে"
                enableVibration(true)
            }

            // 2. Timer Channel
            val timerChannel = NotificationChannel(
                CHANNEL_ID_TIMER,
                "ফোকাস টাইমার ও স্ট্যাটাস",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "চলমান ফোকাস সেশনের সময় ও অগ্রগতির তথ্য"
                enableVibration(false)
            }

            // 3. Security Channel
            val securityChannel = NotificationChannel(
                CHANNEL_ID_SECURITY,
                "নিরাপত্তা ও পিন সতর্কতা",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "ভুল পিন চেষ্টা এবং নিরাপত্তা সংক্রান্ত নোটিফিকেশন"
                enableVibration(true)
            }

            // 4. Motivation & Reminders Channel
            val remindersChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "দৈনিক মোটিভেশন ও রিমাইন্ডার",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "দৈনিক লক্ষ্য ও অভ্যাস সংক্রান্ত নোটিফিকেশন"
                enableVibration(false)
            }

            notificationManager.createNotificationChannels(
                listOf(alertsChannel, timerChannel, securityChannel, remindersChannel)
            )
        }
    }

    private fun getLaunchIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    fun sendBlockAlertNotification(context: Context, appName: String, reason: String = "") {
        if (!FocusPermissionHelper.isNotificationPermissionGranted(context)) return
        initNotificationChannels(context)

        val pendingIntent = getLaunchIntent(context)

        val title = if (appName.contains("প্রতিহত") || appName.contains("ব্লক") || appName.startsWith("🛡️") || appName.contains("Shield")) {
            appName
        } else {
            "🛡️ $appName প্রতিহত করা হয়েছে"
        }

        val fullText = if (reason.isNotBlank()) {
            if (reason.contains("সক্রিয়") || reason.contains("শিল্ড")) reason else "ফোকাস শিল্ড সক্রিয় রয়েছে। ($reason)"
        } else {
            "ফোকাস শিল্ড আপনার একাগ্রতা বজায় রাখতে এই অ্যাপটি সাময়িকভাবে বন্ধ করেছে।"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(if (reason.isNotBlank()) reason else fullText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(fullText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BLOCK, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun sendTimerNotification(context: Context, title: String, message: String, isOngoing: Boolean = false) {
        if (!FocusPermissionHelper.isNotificationPermissionGranted(context)) return
        initNotificationChannels(context)

        val pendingIntent = getLaunchIntent(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TIMER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(isOngoing)
            .setContentIntent(pendingIntent)
            .setAutoCancel(!isOngoing)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_TIMER, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun sendSecurityNotification(context: Context, title: String, message: String) {
        if (!FocusPermissionHelper.isNotificationPermissionGranted(context)) return
        initNotificationChannels(context)

        val pendingIntent = getLaunchIntent(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_SECURITY)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_SECURITY, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun sendTestNotification(context: Context) {
        if (!FocusPermissionHelper.isNotificationPermissionGranted(context)) return
        initNotificationChannels(context)

        val pendingIntent = getLaunchIntent(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🛡️ Focus Shield টেস্ট নোটিফিকেশন")
            .setContentText("আপনার নোটিফিকেশন সিস্টেম সফলভাবে সক্রিয় ও কার্যকর রয়েছে!")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Focus Shield নোটিফিকেশন সার্ভিস সক্রিয় রয়েছে। ব্লকিং অ্যালার্ট, ফোকাস টাইমার আপডেট এবং নিরাপত্তা সতর্কতা এখন থেকে আপনার ডিভাইস বার-এ পাওয়া যাবে।"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_TEST, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun cancelNotification(context: Context, id: Int) {
        try {
            NotificationManagerCompat.from(context).cancel(id)
        } catch (e: Exception) {
            // Ignore
        }
    }
}
