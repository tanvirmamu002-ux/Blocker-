package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.FocusLockConfig
import com.example.data.FocusLockState

class FocusLockPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "focus_shield_lock_prefs"
        private const val KEY_STATE = "focus_lock_state"
        private const val KEY_DURATION_MINUTES = "duration_minutes"
        private const val KEY_BLOCK_APPS = "block_apps"
        private const val KEY_BLOCK_SHORTS = "block_shorts"
        private const val KEY_BLOCK_WEBSITES = "block_websites"
        private const val KEY_IS_STRICT = "is_strict"
        private const val KEY_START_TIME_MS = "start_time_ms"
        private const val KEY_END_TIME_MS = "end_time_ms"
        private const val KEY_EMERGENCY_REASON = "emergency_reason"
        private const val KEY_COMPLETED_SESSION_COUNT = "completed_session_count"
        private const val KEY_TOTAL_SAVED_MINUTES = "total_saved_minutes"

        @Volatile
        private var INSTANCE: FocusLockPreferences? = null

        fun getInstance(context: Context): FocusLockPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FocusLockPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun saveFocusLockState(state: FocusLockState) {
        prefs.edit().putString(KEY_STATE, state.name).apply()
    }

    fun getFocusLockState(): FocusLockState {
        val stateName = prefs.getString(KEY_STATE, FocusLockState.IDLE.name) ?: FocusLockState.IDLE.name
        val state = try {
            FocusLockState.valueOf(stateName)
        } catch (e: Exception) {
            FocusLockState.IDLE
        }

        // Auto transition if active but time expired
        if (state == FocusLockState.ACTIVE || state == FocusLockState.EMERGENCY_REQUEST) {
            val endTime = prefs.getLong(KEY_END_TIME_MS, 0L)
            if (endTime > 0 && System.currentTimeMillis() >= endTime) {
                saveFocusLockState(FocusLockState.COMPLETED)
                return FocusLockState.COMPLETED
            }
        }
        return state
    }

    fun saveFocusLockConfig(config: FocusLockConfig) {
        prefs.edit()
            .putInt(KEY_DURATION_MINUTES, config.durationMinutes)
            .putBoolean(KEY_BLOCK_APPS, config.blockApps)
            .putBoolean(KEY_BLOCK_SHORTS, config.blockShorts)
            .putBoolean(KEY_BLOCK_WEBSITES, config.blockWebsites)
            .putBoolean(KEY_IS_STRICT, config.isStrict)
            .putLong(KEY_START_TIME_MS, config.startTimeMs)
            .putLong(KEY_END_TIME_MS, config.endTimeMs)
            .putString(KEY_EMERGENCY_REASON, config.emergencyReason)
            .apply()
    }

    fun getFocusLockConfig(): FocusLockConfig {
        return FocusLockConfig(
            durationMinutes = prefs.getInt(KEY_DURATION_MINUTES, 25),
            blockApps = prefs.getBoolean(KEY_BLOCK_APPS, true),
            blockShorts = prefs.getBoolean(KEY_BLOCK_SHORTS, true),
            blockWebsites = prefs.getBoolean(KEY_BLOCK_WEBSITES, true),
            isStrict = prefs.getBoolean(KEY_IS_STRICT, true),
            startTimeMs = prefs.getLong(KEY_START_TIME_MS, 0L),
            endTimeMs = prefs.getLong(KEY_END_TIME_MS, 0L),
            emergencyReason = prefs.getString(KEY_EMERGENCY_REASON, null)
        )
    }

    fun getRemainingTimeMs(): Long {
        val endTimeMs = prefs.getLong(KEY_END_TIME_MS, 0L)
        if (endTimeMs <= 0L) return 0L
        val remaining = endTimeMs - System.currentTimeMillis()
        return if (remaining > 0L) remaining else 0L
    }

    fun clearFocusLock() {
        prefs.edit()
            .putString(KEY_STATE, FocusLockState.IDLE.name)
            .putLong(KEY_START_TIME_MS, 0L)
            .putLong(KEY_END_TIME_MS, 0L)
            .apply()
    }

    fun recordCompletedSession(minutes: Int) {
        val currentCount = prefs.getInt(KEY_COMPLETED_SESSION_COUNT, 0)
        val currentSaved = prefs.getInt(KEY_TOTAL_SAVED_MINUTES, 0)
        prefs.edit()
            .putInt(KEY_COMPLETED_SESSION_COUNT, currentCount + 1)
            .putInt(KEY_TOTAL_SAVED_MINUTES, currentSaved + minutes)
            .apply()
    }

    fun getCompletedSessionCount(): Int = prefs.getInt(KEY_COMPLETED_SESSION_COUNT, 0)
    fun getTotalSavedMinutes(): Int = prefs.getInt(KEY_TOTAL_SAVED_MINUTES, 0)
}
