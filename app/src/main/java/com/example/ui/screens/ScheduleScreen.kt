package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FocusRoutine
import com.example.state.FocusViewModel
import com.example.ui.components.RoutineDetailSheet
import com.example.ui.components.getRoutineIconVector
import com.example.ui.components.parseRoutineColor
import com.example.ui.theme.AppTheme

@Composable
fun ScheduleScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    // Check which sub-screen is active for smooth transitions
    AnimatedContent(
        targetState = when {
            viewModel.isCreateRoutineScreenVisible -> "create"
            viewModel.isTimelineScreenVisible -> "timeline"
            else -> "main"
        },
        transitionSpec = {
            if (targetState != "main") {
                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> -width } + fadeOut()
                )
            } else {
                (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> width } + fadeOut()
                )
            }
        },
        label = "schedule_screen_navigation"
    ) { screen ->
        when (screen) {
            "create" -> {
                CreateRoutineScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.isCreateRoutineScreenVisible = false }
                )
            }
            "timeline" -> {
                TimelineScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.isTimelineScreenVisible = false }
                )
            }
            else -> {
                ScheduleMainView(
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
        }
    }

    // Routine Detail Sheet Dialog
    viewModel.selectedRoutineForDetail?.let { routine ->
        RoutineDetailSheet(
            routine = routine,
            viewModel = viewModel,
            onDismiss = { viewModel.selectedRoutineForDetail = null }
        )
    }
}

@Composable
private fun ScheduleMainView(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val activeRoutinesCount = viewModel.focusRoutines.count { it.isEnabled }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // --- 1. Header (Focus Routines) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Focus Routines",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Plan your focus. Protect your time.",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
            }

            // Top Right + Icon Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.border, CircleShape)
                    .clickable { viewModel.isCreateRoutineScreenVisible = true }
                    .testTag("btn_header_add_routine"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Routine",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. Premium "Your time is protected" Hero Card ---
        ProtectedHeroCard(activeCount = activeRoutinesCount)

        Spacer(modifier = Modifier.height(20.dp))

        // --- 3. "Active Now" Section ---
        val activeRoutine = viewModel.focusRoutines.find { it.isActiveNow && it.isEnabled }
            ?: viewModel.focusRoutines.firstOrNull { it.isEnabled }

        if (activeRoutine != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Now",
                    color = colors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.isTimelineScreenVisible = true }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                        .testTag("btn_view_timeline_text")
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = colors.primaryBright,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "View Timeline",
                        color = colors.primaryBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            ActiveNowCard(
                routine = activeRoutine,
                onClick = { viewModel.selectedRoutineForDetail = activeRoutine }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // --- 4. "Your Routines" Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Routines",
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${viewModel.focusRoutines.size} টি রুটিন",
                color = colors.textMuted,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (viewModel.focusRoutines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = colors.textMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No schedules created yet.",
                            color = colors.textSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                viewModel.focusRoutines.forEach { routine ->
                    RoutineListCard(
                        routine = routine,
                        onToggle = { viewModel.toggleRoutine(routine.id) },
                        onClick = { viewModel.selectedRoutineForDetail = routine },
                        onDelete = { viewModel.deleteRoutine(routine.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 5. Big Premium "+ Create New Routine" CTA Button ---
        Button(
            onClick = { viewModel.isCreateRoutineScreenVisible = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = if (colors.isDark) Color(0xFF0B0E14) else Color.White
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(6.dp, RoundedCornerShape(18.dp), spotColor = colors.primary.copy(alpha = 0.4f))
                .testTag("btn_create_new_routine_cta")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Create New Routine",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * 3D-styled Radiant Emerald Shield Hero Banner matching Screen 1
 */
@Composable
private fun ProtectedHeroCard(activeCount: Int) {
    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (colors.isDark) {
                        listOf(Color(0xFF0D2818), Color(0xFF0B1F1C), Color(0xFF0A1926))
                    } else {
                        listOf(Color(0xFFECFDF5), Color(0xFFF0FDF4), Color(0xFFE0F2FE))
                    }
                )
            )
            .border(
                1.dp,
                if (colors.isDark) Color(0xFF10B981).copy(alpha = 0.3f) else Color(0xFFA7F3D0),
                RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your time is protected",
                    color = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$activeCount Active Routines",
                    color = if (colors.isDark) Color(0xFF34D399) else Color(0xFF059669),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "You're on track today! 💚",
                    color = if (colors.isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Glowing 3D Emerald Shield with Orbit Rings Graphic
            Box(
                modifier = Modifier.size(76.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(76.dp)) {
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Outer orbit glow ring
                    drawCircle(
                        color = Color(0xFF10B981).copy(alpha = 0.25f),
                        radius = size.width * 0.44f,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Inner orbit glow ring
                    drawCircle(
                        color = Color(0xFF06B6D4).copy(alpha = 0.2f),
                        radius = size.width * 0.36f,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                // Core Shield Badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF10B981), Color(0xFF059669))
                            )
                        )
                        .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFF10B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Shield Protected",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * "Active Now" Highlighted Card matching Screen 1
 */
@Composable
private fun ActiveNowCard(
    routine: FocusRoutine,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val routineColor = parseRoutineColor(routine.colorHex)
    val iconVector = getRoutineIconVector(routine.iconType)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (colors.isDark) Color(0xFF0F1A1C) else Color(0xFFF4FBF7))
            .border(
                1.2.dp,
                if (colors.isDark) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFB7EAD4),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("card_active_now_routine")
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
                // Icon in mint rounded container
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(routineColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = routineColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = routine.titleEnglish,
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(routineColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Active",
                                color = routineColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${routine.timeRange} • ${routine.durationText} left",
                        color = colors.textSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = routine.targetedAppsBangla,
                        color = routineColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "View Details",
                tint = colors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Routine List Card matching Screen 1 (Your Routines)
 */
@Composable
private fun RoutineListCard(
    routine: FocusRoutine,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = AppTheme.colors
    val routineColor = parseRoutineColor(routine.colorHex)
    val iconVector = getRoutineIconVector(routine.iconType)
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("routine_card_${routine.id}")
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
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(routineColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = routineColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = routine.titleEnglish,
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = routine.timeRange,
                        color = colors.textSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${routine.activeDaysEnglish} • ${routine.durationText}",
                        color = colors.textMuted,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = routine.targetedAppsBangla,
                        color = colors.primaryBright,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = routine.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = if (colors.isDark) Color(0xFF0B0E14) else Color.White,
                        checkedTrackColor = routineColor,
                        uncheckedThumbColor = colors.textMuted,
                        uncheckedTrackColor = colors.surfaceElevated
                    ),
                    modifier = Modifier.testTag("switch_routine_${routine.id}")
                )

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(colors.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("বিস্তারিত দেখুন", color = colors.textPrimary, fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("মুছে ফেলুন", color = colors.alert, fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
