package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WavingHand
import androidx.compose.material.icons.rounded.PanTool
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FocusLockState
import com.example.data.NavigationTab
import com.example.data.RecentActivity
import com.example.state.FocusViewModel
import com.example.ui.components.RadarShieldCard
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EditorialTypography
import com.example.ui.theme.HindSiliguri

@Composable
fun HomeScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val context = LocalContext.current
    val strings = com.example.util.LocalAppStrings.current

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.bindContext(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // 1. Greeting Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = strings.homeGreeting,
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Rounded.WavingHand,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = strings.homeSavedTimePrefix,
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        fontFamily = HindSiliguri
                    )
                    Text(
                        text = viewModel.savedHoursToday,
                        color = colors.primaryBright,
                        fontSize = 13.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Date pill (Soft glassmorphic capsule)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                colors.surfaceElevated.copy(alpha = 0.9f),
                                colors.surface.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(24.dp))
                    .padding(horizontal = 13.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Date",
                        tint = colors.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = viewModel.todayBanglaDate,
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Animated Pulse Radar Shield Card
        RadarShieldCard(viewModel = viewModel)

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Clean Streak Banner Card (24dp Radius with Soft Glow Border)
        StreakBanner(
            streakDays = viewModel.streakDays,
            onBadgesClick = { viewModel.isBadgesDialogVisible = true }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Daily Mindful Quote Card (Uses Noto Serif Bengali for Editorial Reading)
        DailyMindfulQuoteCard()

        Spacer(modifier = Modifier.height(22.dp))

        // 5. Quick Actions (কুইক অ্যাকশন) - 2 Primary Action Cards (Focus Lock & On-Time Block)
        Text(
            text = strings.homeQuickActionsTitle,
            color = colors.textPrimary,
            fontSize = 17.sp,
            fontFamily = HindSiliguri,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Focus Lock Card
            QuickActionCard(
                title = strings.quickActionFocusLock,
                subtitle = if (viewModel.focusLockState == FocusLockState.ACTIVE) strings.quickActionFocusLockActive else strings.quickActionFocusLockSetup,
                icon = Icons.Default.Lock,
                iconTint = colors.primary,
                iconBg = colors.primary.copy(alpha = 0.14f),
                isActive = viewModel.focusLockState == FocusLockState.ACTIVE,
                onClick = {
                    viewModel.triggerFocusLockQuickAction(context)
                },
                modifier = Modifier.weight(1f),
                testTag = "action_focus_lock"
            )

            // 2. On-Time Block Card
            QuickActionCard(
                title = strings.quickActionOneTime,
                subtitle = if (viewModel.isQuickBlockNowActive) strings.quickActionOneTimeActive else strings.quickActionOneTimeDesc,
                icon = Icons.Default.Block,
                iconTint = colors.alert,
                iconBg = colors.alert.copy(alpha = 0.14f),
                isActive = viewModel.isQuickBlockNowActive,
                onClick = {
                    viewModel.toggleQuickBlockNow()
                    viewModel.showToast(
                        if (viewModel.isQuickBlockNowActive) strings.toastInstantBlockApplied else strings.toastBlockRemoved
                    )
                },
                modifier = Modifier.weight(1f),
                testTag = "action_one_time_block"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 7. Recent Activity (সাম্প্রতিক অ্যাক্টিভিটি)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.homeRecentActivity,
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = { viewModel.clearActivities() },
                modifier = Modifier.testTag("btn_clear_activities")
            ) {
                Text(
                    text = strings.homeRecentClear,
                    color = colors.textMuted,
                    fontSize = 13.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            colors.surfaceElevated.copy(alpha = 0.7f),
                            colors.surface
                        )
                    )
                )
                .border(1.dp, colors.borderSubtle, RoundedCornerShape(22.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (viewModel.activities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.homeRecentEmpty,
                        color = colors.textMuted,
                        fontSize = 13.sp,
                        fontFamily = HindSiliguri
                    )
                }
            } else {
                viewModel.activities.forEach { activity ->
                    ActivityRowItem(activity = activity)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun DailyMindfulQuoteCard() {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (colors.isDark) {
                        listOf(
                            colors.surfaceElevated.copy(alpha = 0.6f),
                            colors.surface.copy(alpha = 0.85f)
                        )
                    } else {
                        listOf(
                            colors.surfaceElevated.copy(alpha = 0.5f),
                            colors.surface
                        )
                    }
                )
            )
            .border(1.dp, colors.borderSubtle, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.FormatQuote,
                    contentDescription = "Quote Icon",
                    tint = colors.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.quoteBody,
                    style = EditorialTypography.quoteBody,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.quoteAuthor,
                    style = EditorialTypography.quoteAuthor,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun StreakBanner(
    streakDays: Int,
    onBadgesClick: () -> Unit
) {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (colors.isDark) {
                        listOf(
                            Color(0xFF261C14),
                            colors.surfaceElevated.copy(alpha = 0.7f),
                            colors.surface
                        )
                    } else {
                        listOf(
                            Color(0xFFFFF6ED),
                            colors.surfaceElevated.copy(alpha = 0.5f),
                            colors.surface
                        )
                    }
                )
            )
            .border(
                1.dp,
                if (colors.isDark) Color(0x33D4984F) else Color(0x33C78434),
                RoundedCornerShape(24.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (colors.isDark) Color(0xFF332316) else Color(0xFFFFEBD5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = "Streak Fire",
                tint = colors.warning,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$streakDays ",
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = strings.streakClean,
                    color = colors.warning,
                    fontSize = 14.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = colors.warning,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = strings.streakMsg,
                color = colors.textSecondary,
                fontSize = 12.sp,
                fontFamily = HindSiliguri
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (colors.isDark) Color(0xFF2E2117) else Color(0xFFFFF0DC))
                .border(
                    1.dp,
                    if (colors.isDark) Color(0x44D4984F) else Color(0x44C78434),
                    RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onBadgesClick)
                .padding(horizontal = 12.dp, vertical = 7.dp)
                .testTag("btn_badges")
        ) {
            Text(
                text = strings.streakBadges,
                color = colors.warning,
                fontSize = 12.sp,
                fontFamily = HindSiliguri,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val colors = AppTheme.colors
    Box(
        modifier = modifier
            .height(82.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isActive) {
                    if (colors.isDark) Color(0xFF16252C) else colors.primary.copy(alpha = 0.1f)
                } else {
                    colors.surfaceElevated.copy(alpha = 0.6f)
                }
            )
            .border(
                1.dp,
                if (isActive) iconTint.copy(alpha = 0.5f) else colors.borderSubtle,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag(testTag),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontSize = 13.5.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = colors.textSecondary,
                    fontSize = 11.sp,
                    fontFamily = HindSiliguri,
                    lineHeight = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ActivityRowItem(
    activity: RecentActivity
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceElevated.copy(alpha = 0.8f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (activity.isSuccess) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = colors.secondary,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(colors.alert.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        color = colors.alert,
                        fontSize = 10.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = activity.titleBangla,
                color = colors.textPrimary,
                fontSize = 13.sp,
                fontFamily = HindSiliguri,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = activity.timeAgoBangla,
            color = colors.textMuted,
            fontSize = 11.sp,
            fontFamily = HindSiliguri
        )
    }
}
