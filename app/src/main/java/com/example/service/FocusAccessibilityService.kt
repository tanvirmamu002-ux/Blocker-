package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.data.FocusLockState
import com.example.util.AppUsageTracker
import com.example.util.FocusLockPreferences

class FocusAccessibilityService : AccessibilityService() {

    private val blockedPackages = setOf(
        "com.facebook.katana",
        "com.facebook.orca",
        "com.instagram.android",
        "com.zhiliaoapp.musically",
        "com.ss.android.ugc.trill",
        "com.google.android.youtube",
        "com.twitter.android",
        "com.snapchat.android",
        "com.reddit.frontpage",
        "com.netflix.mediaclient",
        "com.pubg.krmobile"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return

        // Ignore our own app package
        if (packageName == applicationContext.packageName) return

        val prefs = FocusLockPreferences.getInstance(applicationContext)
        val lockState = prefs.getFocusLockState()
        val config = prefs.getFocusLockConfig()

        // 1. Check if Focus Lock session is active
        val isFocusLockBlocking = (lockState == FocusLockState.ACTIVE || lockState == FocusLockState.EMERGENCY_REQUEST) &&
                config.blockApps && (blockedPackages.contains(packageName) || AppUsageTracker.getAppLimitConfig(applicationContext, packageName).second)

        // 2. Check if 24-hour App Screen Time Limit is exceeded
        val isTimeLimitExceeded = AppUsageTracker.isAppLimitExceeded(applicationContext, packageName)

        if (isFocusLockBlocking || isTimeLimitExceeded) {
            // Perform HOME action to exit blocked app immediately
            performGlobalAction(GLOBAL_ACTION_HOME)

            // Launch Focus Shield to display blocking screen
            val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("BLOCK_TRIGGERED", true)
                putExtra("BLOCKED_PACKAGE", packageName)
                putExtra("IS_TIME_LIMIT_EXCEEDED", isTimeLimitExceeded)
            }
            if (launchIntent != null) {
                applicationContext.startActivity(launchIntent)
            }
        }
    }

    override fun onInterrupt() {
        // Service interrupted
    }
}

