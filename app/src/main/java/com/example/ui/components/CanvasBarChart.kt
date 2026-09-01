package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AddictionTrigger
import com.example.data.DailyDisciplineStat
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguri

enum class ChartViewType {
    CURVED_LINE,
    DUAL_BAR
}

/**
 * Premium Interactive Trend Chart featuring Smooth Curved Bezier Line & Dual Bar modes.
 */
@Composable
fun InteractiveTrendChart(
    stats: List<DailyDisciplineStat>,
    period: String = "weekly",
    insightText: String? = null,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var currentChartType by remember { mutableStateOf(ChartViewType.CURVED_LINE) }
    var selectedIndex by remember(stats) { mutableIntStateOf(stats.indexOfFirst { it.isBestDay }.coerceAtLeast(0)) }

    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(currentChartType, stats, period) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        colors.surfaceElevated.copy(alpha = 0.7f),
                        colors.surface
                    )
                )
            )
            .border(1.dp, colors.borderSubtle, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Title & Chart Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (period == "weekly") com.example.util.LocalAppStrings.current.chartWeeklyTrend else com.example.util.LocalAppStrings.current.chartMonthlyTrend,
                        color = colors.textPrimary,
                        fontSize = 15.5.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (period == "weekly") com.example.util.LocalAppStrings.current.chartWeeklyDesc else com.example.util.LocalAppStrings.current.chartMonthlyDesc,
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = HindSiliguri
                    )
                }

                // Switcher pill (Curved Line vs Bar)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceElevated.copy(alpha = 0.9f))
                        .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (currentChartType == ChartViewType.CURVED_LINE) colors.primary else Color.Transparent)
                            .clickable { currentChartType = ChartViewType.CURVED_LINE }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("toggle_chart_line"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ShowChart,
                            contentDescription = "Line Chart",
                            tint = if (currentChartType == ChartViewType.CURVED_LINE) {
                                if (colors.isDark) Color(0xFF0B0E14) else Color.White
                            } else colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (currentChartType == ChartViewType.DUAL_BAR) colors.primary else Color.Transparent)
                            .clickable { currentChartType = ChartViewType.DUAL_BAR }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("toggle_chart_bar"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BarChart,
                            contentDescription = "Bar Chart",
                            tint = if (currentChartType == ChartViewType.DUAL_BAR) {
                                if (colors.isDark) Color(0xFF0B0E14) else Color.White
                            } else colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Day Tooltip Indicator Capsule
            val selectedStat = stats.getOrNull(selectedIndex) ?: stats.firstOrNull()
            if (selectedStat != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surfaceElevated)
                        .border(1.dp, colors.borderSubtle, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colors.primary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val labelHours = String.format(java.util.Locale.US, "%.1f", selectedStat.savedHours)
                        Text(
                            text = com.example.util.LocalAppStrings.current.chartSavedHours(selectedStat.dayBangla, labelHours, period == "weekly"),
                            color = colors.textPrimary,
                            fontSize = 12.5.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colors.alert)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = com.example.util.LocalAppStrings.current.chartBlockedAttempts(selectedStat.blockedAttempts),
                            color = colors.alert,
                            fontSize = 11.5.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Canvas Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                if (currentChartType == ChartViewType.CURVED_LINE) {
                    SmoothCurvedLineCanvas(
                        stats = stats,
                        progress = animProgress.value,
                        selectedIndex = selectedIndex,
                        onSelectIndex = { selectedIndex = it }
                    )
                } else {
                    DualBarCanvas(
                        stats = stats,
                        progress = animProgress.value,
                        selectedIndex = selectedIndex,
                        onSelectIndex = { selectedIndex = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day Labels Row below chart with clickable highlights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                stats.forEachIndexed { index, stat ->
                    val isSelected = index == selectedIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) colors.primary.copy(alpha = 0.15f)
                                else if (stat.isBestDay) colors.primary.copy(alpha = 0.08f)
                                else Color.Transparent
                            )
                            .clickable { selectedIndex = index }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = stat.dayBangla,
                            color = if (isSelected) colors.textPrimary
                            else if (stat.isBestDay) colors.primary
                            else colors.textSecondary,
                            fontSize = 12.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = if (isSelected || stat.isBestDay) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Highlight insight footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceElevated)
                    .border(
                        1.dp,
                        colors.borderSubtle,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.AutoGraph,
                        contentDescription = "Insight",
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = insightText ?: if (period == "weekly") {
                            com.example.util.LocalAppStrings.current.chartInsightWeeklyFallback
                        } else {
                            com.example.util.LocalAppStrings.current.chartInsightMonthlyFallback
                        },
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Smooth Cubic Bezier Curved Line Chart with gradient underfill
 */
@Composable
private fun SmoothCurvedLineCanvas(
    stats: List<DailyDisciplineStat>,
    progress: Float,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    val colors = AppTheme.colors
    val maxSavedHours = 5.0f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(stats) {
                detectTapGestures { offset ->
                    val colWidth = size.width / stats.size
                    val clickedIndex = (offset.x / colWidth).toInt().coerceIn(0, stats.size - 1)
                    onSelectIndex(clickedIndex)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val colWidth = width / stats.size
        val topPadding = 16.dp.toPx()
        val bottomPadding = 12.dp.toPx()
        val graphHeight = height - topPadding - bottomPadding

        // 1. Draw subtle horizontal grid lines
        val gridLines = 3
        for (i in 1..gridLines) {
            val y = topPadding + graphHeight * (i.toFloat() / (gridLines + 1))
            drawLine(
                color = colors.borderSubtle,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
        }

        if (stats.isEmpty()) return@Canvas

        // Calculate (x, y) coordinates for each data point
        val points = stats.mapIndexed { index, stat ->
            val x = colWidth * index + (colWidth / 2f)
            val normalizedY = (stat.savedHours / maxSavedHours).coerceIn(0.1f, 1f)
            val targetY = topPadding + graphHeight * (1f - normalizedY)
            val animatedY = height - (height - targetY) * progress
            Offset(x, animatedY)
        }

        // Build Smooth Cubic Bezier Path
        val strokePath = Path()
        val fillPath = Path()

        strokePath.moveTo(points.first().x, points.first().y)
        fillPath.moveTo(points.first().x, height)
        fillPath.lineTo(points.first().x, points.first().y)

        for (i in 0 until points.size - 1) {
            val p0 = points[i]
            val p1 = points[i + 1]

            val controlX1 = p0.x + (p1.x - p0.x) / 2f
            val controlY1 = p0.y
            val controlX2 = p0.x + (p1.x - p0.x) / 2f
            val controlY2 = p1.y

            strokePath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
            fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
        }

        fillPath.lineTo(points.last().x, height)
        fillPath.close()

        // 2. Draw Gradient Underfill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    colors.primary.copy(alpha = 0.35f * progress),
                    colors.primary.copy(alpha = 0.08f * progress),
                    Color.Transparent
                ),
                startY = topPadding,
                endY = height
            )
        )

        // 3. Draw Curved Stroke
        drawPath(
            path = strokePath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    colors.primary,
                    colors.primaryBright,
                    colors.secondary
                )
            ),
            style = Stroke(
                width = 3.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        // 4. Draw Data Points with Glow
        points.forEachIndexed { index, point ->
            val isSelected = index == selectedIndex
            val isBest = stats[index].isBestDay

            if (isSelected || isBest) {
                // Outer pulse ring
                drawCircle(
                    color = colors.primary.copy(alpha = 0.3f),
                    radius = 12.dp.toPx(),
                    center = point
                )
            }

            // Outer circle
            drawCircle(
                color = if (isSelected) colors.primaryBright else colors.primary,
                radius = if (isSelected) 6.dp.toPx() else 4.5.dp.toPx(),
                center = point
            )

            // Inner center dot
            drawCircle(
                color = colors.background,
                radius = if (isSelected) 2.5.dp.toPx() else 1.8.dp.toPx(),
                center = point
            )
        }
    }
}

/**
 * Modern Dual Bar Canvas comparing Saved Time & Blocked Attempts
 */
@Composable
private fun DualBarCanvas(
    stats: List<DailyDisciplineStat>,
    progress: Float,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    val colors = AppTheme.colors
    val maxSavedHours = 5.0f
    val maxAttempts = 25f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(stats) {
                detectTapGestures { offset ->
                    val colWidth = size.width / stats.size
                    val clickedIndex = (offset.x / colWidth).toInt().coerceIn(0, stats.size - 1)
                    onSelectIndex(clickedIndex)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val colWidth = width / stats.size
        val barWidth = 9.dp.toPx()
        val barSpacing = 4.dp.toPx()
        val cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())

        // Draw subtle horizontal grid lines
        val gridLines = 3
        for (i in 1..gridLines) {
            val y = height * (i.toFloat() / (gridLines + 1))
            drawLine(
                color = colors.borderSubtle,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        stats.forEachIndexed { index, stat ->
            val centerX = colWidth * index + (colWidth / 2f)

            // 1. Saved Hours Bar (Left, Primary Gradient)
            val savedFraction = (stat.savedHours / maxSavedHours).coerceIn(0.08f, 1f) * progress
            val savedBarHeight = height * savedFraction * 0.88f
            val savedTop = height - savedBarHeight
            val savedLeft = centerX - barWidth - (barSpacing / 2f)

            if (stat.isBestDay || index == selectedIndex) {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(colors.primaryBright, colors.primary)
                    ),
                    topLeft = Offset(savedLeft - 2f, savedTop - 2f),
                    size = Size(barWidth + 4f, savedBarHeight + 2f),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                    alpha = 0.35f
                )
            }

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = if (stat.isBestDay) {
                        listOf(colors.primaryBright, colors.primary)
                    } else {
                        listOf(colors.primary.copy(alpha = 0.9f), colors.primary.copy(alpha = 0.55f))
                    }
                ),
                topLeft = Offset(savedLeft, savedTop),
                size = Size(barWidth, savedBarHeight),
                cornerRadius = cornerRadius
            )

            // 2. Blocked Attempts Bar (Right, Coral Gradient)
            val attemptFraction = (stat.blockedAttempts / maxAttempts).coerceIn(0.08f, 1f) * progress
            val attemptBarHeight = height * attemptFraction * 0.88f
            val attemptTop = height - attemptBarHeight
            val attemptLeft = centerX + (barSpacing / 2f)

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(colors.alert.copy(alpha = 0.85f), colors.alert.copy(alpha = 0.45f))
                ),
                topLeft = Offset(attemptLeft, attemptTop),
                size = Size(barWidth, attemptBarHeight),
                cornerRadius = cornerRadius
            )
        }
    }
}

/**
 * Modern Interactive Category Donut Chart (ডোনাট চার্ট)
 */
@Composable
fun CategoryDonutChartCard(
    triggers: List<AddictionTrigger>,
    totalBlockedCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        colors.surfaceElevated.copy(alpha = 0.7f),
                        colors.surface
                    )
                )
            )
            .border(1.dp, colors.borderSubtle, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = com.example.util.LocalAppStrings.current.chartCategoryRatioTitle,
                        color = colors.textPrimary,
                        fontSize = 15.5.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = com.example.util.LocalAppStrings.current.chartCategoryRatioDesc,
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = HindSiliguri
                    )
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DonutLarge,
                        contentDescription = "Donut",
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Donut Chart & Category Legends in Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Donut Canvas with Centered Metric
                Box(
                    modifier = Modifier
                        .size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 14.dp.toPx()
                        val padding = strokeWidth / 2f
                        val arcSize = Size(size.width - padding * 2, size.height - padding * 2)
                        val arcOffset = Offset(padding, padding)

                        var startAngle = -90f

                        triggers.forEach { trigger ->
                            val sweepAngle = (trigger.percentage / 100f) * 360f * animProgress.value
                            val gapAngle = 3.5f

                            if (sweepAngle > gapAngle) {
                                drawArc(
                                    color = trigger.accentColor,
                                    startAngle = startAngle + (gapAngle / 2f),
                                    sweepAngle = sweepAngle - gapAngle,
                                    useCenter = false,
                                    topLeft = arcOffset,
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            startAngle += sweepAngle
                        }
                    }

                    // Center Total Label
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalBlockedCount",
                            color = colors.textPrimary,
                            fontSize = 18.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = com.example.util.LocalAppStrings.current.chartBlockedPrefix,
                            color = colors.textSecondary,
                            fontSize = 10.5.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Legends Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    triggers.forEach { trigger ->
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
                                        .size(9.dp)
                                        .clip(CircleShape)
                                        .background(trigger.accentColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = trigger.name,
                                    color = colors.textPrimary,
                                    fontSize = 12.sp,
                                    fontFamily = HindSiliguri,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "${trigger.percentage}%",
                                color = trigger.accentColor,
                                fontSize = 12.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Gradient Animated Progress Item with dynamic fill and percentage badge
 */
@Composable
fun GradientTriggerProgressItem(
    trigger: AddictionTrigger,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val animatedProgress by animateFloatAsState(
        targetValue = trigger.percentage / 100f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress_${trigger.name}"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(trigger.accentColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = trigger.name,
                    color = colors.textPrimary,
                    fontSize = 13.5.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = com.example.util.LocalAppStrings.current.chartCategoryDetails(trigger.blockedCount, trigger.percentage),
                color = colors.textSecondary,
                fontSize = 12.sp,
                fontFamily = HindSiliguri,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        // Custom Gradient Progress Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(colors.surfaceElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                trigger.accentColor.copy(alpha = 0.7f),
                                trigger.accentColor
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Discipline Mastery Delight Card
 */
@Composable
fun DisciplineMasteryCard(
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surface)
            .border(
                1.dp,
                colors.borderSubtle,
                RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (colors.isDark) Color(0xFF1B3D34) else Color(0xFFE6F4EA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = if (colors.isDark) Color(0xFF4FD1C5) else Color(0xFF0D904F),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = com.example.util.LocalAppStrings.current.chartDisciplineScoreTitle,
                    color = colors.textPrimary,
                    fontSize = 14.5.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = com.example.util.LocalAppStrings.current.chartDisciplineScoreDesc,
                    color = colors.textSecondary,
                    fontSize = 11.5.sp,
                    fontFamily = HindSiliguri
                )
            }
        }
    }
}
