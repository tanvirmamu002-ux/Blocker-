package com.example.ui.components

import android.view.MotionEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppScreenTimeLimit
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguri
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


/**
 * Format minutes into beautiful Bengali and English display
 */
@Composable
fun formatMinutesToBangla(minutes: Int): String {
    return com.example.util.LocalAppStrings.current.formatMinutesLong(minutes)
}

@Composable
fun formatMinutesShort(minutes: Int): String {
    return com.example.util.LocalAppStrings.current.formatMinutesShort(minutes)
}

/**
 * Premium Golden Glow Circular Time Slider
 * Allows selecting minutes between 5 mins to 360 mins (6 hours) or up to 720 mins (12 hours)
 */
@Composable
fun CircularTimeSlider(
    initialMinutes: Int,
    maxMinutes: Int = 360, // default max 6 hours (360 mins)
    stepMinutes: Int = 5,
    modifier: Modifier = Modifier,
    onMinutesChanged: (Int) -> Unit
) {
    val colors = AppTheme.colors
    var currentMinutes by remember(initialMinutes) { mutableIntStateOf(initialMinutes.coerceIn(5, maxMinutes)) }
    val animatedAngle = remember { Animatable((currentMinutes.toFloat() / maxMinutes.toFloat()) * 360f) }
    val coroutineScope = rememberCoroutineScope()

    // Sync when initial minutes change
    LaunchedEffect(initialMinutes) {
        val targetAngle = (initialMinutes.coerceIn(5, maxMinutes).toFloat() / maxMinutes.toFloat()) * 360f
        animatedAngle.animateTo(
            targetValue = targetAngle,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(240.dp)
            .padding(12.dp)
    ) {
        Canvas(
            modifier = Modifier
                .size(216.dp)
                .pointerInput(maxMinutes, stepMinutes) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val touchVec = offset - center
                            var angle = (Math.toDegrees(atan2(touchVec.y.toDouble(), touchVec.x.toDouble())).toFloat() + 90f)
                            if (angle < 0f) angle += 360f
                            
                            val rawFraction = (angle / 360f).coerceIn(0.01f, 1f)
                            val rawMinutes = (rawFraction * maxMinutes).roundToInt()
                            val snappedMinutes = ((rawMinutes + stepMinutes / 2) / stepMinutes * stepMinutes).coerceIn(5, maxMinutes)
                            currentMinutes = snappedMinutes
                            onMinutesChanged(snappedMinutes)
                            coroutineScope.launch {
                                animatedAngle.snapTo((snappedMinutes.toFloat() / maxMinutes.toFloat()) * 360f)
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val touchVec = change.position - center
                            var angle = (Math.toDegrees(atan2(touchVec.y.toDouble(), touchVec.x.toDouble())).toFloat() + 90f)
                            if (angle < 0f) angle += 360f

                            val rawFraction = (angle / 360f).coerceIn(0.01f, 1f)
                            val rawMinutes = (rawFraction * maxMinutes).roundToInt()
                            val snappedMinutes = ((rawMinutes + stepMinutes / 2) / stepMinutes * stepMinutes).coerceIn(5, maxMinutes)
                            if (snappedMinutes != currentMinutes) {
                                currentMinutes = snappedMinutes
                                onMinutesChanged(snappedMinutes)
                                coroutineScope.launch {
                                    animatedAngle.snapTo((snappedMinutes.toFloat() / maxMinutes.toFloat()) * 360f)
                                }
                            }
                        }
                    )
                }
        ) {
            val canvasSize = size.minDimension
            val radius = (canvasSize - 32.dp.toPx()) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val strokeWidth = 14.dp.toPx()
            val sweepAngle = animatedAngle.value.coerceIn(5f, 360f)

            // 1. Background Track (Warm Sand / Dark Charcoal based on theme)
            val trackColor = if (colors.isDark) Color(0xFF26221E) else Color(0xFFEBE4D8)
            drawCircle(
                color = trackColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 2. Subtle Golden Glow Bloom (behind active arc)
            val glowColor = Color(0xFFFF9E1B).copy(alpha = if (colors.isDark) 0.35f else 0.22f)
            drawArc(
                color = glowColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth + 10.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Main Active Golden-Amber Arc
            val goldenSweepBrush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFFFFB703), // Radiant Gold
                    Color(0xFFFF8800), // Amber Brand Orange
                    Color(0xFFF08205), // Deep Amber
                    Color(0xFFFF6F00), // Sunset Amber
                    Color(0xFFFFB703)  // Loop
                ),
                center = center
            )

            drawArc(
                brush = goldenSweepBrush,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 4. Tick Marks (Dial markers at 60min intervals)
            val numTicks = 12
            for (i in 0 until numTicks) {
                val tickAngle = (i * (360f / numTicks)) - 90f
                val rad = Math.toRadians(tickAngle.toDouble())
                val innerR = radius - strokeWidth / 2f - 7.dp.toPx()
                val outerR = radius - strokeWidth / 2f - 3.dp.toPx()
                val tickStart = Offset(
                    (center.x + innerR * cos(rad)).toFloat(),
                    (center.y + innerR * sin(rad)).toFloat()
                )
                val tickEnd = Offset(
                    (center.x + outerR * cos(rad)).toFloat(),
                    (center.y + outerR * sin(rad)).toFloat()
                )
                val isPassed = (i * (360f / numTicks)) <= sweepAngle
                drawCircle(
                    color = if (isPassed) Color(0xFFFF8800).copy(alpha = 0.8f) else trackColor.copy(alpha = 0.6f),
                    radius = 2.dp.toPx(),
                    center = tickStart
                )
            }

            // 5. Golden Glowing Thumb Knob
            val thumbAngleRad = Math.toRadians((sweepAngle - 90f).toDouble())
            val thumbCenter = Offset(
                (center.x + radius * cos(thumbAngleRad)).toFloat(),
                (center.y + radius * sin(thumbAngleRad)).toFloat()
            )

            // Outer Glow Halo for Knob
            drawCircle(
                color = Color(0xFFFF8800).copy(alpha = 0.35f),
                radius = 16.dp.toPx(),
                center = thumbCenter
            )

            // Knob Outer Shell (White/Light Cream)
            drawCircle(
                color = Color.White,
                radius = 11.dp.toPx(),
                center = thumbCenter
            )

            // Knob Center Core (Amber Gold)
            drawCircle(
                color = Color(0xFFFF8800),
                radius = 6.5.dp.toPx(),
                center = thumbCenter
            )
        }

        // Center Content Overlay (Displays crisp time in Bengali)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Clock",
                tint = Color(0xFFFF8800),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatMinutesToBangla(currentMinutes),
                color = colors.textPrimary,
                fontSize = 17.sp,
                fontFamily = HindSiliguri,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "২৪ ঘণ্টার দৈনিক বাজেট",
                color = colors.textSecondary,
                fontSize = 10.5.sp,
                fontFamily = HindSiliguri,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Full App Time Limit Setup Dialog with Circular Slider and Presets
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppTimeLimitSliderDialog(
    app: AppScreenTimeLimit,
    onDismiss: () -> Unit,
    onSaveLimit: (limitMinutes: Int, isStrict: Boolean) -> Unit,
    onRemoveLimit: () -> Unit
) {
    val colors = AppTheme.colors
    var selectedMinutes by remember(app) { mutableIntStateOf(if (app.limitMinutes > 0) app.limitMinutes else 30) }
    var isStrict by remember(app) { mutableStateOf(app.isStrict) }

    // Preset time options requested: 15m, 25m, 45m, 1h, 2h, 4h, 6h
    val presetOptions = listOf(
        15 to formatMinutesShort(15),
        25 to formatMinutesShort(25),
        45 to formatMinutesShort(45),
        60 to formatMinutesShort(60),
        120 to formatMinutesShort(120),
        240 to formatMinutesShort(240),
        360 to formatMinutesShort(360)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .clip(RoundedCornerShape(28.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(28.dp))
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFFF8800).copy(alpha = 0.12f))
                            .border(1.dp, Color(0xFFFF8800).copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = app.appNameBangla,
                            tint = Color(0xFFFF8800),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (com.example.util.LocalAppStrings.current.timeSliderDailyBudget == "24-Hour Daily Budget") app.appNameEnglish else app.appNameBangla,
                            color = colors.textPrimary,
                            fontSize = 16.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${com.example.util.LocalAppStrings.current.timeSliderTodayUsage}: ${app.usedMinutesToday} m",
                            color = colors.textSecondary,
                            fontSize = 11.5.sp,
                            fontFamily = HindSiliguri
                        )
                    }
                }

                // Strict Mode Icon Tag
                if (isStrict) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.alert.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = com.example.util.LocalAppStrings.current.timeSliderStrictLock,
                            color = colors.alert,
                            fontSize = 10.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // The Glowing Circular Time Slider
            CircularTimeSlider(
                initialMinutes = selectedMinutes,
                maxMinutes = 360,
                stepMinutes = 5,
                onMinutesChanged = { newMins ->
                    selectedMinutes = newMins
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Preset Selection Pills (২৫ মিনিট, ১ ঘণ্টা, ২ ঘণ্টা, ৬ ঘণ্টা ইত্যাদি)
            Text(
                text = com.example.util.LocalAppStrings.current.timeSliderQuickPresets,
                color = colors.textSecondary,
                fontSize = 12.sp,
                fontFamily = HindSiliguri,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetOptions.forEach { (mins, label) ->
                    val isSelected = selectedMinutes == mins
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) Color(0xFFFF8800).copy(alpha = 0.15f)
                                else colors.surfaceElevated
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFFF8800) else colors.border,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedMinutes = mins }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color(0xFFFF8800) else colors.textPrimary,
                            fontSize = 11.5.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Strict Mode Switch Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Strict Mode",
                        tint = if (isStrict) colors.alert else colors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "কঠোর লক (Strict Limit)",
                            color = colors.textPrimary,
                            fontSize = 12.5.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "২৪ ঘণ্টার মধ্যে সীমা শেষ হলে পিন ছাড়া আনলক হবে না",
                            color = colors.textSecondary,
                            fontSize = 10.sp,
                            fontFamily = HindSiliguri
                        )
                    }
                }

                Switch(
                    checked = isStrict,
                    onCheckedChange = { isStrict = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.alert
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bottom Action Buttons (Save, Cancel, Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (app.limitMinutes > 0) {
                    OutlinedButton(
                        onClick = onRemoveLimit,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = colors.alert,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("মুছুন", color = colors.alert, fontSize = 12.sp, fontFamily = HindSiliguri)
                    }
                } else {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Text("বাতিল", color = colors.textSecondary, fontSize = 12.5.sp, fontFamily = HindSiliguri)
                    }
                }

                Button(
                    onClick = { onSaveLimit(selectedMinutes, isStrict) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8800)),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("btn_save_circular_time_limit")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "লিমিট সংরক্ষণ করুন",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
