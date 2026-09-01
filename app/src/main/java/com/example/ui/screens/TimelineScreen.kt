package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FocusRoutine
import com.example.state.FocusViewModel
import com.example.ui.components.getRoutineIconVector
import com.example.ui.components.parseRoutineColor
import com.example.ui.theme.AppTheme

data class TimelineSlot(
    val timeLabel: String,
    val routine: FocusRoutine?,
    val dotColor: Color,
    val customDisplayTime: String? = null
)

@Composable
fun TimelineScreen(
    viewModel: FocusViewModel,
    onBack: () -> Unit
) {
    val colors = AppTheme.colors
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: Day, 1: Week, 2: Month
    val filterTabs = listOf("Day", "Week", "Month")

    // Timeline Events data matching Screen 2
    val timelineSlots = listOf(
        TimelineSlot(
            timeLabel = "12 AM",
            routine = viewModel.focusRoutines.find { it.titleEnglish.contains("Sleep", ignoreCase = true) }
                ?: viewModel.focusRoutines.getOrNull(2),
            dotColor = Color(0xFF8B5CF6),
            customDisplayTime = "11:00 PM – 6:00 AM"
        ),
        TimelineSlot(
            timeLabel = "6 AM",
            routine = null,
            dotColor = colors.border
        ),
        TimelineSlot(
            timeLabel = "9 AM",
            routine = viewModel.focusRoutines.find { it.titleEnglish.contains("Deep", ignoreCase = true) }
                ?: viewModel.focusRoutines.getOrNull(1),
            dotColor = Color(0xFF10B981),
            customDisplayTime = "9:00 AM – 1:00 PM"
        ),
        TimelineSlot(
            timeLabel = "1 PM",
            routine = null,
            dotColor = colors.border
        ),
        TimelineSlot(
            timeLabel = "5 PM",
            routine = viewModel.focusRoutines.find { it.titleEnglish.contains("Personal", ignoreCase = true) }
                ?: viewModel.focusRoutines.getOrNull(3),
            dotColor = Color(0xFF3B82F6),
            customDisplayTime = "5:00 PM – 7:00 PM"
        ),
        TimelineSlot(
            timeLabel = "8 PM",
            routine = viewModel.focusRoutines.find { it.titleEnglish.contains("Study", ignoreCase = true) }
                ?: viewModel.focusRoutines.getOrNull(0),
            dotColor = Color(0xFF10B981),
            customDisplayTime = "8:00 PM – 11:00 PM"
        ),
        TimelineSlot(
            timeLabel = "11 PM",
            routine = viewModel.focusRoutines.find { it.titleEnglish.contains("Sleep", ignoreCase = true) }
                ?: viewModel.focusRoutines.getOrNull(2),
            dotColor = Color(0xFF8B5CF6),
            customDisplayTime = "11:00 PM – 6:00 AM"
        ),
        TimelineSlot(
            timeLabel = "12 AM",
            routine = null,
            dotColor = colors.border
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("btn_back_timeline")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.textPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Timeline",
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your day at a glance",
                    color = colors.textSecondary,
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = { viewModel.showToast("ক্যালেন্ডার ভিউ শীঘ্রই আপডেট হবে") },
                modifier = Modifier.testTag("btn_calendar_icon")
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Calendar",
                    tint = colors.primaryBright
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Segmented Tab Filter (Day / Week / Month) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surfaceElevated)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            filterTabs.forEachIndexed { index, tab ->
                val isSelected = selectedFilterIndex == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) colors.primary else Color.Transparent)
                        .clickable { selectedFilterIndex = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) (if (colors.isDark) Color(0xFF0B0E14) else Color.White) else colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Date Navigator (< Today, May 14 >) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.showToast("পূর্ববর্তী দিনের শিডিউল") },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Day",
                    tint = colors.textSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Today, May 14",
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { viewModel.showToast("পরবর্তী দিনের শিডিউল") },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Day",
                    tint = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 24-Hour Vertical Timeline Cards ---
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            timelineSlots.forEachIndexed { index, slot ->
                TimelineRowItem(
                    slot = slot,
                    isLast = index == timelineSlots.size - 1,
                    onRoutineClick = { routine ->
                        viewModel.selectedRoutineForDetail = routine
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- Focus Score Card ---
        FocusScoreCard()

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TimelineRowItem(
    slot: TimelineSlot,
    isLast: Boolean,
    onRoutineClick: (FocusRoutine) -> Unit
) {
    val colors = AppTheme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Time Label Column
        Box(
            modifier = Modifier
                .width(52.dp)
                .padding(top = 2.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = slot.timeLabel,
                color = colors.textMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Timeline Line & Dot Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(slot.dotColor)
                    .border(2.dp, colors.surface, CircleShape)
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (slot.routine != null) 72.dp else 36.dp)
                        .background(colors.border)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Routine Event Card
        if (slot.routine != null) {
            val routine = slot.routine
            val routineColor = parseRoutineColor(routine.colorHex)
            val iconVector = getRoutineIconVector(routine.iconType)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(routineColor.copy(alpha = 0.08f))
                    .border(1.dp, routineColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .clickable { onRoutineClick(routine) }
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(routineColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = routineColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = routine.titleEnglish,
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = slot.customDisplayTime ?: routine.timeRange,
                            color = routineColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun FocusScoreCard() {
    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Focus Score",
                color = colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Circular 87% Progress Gauge
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(64.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width / 2f - 4f

                        // Background Ring
                        drawCircle(
                            color = colors.border,
                            radius = radius,
                            center = center,
                            style = Stroke(width = 6.dp.toPx())
                        )

                        // Progress Arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(colors.primary, colors.secondary, colors.primaryBright)
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * 0.87f,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Text(
                        text = "87%",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Text Description
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Great job!",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "You've planned 18h 30m of focused time today.",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Sparkline Trendline
                Canvas(
                    modifier = Modifier
                        .width(70.dp)
                        .height(36.dp)
                ) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.8f)
                        lineTo(size.width * 0.25f, size.height * 0.6f)
                        lineTo(size.width * 0.5f, size.height * 0.7f)
                        lineTo(size.width * 0.75f, size.height * 0.3f)
                        lineTo(size.width, size.height * 0.1f)
                    }

                    drawPath(
                        path = path,
                        color = colors.primary,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
