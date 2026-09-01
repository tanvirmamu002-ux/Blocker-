package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme
import com.example.util.FocusPermissionHelper

@Composable
fun FocusLockScreen(
    viewModel: FocusViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val context = LocalContext.current

    // State
    var selectedMinutes by remember { mutableIntStateOf(60) } // Default 1 hour
    var isCustomTime by remember { mutableStateOf(false) }
    var customMinutesInput by remember { mutableStateOf("45") }

    var blockApps by remember { mutableStateOf(true) }
    var blockShorts by remember { mutableStateOf(true) }
    var blockWebsites by remember { mutableStateOf(true) }
    var isStrict by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceElevated)
                        .border(1.dp, colors.border, CircleShape)
                        .testTag("focus_lock_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Focus Lock",
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.primary.copy(alpha = 0.12f))
                    .border(1.dp, colors.primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "প্রস্তুত",
                        color = colors.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // --- Scrollable Content ---
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- Intro Hero Card ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colors.surfaceElevated,
                                colors.surface
                            )
                        )
                    )
                    .border(1.dp, colors.border, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Glowing Shield Graphic
                    GlowingShieldHero(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .size(140.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ফোকাস লক সক্রিয় করুন",
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "নির্বাচিত সময়ের মধ্যে আপনি অ্যাপ থেকে বের হতে পারবেন না এবং বিভ্রান্তিকর কনটент ব্লক থাকবে।",
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Circular Arc Gauge & Time Slider Card ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.border, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "সময় নির্বাচন করুন",
                                color = colors.textPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = formatMinutesToBangla(selectedMinutes),
                            color = colors.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gauge + Plus/Minus Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Minus Button
                        IconButton(
                            onClick = {
                                val step = if (selectedMinutes > 60) 15 else 5
                                selectedMinutes = (selectedMinutes - step).coerceAtLeast(5)
                                isCustomTime = false
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(colors.surface)
                                .border(1.dp, colors.border, CircleShape)
                                .testTag("time_minus_btn")
                        ) {
                            Text(
                                text = "−",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }

                        // Circular Gauge
                        CircularTimeGauge(
                            selectedMinutes = selectedMinutes,
                            maxMinutes = 240,
                            modifier = Modifier.size(160.dp),
                            primaryColor = colors.primary,
                            accentColor = Color(0xFF06B6D4),
                            trackColor = colors.border.copy(alpha = 0.6f)
                        )

                        // Plus Button
                        IconButton(
                            onClick = {
                                val step = if (selectedMinutes >= 60) 15 else 5
                                selectedMinutes = (selectedMinutes + step).coerceAtMost(240)
                                isCustomTime = false
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(colors.surface)
                                .border(1.dp, colors.border, CircleShape)
                                .testTag("time_plus_btn")
                        ) {
                            Text(
                                text = "+",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Smooth Slider
                    Slider(
                        value = selectedMinutes.toFloat(),
                        onValueChange = {
                            selectedMinutes = it.toInt()
                            isCustomTime = false
                        },
                        valueRange = 5f..240f,
                        modifier = Modifier.fillMaxWidth().testTag("time_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = colors.primary,
                            activeTrackColor = colors.primary,
                            inactiveTrackColor = colors.border
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Quick Presets & Custom Option ---
            Text(
                text = "দ্রুত সময় নির্বাচন",
                color = colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            val presets = listOf(
                15 to "১৫ মিনিট",
                30 to "৩০ মিনিট",
                60 to "১ ঘণ্টা",
                120 to "২ ঘণ্টা",
                180 to "৩ ঘণ্টা",
                240 to "৪ ঘণ্টা"
            )

            // Preset Grid Layout
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: 15m, 30m, 1h
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.take(3).forEach { (mins, label) ->
                        val isSelected = !isCustomTime && selectedMinutes == mins
                        PresetChip(
                            label = label,
                            isSelected = isSelected,
                            onClick = {
                                isCustomTime = false
                                selectedMinutes = mins
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Row 2: 2h, 3h, 4h
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.drop(3).take(3).forEach { (mins, label) ->
                        val isSelected = !isCustomTime && selectedMinutes == mins
                        PresetChip(
                            label = label,
                            isSelected = isSelected,
                            onClick = {
                                isCustomTime = false
                                selectedMinutes = mins
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Row 3: Custom Preset Option
                PresetChip(
                    label = if (isCustomTime) "Custom (কাস্টম মিনিট)" else "কাস্টম সময় যুক্ত করুন +",
                    isSelected = isCustomTime,
                    onClick = {
                        isCustomTime = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Custom Input Expand
            AnimatedVisibility(
                visible = isCustomTime,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    OutlinedTextField(
                        value = customMinutesInput,
                        onValueChange = { input ->
                            if (input.all { char -> char.isDigit() } && input.length <= 4) {
                                customMinutesInput = input
                                val parsed = input.toIntOrNull()
                                if (parsed != null && parsed in 1..240) {
                                    selectedMinutes = parsed
                                }
                            }
                        },
                        label = { Text("মিনিট লিখুন (১ - ২৪০ মিনিট)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("custom_time_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedContainerColor = colors.surfaceElevated,
                            unfocusedContainerColor = colors.surface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Protection Options ---
            Text(
                text = "সুরক্ষা সেটিংস",
                color = colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ProtectionOptionCard(
                    title = "বিভ্রান্তিকর অ্যাপ ব্লক",
                    subtitle = "সোশাল মিডিয়া ও অন্যান্য অ্যাপ ব্লক",
                    icon = Icons.Default.Apps,
                    iconBg = Color(0xFF0284C7).copy(alpha = 0.15f),
                    iconTint = Color(0xFF0284C7),
                    isChecked = blockApps,
                    onCheckedChange = { blockApps = it },
                    testTag = "toggle_block_apps"
                )

                ProtectionOptionCard(
                    title = "YouTube Shorts & Reels ব্লক",
                    subtitle = "শর্টস এবং রিলস দেখা থেকে বিরত রাখবে",
                    icon = Icons.Default.PlayCircle,
                    iconBg = Color(0xFF10B981).copy(alpha = 0.15f),
                    iconTint = Color(0xFF10B981),
                    isChecked = blockShorts,
                    onCheckedChange = { blockShorts = it },
                    testTag = "toggle_block_shorts"
                )

                ProtectionOptionCard(
                    title = "ওয়েবসাইট ও ডোমেইন ব্লক",
                    subtitle = "নির্বাচিত ওয়েবসাইটগুলো অ্যাক্সেস বন্ধ থাকবে",
                    icon = Icons.Default.Language,
                    iconBg = Color(0xFF06B6D4).copy(alpha = 0.15f),
                    iconTint = Color(0xFF06B6D4),
                    isChecked = blockWebsites,
                    onCheckedChange = { blockWebsites = it },
                    testTag = "toggle_block_websites"
                )

                ProtectionOptionCard(
                    title = "Strict Focus Lock (কঠোর সুরক্ষা)",
                    subtitle = "এই মোডে আপনি নির্ধারিত সময়ের আগে বের হতে পারবেন না",
                    icon = Icons.Default.Shield,
                    iconBg = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    iconTint = Color(0xFFF59E0B),
                    isChecked = isStrict,
                    onCheckedChange = { isStrict = it },
                    testTag = "toggle_strict_lock"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- Bottom Sticky CTA Action Bar ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (!FocusPermissionHelper.areAllRequiredPermissionsGranted(context)) {
                            viewModel.isFocusLockPermissionDialogVisible = true
                        } else {
                            viewModel.startFocusLockSession(
                                context = context,
                                durationMinutes = selectedMinutes,
                                blockApps = blockApps,
                                blockShorts = blockShorts,
                                blockWebsites = blockWebsites,
                                isStrict = isStrict
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF10B981),
                                    Color(0xFF06B6D4)
                                )
                            )
                        )
                        .testTag("start_focus_lock_cta_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ফোকাস লক শুরু করুন (${formatMinutesToBangla(selectedMinutes)})",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "আপনার জরুরি প্রয়োজনে জরুরি এক্সেস ব্যবহার করতে পারেন",
                        color = colors.textMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// --- Composable Components ---

@Composable
private fun PresetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) colors.primary.copy(alpha = 0.15f) else colors.surfaceElevated
            )
            .border(
                1.dp,
                if (isSelected) colors.primary else colors.border,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) colors.primary else colors.textPrimary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProtectionOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val colors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = colors.textSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.primary,
                uncheckedThumbColor = colors.textMuted,
                uncheckedTrackColor = colors.surface
            )
        )
    }
}

@Composable
private fun CircularTimeGauge(
    selectedMinutes: Int,
    maxMinutes: Int = 240,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF10B981),
    accentColor: Color = Color(0xFF06B6D4),
    trackColor: Color = Color(0xFFE2E8F0)
) {
    val progress = (selectedMinutes.toFloat() / maxMinutes.toFloat()).coerceIn(0f, 1f)
    val colors = AppTheme.colors

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val strokeWidth = 14.dp.toPx()
            val center = Offset(size.width / 2, size.height / 2)

            // Track arc (270 degrees sweep starting from 135 deg)
            drawArc(
                color = trackColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress Arc
            if (progress > 0f) {
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(primaryColor, accentColor)
                    ),
                    startAngle = 135f,
                    sweepAngle = 270f * progress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Inner Content Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = formatMinutesShort(selectedMinutes),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "দৈনিক ফোকাস",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun GlowingShieldHero(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ShieldHeroPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(150.dp)) {
            val center = Offset(size.width / 2, size.height / 2)

            // Soft radial glow aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF10B981).copy(alpha = 0.30f),
                        Color(0xFF06B6D4).copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width / 1.8f
                ),
                radius = size.width / 1.8f
            )

            // Orbit line
            drawOval(
                color = Color(0xFF10B981).copy(alpha = 0.45f),
                topLeft = Offset(center.x - 65.dp.toPx(), center.y - 22.dp.toPx()),
                size = Size(130.dp.toPx(), 44.dp.toPx()),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )
            )

            // Orbiting particle dots
            drawCircle(
                color = Color(0xFF06B6D4),
                radius = 3.5.dp.toPx(),
                center = Offset(center.x + 55.dp.toPx(), center.y - 8.dp.toPx())
            )
            drawCircle(
                color = Color(0xFF10B981),
                radius = 3.dp.toPx(),
                center = Offset(center.x - 50.dp.toPx(), center.y + 10.dp.toPx())
            )
        }

        // Glowing Emerald Shield Container
        Box(
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF10B981),
                            Color(0xFF059669),
                            Color(0xFF0D9488)
                        )
                    )
                )
                .border(
                    2.dp,
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFA7F3D0), Color(0xFF67E8F9))
                    ),
                    RoundedCornerShape(22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

// Helpers
private fun formatMinutesToBangla(minutes: Int): String {
    val hrs = minutes / 60
    val mins = minutes % 60
    fun toBanglaDigits(num: Int): String {
        val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        return num.toString().map { if (it.isDigit()) banglaDigits[it - '0'] else it }.joinToString("")
    }
    return when {
        hrs > 0 && mins > 0 -> "${toBanglaDigits(hrs)} ঘণ্টা ${toBanglaDigits(mins)} মি."
        hrs > 0 -> "${toBanglaDigits(hrs)} ঘণ্টা"
        else -> "${toBanglaDigits(mins)} মিনিট"
    }
}

private fun formatMinutesShort(minutes: Int): String {
    val hrs = minutes / 60
    val mins = minutes % 60
    return when {
        hrs > 0 && mins > 0 -> "${hrs}h ${mins}m"
        hrs > 0 -> "${hrs}h"
        else -> "${mins}m"
    }
}
