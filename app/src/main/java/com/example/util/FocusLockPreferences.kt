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

    fun saveRoutines(routines: List<com.example.data.FocusRoutine>) {
        val jsonArray = org.json.JSONArray()
        for (r in routines) {
            val obj = org.json.JSONObject()
            obj.put("id", r.id)
            obj.put("titleBangla", r.titleBangla)
            obj.put("titleEnglish", r.titleEnglish)
            obj.put("timeRange", r.timeRange)
            obj.put("startTime", r.startTime)
            obj.put("endTime", r.endTime)
            obj.put("durationText", r.durationText)
            obj.put("activeDaysBangla", r.activeDaysBangla)
            obj.put("activeDaysEnglish", r.activeDaysEnglish)
            obj.put("targetedAppsBangla", r.targetedAppsBangla)
            obj.put("targetedAppsEnglish", r.targetedAppsEnglish)
            obj.put("colorHex", r.colorHex)
            obj.put("iconType", r.iconType)
            obj.put("blockShorts", r.blockShorts)
            obj.put("blockWebsites", r.blockWebsites)
            obj.put("isStrict", r.isStrict)
            obj.put("isEnabled", r.isEnabled)
            obj.put("isActiveNow", r.isActiveNow)
            jsonArray.put(obj)
        }
        prefs.edit().putString("saved_routines", jsonArray.toString()).apply()
    }

    fun getRoutines(): List<com.example.data.FocusRoutine> {
        val jsonString = prefs.getString("saved_routines", null)
        val list = mutableListOf<com.example.data.FocusRoutine>()
        if (jsonString.isNullOrEmpty()) return list
        try {
            val jsonArray = org.json.JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    com.example.data.FocusRoutine(
                        id = obj.getString("id"),
                        titleBangla = obj.getString("titleBangla"),
                        titleEnglish = obj.getString("titleEnglish"),
                        timeRange = obj.getString("timeRange"),
                        startTime = obj.getString("startTime"),
                        endTime = obj.getString("endTime"),
                        durationText = obj.getString("durationText"),
                        activeDaysBangla = obj.getString("activeDaysBangla"),
                        activeDaysEnglish = obj.getString("activeDaysEnglish"),
                        targetedAppsBangla = obj.getString("targetedAppsBangla"),
                        targetedAppsEnglish = obj.getString("targetedAppsEnglish"),
                        colorHex = obj.getString("colorHex"),
                        iconType = obj.getString("iconType"),
                        blockShorts = obj.getBoolean("blockShorts"),
                        blockWebsites = obj.getBoolean("blockWebsites"),
                        isStrict = obj.getBoolean("isStrict"),
                        isEnabled = obj.getBoolean("isEnabled"),
                        isActiveNow = obj.getBoolean("isActiveNow")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
