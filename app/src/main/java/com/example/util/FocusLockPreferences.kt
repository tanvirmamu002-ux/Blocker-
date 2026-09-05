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

        private const val KEY_ONE_TIME_BLOCK_PACKAGE = "one_time_block_package"
        private const val KEY_ONE_TIME_BLOCK_NAME = "one_time_block_name"
        private const val KEY_ONE_TIME_BLOCK_START_MS = "one_time_block_start_ms"
        private const val KEY_ONE_TIME_BLOCK_END_MS = "one_time_block_end_ms"

        private const val KEY_APP_THEME_MODE = "app_theme_mode"
        private const val KEY_SECURITY_PIN = "security_pin"
        private const val KEY_IS_PIN_CONFIGURED = "is_pin_configured"

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

    fun saveCustomDomains(domains: List<com.example.data.BlockedDomain>) {
        val jsonArray = org.json.JSONArray()
        for (d in domains) {
            val obj = org.json.JSONObject()
            obj.put("id", d.id)
            obj.put("domain", d.domain)
            obj.put("blockedCount", d.blockedCount)
            obj.put("isCustom", d.isCustom)
            obj.put("addedTimeAgo", d.addedTimeAgo)
            jsonArray.put(obj)
        }
        prefs.edit().putString("saved_custom_domains", jsonArray.toString()).apply()
    }

    fun getCustomDomains(): List<com.example.data.BlockedDomain> {
        val jsonString = prefs.getString("saved_custom_domains", null)
        val list = mutableListOf<com.example.data.BlockedDomain>()
        if (jsonString.isNullOrEmpty()) return list
        try {
            val jsonArray = org.json.JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    com.example.data.BlockedDomain(
                        id = obj.getString("id"),
                        domain = obj.getString("domain"),
                        blockedCount = obj.optInt("blockedCount", 0),
                        isCustom = obj.optBoolean("isCustom", true),
                        addedTimeAgo = obj.optString("addedTimeAgo", "যুক্ত")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveCustomKeywords(keywords: List<String>) {
        val jsonArray = org.json.JSONArray()
        for (k in keywords) {
            jsonArray.put(k)
        }
        prefs.edit().putString("saved_custom_keywords", jsonArray.toString()).apply()
    }

    fun getCustomKeywords(): List<String> {
        val jsonString = prefs.getString("saved_custom_keywords", null)
        val list = mutableListOf<String>()
        if (jsonString.isNullOrEmpty()) return list
        try {
            val jsonArray = org.json.JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveOneTimeBlock(packageName: String, appName: String, durationHours: Int = 3) {
        val now = System.currentTimeMillis()
        val durationMs = durationHours * 60 * 60 * 1000L
        prefs.edit()
            .putString(KEY_ONE_TIME_BLOCK_PACKAGE, packageName)
            .putString(KEY_ONE_TIME_BLOCK_NAME, appName)
            .putLong(KEY_ONE_TIME_BLOCK_START_MS, now)
            .putLong(KEY_ONE_TIME_BLOCK_END_MS, now + durationMs)
            .apply()
    }

    fun clearOneTimeBlock() {
        prefs.edit()
            .remove(KEY_ONE_TIME_BLOCK_PACKAGE)
            .remove(KEY_ONE_TIME_BLOCK_NAME)
            .remove(KEY_ONE_TIME_BLOCK_START_MS)
            .remove(KEY_ONE_TIME_BLOCK_END_MS)
            .apply()
    }

    fun getOneTimeBlockPackage(): String? {
        val pkg = prefs.getString(KEY_ONE_TIME_BLOCK_PACKAGE, null) ?: return null
        val endTime = prefs.getLong(KEY_ONE_TIME_BLOCK_END_MS, 0L)
        if (endTime <= 0L || System.currentTimeMillis() >= endTime) {
            clearOneTimeBlock()
            return null
        }
        return pkg
    }

    fun getOneTimeBlockAppName(): String? {
        val pkg = getOneTimeBlockPackage() ?: return null
        return prefs.getString(KEY_ONE_TIME_BLOCK_NAME, pkg)
    }

    fun getOneTimeBlockEndTimeMs(): Long {
        return prefs.getLong(KEY_ONE_TIME_BLOCK_END_MS, 0L)
    }

    fun getOneTimeBlockRemainingMs(): Long {
        val endTime = prefs.getLong(KEY_ONE_TIME_BLOCK_END_MS, 0L)
        if (endTime <= 0L) return 0L
        val remaining = endTime - System.currentTimeMillis()
        return if (remaining > 0L) remaining else 0L
    }

    fun isOneTimeBlockActive(): Boolean {
        return getOneTimeBlockRemainingMs() > 0L && !prefs.getString(KEY_ONE_TIME_BLOCK_PACKAGE, null).isNullOrEmpty()
    }

    fun saveActivities(activities: List<com.example.data.RecentActivity>) {
        val jsonArray = org.json.JSONArray()
        for (a in activities) {
            val obj = org.json.JSONObject()
            obj.put("id", a.id)
            obj.put("titleBangla", a.titleBangla)
            obj.put("titleEnglish", a.titleEnglish)
            obj.put("timeAgoBangla", a.timeAgoBangla)
            obj.put("timeAgoEnglish", a.timeAgoEnglish)
            obj.put("isSuccess", a.isSuccess)
            obj.put("iconType", a.iconType)
            obj.put("isSensitive", a.isSensitive)
            jsonArray.put(obj)
        }
        prefs.edit().putString("saved_recent_activities", jsonArray.toString()).apply()
    }

    fun getActivities(): List<com.example.data.RecentActivity> {
        val jsonString = prefs.getString("saved_recent_activities", null)
        val list = mutableListOf<com.example.data.RecentActivity>()
        if (jsonString.isNullOrEmpty()) return list
        try {
            val jsonArray = org.json.JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    com.example.data.RecentActivity(
                        id = obj.getString("id"),
                        titleBangla = obj.getString("titleBangla"),
                        titleEnglish = obj.getString("titleEnglish"),
                        timeAgoBangla = obj.getString("timeAgoBangla"),
                        timeAgoEnglish = obj.getString("timeAgoEnglish"),
                        isSuccess = obj.optBoolean("isSuccess", false),
                        iconType = obj.optString("iconType", "blocked"),
                        isSensitive = obj.optBoolean("isSensitive", false)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveProtectedActivities(activities: List<com.example.data.RecentActivity>) {
        val jsonArray = org.json.JSONArray()
        for (a in activities) {
            val obj = org.json.JSONObject()
            obj.put("id", a.id)
            obj.put("titleBangla", a.titleBangla)
            obj.put("titleEnglish", a.titleEnglish)
            obj.put("timeAgoBangla", a.timeAgoBangla)
            obj.put("timeAgoEnglish", a.timeAgoEnglish)
            obj.put("isSuccess", a.isSuccess)
            obj.put("iconType", a.iconType)
            obj.put("isSensitive", a.isSensitive)
            jsonArray.put(obj)
        }
        prefs.edit().putString("saved_protected_activities", jsonArray.toString()).apply()
    }

    fun getProtectedActivities(): List<com.example.data.RecentActivity> {
        val jsonString = prefs.getString("saved_protected_activities", null)
        val list = mutableListOf<com.example.data.RecentActivity>()
        if (jsonString.isNullOrEmpty()) return list
        try {
            val jsonArray = org.json.JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    com.example.data.RecentActivity(
                        id = obj.getString("id"),
                        titleBangla = obj.getString("titleBangla"),
                        titleEnglish = obj.getString("titleEnglish"),
                        timeAgoBangla = obj.getString("timeAgoBangla"),
                        timeAgoEnglish = obj.getString("timeAgoEnglish"),
                        isSuccess = obj.optBoolean("isSuccess", false),
                        iconType = obj.optString("iconType", "blocked"),
                        isSensitive = obj.optBoolean("isSensitive", true)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveAppThemeMode(mode: com.example.data.AppThemeMode) {
        prefs.edit().putString(KEY_APP_THEME_MODE, mode.name).apply()
    }

    fun getAppThemeMode(): com.example.data.AppThemeMode {
        val name = prefs.getString(KEY_APP_THEME_MODE, com.example.data.AppThemeMode.LIGHT.name)
        return try {
            com.example.data.AppThemeMode.valueOf(name ?: com.example.data.AppThemeMode.LIGHT.name)
        } catch (e: Exception) {
            com.example.data.AppThemeMode.LIGHT
        }
    }

    fun saveSecurityPin(pin: String) {
        prefs.edit()
            .putString(KEY_SECURITY_PIN, pin)
            .putBoolean(KEY_IS_PIN_CONFIGURED, true)
            .apply()
    }

    fun deleteSecurityPin() {
        prefs.edit()
            .remove(KEY_SECURITY_PIN)
            .putBoolean(KEY_IS_PIN_CONFIGURED, false)
            .apply()
    }

    fun getSecurityPin(): String {
        return prefs.getString(KEY_SECURITY_PIN, "1234") ?: "1234"
    }

    fun isPinConfigured(): Boolean {
        return prefs.getBoolean(KEY_IS_PIN_CONFIGURED, true)
    }

    // Notification Preferences
    fun getNotifBlocking(): Boolean = prefs.getBoolean("notif_blocking", true)
    fun saveNotifBlocking(value: Boolean) = prefs.edit().putBoolean("notif_blocking", value).apply()

    fun getNotifTimer(): Boolean = prefs.getBoolean("notif_timer", true)
    fun saveNotifTimer(value: Boolean) = prefs.edit().putBoolean("notif_timer", value).apply()

    fun getNotifSecurity(): Boolean = prefs.getBoolean("notif_security", true)
    fun saveNotifSecurity(value: Boolean) = prefs.edit().putBoolean("notif_security", value).apply()

    fun getNotifReminders(): Boolean = prefs.getBoolean("notif_reminders", true)
    fun saveNotifReminders(value: Boolean) = prefs.edit().putBoolean("notif_reminders", value).apply()

    // Onboarding & User Profile Info
    fun isOnboardingCompleted(): Boolean = prefs.getBoolean("onboarding_completed", false)
    fun setOnboardingCompleted(completed: Boolean) = prefs.edit().putBoolean("onboarding_completed", completed).apply()

    fun getUserName(): String? = prefs.getString("user_name", null)
    fun saveUserName(name: String) = prefs.edit().putString("user_name", name).apply()

    fun getUserReligion(): String? = prefs.getString("user_religion", null)
    fun saveUserReligion(religion: String) = prefs.edit().putString("user_religion", religion).apply()

    // Adult Content & Keyword Protection Filter
    fun isAdultContentBlockerEnabled(): Boolean = prefs.getBoolean("filter_adult_content_blocker", true)
    fun saveAdultContentBlockerEnabled(enabled: Boolean) = prefs.edit().putBoolean("filter_adult_content_blocker", enabled).apply()

    // Advanced DNS Protection Setting
    fun isDnsProtectionEnabled(): Boolean = prefs.getBoolean("filter_dns_protection", false)
    fun saveDnsProtectionEnabled(enabled: Boolean) = prefs.edit().putBoolean("filter_dns_protection", enabled).apply()

    // Social Media Platforms Blocklist (All OFF by default)
    fun getBlockedSocialPackages(): Set<String> {
        if (!prefs.getBoolean("social_initialized_all_off_v1", false)) {
            prefs.edit()
                .putStringSet("blocked_social_packages", emptySet())
                .putBoolean("social_initialized_all_off_v1", true)
                .apply()
            return emptySet()
        }
        return prefs.getStringSet("blocked_social_packages", emptySet()) ?: emptySet()
    }

    fun saveBlockedSocialPackages(packages: Set<String>) {
        prefs.edit().putStringSet("blocked_social_packages", packages).apply()
    }

    fun isSocialPackageBlocked(packageName: String): Boolean {
        return getBlockedSocialPackages().contains(packageName)
    }

    fun setSocialPackageBlocked(packageName: String, blocked: Boolean) {
        val current = getBlockedSocialPackages().toMutableSet()
        if (blocked) {
            current.add(packageName)
        } else {
            current.remove(packageName)
        }
        saveBlockedSocialPackages(current)
    }

    // Short Video & Reels Platforms Blocklist (All OFF by default)
    fun getBlockedShortsPackages(): Set<String> {
        if (!prefs.getBoolean("shorts_initialized_all_off_v1", false)) {
            prefs.edit()
                .putStringSet("blocked_shorts_packages", emptySet())
                .putBoolean("shorts_initialized_all_off_v1", true)
                .apply()
            return emptySet()
        }
        return prefs.getStringSet("blocked_shorts_packages", emptySet()) ?: emptySet()
    }

    fun saveBlockedShortsPackages(packages: Set<String>) {
        prefs.edit().putStringSet("blocked_shorts_packages", packages).apply()
    }

    fun isShortsPackageBlocked(packageName: String): Boolean {
        return getBlockedShortsPackages().contains(packageName)
    }

    fun setShortsPackageBlocked(packageName: String, blocked: Boolean) {
        val current = getBlockedShortsPackages().toMutableSet()
        if (blocked) {
            current.add(packageName)
        } else {
            current.remove(packageName)
        }
        saveBlockedShortsPackages(current)
    }
}

