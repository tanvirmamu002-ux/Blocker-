package com.example.data

enum class FocusLockState {
    IDLE,
    STARTING,
    ACTIVE,
    EMERGENCY_REQUEST,
    COMPLETED,
    CANCELLED
}

data class FocusLockConfig(
    val durationMinutes: Int = 25,
    val blockApps: Boolean = true,
    val blockShorts: Boolean = true,
    val blockWebsites: Boolean = true,
    val isStrict: Boolean = true,
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 0L,
    val emergencyReason: String? = null
)
