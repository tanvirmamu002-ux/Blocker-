package com.example.data

import java.util.UUID

data class AppNotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,
    val timeAgo: String,
    val type: String = "alert", // "alert", "shield", "security", "timer"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
