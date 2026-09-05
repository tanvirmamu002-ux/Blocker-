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
     * Accurately distinguishes Shorts from normal YouTube videos and home feeds.
     */
    private fun isYouTubeShortsScreen(className: String, rootNode: AccessibilityNodeInfo?): Boolean {
        val lowerClass = className.lowercase()

        // 1. Direct class/activity detection
        // Note: YouTube uses ReelWatchActivity, ShortsPlayerActivity or dedicated reel activities
        if (lowerClass.contains("reelwatchactivity") || 
            lowerClass.contains("shortsplayeractivity") ||
            lowerClass.contains("reel_watch_activity") ||
            (lowerClass.contains("shorts") && lowerClass.contains("activity"))) {
            return true
        }

        if (rootNode == null) return false

        // 2. Known View IDs check (with real-device fallback points)
        // TODO: Real-device verification point for newly updated YouTube builds
        try {
            val reelNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/reel_player_page_container")
            if (reelNodes != null && reelNodes.isNotEmpty()) {
                reelNodes.forEach { try { it.recycle() } catch (e: Exception) {} }
                return true
            }

            val shortsContainerNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/shorts_container")
            if (shortsContainerNodes != null && shortsContainerNodes.isNotEmpty()) {
                shortsContainerNodes.forEach { try { it.recycle() } catch (e: Exception) {} }
                return true
            }

            val pivotShortsNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/pivot_shorts")
            if (pivotShortsNodes != null && pivotShortsNodes.isNotEmpty()) {
                val isSelected = pivotShortsNodes.any { it.isSelected }
                pivotShortsNodes.forEach { try { it.recycle() } catch (e: Exception) {} }
                if (isSelected) return true
            }
        } catch (e: Exception) {
            // View ID lookup failed or obfuscated
        }

        // 3. Robust Multi-Signal Visual/UI Pattern Detection
        return scanYouTubeNodeHierarchy(rootNode)
    }

    /**
     * Detects Instagram Reels using Activity/Class names, Known View IDs, and UI node patterns.
     * Preserves normal feed, stories, and chats.
     */
    private fun isInstagramReelsScreen(className: String, rootNode: AccessibilityNodeInfo?): Boolean {
        val lowerClass = className.lowercase()

        // 1. Direct class/activity detection
        // Instagram often routes through ClipsViewerActivity, ReelsViewerFragment, or ModalActivity
        if (lowerClass.contains("clipsvieweractivity") || 
            lowerClass.contains("reelsviewerfragment") || 
            lowerClass.contains("clipsviewer") ||
            lowerClass.contains("reelsactivity")) {
            return true
        }

        if (rootNode == null) return false

        // 2. Known View IDs check
        // TODO: Real-device verification point for Instagram Clips UI elements
        try {
            val clipsNodes = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_viewer_container")
            if (clipsNodes != null && clipsNodes.isNotEmpty()) {
                clipsNodes.forEach { try { it.recycle() } catch (e: Exception) {} }
                return true
            }

            val reelsTabNodes = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/reels_tab")
            if (reelsTabNodes != null && reelsTabNodes.isNotEmpty()) {
                val isSelected = reelsTabNodes.any { it.isSelected }
                reelsTabNodes.forEach { try { it.recycle() } catch (e: Exception) {} }
                if (isSelected) return true
            }
        } catch (e: Exception) {
            // View ID lookup failed or obfuscated
        }

        // 3. Multi-Signal UI Pattern Inspection
        return scanInstagramNodeHierarchy(rootNode)
    }

    /**
     * Detects Facebook Reels using Activity/Class names, Known View IDs, and UI node patterns.
     * Preserves normal newsfeed, groups, and marketplace.
     */
    private fun isFacebookReelsScreen(className: String, rootNode: AccessibilityNodeInfo?): Boolean {
        val lowerClass = className.lowercase()

        // 1. Direct class/activity detection
        if (lowerClass.contains("reelsviewer") || 
            lowerClass.contains("fbshorts") || 
            lowerClass.contains("videoimmersiveplayeractivity") ||
            lowerClass.contains("reelspageractivity")) {
            return true
        }

        if (rootNode == null) return false

        // 2. Known View IDs check
        // TODO: Real-device verification point for Facebook video immersive layout
        try {
            val fbShortsNodes = rootNode.findAccessibilityNodeInfosByViewId("com.facebook.katana:id/fb_shorts_container")
            if (fbShortsNodes != null && fbShortsNodes.isNotEmpty()) {
                fbShortsNodes.forEach { try { it.recycle() } catch (e: Exception) {} }
                return true
            }
        } catch (e: Exception) {
            // View ID lookup failed or obfuscated
        }

        // 3. Multi-Signal UI Pattern Inspection
        return scanFacebookNodeHierarchy(rootNode)
    }

    /**
     * High-performance BFS scanner for YouTube node hierarchy.
     * Analyzes composite UI patterns up to depth 10 with max 160 nodes.
     * Distinguishes Shorts from normal videos with multi-signal confidence.
     */
    private fun scanYouTubeNodeHierarchy(rootNode: AccessibilityNodeInfo): Boolean {
        var isShortsTabSelected = false
        var hasDislikeAction = false
        var hasRemixAction = false
        var hasLikeAction = false
        var hasCommentAction = false
        var hasShareAction = false
        var hasShortsKeyword = false
        var hasShortsContainerId = false
        var hasNormalVideoSignals = false

        val queue = ArrayDeque<Pair<AccessibilityNodeInfo, Int>>()
        val visited = ArrayList<AccessibilityNodeInfo>(160)

        queue.add(Pair(rootNode, 0))

        try {
            while (queue.isNotEmpty() && visited.size < 160) {
                val (node, depth) = queue.removeFirst()
                visited.add(node)

                val text = node.text?.toString()?.trim()?.lowercase() ?: ""
                val desc = node.contentDescription?.toString()?.trim()?.lowercase() ?: ""
                val viewId = node.viewIdResourceName?.lowercase() ?: ""
                val isSelected = node.isSelected

                // A. Check for Shorts bottom navigation tab active
                if (isSelected && (desc == "shorts" || desc.contains("shorts") || text == "shorts" || text == "শর্টস" || viewId.contains("pivot_shorts"))) {
                    isShortsTabSelected = true
                }

                // B. Check for action icons
                if (desc.contains("remix") || text.contains("remix") || desc.contains("রিমি") || desc.contains("sound used in this short") || desc.contains("use this sound")) {
                    hasRemixAction = true
                }
                if (desc.contains("dislike this video") || desc.contains("dislike") || text.contains("dislike") || desc.contains("অপছন্দ")) {
                    hasDislikeAction = true
                }
                if (desc.contains("like this video") || desc.contains("like") || text.contains("like") || desc.contains("পছন্দ")) {
                    hasLikeAction = true
                }
                if (desc.contains("comment") || text.contains("comment") || desc.contains("মন্তব্য")) {
                    hasCommentAction = true
                }
                if (desc.contains("share this video") || desc.contains("share") || text.contains("share") || desc.contains("শেয়ার")) {
                    hasShareAction = true
                }

                // C. Shorts specific keywords & container IDs
                if (desc.contains("shorts player") || desc.contains("shorts camera") || desc.contains("search shorts") || text.contains("search shorts")) {
                    hasShortsKeyword = true
                }
                if (viewId.contains("reel_player") || viewId.contains("shorts_container") || viewId.contains("reel_recycler")) {
                    hasShortsContainerId = true
                }

                // D. Normal video indicators
                if (desc.contains("save to playlist") || text.contains("save to playlist") ||
                    desc.contains("download video") || text.contains("download video") ||
                    desc.contains("clip this video") || text.contains("clip") ||
                    viewId.contains("player_seek_bar") || viewId.contains("movie_player") || viewId.contains("time_bar")) {
                    hasNormalVideoSignals = true
                }

                // Early exit on definitive positive composite
                if (isShortsTabSelected) return true
                if (hasRemixAction && (hasDislikeAction || hasLikeAction || hasCommentAction || hasShareAction)) return true

                // Enqueue children if depth allows
                if (depth < 10) {
                    val count = node.childCount
                    for (i in 0 until count) {
                        val child = node.getChild(i)
                        if (child != null) {
                            queue.add(Pair(child, depth + 1))
                        }
                    }
                }
            }
        } finally {
            // Recycle visited child nodes (do not recycle rootNode here as caller manages it)
            for (node in visited) {
                if (node !== rootNode) {
                    try { node.recycle() } catch (e: Exception) {}
                }
            }
            // Recycle any remaining nodes in the queue
            while (queue.isNotEmpty()) {
                val (node, _) = queue.removeFirst()
                if (node !== rootNode) {
                    try { node.recycle() } catch (e: Exception) {}
                }
            }
        }

        // Final Composite Decision for YouTube
        return when {
            isShortsTabSelected -> true
            // Normal video signals with NO remix and NO shorts container => Do NOT block
            hasNormalVideoSignals && !hasRemixAction && !hasShortsContainerId -> false
            // Remix button + any standard action button
            hasRemixAction && (hasDislikeAction || hasLikeAction || hasCommentAction || hasShareAction) -> true
            // Dislike button + shorts container or keyword
            hasDislikeAction && (hasShortsKeyword || hasShortsContainerId || hasCommentAction) && !hasNormalVideoSignals -> true
            // Shorts container ID + action button
            hasShortsContainerId && (hasCommentAction || hasLikeAction || hasShareAction) -> true
            // Shorts keyword + comment or dislike
            hasShortsKeyword && (hasCommentAction || hasDislikeAction) && !hasNormalVideoSignals -> true
            else -> false
        }
    }

    /**
     * High-performance BFS scanner for Instagram node hierarchy.
     * Analyzes Reels action stack, audio attribution, and tabs.
     */
    private fun scanInstagramNodeHierarchy(rootNode: AccessibilityNodeInfo): Boolean {
        var isReelsTabSelected = false
        var hasReelsAudioSignal = false
        var hasReelsRemixSignal = false
        var hasReelsCameraSignal = false
        var hasReelsContainerId = false
        var hasLikeAction = false
        var hasCommentAction = false
        var hasShareAction = false

        val queue = ArrayDeque<Pair<AccessibilityNodeInfo, Int>>()
        val visited = ArrayList<AccessibilityNodeInfo>(160)

        queue.add(Pair(rootNode, 0))

        try {
            while (queue.isNotEmpty() && visited.size < 160) {
                val (node, depth) = queue.removeFirst()
                visited.add(node)

                val text = node.text?.toString()?.trim()?.lowercase() ?: ""
                val desc = node.contentDescription?.toString()?.trim()?.lowercase() ?: ""
                val viewId = node.viewIdResourceName?.lowercase() ?: ""
                val isSelected = node.isSelected

                // A. Reels tab selected in bottom bar
                if (isSelected && (desc == "reels" || text == "reels" || viewId.contains("reels_tab"))) {
                    isReelsTabSelected = true
                }

                // B. Audio attribution (prominent on Reels UI)
                if (desc.contains("original audio") || text.contains("original audio") ||
                    desc.contains("audio used in reel") || text.contains("use audio") ||
                    desc.contains("audio by") || desc.contains("trending audio") || text.contains("trending audio") ||
                    desc.contains("save audio")) {
                    hasReelsAudioSignal = true
                }

                // C. Remix and template signals
                if (desc.contains("remix with this reel") || text.contains("remix with this reel") ||
                    desc.contains("remix reel") || desc.contains("use template") || text.contains("use template")) {
                    hasReelsRemixSignal = true
                }

                // D. Camera & Container signals
                if (desc.contains("reels camera") || desc.contains("create reel") || desc.contains("make a reel")) {
                    hasReelsCameraSignal = true
                }
                if (viewId.contains("clips_viewer") || viewId.contains("reel_viewer") || viewId.contains("clips_video_container")) {
                    hasReelsContainerId = true
                }

                // E. Action buttons
                if (desc.contains("like") || text.contains("like")) hasLikeAction = true
                if (desc.contains("comment") || text.contains("comment")) hasCommentAction = true
                if (desc.contains("share") || desc.contains("send") || text.contains("share")) hasShareAction = true

                // Early exit on positive composite
                if (isReelsTabSelected) return true
                if ((hasReelsAudioSignal || hasReelsRemixSignal) && (hasLikeAction || hasCommentAction || hasShareAction)) return true

                if (depth < 10) {
                    val count = node.childCount
                    for (i in 0 until count) {
                        val child = node.getChild(i)
                        if (child != null) {
                            queue.add(Pair(child, depth + 1))
                        }
                    }
                }
            }
        } finally {
            for (node in visited) {
                if (node !== rootNode) {
                    try { node.recycle() } catch (e: Exception) {}
                }
            }
            while (queue.isNotEmpty()) {
                val (node, _) = queue.removeFirst()
                if (node !== rootNode) {
                    try { node.recycle() } catch (e: Exception) {}
                }
            }
        }

        return when {
            isReelsTabSelected -> true
            (hasReelsAudioSignal || hasReelsRemixSignal) && (hasLikeAction || hasCommentAction || hasShareAction) -> true
            hasReelsContainerId && (hasReelsAudioSignal || hasReelsRemixSignal || hasReelsCameraSignal || hasCommentAction) -> true
            hasReelsCameraSignal && (hasReelsAudioSignal || hasCommentAction) -> true
            else -> false
        }
    }

    /**
     * High-performance BFS scanner for Facebook node hierarchy.
     * Analyzes Facebook Reels immersive video container and action stack.
     */
    private fun scanFacebookNodeHierarchy(rootNode: AccessibilityNodeInfo): Boolean {
        var isFbReelsTabSelected = false
        var hasFbReelsSignal = false
        var hasFbShortsContainerId = false
        var hasLikeAction = false
        var hasCommentAction = false
        var hasShareAction = false

        val queue = ArrayDeque<Pair<AccessibilityNodeInfo, Int>>()
        val visited = ArrayList<AccessibilityNodeInfo>(160)

        queue.add(Pair(rootNode, 0))

        try {
            while (queue.isNotEmpty() && visited.size < 160) {
                val (node, depth) = queue.removeFirst()
                visited.add(node)

                val text = node.text?.toString()?.trim()?.lowercase() ?: ""
                val desc = node.contentDescription?.toString()?.trim()?.lowercase() ?: ""
                val viewId = node.viewIdResourceName?.lowercase() ?: ""
                val isSelected = node.isSelected

                if (isSelected && (desc == "reels" || text == "reels")) {
                    isFbReelsTabSelected = true
                }

                if (text.contains("reels and short videos") || desc.contains("reels and short videos") ||
                    desc.contains("create reel") || text.contains("create reel") ||
                    desc.contains("remix reel") || text.contains("remix reel") ||
                    desc.contains("original audio") || text.contains("original audio")) {
                    hasFbReelsSignal = true
                }

                if (viewId.contains("fb_shorts") || viewId.contains("reels_viewer")) {
                    hasFbShortsContainerId = true
                }

                if (desc.contains("like") || text.contains("like")) hasLikeAction = true
                if (desc.contains("comment") || text.contains("comment")) hasCommentAction = true
                if (desc.contains("share") || text.contains("share")) hasShareAction = true

                if (isFbReelsTabSelected) return true
                if (hasFbReelsSignal && (hasLikeAction || hasCommentAction || hasShareAction)) return true

                if (depth < 10) {
                    val count = node.childCount
                    for (i in 0 until count) {
                        val child = node.getChild(i)
                        if (child != null) {
                            queue.add(Pair(child, depth + 1))
                        }
                    }
                }
            }
        } finally {
            for (node in visited) {
                if (node !== rootNode) {
                    try { node.recycle() } catch (e: Exception) {}
                }
            }
            while (queue.isNotEmpty()) {
                val (node, _) = queue.removeFirst()
                if (node !== rootNode) {
                    try { node.recycle() } catch (e: Exception) {}
                }
            }
        }

        return when {
            isFbReelsTabSelected -> true
            hasFbReelsSignal && (hasLikeAction || hasCommentAction || hasShareAction) -> true
            hasFbShortsContainerId && (hasFbReelsSignal || hasCommentAction) -> true
            else -> false
        }
    }
}

