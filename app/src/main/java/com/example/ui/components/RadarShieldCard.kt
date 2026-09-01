package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarShieldCard(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")

    // Continuous 360-degree sweep angle
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    // Pulse Ring 1 progress (0f to 1f)
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse1"
    )

    // Pulse Ring 2 progress (0f to 1f) with delay feel
    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, delayMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse2"
    )

    val isActive = viewModel.isProtectionActive
    val primaryColor = if (isActive) colors.primary else colors.alert
    val accentGlow = if (isActive) colors.secondary else colors.alert

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (colors.isDark) {
                        listOf(
                            colors.surfaceElevated.copy(alpha = 0.85f),
                            colors.surface,
                            colors.background
                        )
                    } else {
                        listOf(
                            colors.surface,
                            colors.surfaceElevated.copy(alpha = 0.9f),
                            colors.surface
                        )
                    }
                )
            )
            .border(1.dp, colors.borderSubtle, RoundedCornerShape(26.dp))
            .padding(top = 22.dp, start = 16.dp, end = 16.dp, bottom = 22.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Radar Canvas Area (220dp width & height)
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .testTag("radar_shield_container"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = size.width / 2f - 10f

                    // 1. Radial ambient background glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = if (isActive) 0.18f else 0.05f),
                                primaryColor.copy(alpha = if (isActive) 0.06f else 0.02f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = maxRadius
                        ),
                        radius = maxRadius,
                        center = center
                    )

                    // 2. Crosshair grid lines (North-South, East-West)
                    val crosshairColor = primaryColor.copy(alpha = 0.15f)
                    drawLine(
                        color = crosshairColor,
                        start = Offset(center.x - maxRadius, center.y),
                        end = Offset(center.x + maxRadius, center.y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f), 0f)
                    )
                    drawLine(
                        color = crosshairColor,
                        start = Offset(center.x, center.y - maxRadius),
                        end = Offset(center.x, center.y + maxRadius),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f), 0f)
                    )

                    // 3. Concentric radar range rings (0.36f, 0.60f, 0.82f, 1.0f)
                    val rings = listOf(0.36f, 0.60f, 0.82f, 1.0f)
                    rings.forEachIndexed { index, factor ->
                        val r = maxRadius * factor
                        val isOuter = index == rings.lastIndex
                        val isMiddle = index == 1 || index == 2
                        drawCircle(
                            color = primaryColor.copy(alpha = if (isOuter) 0.55f else if (isMiddle) 0.22f else 0.3f),
                            radius = r,
                            center = center,
                            style = Stroke(
                                width = if (isOuter) 2.dp.toPx() else 1.dp.toPx(),
                                pathEffect = if (isMiddle) PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f) else null
                            )
                        )
                    }

                    // 4. Perimeter tick marks (every 30 degrees)
                    for (deg in 0 until 360 step 30) {
                        val angleRad = Math.toRadians(deg.toDouble())
                        val isMajor = deg % 90 == 0
                        val tickLen = if (isMajor) 8.dp.toPx() else 4.dp.toPx()
                        val startR = maxRadius - tickLen
                        val endR = maxRadius

                        val startX = (center.x + startR * cos(angleRad)).toFloat()
                        val startY = (center.y + startR * sin(angleRad)).toFloat()
                        val endX = (center.x + endR * cos(angleRad)).toFloat()
                        val endY = (center.y + endR * sin(angleRad)).toFloat()

                        drawLine(
                            color = primaryColor.copy(alpha = if (isMajor) 0.6f else 0.3f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // 5. Staggered Animated Expanding Pulse Rings (When Active)
                    if (isActive) {
                        // Pulse Ring 1
                        val pulseRadius1 = (maxRadius * 0.40f) + (maxRadius * 0.58f * pulse1)
                        val pulseAlpha1 = (1f - pulse1) * 0.6f
                        drawCircle(
                            color = primaryColor.copy(alpha = pulseAlpha1),
                            radius = pulseRadius1,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Pulse Ring 2
                        val pulseRadius2 = (maxRadius * 0.40f) + (maxRadius * 0.58f * pulse2)
                        val pulseAlpha2 = (1f - pulse2) * 0.5f
                        drawCircle(
                            color = accentGlow.copy(alpha = pulseAlpha2),
                            radius = pulseRadius2,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }

                    // 6. Threat Blips / Radar Target Nodes
                    if (isActive) {
                        val blips = listOf(
                            Pair(Offset(center.x + maxRadius * 0.55f, center.y - maxRadius * 0.45f), colors.alert),
                            Pair(Offset(center.x - maxRadius * 0.52f, center.y + maxRadius * 0.38f), colors.alert),
                            Pair(Offset(center.x + maxRadius * 0.32f, center.y + maxRadius * 0.58f), colors.secondary)
                        )
                        blips.forEach { (pos, col) ->
                            drawCircle(
                                color = col.copy(alpha = 0.3f),
                                radius = 6.dp.toPx(),
                                center = pos
                            )
                            drawCircle(
                                color = col,
                                radius = 3.dp.toPx(),
                                center = pos
                            )
                        }
                    }

                    // 7. Rotating 360-degree Radar Sweep Beam
                    if (isActive) {
                        rotate(degrees = sweepAngle, pivot = center) {
                            // Sweeping sector tail
                            drawArc(
                                brush = Brush.sweepGradient(
                                    0.0f to primaryColor.copy(alpha = 0.0f),
                                    0.75f to primaryColor.copy(alpha = 0.0f),
                                    0.92f to primaryColor.copy(alpha = 0.15f),
                                    1.0f to primaryColor.copy(alpha = 0.45f),
                                    center = center
                                ),
                                startAngle = 0f,
                                sweepAngle = 90f,
                                useCenter = true,
                                topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                                size = Size(maxRadius * 2, maxRadius * 2)
                            )

                            // Leading sweep line
                            drawLine(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.1f),
                                        primaryColor.copy(alpha = 0.85f),
                                        if (colors.isDark) Color.White else colors.primaryBright
                                    ),
                                    start = center,
                                    end = Offset(center.x + maxRadius, center.y)
                                ),
                                start = center,
                                end = Offset(center.x + maxRadius, center.y),
                                strokeWidth = 2.2.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Glowing tip dot
                            drawCircle(
                                color = colors.primaryBright,
                                radius = 3.5.dp.toPx(),
                                center = Offset(center.x + maxRadius, center.y)
                            )
                        }
                    }
                }

                // Central Shield Pod
                Box(
                    modifier = Modifier
                        .size(106.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = if (colors.isDark) {
                                    listOf(Color(0xFF131D28), Color(0xFF090E14))
                                } else {
                                    listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0))
                                }
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.9f),
                                    primaryColor.copy(alpha = 0.25f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield Status",
                            tint = primaryColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isActive) com.example.util.LocalAppStrings.current.radarProtected else com.example.util.LocalAppStrings.current.radarPaused,
                            color = primaryColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.4.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Title
            Text(
                text = if (isActive) com.example.util.LocalAppStrings.current.radarShieldActive else com.example.util.LocalAppStrings.current.radarShieldPaused,
                color = colors.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = if (isActive)
                    com.example.util.LocalAppStrings.current.radarShieldActiveDesc
                else
                    com.example.util.LocalAppStrings.current.radarShieldPausedDesc,
                color = colors.textSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
