package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppScreenTimeLimit
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguri
import kotlin.math.roundToInt

@Composable
fun ScreenTimeLimitDialog(
    viewModel: FocusViewModel
) {
    if (!viewModel.isScreenTimeLimitDialogVisible) return

    val colors = AppTheme.colors
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("সবগুলো") }

    // Reload apps on launch
    LaunchedEffect(Unit) {
        viewModel.loadPerAppLimits(context)
    }

    Dialog(
        onDismissRequest = { viewModel.isScreenTimeLimitDialogVisible = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .background(colors.background)
                .border(1.dp, colors.border, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                // 1. Header with Title, Refresh & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFF8800).copy(alpha = 0.14f))
                                .border(1.dp, Color(0xFFFF8800).copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Screen Time",
                                tint = Color(0xFFFF8800),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "অ্যাপ স্ক্রিন টাইম লিমিট",
                                color = colors.textPrimary,
                                fontSize = 17.5.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "২৪ ঘণ্টার দৈনিক বাজেট (রাত ১২:০০ টা পর্যন্ত)",
                                color = colors.textSecondary,
                                fontSize = 11.5.sp,
                                fontFamily = HindSiliguri
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.loadPerAppLimits(context) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = Color(0xFFFF8800),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = { viewModel.isScreenTimeLimitDialogVisible = false },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = colors.textMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Overview Stats Card
                val activeLimitsCount = viewModel.appScreenTimeLimits.count { it.isEnabled && it.limitMinutes > 0 }
                val totalUsedMins = viewModel.appScreenTimeLimits.sumOf { it.usedMinutesToday }
                val exceededCount = viewModel.appScreenTimeLimits.count { it.isEnabled && it.limitMinutes > 0 && it.usedMinutesToday >= it.limitMinutes }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.borderSubtle, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "সীমিত অ্যাপস",
                                color = colors.textSecondary,
                                fontSize = 11.5.sp,
                                fontFamily = HindSiliguri
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$activeLimitsCount টি অ্যাপ",
                                color = colors.textPrimary,
                                fontSize = 16.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(colors.borderSubtle)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "আজকের মোট ব্যবহার",
                                color = colors.textSecondary,
                                fontSize = 11.5.sp,
                                fontFamily = HindSiliguri
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${totalUsedMins / 60}ঘ. ${totalUsedMins % 60}মি.",
                                color = Color(0xFFFF8800),
                                fontSize = 16.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(colors.borderSubtle)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "লকড অ্যাপস",
                                color = colors.textSecondary,
                                fontSize = 11.5.sp,
                                fontFamily = HindSiliguri
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (exceededCount > 0) "$exceededCount টি লকড 🔒" else "সব নিয়ন্ত্রণে ✅",
                                color = if (exceededCount > 0) colors.alert else colors.secondary,
                                fontSize = 13.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "অ্যাপের নাম বা প্যাকেজ দিয়ে খুঁজুন...",
                            color = colors.textMuted,
                            fontSize = 13.sp,
                            fontFamily = HindSiliguri
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFFF8800),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = colors.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8800),
                        unfocusedBorderColor = colors.border,
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Category Filter Chips
                val categoryFilters = listOf("সবগুলো", "সীমা সক্রিয়", "সোশ্যাল মিডিয়া", "ভিডিও ও বিনোদন", "মেসেজিং ও যোগাযোগ", "গেমিং", "অন্যান্য")
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categoryFilters) { category ->
                        val isSelected = selectedCategoryFilter == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) Color(0xFFFF8800).copy(alpha = 0.15f) else colors.surfaceElevated
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFFFF8800) else colors.borderSubtle,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCategoryFilter = category }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color(0xFFFF8800) else colors.textSecondary,
                                fontSize = 11.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Filtered App List
                val filteredApps = viewModel.appScreenTimeLimits.filter { app ->
                    val matchesQuery = app.appNameBangla.contains(searchQuery, ignoreCase = true) ||
                            app.appNameEnglish.contains(searchQuery, ignoreCase = true) ||
                            app.packageName.contains(searchQuery, ignoreCase = true)

                    val matchesCategory = when (selectedCategoryFilter) {
                        "সবগুলো" -> true
                        "সীমা সক্রিয়" -> app.isEnabled && app.limitMinutes > 0
                        else -> app.category.contains(selectedCategoryFilter, ignoreCase = true)
                    }

                    matchesQuery && matchesCategory
                }

                if (filteredApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = "No apps",
                                tint = colors.textMuted,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "কোনো অ্যাপ পাওয়া যায়নি",
                                color = colors.textSecondary,
                                fontSize = 13.sp,
                                fontFamily = HindSiliguri
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            AppScreenTimeItemCard(
                                app = app,
                                onPlusClick = {
                                    viewModel.openAppSliderDialog(app)
                                },
                                onToggleEnabled = {
                                    viewModel.toggleAppLimitEnabled(context, app.packageName)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Circular Slider Modal Dialog when a specific app's Plus icon is pressed
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
}

/**
 * Individual App Item Card with App Icon, Name, Progress Bar, Usage Details and the Plus (+) Button
 */
@Composable
private fun AppScreenTimeItemCard(
    app: AppScreenTimeLimit,
    onPlusClick: () -> Unit,
    onToggleEnabled: () -> Unit
) {
    val colors = AppTheme.colors
    val hasLimit = app.isEnabled && app.limitMinutes > 0
    val isExceeded = hasLimit && app.usedMinutesToday >= app.limitMinutes
    val progress = if (hasLimit) (app.usedMinutesToday.toFloat() / app.limitMinutes.toFloat()).coerceIn(0f, 1f) else 0f

    val brandColor = when (app.iconType) {
        "facebook" -> Color(0xFF1877F2)
        "youtube" -> Color(0xFFFF0000)
        "instagram" -> Color(0xFFE1306C)
        "tiktok" -> Color(0xFF00F2FE)
        "whatsapp" -> Color(0xFF25D366)
        "chrome" -> Color(0xFF4285F4)
        "telegram" -> Color(0xFF0088CC)
        "twitter" -> Color(0xFF1DA1F2)
        "snapchat" -> Color(0xFFFFFC00)
        "reddit" -> Color(0xFFFF4500)
        "netflix" -> Color(0xFFE50914)
        "games" -> Color(0xFFFF5722)
        else -> Color(0xFFFF8800)
    }

    val brandIconVector: ImageVector = when (app.iconType) {
        "facebook" -> Icons.Default.ThumbUp
        "youtube" -> Icons.Default.PlayCircle
        "instagram" -> Icons.Default.CameraAlt
        "tiktok" -> Icons.Default.MusicNote
        "whatsapp" -> Icons.Default.Chat
        "chrome" -> Icons.Default.Language
        "telegram" -> Icons.Default.Send
        "twitter" -> Icons.Default.Tag
        "snapchat" -> Icons.Default.Videocam
        "reddit" -> Icons.Default.Forum
        "netflix" -> Icons.Default.Movie
        "games" -> Icons.Default.SportsEsports
        else -> Icons.Default.Smartphone
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(
                1.dp,
                if (isExceeded) colors.alert.copy(alpha = 0.5f)
                else if (hasLimit) Color(0xFFFF8800).copy(alpha = 0.25f)
                else colors.borderSubtle,
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: App Brand Icon + App Details
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(brandColor.copy(alpha = 0.14f))
                            .border(1.dp, brandColor.copy(alpha = 0.35f), RoundedCornerShape(13.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = brandIconVector,
                            contentDescription = app.appNameBangla,
                            tint = brandColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = app.appNameBangla,
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(1.dp))

                        if (hasLimit) {
                            Text(
                                text = "২৪ ঘণ্টার দৈনিক বাজেট: ${formatMinutesShort(app.limitMinutes)}",
                                color = if (isExceeded) colors.alert else Color(0xFFFF8800),
                                fontSize = 11.5.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "সীমা নির্ধারণ করা হয়নি",
                                color = colors.textMuted,
                                fontSize = 11.sp,
                                fontFamily = HindSiliguri
                            )
                        }
                    }
                }

                // Right: Plus (+) / Slider Edit Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (hasLimit) Color(0xFFFF8800).copy(alpha = 0.15f)
                                else colors.surfaceElevated
                            )
                            .border(
                                1.dp,
                                if (hasLimit) Color(0xFFFF8800)
                                else colors.border,
                                CircleShape
                            )
                            .clickable { onPlusClick() }
                            .testTag("btn_plus_app_slider_${app.packageName}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (hasLimit) Icons.Default.Tune else Icons.Default.Add,
                            contentDescription = "Set Limit",
                            tint = if (hasLimit) Color(0xFFFF8800) else colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Usage Progress Bar & Remaining Time Row
            if (hasLimit) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ব্যবহৃত: ${app.usedMinutesToday} মি. (${(progress * 100).roundToInt()}%)",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontFamily = HindSiliguri
                    )

                    if (isExceeded) {
                        Text(
                            text = "🚫 সীমা শেষ (লকড)",
                            color = colors.alert,
                            fontSize = 11.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "অবশিষ্ট: ${app.limitMinutes - app.usedMinutesToday} মিনিট",
                            color = Color(0xFFFF8800),
                            fontSize = 11.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = if (isExceeded) colors.alert else if (progress > 0.8f) Color(0xFFFF8800) else colors.secondary,
                    trackColor = colors.surfaceElevated
                )
            }
        }
    }
}
