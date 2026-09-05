package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.data.FocusLockState
import com.example.data.RecentActivity
import com.example.util.AdultContentKeywords
import com.example.util.AppUsageTracker
import com.example.util.FocusLockPreferences
import com.example.util.FocusNotificationHelper

class FocusAccessibilityService : AccessibilityService() {

    private var lastNotificationTimeMs = 0L
    private var lastBlockActionTimeMs = 0L
    private var lastBlockedPackageName = ""
    private var lastAdultKeywordBlockTimeMs = 0L

    // Battery optimization & throttle tracking
    private var lastProcessTimeMs = 0L
    private var lastShortsBlockTimeMs = 0L
    private var lastProcessedPackage = ""

    private lateinit var overlayManager: AntiFlashOverlayManager

    override fun onCreate() {
        super.onCreate()
        overlayManager = AntiFlashOverlayManager(applicationContext)
    }

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

        // 1. Strict Event Filtering: Ignore any event that is not TYPE_WINDOW_STATE_CHANGED
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val packageName = event.packageName?.toString() ?: return

        // Ignore our own app package immediately
        if (packageName == applicationContext.packageName) return

        val now = System.currentTimeMillis()

        // 2. Throttle: Limit node and state evaluations to at least 300ms apart for same package
        if (packageName == lastProcessedPackage && now - lastProcessTimeMs < 300L) {
            return
        }
        lastProcessTimeMs = now
        lastProcessedPackage = packageName

        val className = event.className?.toString() ?: ""
        val prefs = FocusLockPreferences.getInstance(applicationContext)

        // -------------------------------------------------------------
        // Dedicated Short Video / Reels Blocking System (Autonomous & Targeted)
        // -------------------------------------------------------------
        // Battery optimization: Early-exit immediately if not a target shorts host
        if (ShortVideoDetector.isPotentialShortsHost(packageName)) {
            val rootNode = rootInActiveWindow
            val isShorts = ShortVideoDetector.shouldBlockShortVideo(
                context = applicationContext,
                packageName = packageName,
                className = className,
                rootNode = rootNode
            )
            rootNode?.recycle()

            if (isShorts) {
                // Guard: Prevent rapid multiple firing on the same screen (1500ms guard)
                if (now - lastShortsBlockTimeMs > 1500L) {
                    lastShortsBlockTimeMs = now

                    // Step 1: Opaque full-screen overlay to immediately eliminate content flash
                    overlayManager.showTemporaryAntiFlashOverlay(dismissAfterMs = 600L)

                    // Step 2: Kick user out of the Reels/Shorts screen back to home
                    performGlobalAction(GLOBAL_ACTION_HOME)

                    // Step 3: Record protected activity
                    val activity = RecentActivity(
                        id = System.currentTimeMillis().toString(),
                        titleBangla = "শর্টস / রিলস ভিডিও প্রতিহত",
                        titleEnglish = "Short Video / Reels Blocked",
                        timeAgoBangla = "এইমাত্র",
                        timeAgoEnglish = "Just now",
                        isSuccess = true,
                        iconType = "shorts",
                        isSensitive = false
                    )
                    val currentProtected = prefs.getProtectedActivities().toMutableList()
                    currentProtected.add(0, activity)
                    prefs.saveProtectedActivities(currentProtected)

                    // Step 4: Notification alert if enabled
                    if (prefs.getNotifBlocking()) {
                        FocusNotificationHelper.sendBlockAlertNotification(
                            context = applicationContext,
                            appName = when {
                                packageName.contains("youtube") -> "YouTube Shorts"
                                packageName.contains("instagram") -> "Instagram Reels"
                                packageName.contains("facebook") -> "Facebook Reels"
                                packageName.contains("tiktok") -> "TikTok"
                                packageName.contains("like") -> "Likee"
                                else -> "Short Video"
                            },
                            reason = "আসক্তিকর শর্ট ভিডিও ও রিলস স্ক্রিন ব্লক করা হয়েছে"
                        )
                    }

                    // Step 5: User feedback via non-intrusive Toast
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        android.widget.Toast.makeText(
                            applicationContext,
                            "⚡ শর্ট ভিডিও / রিলস ব্লক করা হয়েছে",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return
            }
        }

        val lockState = prefs.getFocusLockState()
        val config = prefs.getFocusLockConfig()

        var shouldBlock = false

        // 1. Check if Focus Lock session is active
        if (lockState == FocusLockState.ACTIVE || lockState == FocusLockState.EMERGENCY_REQUEST) {
            if (config.blockApps && (blockedPackages.contains(packageName) || AppUsageTracker.getAppLimitConfig(applicationContext, packageName).second)) {
                shouldBlock = true
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

        // 4. Real Keyword Protection System (Adult / NSFW Content Blocker)
        // Works in browsers and media apps whenever Adult Content Blocker is enabled
        if (!shouldBlock && !isTimeLimitExceeded && !isOneTimeBlocked && prefs.isAdultContentBlockerEnabled()) {
            val rootNode = rootInActiveWindow
            if (rootNode != null) {
                val foundAdult = searchForAdultContent(rootNode)
                rootNode.recycle()
                if (foundAdult) {
                    if (now - lastAdultKeywordBlockTimeMs > 1200L) {
                        lastAdultKeywordBlockTimeMs = now

                        // Trigger immediate BACK action to instantly close/exit the objectionable screen
                        performGlobalAction(GLOBAL_ACTION_BACK)

                        // Record in Protected Activities
                        val activity = RecentActivity(
                            id = System.currentTimeMillis().toString(),
                            titleBangla = "এডাল্ট কনটেন্ট কিওয়ার্ড প্রতিহত",
                            titleEnglish = "Adult Content Keyword Blocked",
                            timeAgoBangla = "এইমাত্র",
                            timeAgoEnglish = "Just now",
                            isSuccess = true,
                            iconType = "shield",
                            isSensitive = true
                        )
                        val currentProtected = prefs.getProtectedActivities().toMutableList()
                        currentProtected.add(0, activity)
                        prefs.saveProtectedActivities(currentProtected)

                        // Send security notification if enabled
                        if (prefs.getNotifSecurity()) {
                            FocusNotificationHelper.sendSecurityNotification(
                                context = applicationContext,
                                title = "সুরক্ষিত ফিল্টার সক্রিয় 🛡️",
                                message = "এডাল্ট কনটেন্ট শনাক্ত হওয়ায় তাৎক্ষণিক পেজ থেকে ব্যাক করা হয়েছে"
                            )
                        }

                        // User feedback via non-intrusive Toast
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            android.widget.Toast.makeText(
                                applicationContext,
                                "🛡️ এডাল্ট কনটেন্ট কিওয়ার্ড শনাক্ত হওয়ায় তাৎক্ষণিক ব্যাক করা হয়েছে",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    return
                }
            }
        }

        if (shouldBlock || isTimeLimitExceeded || isOneTimeBlocked) {
            // Debounce blocking actions for the same package within 1.5 seconds to prevent spam and crashes
            if (now - lastBlockActionTimeMs < 1500L && packageName == lastBlockedPackageName) {
                return
            }
            lastBlockActionTimeMs = now
            lastBlockedPackageName = packageName

            // 1. Perform HOME action to exit blocked app immediately and return user safely to their home screen
            performGlobalAction(GLOBAL_ACTION_HOME)

            // Resolve friendly App Name
            val appName = try {
                val pm = applicationContext.packageManager
                val appInfo = pm.getApplicationInfo(packageName, 0)
                pm.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                packageName
            }

            // 2. Send notification alert with live countdown if enabled
            if (prefs.getNotifBlocking()) {
                if (isOneTimeBlocked) {
                    val endTimeMs = prefs.getOneTimeBlockEndTimeMs()
                    FocusNotificationHelper.sendOneTimeBlockNotification(
                        context = applicationContext,
                        appName = appName,
                        endTimeMs = endTimeMs
                    )
                } else {
                    val blockReason = when {
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

            // 3. User feedback via Toast on Main Thread without crashing or bouncing into MainActivity
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                val toastMsg = if (isOneTimeBlocked) {
                    val remainingMs = prefs.getOneTimeBlockRemainingMs().coerceAtLeast(0L)
                    val remainingMinutes = ((remainingMs + 59999L) / 60000L).coerceAtLeast(1)
                    val hours = remainingMinutes / 60
                    val mins = remainingMinutes % 60
                    val timeText = if (hours > 0) "$hours ঘণ্টা $mins মিনিট" else "$mins মিনিট"
                    "🔒 \"$appName\" সাময়িকভাবে ব্লক রয়েছে। আর $timeText পর ব্যবহার করতে পারবেন।"
                } else if (isTimeLimitExceeded) {
                    "⏳ \"$appName\"-এর দৈনিক স্ক্রিন টাইম লিমিট শেষ হয়েছে।"
                } else {
                    "🛡️ ফোকাস লক সক্রিয় থাকায় \"$appName\" বন্ধ করা হয়েছে।"
                }
                android.widget.Toast.makeText(applicationContext, toastMsg, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
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

    /**
     * Traverses the active view hierarchy and checks node text and content descriptions
     * against the high-risk adult/NSFW keyword protection dictionary.
     * Recycles child nodes to avoid memory leaks.
     */
    private fun searchForAdultContent(node: AccessibilityNodeInfo): Boolean {
        val text = node.text?.toString()
        if (AdultContentKeywords.containsAdultKeyword(text)) {
            return true
        }

        val desc = node.contentDescription?.toString()
        if (AdultContentKeywords.containsAdultKeyword(desc)) {
            return true
        }

        val childCount = node.childCount
        for (i in 0 until childCount) {
            val child = node.getChild(i)
            if (child != null) {
                val found = searchForAdultContent(child)
                child.recycle()
                if (found) return true
            }
        }
        return false
    }

    override fun onInterrupt() {
        // Service interrupted
    }
}

