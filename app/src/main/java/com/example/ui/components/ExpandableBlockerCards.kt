package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CategoryFilter
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguri
import com.example.util.FocusLockPreferences

/**
 * Data specification for individual apps/services inside Social Media or Short Video blockers
 */
data class BlockableAppItem(
    val name: String,
    val packageName: String,
    val desc: String,
    val category: String, // "social" or "shorts"
    val isReelsOnly: Boolean = false
)

/**
 * Comprehensive list of popular Social Media platforms.
 * Covers both popular and lightweight/replacement variants.
 */
val POPULAR_SOCIAL_MEDIA_APPS = listOf(
    BlockableAppItem("Facebook", "com.facebook.katana", "নিউজফিড, পোস্ট ও মেসেজিং", "social"),
    BlockableAppItem("Facebook Lite", "com.facebook.lite", "লাইটওয়েট ফেসবুক ক্লায়েন্ট", "social"),
    BlockableAppItem("Instagram", "com.instagram.android", "ফটো, স্টোরি ও ফিড স্ক্রলিং", "social"),
    BlockableAppItem("Instagram Lite", "com.instagram.lite", "লাইট সংস্করণ ইনস্টাগ্রাম", "social"),
    BlockableAppItem("TikTok", "com.zhiliaoapp.musically", "জনপ্রিয় ভাইরাল ভিডিও ও সোশাল ফিড", "social"),
    BlockableAppItem("TikTok Lite", "com.zhiliaoapp.musically.go", "কম ডেটা ব্যবহারকারী টিকটক", "social"),
    BlockableAppItem("X (Twitter)", "com.twitter.android", "মাইক্রোব্লগিং ও ট্রেন্ডিং টপিক", "social"),
    BlockableAppItem("Pinterest", "com.pinterest", "আইডিয়া, ইমেজ পিন ও স্ক্রল বোর্ড", "social"),
    BlockableAppItem("Snapchat", "com.snapchat.android", "স্ন্যাপ, ফিল্টার ও স্টোরি", "social"),
    BlockableAppItem("Reddit", "com.reddit.frontpage", "কমিউনিটি ডিসকাশন ও মিডিয়া ফিড", "social"),
    BlockableAppItem("Threads", "com.instagram.barcelona", "ইনস্টাগ্রাম থ্রেডস টেক্সট কনভারসেশন", "social"),
    BlockableAppItem("Likee", "video.like", "সোশ্যাল ভিডিও ও লাইভ স্ট্রিম", "social"),
    BlockableAppItem("Likee Lite", "video.like.lite", "লাইটওয়েট লাইকি ভিডিও", "social"),
    BlockableAppItem("Tumblr", "com.tumblr", "সোশ্যাল ব্লগিং ও মিডিয়া ব্রাউজিং", "social"),
    BlockableAppItem("VK (ВКонтакте)", "com.vkontakte.android", "সোশ্যাল নেটওয়ার্ক ও মেসেজিং", "social"),
    BlockableAppItem("Discord", "com.discord", "কমিউনিটি সার্ভার ও চ্যাট ফিড", "social")
)

/**
 * Comprehensive list of popular Short Video / Reels platforms & specific features.
 */
val POPULAR_SHORT_VIDEO_APPS = listOf(
    BlockableAppItem("Facebook Reels", "com.facebook.katana.reels", "ফেসবুক রিলস ও ওয়াচ শর্ট ফিড", "shorts", isReelsOnly = true),
    BlockableAppItem("Instagram Reels", "com.instagram.android.reels", "ইনস্টাগ্রাম অ্যালগরিদমিক রিলস ফিড", "shorts", isReelsOnly = true),
    BlockableAppItem("YouTube Shorts", "com.google.android.youtube.shorts", "ইউটিউব শর্টস ফিড ও ইনফিনিট স্ক্রল", "shorts", isReelsOnly = true),
    BlockableAppItem("TikTok", "com.zhiliaoapp.musically", "ইনফিনিট শর্ট-ফর্ম ভিডিও লুপ", "shorts"),
    BlockableAppItem("TikTok Lite", "com.zhiliaoapp.musically.go", "লাইটওয়েট শর্ট ভিডিও স্ক্রলার", "shorts"),
    BlockableAppItem("Likee", "video.like", "শর্ট ভিডিও, ডুয়েট ও এফেক্ট ফিড", "shorts"),
    BlockableAppItem("Likee Lite", "video.like.lite", "ছোট সাইজের লাইকি শর্টস", "shorts"),
    BlockableAppItem("Snapchat Spotlight", "com.snapchat.android.spotlight", "স্ন্যাপচ্যাট স্পটলাইট শর্ট ভিডিও", "shorts", isReelsOnly = true),
    BlockableAppItem("Moj (শর্ট ভিডিও)", "in.moj.app", "জনপ্রিয় লোকাল শর্ট ভিডিও প্ল্যাটফর্ম", "shorts"),
    BlockableAppItem("Moj Lite+", "in.moj.app.lite", "লাইট সংস্করণ মজ ভিডিও", "shorts"),
    BlockableAppItem("Josh (শর্ট ভিডিও)", "com.eterno.shortvideos", "ভাইরাল শর্টস ও বিনোদন ভিডিও", "shorts"),
    BlockableAppItem("Kwai (শর্ট ভিডিও)", "com.kwai.video", "ইনফিনিট শর্ট ভিডিও ব্রাউজিং", "shorts")
)

/**
 * Modern Expandable Social Media Blocker Card with individual app toggles.
 * Direct switch is removed from the header. Expanding reveals individual toggles
 * for both installed apps and pre-emptive blocking of all popular social media apps.
 */
@Composable
fun ExpandableSocialMediaBlockerCard(
    filter: CategoryFilter,
    viewModel: FocusViewModel,
    context: Context
) {
    val colors = AppTheme.colors
    val prefs = remember { FocusLockPreferences.getInstance(context) }
    var isExpanded by remember { mutableStateOf(false) }

    // Read saved blocked social media packages (Starts 100% empty/OFF by default)
    val savedBlockedSocial = remember { prefs.getBlockedSocialPackages() }

    // Map of packageName to blocked state synced with FocusLockPreferences (All OFF initially)
    val blockedAppsMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            POPULAR_SOCIAL_MEDIA_APPS.forEach { appItem ->
                put(appItem.packageName, savedBlockedSocial.contains(appItem.packageName))
            }
        }
    }

    val activeCount = blockedAppsMap.count { it.value }

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "chevron_rotation_social"
    )

    // Dynamic glow border and background
    val hasAnyBlocked = activeCount > 0
    val cardBackgroundBrush = if (hasAnyBlocked) {
        if (colors.isDark) {
            Brush.horizontalGradient(
                listOf(Color(0xFF0C241E), Color(0xFF0A1F26), Color(0xFF131822))
            )
        } else {
            Brush.horizontalGradient(
                listOf(Color(0xFFF0FDF4), Color(0xFFECFEFF), Color(0xFFFFFFFF))
            )
        }
    } else {
        if (colors.isDark) {
            Brush.horizontalGradient(
                listOf(Color(0xFF241417), Color(0xFF1C1316), Color(0xFF16151A))
            )
        } else {
            Brush.horizontalGradient(
                listOf(Color(0xFFFEF2F2), Color(0xFFFFF5F5), Color(0xFFFFFFFF))
            )
        }
    }

    val cardBorderBrush = if (isExpanded) {
        Brush.horizontalGradient(
            listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))
        )
    } else if (hasAnyBlocked) {
        Brush.horizontalGradient(
            listOf(Color(0xFF10B981).copy(alpha = 0.75f), Color(0xFF06B6D4).copy(alpha = 0.65f))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color(0xFFEF4444).copy(alpha = 0.55f), Color(0xFFF87171).copy(alpha = 0.35f))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBackgroundBrush)
            .border(width = 1.2.dp, brush = cardBorderBrush, shape = RoundedCornerShape(18.dp))
            .animateContentSize(animationSpec = tween(320, easing = FastOutSlowInEasing))
            .testTag("card_expandable_social_media")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Tap to expand/collapse (NO master switch)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 15.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Icon + Titles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF3B82F6).copy(alpha = 0.16f))
                            .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Social Media Blocker",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.padding(end = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "সোশ্যাল মিডিয়া ব্লকার (Social Media)",
                                color = colors.textPrimary,
                                fontSize = 14.5.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (activeCount > 0) "$activeCount টি প্ল্যাটফর্ম ব্লক নির্বাচিত" else "কোনো অ্যাপ এখনো ব্লক করা হয়নি (ট্যাপ করে খুলুন)",
                            color = if (activeCount > 0) Color(0xFF10B981) else colors.textSecondary,
                            fontSize = 11.sp,
                            fontFamily = HindSiliguri,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Right: Modern Chevron indicating expandable list
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (isExpanded) Color(0xFF3B82F6).copy(alpha = 0.15f)
                            else colors.surfaceElevated
                        )
                        .border(
                            1.dp,
                            if (isExpanded) Color(0xFF3B82F6).copy(alpha = 0.4f)
                            else colors.borderSubtle,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Expand Social Media Apps",
                        tint = if (isExpanded) Color(0xFF3B82F6) else colors.textMuted,
                        modifier = Modifier
                            .size(13.dp)
                            .rotate(chevronRotation)
                    )
                }
            }

            // Expandable List of Social Media Apps
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
                ) {
                    HorizontalDivider(
                        color = colors.borderSubtle,
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Informational guidance note
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF3B82F6).copy(alpha = 0.08f))
                            .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "💡 যেকোনো অ্যাপ আলাদাভাবে অন/অফ করুন। যেসব অ্যাপ নির্বাচন করবেন শুধুমাত্র সেগুলোই ব্লক থাকবে। এমনকি পরবর্তীতে ডাউনলোড করলেও পূর্বে ব্লক সক্রিয় থাকলে খুলবে না।",
                            color = if (colors.isDark) Color(0xFF93C5FD) else Color(0xFF1D4ED8),
                            fontSize = 11.sp,
                            fontFamily = HindSiliguri,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // List of apps with individual switches
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        POPULAR_SOCIAL_MEDIA_APPS.forEach { appItem ->
                            val isAppBlocked = blockedAppsMap[appItem.packageName] ?: false
                            BlockableAppRowItem(
                                appItem = appItem,
                                isBlocked = isAppBlocked,
                                context = context,
                                onToggle = {
                                    val newState = !isAppBlocked
                                    blockedAppsMap[appItem.packageName] = newState
                                    prefs.setSocialPackageBlocked(appItem.packageName, newState)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern Expandable Short Video / Reels Blocker Card with individual app toggles.
 * Direct switch is removed from header. Expanding reveals individual toggles
 * for YouTube Shorts, Instagram Reels, Facebook Reels, TikTok, Likee, Moj, etc.
 */
@Composable
fun ExpandableShortVideoBlockerCard(
    filter: CategoryFilter,
    viewModel: FocusViewModel,
    context: Context
) {
    val colors = AppTheme.colors
    var isExpanded by remember { mutableStateOf(false) }

    val prefs = remember { FocusLockPreferences.getInstance(context) }
    val savedBlockedShorts = remember { prefs.getBlockedShortsPackages() }

    // Map of packageName to blocked state synced with FocusLockPreferences
    val blockedShortsMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            POPULAR_SHORT_VIDEO_APPS.forEach { appItem ->
                put(appItem.packageName, savedBlockedShorts.contains(appItem.packageName))
            }
        }
    }

    val activeCount = blockedShortsMap.count { it.value }

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "chevron_rotation_shorts"
    )

    // Dynamic glow border and background
    val hasAnyBlocked = activeCount > 0
    val cardBackgroundBrush = if (hasAnyBlocked) {
        if (colors.isDark) {
            Brush.horizontalGradient(
                listOf(Color(0xFF1C1322), Color(0xFF221128), Color(0xFF16151A))
            )
        } else {
            Brush.horizontalGradient(
                listOf(Color(0xFFFAF5FF), Color(0xFFFDF4FF), Color(0xFFFFFFFF))
            )
        }
    } else {
        if (colors.isDark) {
            Brush.horizontalGradient(
                listOf(Color(0xFF241417), Color(0xFF1C1316), Color(0xFF16151A))
            )
        } else {
            Brush.horizontalGradient(
                listOf(Color(0xFFFEF2F2), Color(0xFFFFF5F5), Color(0xFFFFFFFF))
            )
        }
    }

    val cardBorderBrush = if (isExpanded) {
        Brush.horizontalGradient(
            listOf(Color(0xFFA855F7), Color(0xFFEC4899))
        )
    } else if (hasAnyBlocked) {
        Brush.horizontalGradient(
            listOf(Color(0xFFA855F7).copy(alpha = 0.75f), Color(0xFFEC4899).copy(alpha = 0.65f))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color(0xFFEF4444).copy(alpha = 0.55f), Color(0xFFF87171).copy(alpha = 0.35f))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBackgroundBrush)
            .border(width = 1.2.dp, brush = cardBorderBrush, shape = RoundedCornerShape(18.dp))
            .animateContentSize(animationSpec = tween(320, easing = FastOutSlowInEasing))
            .testTag("card_expandable_shorts_blocker")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Tap to expand/collapse (NO master switch)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 15.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Icon + Titles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFA855F7).copy(alpha = 0.16f))
                            .border(1.dp, Color(0xFFA855F7).copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Short Video Blocker",
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.padding(end = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "শর্ট ভিডিও ও রিলস ব্লকার (Reels/Shorts)",
                                color = colors.textPrimary,
                                fontSize = 14.5.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (activeCount > 0) "$activeCount টি রিলস/শর্টস সেবা ব্লক সক্রিয়" else "কোনো শর্টস এখনো ব্লক করা হয়নি (ট্যাপ করে খুলুন)",
                            color = if (activeCount > 0) Color(0xFFA855F7) else colors.textSecondary,
                            fontSize = 11.sp,
                            fontFamily = HindSiliguri,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Right: Modern Chevron indicating expandable list
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (isExpanded) Color(0xFFA855F7).copy(alpha = 0.15f)
                            else colors.surfaceElevated
                        )
                        .border(
                            1.dp,
                            if (isExpanded) Color(0xFFA855F7).copy(alpha = 0.4f)
                            else colors.borderSubtle,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Expand Short Video Apps",
                        tint = if (isExpanded) Color(0xFFA855F7) else colors.textMuted,
                        modifier = Modifier
                            .size(13.dp)
                            .rotate(chevronRotation)
                    )
                }
            }

            // Expandable List of Short Video Apps & Services
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
                ) {
                    HorizontalDivider(
                        color = colors.borderSubtle,
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Informational guidance note
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFA855F7).copy(alpha = 0.08f))
                            .border(1.dp, Color(0xFFA855F7).copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "💡 আসক্তিকর রিলস ও শর্টস ফিডগুলো আলাদাভাবে নিয়ন্ত্রণ করুন। আপনার প্রয়োজনীয় অংশ চালু রেখে কেবল সময় অপচয়কারী শর্ট ভিডিও বন্ধ রাখতে পারবেন।",
                            color = if (colors.isDark) Color(0xFFD8B4FE) else Color(0xFF7E22CE),
                            fontSize = 11.sp,
                            fontFamily = HindSiliguri,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // List of short video services with individual switches
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        POPULAR_SHORT_VIDEO_APPS.forEach { appItem ->
                            val isAppBlocked = blockedShortsMap[appItem.packageName] ?: false
                            BlockableAppRowItem(
                                appItem = appItem,
                                isBlocked = isAppBlocked,
                                context = context,
                                onToggle = {
                                    val newState = !isAppBlocked
                                    blockedShortsMap[appItem.packageName] = newState
                                    prefs.setShortsPackageBlocked(appItem.packageName, newState)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Clean, modern list item displaying an individual App's icon, name,
 * description, installed status badge, and independent toggle switch.
 */
@Composable
fun BlockableAppRowItem(
    appItem: BlockableAppItem,
    isBlocked: Boolean,
    context: Context,
    onToggle: () -> Unit
) {
    val colors = AppTheme.colors

    // Check if the app is actually installed on the physical device
    val basePackage = if (appItem.isReelsOnly) {
        appItem.packageName.substringBeforeLast(".")
    } else {
        appItem.packageName
    }

    val isInstalled = remember(basePackage) {
        try {
            context.packageManager.getPackageInfo(basePackage, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    val appIconDrawable = remember(basePackage, isInstalled) {
        if (isInstalled) {
            try {
                context.packageManager.getApplicationIcon(basePackage)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isBlocked) {
                    if (colors.isDark) Color(0xFF1E293B).copy(alpha = 0.5f) else Color(0xFFF8FAFC)
                } else colors.surfaceElevated
            )
            .border(
                1.dp,
                if (isBlocked) {
                    Color(0xFF10B981).copy(alpha = 0.35f)
                } else colors.borderSubtle,
                RoundedCornerShape(12.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Icon + Name + Description
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (appIconDrawable != null) {
                    RealAppIcon(
                        drawable = appIconDrawable,
                        sizeDp = 38
                    )
                } else {
                    // Fallback visual icon box
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (appItem.category == "shorts") Color(0xFFA855F7).copy(alpha = 0.14f)
                                else Color(0xFF3B82F6).copy(alpha = 0.14f)
                            )
                            .border(
                                1.dp,
                                if (appItem.category == "shorts") Color(0xFFA855F7).copy(alpha = 0.3f)
                                else Color(0xFF3B82F6).copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (appItem.category == "shorts") Icons.Default.PlayCircle else Icons.Default.Public,
                            contentDescription = appItem.name,
                            tint = if (appItem.category == "shorts") Color(0xFFA855F7) else Color(0xFF3B82F6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = appItem.name,
                            color = colors.textPrimary,
                            fontSize = 13.5.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Status badge (ইনস্টলড or ক্লাউড ব্লকিং)
                        if (isInstalled) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.12f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "ইনস্টলড",
                                    color = Color(0xFF10B981),
                                    fontSize = 9.sp,
                                    fontFamily = HindSiliguri,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = appItem.desc,
                        color = colors.textSecondary,
                        fontSize = 10.5.sp,
                        fontFamily = HindSiliguri,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Independent Switch
            Switch(
                checked = isBlocked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF10B981),
                    uncheckedThumbColor = if (colors.isDark) Color(0xFFEF4444) else Color(0xFFDC2626),
                    uncheckedTrackColor = if (colors.isDark) Color(0xFF3B181D) else Color(0xFFFEE2E2),
                    uncheckedBorderColor = Color(0xFFEF4444).copy(alpha = 0.4f)
                ),
                modifier = Modifier.testTag("switch_app_${appItem.packageName}")
            )
        }
    }
}
