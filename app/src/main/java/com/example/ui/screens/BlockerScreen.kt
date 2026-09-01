package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Chat
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BlockedDomain
import com.example.data.CategoryFilter
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme

@Composable
fun BlockerScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

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
            // Per-App Screen Time Limit Action Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.surface)
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(Color(0xFFFFB703), Color(0xFFFF8800))),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable { viewModel.isScreenTimeLimitDialogVisible = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("card_blocker_screen_time_limit")
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
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFF8800).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFFF8800).copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "অ্যাপ স্ক্রিন টাইম লিমিট",
                                    color = colors.textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFF8800).copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Circular Slider",
                                        color = Color(0xFFFF8800),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "প্রতিটি অ্যাপের জন্য ২৪ ঘণ্টার সীমা নির্ধারণ ও ম্যানেজ করুন",
                                color = colors.textSecondary,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF8800).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Configure",
                            tint = Color(0xFFFF8800),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Standard Category Filters with Switches
            viewModel.categoryFilters.forEach { filter ->
                CategoryFilterCard(
                    filter = filter,
                    onToggle = { viewModel.toggleCategoryFilter(filter.id) }
                )
            }

            // Custom Blocklist Card (Expandable serial layout)
            ExpandableCustomBlocklistCard(
                viewModel = viewModel
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
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

    Row(
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
            .padding(horizontal = 14.dp, vertical = 13.dp),
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

    val presetWebsites = listOf("tiktok.com", "instagram.com", "reddit.com", "x.com", "twitch.tv")
    val presetKeywords = listOf("casino", "betting", "adult", "porn", "shorts", "dating")

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
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ওয়েবসাইট (${viewModel.customDomains.size})",
                                        color = if (activeTab == 0) colors.primaryBright else colors.textSecondary,
                                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = {
                                activeTab = 1
                                textInput = ""
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "কিওয়ার্ড (${viewModel.customKeywords.size})",
                                        color = if (activeTab == 1) colors.primaryBright else colors.textSecondary,
                                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input Box + Add & Save Buttons
                    Text(
                        text = if (activeTab == 0) "নতুন ওয়েবসাইট বা লিঙ্ক যোগ করুন" else "নতুন সার্চ কিওয়ার্ড যোগ করুন",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = {
                                Text(
                                    text = if (activeTab == 0) "যেমন: facebook.com, youtube.com" else "যেমন: casino, betting, 18+",
                                    color = colors.textMuted,
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (activeTab == 0) Icons.Default.Link else Icons.Default.Tag,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border,
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary,
                                focusedContainerColor = colors.surfaceElevated,
                                unfocusedContainerColor = colors.surfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_blocklist_text")
                        )

                        Button(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    if (activeTab == 0) {
                                        viewModel.addCustomDomain(textInput)
                                    } else {
                                        viewModel.addCustomKeyword(textInput)
                                    }
                                    textInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary,
                                contentColor = if (colors.isDark) Color(0xFF0D1117) else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("btn_save_blocklist_item")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "সংরক্ষণ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Add Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "দ্রুত যোগ:", color = colors.textMuted, fontSize = 10.sp)
                        val presets = if (activeTab == 0) presetWebsites else presetKeywords
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            presets.take(4).forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colors.surfaceElevated)
                                        .border(1.dp, colors.border, RoundedCornerShape(6.dp))
                                        .clickable {
                                            if (activeTab == 0) viewModel.addCustomDomain(item)
                                            else viewModel.addCustomKeyword(item)
                                        }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "+ $item",
                                        color = colors.primaryBright,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
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
                                text = "ব্লক করা ওয়েবসাইটসমূহ (${viewModel.customDomains.size})",
                                color = colors.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (viewModel.customDomains.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.surfaceElevated)
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "কোনো ওয়েবসাইট যুক্ত নেই",
                                    color = colors.textMuted,
                                    fontSize = 12.sp
                                )
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
                                text = "ব্লক করা কিওয়ার্ডসমূহ (${viewModel.customKeywords.size})",
                                color = colors.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (viewModel.customKeywords.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.surfaceElevated)
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "কোনো কিওয়ার্ড যুক্ত নেই",
                                    color = colors.textMuted,
                                    fontSize = 12.sp
                                )
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
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.borderLight, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = item.domain,
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${item.blockedCount} বার প্রতিহত • ${item.addedTimeAgo}",
                    color = colors.textMuted,
                    fontSize = 10.sp
                )
            }
        }

        // Delete Button
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(30.dp)
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
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.alert.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Tag,
                contentDescription = null,
                tint = colors.alert,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = keyword,
                color = colors.textPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(20.dp)
                    .testTag("btn_delete_kw_$keyword")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Keyword",
                    tint = colors.alert,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}
