package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.data.FocusLockState
import com.example.util.AppUsageTracker
import com.example.util.FocusLockPreferences
import com.example.util.FocusNotificationHelper

class FocusAccessibilityService : AccessibilityService() {

    private var lastNotificationTimeMs = 0L

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
                        val customDomains = prefs.getCustomDomains().map { it.domain }
                        val customKeywords = prefs.getCustomKeywords()
                        val foundWebsite = searchForDistractingWebsites(rootNode, customDomains, customKeywords)
                        if (foundWebsite) shouldBlock = true
                        rootNode.recycle()
                    }
                }
            }
        }

        // 2. Check if 24-hour App Screen Time Limit is exceeded
        val isTimeLimitExceeded = AppUsageTracker.isAppLimitExceeded(applicationContext, packageName)

        // 3. Check if One-Time Block is active for this package
        val oneTimeBlockedPackage = prefs.getOneTimeBlockPackage()
        val isOneTimeBlocked = !oneTimeBlockedPackage.isNullOrEmpty() && oneTimeBlockedPackage == packageName

        if (shouldBlock || isTimeLimitExceeded || isOneTimeBlocked) {
            // Perform HOME action to exit blocked app immediately
            performGlobalAction(GLOBAL_ACTION_HOME)

            // Send notification alert if enabled
            val now = System.currentTimeMillis()
            if (now - lastNotificationTimeMs > 3000L) {
                lastNotificationTimeMs = now
                if (prefs.getNotifBlocking()) {
                    val appName = try {
                        val pm = applicationContext.packageManager
                        val appInfo = pm.getApplicationInfo(packageName, 0)
                        pm.getApplicationLabel(appInfo).toString()
                    } catch (e: Exception) {
                        packageName
                    }
                    val blockReason = when {
                        isOneTimeBlocked -> "এককালীন ব্লক সক্রিয় থাকায় অ্যাপটি বন্ধ করা হয়েছে"
                        isTimeLimitExceeded -> "দৈনিক স্ক্রিন টাইম লিমিট শেষ হয়ে যাওয়ায় অ্যাপটি বন্ধ করা হয়েছে"
                        else -> "ফোকাস লক সক্রিয় থাকায় প্রবেশ প্রতিহত করা হয়েছে"
                    }
                    FocusNotificationHelper.sendBlockAlertNotification(
                        context = applicationContext,
                        appName = appName,
                        reason = blockReason
                    )
                }
            }

            // Launch Focus Shield to display blocking screen
            val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("BLOCK_TRIGGERED", true)
                putExtra("BLOCKED_PACKAGE", packageName)
                putExtra("IS_TIME_LIMIT_EXCEEDED", isTimeLimitExceeded)
                putExtra("IS_ONE_TIME_BLOCKED", isOneTimeBlocked)
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

    private fun searchForDistractingWebsites(
        node: AccessibilityNodeInfo,
        customDomains: List<String> = emptyList(),
        customKeywords: List<String> = emptyList()
    ): Boolean {
        val text = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""
        val combined = "$text $desc"

        if (combined.contains("facebook.com") || combined.contains("instagram.com") || combined.contains("tiktok.com") || combined.contains("reddit.com") || combined.contains("twitter.com") || combined.contains("x.com")) {
            return true
        }

        for (domain in customDomains) {
            if (domain.isNotBlank() && combined.contains(domain.lowercase())) {
                return true
            }
        }

        for (kw in customKeywords) {
            if (kw.isNotBlank() && combined.contains(kw.lowercase())) {
                return true
            }
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                if (searchForDistractingWebsites(child, customDomains, customKeywords)) return true
                child.recycle()
            }
        }
        return false
    }

    override fun onInterrupt() {
        // Service interrupted
    }
}

