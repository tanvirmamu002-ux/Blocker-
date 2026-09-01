package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FocusRoutine
import com.example.state.FocusViewModel
import com.example.ui.components.getRoutineIconVector
import com.example.ui.components.parseRoutineColor
import com.example.ui.theme.AppTheme

@Composable
fun CreateRoutineScreen(
    viewModel: FocusViewModel,
    onBack: () -> Unit
) {
    val colors = AppTheme.colors
    var currentStep by remember { mutableIntStateOf(1) } // 1: Basics, 2: Time, 3: Days, 4: Block, 5: Review

    // Step 1: Basics State
    var routineName by remember { mutableStateOf("Study Focus") }
    var selectedColorHex by remember { mutableStateOf("#10B981") }
    var selectedIconType by remember { mutableStateOf("book") }

    // Step 2: Time State
    var startTime by remember { mutableStateOf("08:00 PM") }
    var endTime by remember { mutableStateOf("11:00 PM") }
    var durationText by remember { mutableStateOf("3h") }

    // Step 3: Days State
    val dayLabels = listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র", "শনি")
    val selectedDays = remember { mutableStateListOf("সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র") }

    // Step 4: Block Rules State
    val availableAppCategories = listOf(
        "Social Media (Facebook, Instagram, X)",
        "Shorts & Video (YouTube, TikTok)",
        "Games (Gaming Apps)",
        "Messaging Apps",
        "Shopping & E-commerce"
    )
    val selectedAppCategories = remember {
        mutableStateListOf(
            "Social Media (Facebook, Instagram, X)",
            "Shorts & Video (YouTube, TikTok)"
        )
    }
    var blockShorts by remember { mutableStateOf(true) }
    var blockWebsites by remember { mutableStateOf(true) }
    var isStrict by remember { mutableStateOf(true) }

    fun saveRoutine() {
        val daysString = if (selectedDays.size == 7) {
            "প্রতিদিন"
        } else if (selectedDays.containsAll(listOf("সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র")) && selectedDays.size == 5) {
            "সোম – শুক্র"
        } else {
            selectedDays.joinToString(", ")
        }

        val appsSummary = if (selectedAppCategories.size >= 2) {
            "Social & Video Apps"
        } else if (selectedAppCategories.isNotEmpty()) {
            selectedAppCategories.first().substringBefore(" (")
        } else {
            "All Distractions"
        }

        val newRoutine = FocusRoutine(
            id = System.currentTimeMillis().toString(),
            titleBangla = routineName.ifBlank { "Custom Focus" },
            titleEnglish = routineName.ifBlank { "Custom Focus" },
            timeRange = "$startTime – $endTime",
            startTime = startTime,
            endTime = endTime,
            durationText = durationText,
            activeDaysBangla = daysString,
            activeDaysEnglish = daysString,
            targetedAppsBangla = "$appsSummary • Shorts",
            targetedAppsEnglish = "$appsSummary • Shorts",
            colorHex = selectedColorHex,
            iconType = selectedIconType,
            blockShorts = blockShorts,
            blockWebsites = blockWebsites,
            isStrict = isStrict,
            isEnabled = true,
            isActiveNow = false
        )

        viewModel.addRichRoutine(newRoutine)
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (currentStep > 1) currentStep--
                    else onBack()
                },
                modifier = Modifier.testTag("btn_back_create_routine")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.textPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = com.example.util.LocalAppStrings.current.routineCreateTitle,
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = com.example.util.LocalAppStrings.current.routineCreateSubtitle,
                    color = colors.textSecondary,
                    fontSize = 11.sp
                )
            }

            Text(
                text = "Save",
                color = colors.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { saveRoutine() }
                    .padding(8.dp)
                    .testTag("btn_top_save_routine")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 5-Step Visual Stepper Bar ---
        RoutineStepperBar(currentStep = currentStep)

        Spacer(modifier = Modifier.height(16.dp))

        // --- Step Body with Animated Transition ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "step_transition"
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (step) {
                        1 -> StepBasics(
                            routineName = routineName,
                            onNameChange = { routineName = it },
                            selectedColorHex = selectedColorHex,
                            onColorChange = { selectedColorHex = it },
                            selectedIconType = selectedIconType,
                            onIconChange = { selectedIconType = it },
                            onNext = { currentStep = 2 }
                        )

                        2 -> StepTime(
                            startTime = startTime,
                            onStartTimeChange = { startTime = it },
                            endTime = endTime,
                            onEndTimeChange = { endTime = it },
                            durationText = durationText,
                            onDurationChange = { durationText = it },
                            onBack = { currentStep = 1 },
                            onNext = { currentStep = 3 }
                        )

                        3 -> StepDays(
                            dayLabels = dayLabels,
                            selectedDays = selectedDays,
                            onBack = { currentStep = 2 },
                            onNext = { currentStep = 4 }
                        )

                        4 -> StepBlockRules(
                            availableAppCategories = availableAppCategories,
                            selectedAppCategories = selectedAppCategories,
                            blockShorts = blockShorts,
                            onBlockShortsChange = { blockShorts = it },
                            blockWebsites = blockWebsites,
                            onBlockWebsitesChange = { blockWebsites = it },
                            isStrict = isStrict,
                            onStrictChange = { isStrict = it },
                            onBack = { currentStep = 3 },
                            onNext = { currentStep = 5 }
                        )

                        5 -> StepReview(
                            routineName = routineName,
                            colorHex = selectedColorHex,
                            iconType = selectedIconType,
                            timeRange = "$startTime – $endTime",
                            durationText = durationText,
                            selectedDays = selectedDays,
                            selectedApps = selectedAppCategories,
                            blockShorts = blockShorts,
                            blockWebsites = blockWebsites,
                            isStrict = isStrict,
                            onBack = { currentStep = 4 },
                            onSave = { saveRoutine() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineStepperBar(currentStep: Int) {
    val colors = AppTheme.colors
    val stepTitles = listOf("Basics", "Time", "Days", "Block", "Review")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stepTitles.forEachIndexed { index, title ->
            val stepNumber = index + 1
            val isCompleted = stepNumber < currentStep
            val isActive = stepNumber == currentStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isActive -> colors.primary
                                isCompleted -> colors.primary.copy(alpha = 0.2f)
                                else -> colors.surfaceElevated
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                isActive -> colors.primary
                                isCompleted -> colors.primary
                                else -> colors.border
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = colors.primaryBright,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = stepNumber.toString(),
                            color = if (isActive) (if (colors.isDark) Color(0xFF0B0E14) else Color.White) else colors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
                    color = if (isActive) colors.primaryBright else colors.textMuted,
                    fontSize = 10.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (index < stepTitles.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (stepNumber < currentStep) colors.primary else colors.border)
                        .padding(horizontal = 2.dp)
                )
            }
        }
    }
}

/* ================= STEP 1: BASICS ================= */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepBasics(
    routineName: String,
    onNameChange: (String) -> Unit,
    selectedColorHex: String,
    onColorChange: (String) -> Unit,
    selectedIconType: String,
    onIconChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val colors = AppTheme.colors

    val colorOptions = listOf(
        "#10B981", // Emerald
        "#8B5CF6", // Purple
        "#3B82F6", // Blue
        "#F59E0B", // Orange
        "#EF4444", // Coral Pink
        "#06B6D4"  // Teal Cyan
    )

    val iconOptions = listOf(
        Pair("book", Icons.Default.Book),
        Pair("briefcase", Icons.Default.Work),
        Pair("laptop", Icons.Default.Computer),
        Pair("person", Icons.Default.Person),
        Pair("moon", Icons.Default.Nightlight),
        Pair("game", Icons.Default.SportsEsports),
        Pair("fitness", Icons.Default.FitnessCenter),
        Pair("shield", Icons.Default.Shield)
    )

    val quickNames = listOf("Study Focus", "Deep Work", "Night Detox", "Personal Time", "Reading Sprint")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        // Name Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Routine Name",
                color = colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${routineName.length}/20",
                color = colors.textMuted,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = routineName,
            onValueChange = { if (it.length <= 20) onNameChange(it) },
            leadingIcon = {
                Icon(
                    imageVector = getRoutineIconVector(selectedIconType),
                    contentDescription = null,
                    tint = parseRoutineColor(selectedColorHex),
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = parseRoutineColor(selectedColorHex),
                unfocusedBorderColor = colors.border,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                focusedContainerColor = colors.surfaceElevated,
                unfocusedContainerColor = colors.surfaceElevated
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_routine_name_step1")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick suggestions
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickNames.forEach { name ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (routineName == name) parseRoutineColor(selectedColorHex).copy(alpha = 0.15f) else colors.surfaceElevated)
                        .border(1.dp, if (routineName == name) parseRoutineColor(selectedColorHex) else colors.border, RoundedCornerShape(8.dp))
                        .clickable { onNameChange(name) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = name,
                        color = if (routineName == name) parseRoutineColor(selectedColorHex) else colors.textSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Color Picker
        Text(
            text = "Routine Color",
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            colorOptions.forEach { hex ->
                val c = parseRoutineColor(hex)
                val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(c)
                        .border(
                            if (isSelected) 3.dp else 1.dp,
                            if (isSelected) (if (colors.isDark) Color.White else Color(0xFF0F172A)) else Color.Transparent,
                            CircleShape
                        )
                        .clickable { onColorChange(hex) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Icon Picker
        Text(
            text = "Routine Icon",
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            iconOptions.take(5).forEach { (type, iconVec) ->
                val isSelected = selectedIconType == type
                val themeCol = parseRoutineColor(selectedColorHex)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) themeCol.copy(alpha = 0.15f) else colors.surfaceElevated)
                        .border(
                            1.5.dp,
                            if (isSelected) themeCol else colors.border,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onIconChange(type) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVec,
                        contentDescription = type,
                        tint = if (isSelected) themeCol else colors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Info Callout Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colors.primary.copy(alpha = 0.08f))
                .border(1.dp, colors.primary.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "This routine will help you stay focused",
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "We will block distractions so you can focus on what really matters.",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Next Button
        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = parseRoutineColor(selectedColorHex),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_next_step1")
        ) {
            Text(text = "Next: Set Time", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

/* ================= STEP 2: TIME ================= */
@Composable
private fun StepTime(
    startTime: String,
    onStartTimeChange: (String) -> Unit,
    endTime: String,
    onEndTimeChange: (String) -> Unit,
    durationText: String,
    onDurationChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val colors = AppTheme.colors

    val presetTimeBlocks = listOf(
        Triple("সকাল (Morning)", "09:00 AM", "01:00 PM"),
        Triple("দুপুর (Afternoon)", "02:00 PM", "05:00 PM"),
        Triple("রাত (Study)", "08:00 PM", "11:00 PM"),
        Triple("ঘুম (Sleep)", "11:00 PM", "06:00 AM")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(
            text = com.example.util.LocalAppStrings.current.routineTimeLabel,
            color = colors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = com.example.util.LocalAppStrings.current.routineTimeDesc,
            color = colors.textSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Time Input Pickers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = com.example.util.LocalAppStrings.current.routineStartTime, color = colors.textSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = startTime,
                    onValueChange = onStartTimeChange,
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
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
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = com.example.util.LocalAppStrings.current.routineEndTime, color = colors.textSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = endTime,
                    onValueChange = onEndTimeChange,
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = colors.secondary, modifier = Modifier.size(16.dp))
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
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Duration Info Chip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.primary.copy(alpha = 0.1f))
                .border(1.dp, colors.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = com.example.util.LocalAppStrings.current.routineProtectedTime, color = colors.textSecondary, fontSize = 12.sp)
                Text(
                    text = "$durationText ${com.example.util.LocalAppStrings.current.routineProtectedTimeDesc}",
                    color = colors.primaryBright,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Preset Slots
        Text(text = com.example.util.LocalAppStrings.current.routineQuickPreset, color = colors.textMuted, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            presetTimeBlocks.forEach { (label, s, e) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.surfaceElevated)
                        .border(1.dp, colors.borderLight, RoundedCornerShape(10.dp))
                        .clickable {
                            onStartTimeChange(s)
                            onEndTimeChange(e)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = label, color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(text = "$s – $e", color = colors.primaryBright, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Back", color = colors.textSecondary)
            }

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1.5f)
            ) {
                Text(text = "Next: Select Days", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

/* ================= STEP 3: DAYS ================= */
@Composable
private fun StepDays(
    dayLabels: List<String>,
    selectedDays: MutableList<String>,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val colors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(
            text = com.example.util.LocalAppStrings.current.routineRepeatDays,
            color = colors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = com.example.util.LocalAppStrings.current.routineRepeatDaysDesc,
            color = colors.textSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Preset Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                    .clickable {
                        selectedDays.clear()
                        selectedDays.addAll(dayLabels)
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = com.example.util.LocalAppStrings.current.routineAllDays, color = colors.primaryBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                    .clickable {
                        selectedDays.clear()
                        selectedDays.addAll(listOf("সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র"))
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = com.example.util.LocalAppStrings.current.routineWeekdays, color = colors.primaryBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Day Selector Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayLabels.forEach { day ->
                val isSelected = selectedDays.contains(day)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) colors.primary else colors.surfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) colors.primary else colors.border,
                            CircleShape
                        )
                        .clickable {
                            if (isSelected) selectedDays.remove(day)
                            else selectedDays.add(day)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        color = if (isSelected) (if (colors.isDark) Color(0xFF0B0E14) else Color.White) else colors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Back", color = colors.textSecondary)
            }

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1.5f)
            ) {
                Text(text = "Next: Block Rules", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

/* ================= STEP 4: BLOCK RULES ================= */
@Composable
private fun StepBlockRules(
    availableAppCategories: List<String>,
    selectedAppCategories: MutableList<String>,
    blockShorts: Boolean,
    onBlockShortsChange: (Boolean) -> Unit,
    blockWebsites: Boolean,
    onBlockWebsitesChange: (Boolean) -> Unit,
    isStrict: Boolean,
    onStrictChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val colors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(
            text = com.example.util.LocalAppStrings.current.routineBlockingFilter,
            color = colors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = com.example.util.LocalAppStrings.current.routineBlockingFilterDesc,
            color = colors.textSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // App Categories
        Text(text = com.example.util.LocalAppStrings.current.routineTargetCategory, color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            availableAppCategories.forEach { category ->
                val isChecked = selectedAppCategories.contains(category)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (isChecked) selectedAppCategories.remove(category)
                            else selectedAppCategories.add(category)
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (it) selectedAppCategories.add(category)
                            else selectedAppCategories.remove(category)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = colors.primary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = category, color = colors.textPrimary, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = colors.border, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Block Shorts & Reels Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = com.example.util.LocalAppStrings.current.routineShortsBlocking, color = colors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = com.example.util.LocalAppStrings.current.routineShortsBlockingDesc, color = colors.textSecondary, fontSize = 10.sp)
            }
            Switch(
                checked = blockShorts,
                onCheckedChange = onBlockShortsChange,
                colors = SwitchDefaults.colors(checkedTrackColor = colors.primary)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Block Custom Websites Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = com.example.util.LocalAppStrings.current.routineWebBlocking, color = colors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = com.example.util.LocalAppStrings.current.routineWebBlockingDesc, color = colors.textSecondary, fontSize = 10.sp)
            }
            Switch(
                checked = blockWebsites,
                onCheckedChange = onBlockWebsitesChange,
                colors = SwitchDefaults.colors(checkedTrackColor = colors.primary)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Strict Protection Mode Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = com.example.util.LocalAppStrings.current.routineStrictMode, color = colors.alert, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = com.example.util.LocalAppStrings.current.routineStrictModeDesc, color = colors.textSecondary, fontSize = 10.sp)
            }
            Switch(
                checked = isStrict,
                onCheckedChange = onStrictChange,
                colors = SwitchDefaults.colors(checkedTrackColor = colors.alert)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Back", color = colors.textSecondary)
            }

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1.5f)
            ) {
                Text(text = "Next: Review & Save", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

/* ================= STEP 5: REVIEW ================= */
@Composable
private fun StepReview(
    routineName: String,
    colorHex: String,
    iconType: String,
    timeRange: String,
    durationText: String,
    selectedDays: List<String>,
    selectedApps: List<String>,
    blockShorts: Boolean,
    blockWebsites: Boolean,
    isStrict: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val colors = AppTheme.colors
    val themeColor = parseRoutineColor(colorHex)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(
            text = com.example.util.LocalAppStrings.current.routineSummaryTitle,
            color = colors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = com.example.util.LocalAppStrings.current.routineSummaryDesc,
            color = colors.textSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Review Preview Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceElevated)
                .border(1.5.dp, themeColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(themeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getRoutineIconVector(iconType),
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = routineName,
                            color = colors.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$timeRange • $durationText",
                            color = themeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                HorizontalDivider(color = colors.border, thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = com.example.util.LocalAppStrings.current.routineActiveDays, color = colors.textSecondary, fontSize = 12.sp)
                    Text(text = selectedDays.joinToString(", "), color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = com.example.util.LocalAppStrings.current.routineBlockedApps, color = colors.textSecondary, fontSize = 12.sp)
                    Text(text = "${selectedApps.size} ${com.example.util.LocalAppStrings.current.routineCategoryCount}", color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Shorts & Websites:", color = colors.textSecondary, fontSize = 12.sp)
                    Text(text = if (blockShorts && blockWebsites) com.example.util.LocalAppStrings.current.routineCustomFilter else com.example.util.LocalAppStrings.current.routineCustomFilter, color = colors.primaryBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = com.example.util.LocalAppStrings.current.routineProtectionMode, color = colors.textSecondary, fontSize = 12.sp)
                    Text(
                        text = if (isStrict) com.example.util.LocalAppStrings.current.routineStrictLabel else com.example.util.LocalAppStrings.current.routineStandardLabel,
                        color = if (isStrict) colors.alert else colors.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save CTA
        Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(
                containerColor = themeColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_save_and_activate_routine")
        ) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Create & Activate Routine", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text(text = com.example.util.LocalAppStrings.current.routineEditSave, color = colors.textSecondary, fontSize = 12.sp)
        }
    }
}
