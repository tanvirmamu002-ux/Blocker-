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
    private var lastShortsEjectionTimeMs = 0L
    private var lastSocialBlockTimeMs = 0L
    private var consecutiveShortsEjections = 0
    private var lastProcessedPackage = ""

    private val serviceHandler = android.os.Handler(android.os.Looper.getMainLooper())
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

        val eventType = event.eventType
        // Listen to window state, content changes, scrolling, and user clicks
        if (eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED &&
            eventType != AccessibilityEvent.TYPE_VIEW_CLICKED) {
            return
        }

        val packageName = event.packageName?.toString() ?: return

        // Ignore our own app package immediately
        if (packageName == applicationContext.packageName) return

        val isPotentialShorts = ShortVideoDetector.isPotentialShortsHost(packageName)
        val prefs = FocusLockPreferences.getInstance(applicationContext)
        val lockState = prefs.getFocusLockState()
        val isLockActive = (lockState == FocusLockState.ACTIVE || lockState == FocusLockState.EMERGENCY_REQUEST)
        val isAdultFilterEnabled = prefs.isAdultContentBlockerEnabled()
        val isTimeLimitConfigured = AppUsageTracker.getAppLimitConfig(applicationContext, packageName).second
        val oneTimeBlockedPackage = prefs.getOneTimeBlockPackage()
        val isOneTimeBlocked = !oneTimeBlockedPackage.isNullOrEmpty() && oneTimeBlockedPackage == packageName
        val isSocialBlocked = prefs.isSocialPackageBlocked(packageName)

        // Ultra-fast O(1) early-exit if package is not relevant to any active protection feature
        if (!isPotentialShorts && !isLockActive && !isAdultFilterEnabled && !isTimeLimitConfigured && !isOneTimeBlocked && !isSocialBlocked) {
            return
        }

        val now = System.currentTimeMillis()

        // Responsive throttle: Clicks and Window switches are checked immediately (40ms), while scrolls debounce at 160ms
        val minInterval = if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            40L
        } else {
            160L
        }

        if (packageName == lastProcessedPackage && now - lastProcessTimeMs < minInterval) {
            return
        }
        lastProcessTimeMs = now
        lastProcessedPackage = packageName

        val className = event.className?.toString() ?: ""

        // -------------------------------------------------------------
        // Dedicated Short Video / Reels Blocking System (Autonomous & Targeted)
        // -------------------------------------------------------------
        if (isPotentialShorts) {
            val rootNode = rootInActiveWindow
            val isShorts = ShortVideoDetector.shouldBlockShortVideo(
                context = applicationContext,
                packageName = packageName,
                className = className,
                rootNode = rootNode
            )
            try { rootNode?.recycle() } catch (e: Exception) {}

            if (isShorts) {
                val timeSinceLastEject = now - lastShortsEjectionTimeMs

                // Micro-guard (180ms) prevents duplicate execution on the exact same frame
                if (timeSinceLastEject > 180L) {
                    if (timeSinceLastEject < 4000L) {
                        consecutiveShortsEjections++
                    } else {
                        consecutiveShortsEjections = 1
                    }
                    lastShortsEjectionTimeMs = now

                    // Step 1: Opaque overlay to instantly eliminate content flash (dismisses quickly after 350ms)
                    overlayManager.showTemporaryAntiFlashOverlay(dismissAfterMs = 350L)

                    // Step 2: Decisive physical exit from the screen
                    if (consecutiveShortsEjections > 1) {
                        // Rapid re-entry or stubborn foreground: Kick straight to Android Home Screen
                        performGlobalAction(GLOBAL_ACTION_HOME)
                        sendExplicitHomeIntent()
                    } else {
                        // First attempt: Pop back (returns to normal feed if tapped from feed)
                        val backHandled = performGlobalAction(GLOBAL_ACTION_BACK)
                        if (!backHandled) {
                            performGlobalAction(GLOBAL_ACTION_HOME)
                            sendExplicitHomeIntent()
                        }
                    }

                    // Step 3: Fast Watchdog Verification - Ensures user is never left stuck or bypassing
                    serviceHandler.postDelayed({
                        verifyAndForceEjectIfStillInShorts(packageName)
                    }, 220L)
                    serviceHandler.postDelayed({
                        verifyAndForceEjectIfStillInShorts(packageName)
                    }, 480L)

                    // Step 4: Record protected activity & notify user (debounced to avoid spamming)
                    if (now - lastNotificationTimeMs > 2500L) {
                        lastNotificationTimeMs = now

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

                        serviceHandler.post {
                            android.widget.Toast.makeText(
                                applicationContext,
                                "⚡ শর্ট ভিডিও / রিলস স্ক্রিন থেকে বের করে দেওয়া হয়েছে",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                return
            }
        }


        // 0. Check if package is blocked under Social Media Blocker
        if (isSocialBlocked) {
            if (now - lastSocialBlockTimeMs > 1200L) {
                lastSocialBlockTimeMs = now
                overlayManager.showTemporaryAntiFlashOverlay(dismissAfterMs = 400L)
                performGlobalAction(GLOBAL_ACTION_HOME)
                sendExplicitHomeIntent()

                val activity = RecentActivity(
                    id = System.currentTimeMillis().toString(),
                    titleBangla = "সোশ্যাল মিডিয়া অ্যাপ প্রতিহত",
                    titleEnglish = "Social Media App Blocked",
                    timeAgoBangla = "এইমাত্র",
                    timeAgoEnglish = "Just now",
                    isSuccess = true,
                    iconType = "social",
                    isSensitive = false
                )
                val currentProtected = prefs.getProtectedActivities().toMutableList()
                currentProtected.add(0, activity)
                prefs.saveProtectedActivities(currentProtected)

                serviceHandler.post {
                    android.widget.Toast.makeText(
                        applicationContext,
                        "🛡️ সোশ্যাল মিডিয়া ব্লকার: অ্যাপটি ব্লক করা রয়েছে",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return
        }

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

        // 3. Real Keyword Protection System (Adult / NSFW Content Blocker)
        // Works in browsers and media apps whenever Adult Content Blocker is enabled
        if (!shouldBlock && !isTimeLimitExceeded && !isOneTimeBlocked && isAdultFilterEnabled) {
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

    /**
     * Dispatches an explicit Home intent to the Android ActivityManager to guarantee
     * that the user is immediately taken back to their Home Screen even if the global
     * accessibility action was delayed by an in-app transition or modal.
     */
    private fun sendExplicitHomeIntent() {
        try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            startActivity(homeIntent)
        } catch (e: Exception) {
            // Global action already requested
        }
    }

    /**
     * Delayed Watchdog Verification:
     * Inspects active window nodes ~200-480ms after ejection.
     * If the foreground is still showing the target Shorts/Reels screen, it forcibly re-ejects the user.
     */
    private fun verifyAndForceEjectIfStillInShorts(targetPackage: String) {
        val rootNode = rootInActiveWindow ?: return
        try {
            val stillShorts = ShortVideoDetector.shouldBlockShortVideo(
                context = applicationContext,
                packageName = targetPackage,
                className = "",
                rootNode = rootNode
            )
            if (stillShorts) {
                performGlobalAction(GLOBAL_ACTION_HOME)
                sendExplicitHomeIntent()
            }
        } catch (e: Exception) {
            // Handled
        } finally {
            try { rootNode.recycle() } catch (e: Exception) {}
        }
    }

    override fun onInterrupt() {
        // Service interrupted
    }
}

