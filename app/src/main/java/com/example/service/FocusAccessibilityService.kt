package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
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

        var shouldBlock = false

        // 1. Check if Focus Lock session is active
        if (lockState == FocusLockState.ACTIVE || lockState == FocusLockState.EMERGENCY_REQUEST) {
            if (config.blockApps && (blockedPackages.contains(packageName) || AppUsageTracker.getAppLimitConfig(applicationContext, packageName).second)) {
                shouldBlock = true
            }
            
            if (!shouldBlock && config.blockShorts) {
                if (packageName.contains("tiktok") || packageName.contains("musically") || packageName.contains("trill")) {
                    shouldBlock = true
                } else if (packageName.contains("youtube") || packageName.contains("instagram") || packageName.contains("facebook")) {
                    val rootNode = rootInActiveWindow
                    if (rootNode != null) {
                        val foundShorts = searchForShortsKeywords(rootNode)
                        if (foundShorts) shouldBlock = true
                        rootNode.recycle()
                    }
                }
            }

            if (!shouldBlock && config.blockWebsites) {
                if (packageName.contains("chrome") || packageName.contains("browser") || packageName.contains("firefox")) {
                    val rootNode = rootInActiveWindow
                    if (rootNode != null) {
                        val foundWebsite = searchForDistractingWebsites(rootNode)
                        if (foundWebsite) shouldBlock = true
                        rootNode.recycle()
                    }
                }
            }
        }

        // 2. Check if 24-hour App Screen Time Limit is exceeded
        val isTimeLimitExceeded = AppUsageTracker.isAppLimitExceeded(applicationContext, packageName)

        if (shouldBlock || isTimeLimitExceeded) {
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

    private fun searchForShortsKeywords(node: AccessibilityNodeInfo): Boolean {
        val text = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""
        
        if (text.contains("shorts") || desc.contains("shorts") || text.contains("reels") || desc.contains("reels")) {
            return true
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                if (searchForShortsKeywords(child)) return true
                child.recycle()
            }
        }
        return false
    }

    private fun searchForDistractingWebsites(node: AccessibilityNodeInfo): Boolean {
        val text = node.text?.toString()?.lowercase() ?: ""
        if (text.contains("facebook.com") || text.contains("instagram.com") || text.contains("tiktok.com") || text.contains("reddit.com") || text.contains("twitter.com") || text.contains("x.com")) {
            return true
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                if (searchForDistractingWebsites(child)) return true
                child.recycle()
            }
        }
        return false
    }

    override fun onInterrupt() {
        // Service interrupted
    }
}

