package com.example.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NoAdultContent
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppScreenTimeLimit
import com.example.data.BlockedDomain
import com.example.data.CategoryFilter
import com.example.state.FocusViewModel
import com.example.ui.components.AppTimeLimitSliderDialog
import com.example.ui.components.ExpandableShortVideoBlockerCard
import com.example.ui.components.ExpandableSocialMediaBlockerCard
import com.example.ui.components.RealAppIcon
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguri
import com.example.util.AppUsageTracker
import kotlin.math.roundToInt

@Composable
fun BlockerScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadPerAppLimits(context)
        viewModel.refreshRealUsageAndAnalytics(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // --- Header (Clean title and subtitle without the top badge) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "কন্টেন্ট ও ডোমেন ব্লকার",
                color = colors.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "আসক্তি ও ক্ষতিকর কনটেন্ট স্বয়ংক্রিয়ভাবে ফিল্টার করুন",
                color = colors.textSecondary,
                fontSize = 12.5.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- Serial List Section ---
        Text(
            text = "ব্লকিং ও সিকিউরিটি কন্ট্রোল",
            color = colors.textPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Serial Cards List (Dual-state glowing items)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Refactored Minimal App Screen Time Limit Card with Modern Chevron Navigation Icon
            AppScreenTimeLimitExpandableCard(
                viewModel = viewModel,
                context = context
            )

            // Standard Category Filters with Switches & Expandable Sub-App Selectors
            viewModel.categoryFilters.forEach { filter ->
                when (filter.id) {
                    "social_media" -> {
                        ExpandableSocialMediaBlockerCard(
                            filter = filter,
                            viewModel = viewModel,
                            context = context
                        )
                    }
                    "shorts_blocker" -> {
                        ExpandableShortVideoBlockerCard(
                            filter = filter,
                            viewModel = viewModel,
                            context = context
                        )
                    }
                    else -> {
                        CategoryFilterCard(
                            filter = filter,
                            onToggle = { viewModel.toggleCategoryFilter(filter.id) }
                        )
                    }
                }
            }

            // Custom Blocklist Card (Expandable serial layout)
            ExpandableCustomBlocklistCard(
                viewModel = viewModel
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Minimal, Professional App Screen Time Limit Card.
 * Clean layout: No internal extra buttons/sliders.
 * Modern Chevron/Navigation Icon at bottom-right.
 * Smooth expand/slide animation revealing:
 * 1. Overview stats (Limited Apps, Total Usage Today, Locked Apps)
 * 2. Category filtering chips (সবগুলো, সীমা সক্রিয়, সোশ্যাল মিডিয়া, ভিডিও ও বিনোদন, শর্ট ভিডিও)
 * 3. A-B-C alphabetically sorted list of real installed device apps with authentic icons & package names.
 */
@Composable
private fun AppScreenTimeLimitExpandableCard(
    viewModel: FocusViewModel,
    context: Context
) {
    val colors = AppTheme.colors
    var isExpanded by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("সমস্ত অ্যাপস") }

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "chevron_rotation"
    )

    // Modal dialog when an app is tapped to set its screen time limit
    if (viewModel.isAppSliderDialogVisible && viewModel.selectedAppForLimitSlider != null) {
        Dialog(
            onDismissRequest = { viewModel.closeAppSliderDialog() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AppTimeLimitSliderDialog(
                app = viewModel.selectedAppForLimitSlider!!,
                onDismiss = { viewModel.closeAppSliderDialog() },
                onSaveLimit = { minutes, isStrict ->
                    viewModel.saveAppLimit(
                        context = context,
                        packageName = viewModel.selectedAppForLimitSlider!!.packageName,
                        limitMinutes = minutes,
                        isStrict = isStrict
                    )
                },
                onRemoveLimit = {
                    viewModel.removeAppLimit(
                        context = context,
                        packageName = viewModel.selectedAppForLimitSlider!!.packageName
                    )
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(
                1.dp,
                if (isExpanded) Brush.horizontalGradient(listOf(Color(0xFFFFB703), Color(0xFFFF8800)))
                else Brush.horizontalGradient(listOf(colors.borderSubtle, colors.borderSubtle)),
                RoundedCornerShape(18.dp)
            )
            .animateContentSize(
                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
            )
            .testTag("card_blocker_screen_time_limit")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main Minimal Card Content (Clickable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isExpanded = !isExpanded
                        if (isExpanded && viewModel.appScreenTimeLimits.isEmpty()) {
                            viewModel.loadPerAppLimits(context)
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Icon + Clean Title & Subtitle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFF8800).copy(alpha = 0.14f))
                            .border(1.dp, Color(0xFFFF8800).copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Screen Time",
                            tint = Color(0xFFFF8800),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "অ্যাপ স্ক্রিন টাইম লিমিট",
                            color = colors.textPrimary,
                            fontSize = 15.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "প্রতিটি অ্যাপের জন্য দৈনিক সীমা নির্ধারণ ও নিয়ন্ত্রণ করুন",
                            color = colors.textSecondary,
                            fontSize = 11.5.sp,
                            fontFamily = HindSiliguri,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Modern Navigation / Chevron Icon at the right
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (isExpanded) Color(0xFFFF8800).copy(alpha = 0.16f)
                            else colors.surfaceElevated
                        )
                        .border(
                            1.dp,
                            if (isExpanded) Color(0xFFFF8800).copy(alpha = 0.4f)
                            else colors.borderSubtle,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Expand Screen Time Limit",
                        tint = if (isExpanded) Color(0xFFFF8800) else colors.textMuted,
                        modifier = Modifier
                            .size(13.dp)
                            .rotate(chevronRotation)
                    )
                }
            }

            // Expanded Options Panel with Smooth Animation
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(
                        color = colors.borderSubtle,
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    // 1. Overview Summary Stats Row
                    val activeLimitsCount = viewModel.appScreenTimeLimits.count { it.isEnabled && it.limitMinutes > 0 }
                    val totalUsedMins = viewModel.appScreenTimeLimits.sumOf { it.usedMinutesToday }
                    val lockedCount = viewModel.appScreenTimeLimits.count { it.isEnabled && it.limitMinutes > 0 && it.usedMinutesToday >= it.limitMinutes }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.surfaceElevated)
                            .border(1.dp, colors.borderSubtle, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Limited Apps Stat
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "সীমিত অ্যাপস",
                                    color = colors.textSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = HindSiliguri
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$activeLimitsCount টি",
                                    color = colors.textPrimary,
                                    fontSize = 15.sp,
                                    fontFamily = HindSiliguri,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(colors.borderSubtle)
                            )

                            // Total Usage Today Stat (Real Data)
                            Column(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .padding(horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "আজকের মোট ব্যবহার",
                                    color = colors.textSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = HindSiliguri
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${totalUsedMins / 60}ঘ. ${totalUsedMins % 60}মি.",
                                    color = Color(0xFFFF8800),
                                    fontSize = 15.sp,
                                    fontFamily = HindSiliguri,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(colors.borderSubtle)
                            )

                            // Locked Apps Stat
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "লকড অ্যাপস",
                                    color = colors.textSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = HindSiliguri
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (lockedCount > 0) "$lockedCount টি" else "০ টি",
                                    color = if (lockedCount > 0) colors.alert else colors.textPrimary,
                                    fontSize = 15.sp,
                                    fontFamily = HindSiliguri,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Exact 3 Category Filter Chips as requested
                    val filterCategories = listOf("সমস্ত অ্যাপস", "ইতিমধ্যে সিলেক্ট করা", "বর্তমানে ব্লক করা")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filterCategories.forEach { category ->
                            val isSelected = selectedCategoryFilter == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) Color(0xFFFF8800).copy(alpha = 0.16f)
                                        else colors.surfaceElevated
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFFFF8800) else colors.borderSubtle,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedCategoryFilter = category }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = category,
                                    color = if (isSelected) Color(0xFFFF8800) else colors.textSecondary,
                                    fontSize = 11.5.sp,
                                    fontFamily = HindSiliguri,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Real Installed App List (Filtered by selected category)
                    val sortedAndFilteredApps = remember(viewModel.appScreenTimeLimits.toList(), selectedCategoryFilter) {
                        viewModel.appScreenTimeLimits.filter { app ->
                            when (selectedCategoryFilter) {
                                "সমস্ত অ্যাপস" -> true
                                "ইতিমধ্যে সিলেক্ট করা" -> app.isEnabled && app.limitMinutes > 0
                                "বর্তমানে ব্লক করা" -> app.isEnabled && app.limitMinutes > 0 && app.usedMinutesToday >= app.limitMinutes
                                else -> true
                            }
                        }.sortedBy { it.appNameBangla.lowercase() }
                    }

                    if (viewModel.isAppLimitsLoading && viewModel.appScreenTimeLimits.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = Color(0xFFFF8800),
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "অ্যাপস তালিকা লোড হচ্ছে...",
                                    color = colors.textSecondary,
                                    fontSize = 12.sp,
                                    fontFamily = HindSiliguri
                                )
                            }
                        }
                    } else if (sortedAndFilteredApps.isEmpty()) {
                        val emptyMessage = when (selectedCategoryFilter) {
                            "ইতিমধ্যে সিলেক্ট করা" -> "এখনও কোনো অ্যাপ সিলেক্ট করা হয়নি"
                            "বর্তমানে ব্লক করা" -> "বর্তমানে কোনো অ্যাপ ব্লক বা লকড নেই"
                            else -> "কোনো অ্যাপ পাওয়া যায়নি"
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = null,
                                    tint = colors.textMuted,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = emptyMessage,
                                    color = colors.textSecondary,
                                    fontSize = 12.sp,
                                    fontFamily = HindSiliguri
                                )
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            sortedAndFilteredApps.forEach { app ->
                                RealAppScreenTimeRowItem(
                                    app = app,
                                    context = context,
                                    onClick = { viewModel.openAppSliderDialog(app) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual App Item displaying authentic App Icon and App Name only (No Package Name, No Demo Apps).
 * Displays real foreground usage today and active limit / lock state.
 */
@Composable
private fun RealAppScreenTimeRowItem(
    app: AppScreenTimeLimit,
    context: Context,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val hasLimit = app.isEnabled && app.limitMinutes > 0
    val isExceeded = hasLimit && app.usedMinutesToday >= app.limitMinutes

    // Fetch real drawable icon from high-performance cache
    val appIconDrawable: Drawable? = remember(app.packageName) {
        AppUsageTracker.getCachedAppIcon(context, app.packageName)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceElevated)
            .border(
                1.dp,
                if (isExceeded) colors.alert.copy(alpha = 0.45f)
                else if (hasLimit) Color(0xFFFF8800).copy(alpha = 0.3f)
                else colors.borderSubtle,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 11.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Real App Icon + App Name (NO PACKAGE NAME)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RealAppIcon(
                    drawable = appIconDrawable,
                    sizeDp = 42
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = app.appNameBangla,
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (hasLimit) {
                            if (isExceeded) "বর্তমানে ব্লক করা রয়েছে 🔒"
                            else "দৈনিক সীমা: ${app.limitMinutes} মি."
                        } else {
                            "সীমা নির্ধারণ করা নেই"
                        },
                        color = if (isExceeded) colors.alert else if (hasLimit) Color(0xFFFF8800) else colors.textMuted,
                        fontSize = 11.sp,
                        fontFamily = HindSiliguri,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right: Real Usage Today & Limit Status
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${app.usedMinutesToday} মি. ব্যবহৃত",
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                if (hasLimit) {
                    if (isExceeded) {
                        Text(
                            text = "সীমা শেষ",
                            color = colors.alert,
                            fontSize = 11.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        val remaining = (app.limitMinutes - app.usedMinutesToday).coerceAtLeast(0)
                        Text(
                            text = "${remaining} মি. বাকি",
                            color = Color(0xFFFF8800),
                            fontSize = 11.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun CategoryFilterCard(
    filter: CategoryFilter,
    onToggle: () -> Unit
) {
    val colors = AppTheme.colors
    val icon = when (filter.iconType) {
        "adult" -> Icons.Default.NoAdultContent
        "social" -> Icons.Default.Public
        "shorts" -> Icons.Default.PlayCircle
        "security" -> Icons.Default.Shield
        "gambling" -> Icons.Default.Casino
        "app" -> Icons.Default.Apps
        "dns" -> Icons.Default.Dns
        "telegram" -> Icons.Default.Chat
        "power" -> Icons.Default.PowerSettingsNew
        else -> Icons.Default.SportsEsports
    }

    // Dynamic Dual-State Colors & Glow Brushes
    val cardBackgroundBrush = if (filter.isEnabled) {
        if (colors.isDark) {
            Brush.horizontalGradient(
                listOf(
                    Color(0xFF0C241E), // Soft dark emerald
                    Color(0xFF0A1F26), // Soft dark cyan
                    Color(0xFF131822)
                )
            )
        } else {
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFF0FDF4), // Soft emerald glow
                    Color(0xFFECFEFF), // Soft cyan glow
                    Color(0xFFFFFFFF)  // Crisp white
                )
            )
        }
    } else {
        if (colors.isDark) {
            Brush.horizontalGradient(
                listOf(
                    Color(0xFF241417), // Dark red tint glow
                    Color(0xFF1C1316),
                    Color(0xFF16151A)
                )
            )
        } else {
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFFEF2F2), // Soft reddish tint
                    Color(0xFFFFF5F5), // Light warm reddish white
                    Color(0xFFFFFFFF)  // White
                )
            )
        }
    }

    val cardBorderBrush = if (filter.isEnabled) {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF10B981).copy(alpha = 0.75f), // Emerald green glow
                Color(0xFF06B6D4).copy(alpha = 0.65f)  // Cyan blue glow
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                Color(0xFFEF4444).copy(alpha = 0.55f), // Red alert glow
                Color(0xFFF87171).copy(alpha = 0.35f)
            )
        )
    }

    val iconBg = if (filter.isEnabled) {
        Color(0xFF10B981).copy(alpha = 0.18f)
    } else {
        Color(0xFFEF4444).copy(alpha = 0.14f)
    }

    val iconTint = if (filter.isEnabled) {
        Color(0xFF10B981)
    } else {
        Color(0xFFEF4444)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackgroundBrush)
            .border(
                width = 1.2.dp,
                brush = cardBorderBrush,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg)
                        .border(
                            1.dp,
                            if (filter.isEnabled) Color(0xFF10B981).copy(alpha = 0.3f) else Color(0xFFEF4444).copy(alpha = 0.25f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = filter.titleBangla,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.padding(end = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = filter.titleBangla,
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = filter.descBangla,
                        color = if (filter.isEnabled) colors.textSecondary else if (colors.isDark) Color(0xFFFCA5A5) else Color(0xFFDC2626),
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            Switch(
                checked = filter.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF10B981),
                    uncheckedThumbColor = if (colors.isDark) Color(0xFFEF4444) else Color(0xFFDC2626),
                    uncheckedTrackColor = if (colors.isDark) Color(0xFF3B181D) else Color(0xFFFEE2E2),
                    uncheckedBorderColor = Color(0xFFEF4444).copy(alpha = 0.4f)
                ),
                modifier = Modifier.testTag("switch_${filter.id}")
            )
        }

        // If adult filter is enabled, show explicit prompt regarding Advanced DNS
        if (filter.id == "adult" && filter.isEnabled) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0EA5E9).copy(alpha = 0.12f))
                    .border(1.dp, Color(0xFF0EA5E9).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "💡 অনুগ্রহ করে নিচে সিকিউরিটি সেকশনে 'অ্যাডভান্সড প্রাইভেট ডিএনএস' চালু করুন — এতে ডিভাইস লেভেলে শতভাগ ১৮+ কনটেন্ট স্বয়ংক্রিয়ভাবে ব্লক থাকবে।",
                    color = Color(0xFF0284C7),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontFamily = com.example.ui.theme.HindSiliguri
                )
            }
        }
    }
}

/**
 * Expandable Custom Blocklist Card
 * Exactly matches the height and layout of other serial items, but instead of a switch,
 * tapping it smoothly expands to reveal:
 * - Tab switcher (Website / Keyword)
 * - Input text field with Add / Save button
 * - Quick presets
 * - Blocked websites list with delete buttons
 * - Blocked keywords list with delete buttons
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExpandableCustomBlocklistCard(
    viewModel: FocusViewModel
) {
    val colors = AppTheme.colors
    var isExpanded by remember { mutableStateOf(false) }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Website/Domain, 1: Keyword
    var textInput by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val totalBlockedCount = viewModel.customDomains.size + viewModel.customKeywords.size

    val isCustomActive = totalBlockedCount > 0
    val cardBackgroundBrush = if (isCustomActive) {
        if (colors.isDark) {
            Brush.horizontalGradient(
                listOf(
                    Color(0xFF0C241E),
                    Color(0xFF0A1F26),
                    Color(0xFF131822)
                )
            )
        } else {
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFF0FDF4),
                    Color(0xFFECFEFF),
                    Color(0xFFFFFFFF)
                )
            )
        }
    } else {
        if (colors.isDark) {
            Brush.horizontalGradient(
                listOf(
                    Color(0xFF241417),
                    Color(0xFF1C1316),
                    Color(0xFF16151A)
                )
            )
        } else {
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFFEF2F2),
                    Color(0xFFFFF5F5),
                    Color(0xFFFFFFFF)
                )
            )
        }
    }

    val cardBorderBrush = if (isCustomActive) {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF10B981).copy(alpha = 0.75f),
                Color(0xFF06B6D4).copy(alpha = 0.65f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                Color(0xFFEF4444).copy(alpha = 0.55f),
                Color(0xFFF87171).copy(alpha = 0.35f)
            )
        )
    }

    val iconBg = if (isCustomActive) Color(0xFF10B981).copy(alpha = 0.18f) else Color(0xFFEF4444).copy(alpha = 0.14f)
    val iconTint = if (isCustomActive) Color(0xFF10B981) else Color(0xFFEF4444)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackgroundBrush)
            .border(
                width = 1.2.dp,
                brush = cardBorderBrush,
                shape = RoundedCornerShape(16.dp)
            )
            .animateContentSize()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // --- Collapsed / Main Header Row (Matches serial layout) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 14.dp, vertical = 13.dp)
                    .testTag("btn_custom_blocklist_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(iconBg)
                            .border(
                                1.dp,
                                if (isCustomActive) Color(0xFF10B981).copy(alpha = 0.3f) else Color(0xFFEF4444).copy(alpha = 0.25f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Custom Blocklist",
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.padding(end = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "কাস্টম ব্লকলিস্ট",
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isCustomActive) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$totalBlockedCount টি রুল",
                                    color = if (isCustomActive) Color(0xFF10B981) else Color(0xFFEF4444),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ওয়েবসাইট ও কিওয়ার্ড ব্লক করার অপশন এবং তালিকা",
                            color = if (isCustomActive) colors.textSecondary else if (colors.isDark) Color(0xFFFCA5A5) else Color(0xFFDC2626),
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Expand/Collapse Chevron Indicator
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceElevated)
                        .border(1.dp, colors.borderLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = if (isExpanded) colors.primaryBright else colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // --- Expanded Section (Opens smoothly underneath) ---
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    HorizontalDivider(color = colors.border, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Selector: Website vs Keyword
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = colors.surfaceElevated,
                        contentColor = colors.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                                color = colors.primary
                            )
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = {
                                activeTab = 0
                                textInput = ""
                                validationError = null
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ওয়েবসাইট (${viewModel.customDomains.size})",
                                        color = if (activeTab == 0) colors.primaryBright else colors.textSecondary,
                                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        fontFamily = HindSiliguri
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = {
                                activeTab = 1
                                textInput = ""
                                validationError = null
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "কিওয়ার্ড (${viewModel.customKeywords.size})",
                                        color = if (activeTab == 1) colors.primaryBright else colors.textSecondary,
                                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        fontFamily = HindSiliguri
                                    )
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input Box + Save Button (Aligned on the same line, equal height, wide left input, compact right button)
                    Text(
                        text = if (activeTab == 0) "ওয়েবসাইট লিংক বা ডোমেইন লিখুন" else "সার্চ কিওয়ার্ড লিখুন",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Left Input Box: Wide, modern, matching button height exactly
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surfaceElevated)
                                .border(
                                    1.dp,
                                    if (validationError != null) colors.alert.copy(alpha = 0.85f) else colors.border,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (activeTab == 0) Icons.Default.Public else Icons.Default.Tag,
                                    contentDescription = null,
                                    tint = if (validationError != null) colors.alert else colors.primary,
                                    modifier = Modifier.size(17.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (textInput.isEmpty()) {
                                        Text(
                                            text = if (activeTab == 0) "examplewebsite.com" else "যেমন: keyword",
                                            color = colors.textMuted.copy(alpha = 0.55f),
                                            fontSize = 12.5.sp,
                                            fontFamily = HindSiliguri
                                        )
                                    }
                                    BasicTextField(
                                        value = textInput,
                                        onValueChange = { input ->
                                            textInput = input
                                            if (validationError != null) validationError = null
                                        },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            color = colors.textPrimary,
                                            fontSize = 13.sp,
                                            fontFamily = HindSiliguri,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        cursorBrush = SolidColor(colors.primary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_blocklist_text")
                                    )
                                }

                                if (textInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            textInput = ""
                                            validationError = null
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = colors.textMuted,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Right Save Button: Compact, equal height (46.dp), perfectly aligned
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.primary)
                                .clickable {
                                    if (activeTab == 0) {
                                        val isValid = viewModel.cleanAndValidateDomain(textInput) != null
                                        if (!isValid) {
                                            validationError = "দয়া করে সঠিক ওয়েবসাইট লিংক বা ডোমেইন লিখুন (যেমন: examplewebsite.com)"
                                            viewModel.showToast("দয়া করে সঠিক ওয়েবসাইট লিংক বা ডোমেইন লিখুন (যেমন: examplewebsite.com)")
                                        } else {
                                            val saved = viewModel.addCustomDomain(textInput)
                                            if (saved) {
                                                textInput = ""
                                                validationError = null
                                            }
                                        }
                                    } else {
                                        if (textInput.isBlank()) {
                                            validationError = "দয়া করে একটি কিওয়ার্ড লিখুন"
                                            viewModel.showToast("দয়া করে একটি কিওয়ার্ড লিখুন")
                                        } else {
                                            val saved = viewModel.addCustomKeyword(textInput)
                                            if (saved) {
                                                textInput = ""
                                                validationError = null
                                            }
                                        }
                                    }
                                }
                                .padding(horizontal = 14.dp)
                                .testTag("btn_save_blocklist_item"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save",
                                    tint = if (colors.isDark) Color(0xFF0D1117) else Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "সংরক্ষণ করুন",
                                    color = if (colors.isDark) Color(0xFF0D1117) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    fontFamily = HindSiliguri
                                )
                            }
                        }
                    }

                    if (validationError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = validationError!!,
                            color = colors.alert,
                            fontSize = 11.sp,
                            fontFamily = HindSiliguri
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Blocked Items Summary & List with Delete Buttons ---
                    if (activeTab == 0) {
                        // Websites / Domains List
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ব্লক করা ওয়েবসাইট সমূহ (${viewModel.customDomains.size})",
                                color = colors.textPrimary,
                                fontSize = 13.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (viewModel.customDomains.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surfaceElevated)
                                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
                                    .padding(vertical = 18.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = colors.textMuted.copy(alpha = 0.5f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "কোনো ওয়েবসাইট এখনও ব্লক করা হয়নি",
                                        color = colors.textSecondary,
                                        fontSize = 12.sp,
                                        fontFamily = HindSiliguri,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "উপরের বক্সে লিংক লিখে “সংরক্ষণ করুন” চাপুন",
                                        color = colors.textMuted,
                                        fontSize = 11.sp,
                                        fontFamily = HindSiliguri
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                viewModel.customDomains.forEach { domainItem ->
                                    CustomDomainRow(
                                        item = domainItem,
                                        onDelete = { viewModel.removeCustomDomain(domainItem.id) }
                                    )
                                }
                            }
                        }
                    } else {
                        // Keywords List
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ব্লক করা কিওয়ার্ড সমূহ (${viewModel.customKeywords.size})",
                                color = colors.textPrimary,
                                fontSize = 13.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (viewModel.customKeywords.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surfaceElevated)
                                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
                                    .padding(vertical = 18.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Tag,
                                        contentDescription = null,
                                        tint = colors.textMuted.copy(alpha = 0.5f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "কোনো কিওয়ার্ড এখনও ব্লক করা হয়নি",
                                        color = colors.textSecondary,
                                        fontSize = 12.sp,
                                        fontFamily = HindSiliguri,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "উপরের বক্সে কিওয়ার্ড লিখে “সংরক্ষণ করুন” চাপুন",
                                        color = colors.textMuted,
                                        fontSize = 11.sp,
                                        fontFamily = HindSiliguri
                                    )
                                }
                            }
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                viewModel.customKeywords.forEach { keyword ->
                                    RemovableKeywordChip(
                                        keyword = keyword,
                                        onDelete = { viewModel.removeCustomKeyword(keyword) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CustomDomainRow(
    item: BlockedDomain,
    onDelete: () -> Unit
) {
    val colors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = item.domain,
                    color = colors.textPrimary,
                    fontSize = 13.5.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (item.blockedCount > 0) "${item.blockedCount} বার প্রতিহত • ${item.addedTimeAgo}" else item.addedTimeAgo,
                    color = colors.textMuted,
                    fontSize = 11.sp,
                    fontFamily = HindSiliguri
                )
            }
        }

        // Delete Button
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(34.dp)
                .testTag("btn_delete_domain_${item.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = colors.alert.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun RemovableKeywordChip(
    keyword: String,
    onDelete: () -> Unit
) {
    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.alert.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(start = 10.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Tag,
                contentDescription = null,
                tint = colors.alert,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = keyword,
                color = colors.textPrimary,
                fontSize = 12.5.sp,
                fontFamily = HindSiliguri,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("btn_delete_kw_$keyword")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Keyword",
                    tint = colors.alert,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
