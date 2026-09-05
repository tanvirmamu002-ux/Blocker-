package com.example.service

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.example.util.FocusLockPreferences

/**
 * High-performance, battery-optimized Short Video & Reels Detector.
 *
 * Requirements:
 * 1. Early-exit if target app is not in the user's enabled list.
 * 2. Instagram, Facebook, YouTube: DO NOT block the whole app. ONLY detect Reels/Shorts screens.
 * 3. TikTok, Likee, Moj, etc.: If selected, whole app is blocked.
 * 4. Max 2-level node traversal to avoid battery drain and lag.
 * 5. Immediate AccessibilityNodeInfo recycling.
 * 6. Combination of Package + Activity/Class name + UI/node patterns.
 */
object ShortVideoDetector {

    // YouTube packages
    const val PKG_YOUTUBE = "com.google.android.youtube"
    const val PKG_YOUTUBE_SHORTS_KEY = "com.google.android.youtube.shorts"

    // Instagram packages
    const val PKG_INSTAGRAM = "com.instagram.android"
    const val PKG_INSTAGRAM_LITE = "com.instagram.lite"
    const val PKG_INSTAGRAM_REELS_KEY = "com.instagram.android.reels"

    // Facebook packages
    const val PKG_FACEBOOK = "com.facebook.katana"
    const val PKG_FACEBOOK_LITE = "com.facebook.lite"
    const val PKG_FACEBOOK_REELS_KEY = "com.facebook.katana.reels"

    // Standalone Short-Video Apps (Full app block if user has them selected)
    private val STANDALONE_SHORTS_PACKAGES = setOf(
        "com.zhiliaoapp.musically",      // TikTok
        "com.zhiliaoapp.musically.go",   // TikTok Lite
        "com.ss.android.ugc.trill",      // TikTok alternate
        "video.like",                    // Likee
        "video.like.lite",               // Likee Lite
        "in.moj.app",                    // Moj
        "in.moj.app.lite",               // Moj Lite
        "com.eterno.shortvideos",        // Josh
        "com.kwai.video"                 // Kwai
    )

    /**
     * Checks if this package is even a candidate for short video blocking.
     * Allows O(1) early exit before doing any class or node checks.
     */
    fun isPotentialShortsHost(packageName: String): Boolean {
        return packageName == PKG_YOUTUBE ||
                packageName == PKG_INSTAGRAM ||
                packageName == PKG_INSTAGRAM_LITE ||
                packageName == PKG_FACEBOOK ||
                packageName == PKG_FACEBOOK_LITE ||
                STANDALONE_SHORTS_PACKAGES.contains(packageName)
    }

    /**
     * Determines whether the current window/state should trigger a short video block.
     *
     * @param context Application context to read user preferences
     * @param packageName Foreground package name
     * @param className Current Activity / Window class name
     * @param rootNode Root AccessibilityNodeInfo (optional, max 2 levels traversed)
     * @return true if short-form video feed / app should be blocked
     */
    fun shouldBlockShortVideo(
        context: Context,
        packageName: String,
        className: String,
        rootNode: AccessibilityNodeInfo?
    ): Boolean {
        val prefs = FocusLockPreferences.getInstance(context)
        val blockedKeys = prefs.getBlockedShortsPackages()

        // 1. Standalone Short-Video Apps: Entire app is blocked if in user preference
        if (STANDALONE_SHORTS_PACKAGES.contains(packageName)) {
            val isSelected = blockedKeys.contains(packageName) ||
                    (packageName.contains("tiktok") && blockedKeys.contains("com.zhiliaoapp.musically")) ||
                    (packageName.contains("like") && blockedKeys.contains("video.like")) ||
                    (packageName.contains("moj") && blockedKeys.contains("in.moj.app")) ||
                    (packageName.contains("josh") && blockedKeys.contains("com.eterno.shortvideos")) ||
                    (packageName.contains("kwai") && blockedKeys.contains("com.kwai.video"))
            return isSelected
        }

        // 2. YouTube: Only block if Shorts screen is detected AND user enabled YouTube Shorts
        if (packageName == PKG_YOUTUBE) {
            if (!blockedKeys.contains(PKG_YOUTUBE_SHORTS_KEY)) {
                return false
            }
            return isYouTubeShortsScreen(className, rootNode)
        }

        // 3. Instagram: Only block if Reels screen is detected AND user enabled Instagram Reels
        if (packageName == PKG_INSTAGRAM || packageName == PKG_INSTAGRAM_LITE) {
            if (!blockedKeys.contains(PKG_INSTAGRAM_REELS_KEY)) {
                return false
            }
            return isInstagramReelsScreen(className, rootNode)
        }

        // 4. Facebook: Only block if Reels screen is detected AND user enabled Facebook Reels
        if (packageName == PKG_FACEBOOK || packageName == PKG_FACEBOOK_LITE) {
            if (!blockedKeys.contains(PKG_FACEBOOK_REELS_KEY)) {
                return false
            }
            return isFacebookReelsScreen(className, rootNode)
        }

        return false
    }

    /**
     * Detects YouTube Shorts using Activity/Class names, Known View IDs, and UI node patterns.
     */
    private fun isYouTubeShortsScreen(className: String, rootNode: AccessibilityNodeInfo?): Boolean {
        val lowerClass = className.lowercase()

        // 1. Direct class/activity detection
        // Note: YouTube often uses ReelWatchActivity or ShortsPlayerActivity or fragments
        if (lowerClass.contains("reel") || lowerClass.contains("shorts") || lowerClass.contains("reelwatchactivity")) {
            return true
        }

        if (rootNode == null) return false

        // 2. Known View IDs check (YouTube uses reel_player, shorts_container, etc.)
        // TODO: Real-device verification for newly updated YouTube builds if ID changes
        try {
            val reelNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/reel_player_page_container")
            if (reelNodes != null && reelNodes.isNotEmpty()) {
                reelNodes.forEach { it.recycle() }
                return true
            }

            val shortsContainerNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/shorts_container")
            if (shortsContainerNodes != null && shortsContainerNodes.isNotEmpty()) {
                shortsContainerNodes.forEach { it.recycle() }
                return true
            }

            val pivotShortsNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/pivot_shorts")
            if (pivotShortsNodes != null && pivotShortsNodes.isNotEmpty()) {
                val isSelected = pivotShortsNodes.any { it.isSelected }
                pivotShortsNodes.forEach { it.recycle() }
                if (isSelected) return true
            }
        } catch (e: Exception) {
            // Ignore view ID lookup error
        }

        // 3. Fast shallow node inspection (Max depth = 2)
        return shallowScanForShortsPattern(rootNode, currentDepth = 0, maxDepth = 2, targetType = "shorts")
    }

    /**
     * Detects Instagram Reels using Activity/Class names, Known View IDs, and UI node patterns.
     */
    private fun isInstagramReelsScreen(className: String, rootNode: AccessibilityNodeInfo?): Boolean {
        val lowerClass = className.lowercase()

        // 1. Direct class/activity detection
        // Instagram often routes through ClipsViewerActivity, ReelsViewerFragment, or ModalActivity
        if (lowerClass.contains("clipsviewer") || lowerClass.contains("reelsviewer") || lowerClass.contains("clipsactivity")) {
            return true
        }

        if (rootNode == null) return false

        // 2. Known View IDs check
        // TODO: Real-device verification for Instagram Clips UI elements
        try {
            val clipsNodes = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_viewer_container")
            if (clipsNodes != null && clipsNodes.isNotEmpty()) {
                clipsNodes.forEach { it.recycle() }
                return true
            }

            val reelsTabNodes = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/reels_tab")
            if (reelsTabNodes != null && reelsTabNodes.isNotEmpty()) {
                val isSelected = reelsTabNodes.any { it.isSelected }
                reelsTabNodes.forEach { it.recycle() }
                if (isSelected) return true
            }
        } catch (e: Exception) {
            // Ignore view ID lookup error
        }

        // 3. Fast shallow node inspection (Max depth = 2)
        return shallowScanForShortsPattern(rootNode, currentDepth = 0, maxDepth = 2, targetType = "reels")
    }

    /**
     * Detects Facebook Reels using Activity/Class names, Known View IDs, and UI node patterns.
     */
    private fun isFacebookReelsScreen(className: String, rootNode: AccessibilityNodeInfo?): Boolean {
        val lowerClass = className.lowercase()

        // 1. Direct class/activity detection
        if (lowerClass.contains("reelsviewer") || lowerClass.contains("fbshorts") || lowerClass.contains("videoimmersiveplayeractivity")) {
            return true
        }

        if (rootNode == null) return false

        // 2. Known View IDs check
        // TODO: Real-device verification for Facebook video immersive layout
        try {
            val fbShortsNodes = rootNode.findAccessibilityNodeInfosByViewId("com.facebook.katana:id/fb_shorts_container")
            if (fbShortsNodes != null && fbShortsNodes.isNotEmpty()) {
                fbShortsNodes.forEach { it.recycle() }
                return true
            }
        } catch (e: Exception) {
            // Ignore view ID lookup error
        }

        // 3. Fast shallow node inspection (Max depth = 2)
        return shallowScanForShortsPattern(rootNode, currentDepth = 0, maxDepth = 2, targetType = "fb_reels")
    }

    /**
     * Strict 2-level traversal to detect Reels/Shorts UI indicators without heavy processing.
     * Recycles each child node immediately to prevent memory leaks and GC pressure.
     */
    private fun shallowScanForShortsPattern(
        node: AccessibilityNodeInfo,
        currentDepth: Int,
        maxDepth: Int,
        targetType: String
    ): Boolean {
        // Check current node properties
        val text = node.text?.toString()?.trim()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.trim()?.lowercase() ?: ""

        when (targetType) {
            "shorts" -> {
                // Look for YouTube Shorts specific indicators (e.g. Selected "Shorts" bottom navigation tab or heading)
                if (node.isSelected && (desc == "shorts" || text == "shorts")) {
                    return true
                }
                if (desc.contains("shorts player") || desc.contains("shorts camera") || desc.contains("remix this short")) {
                    return true
                }
            }
            "reels" -> {
                // Look for Instagram Reels specific indicators (e.g. Selected "Reels" tab or audio attribution)
                if (node.isSelected && (desc == "reels" || text == "reels")) {
                    return true
                }
                if (desc.contains("reels camera") || desc.contains("remix reel") || desc.contains("audio used in reel") || text.contains("use audio")) {
                    return true
                }
            }
            "fb_reels" -> {
                if (node.isSelected && (desc == "reels" || text == "reels")) {
                    return true
                }
                if (desc.contains("create reel") || desc.contains("reels audio") || text.contains("reels and short videos")) {
                    return true
                }
            }
        }

        if (currentDepth >= maxDepth) {
            return false
        }

        // Traverse children up to maxDepth
        val childCount = node.childCount
        for (i in 0 until childCount) {
            val child = node.getChild(i)
            if (child != null) {
                val found = shallowScanForShortsPattern(child, currentDepth + 1, maxDepth, targetType)
                child.recycle()
                if (found) return true
            }
        }
        return false
    }
}
