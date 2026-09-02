package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.FocusRoutine
import com.example.state.FocusViewModel
import com.example.ui.components.getRoutineIconVector
import com.example.ui.components.parseRoutineColor
import com.example.ui.theme.AppTheme
import java.util.Calendar

@Composable
fun CreateRoutineScreen(
    viewModel: FocusViewModel,
    onBack: () -> Unit
) {
    val colors = AppTheme.colors
    var currentStep by remember { mutableIntStateOf(1) } // 1: Apps, 2: Time, 3: Settings, 4: Review
    val context = LocalContext.current

    // Step 1: Apps
    val selectedAppPackages = remember { mutableStateListOf<String>() }

    // Step 2: Time & Days
    var startTime by remember { mutableStateOf("09:00 AM") }
    var endTime by remember { mutableStateOf("11:00 AM") }
    val dayLabels = listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র", "শনি")
    val selectedDays = remember { mutableStateListOf("সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র") }

    // Step 3: Settings
    var routineName by remember { mutableStateOf("My Schedule") }
    var selectedColorHex by remember { mutableStateOf("#10B981") }
    var selectedIconType by remember { mutableStateOf("book") }
    var blockShorts by remember { mutableStateOf(true) }
    var blockWebsites by remember { mutableStateOf(true) }
    var isStrict by remember { mutableStateOf(true) }

    fun calculateDuration(start: String, end: String): String {
        try {
            val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.ENGLISH)
            val sTime = format.parse(start)
            var eTime = format.parse(end)
            if (sTime != null && eTime != null) {
                if (eTime.before(sTime)) {
                    eTime = java.util.Date(eTime.time + 24 * 60 * 60 * 1000)
                }
                val diffMs = eTime.time - sTime.time
                val diffHours = diffMs / (1000 * 60 * 60)
                val diffMinutes = (diffMs / (1000 * 60)) % 60
                return if (diffMinutes > 0) "${diffHours}h ${diffMinutes}m" else "${diffHours}h"
            }
        } catch (e: Exception) {}
        return "Custom"
    }

    val durationText = calculateDuration(startTime, endTime)

    fun saveRoutine() {
        val daysString = if (selectedDays.size == 7) {
            "প্রতিদিন"
        } else if (selectedDays.containsAll(listOf("সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র")) && selectedDays.size == 5) {
            "সোম – শুক্র"
        } else {
            selectedDays.joinToString(", ")
        }

        val appsSummary = if (selectedAppPackages.size > 2) {
            "${selectedAppPackages.size} Apps"
        } else if (selectedAppPackages.isNotEmpty()) {
            val pkg = selectedAppPackages.first()
            val app = viewModel.installedApps.find { it.packageName == pkg }
            app?.name ?: "Apps"
        } else {
            "All Distractions"
        }

        // We save the JSON of packages so it can be used for actual blocking later
        val packagesJson = org.json.JSONArray(selectedAppPackages).toString()

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
                    contentDescription = "পেছনে",
                    tint = colors.textPrimary
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "নতুন রুটিন তৈরি",
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ধাপ $currentStep / 4",
                    color = colors.textSecondary,
                    fontSize = 11.sp
                )
            }
            Text(
                text = "সেভ",
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

        // --- Roadmap Progress Indicator (1)---(2)---(3)---(4) ---
        RoutineStepperBar(currentStep = currentStep, totalSteps = 4)

        Spacer(modifier = Modifier.height(16.dp))

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
                if (step == 1) {
                    // Step 1 uses LazyColumn internally to fill height properly
                    StepAppSelection(
                        viewModel = viewModel,
                        selectedAppPackages = selectedAppPackages,
                        onBack = onBack,
                        onNext = { currentStep = 2 }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        when (step) {
                            2 -> StepTimeAndDays(
                                context = context,
                                startTime = startTime,
                                onStartTimeChange = { startTime = it },
                                endTime = endTime,
                                onEndTimeChange = { endTime = it },
                                dayLabels = dayLabels,
                                selectedDays = selectedDays,
                                onBack = { currentStep = 1 },
                                onNext = { currentStep = 3 }
                            )
                            3 -> StepSettings(
                                routineName = routineName,
                                onNameChange = { routineName = it },
                                selectedColorHex = selectedColorHex,
                                onColorChange = { selectedColorHex = it },
                                selectedIconType = selectedIconType,
                                onIconChange = { selectedIconType = it },
                                blockShorts = blockShorts,
                                onBlockShortsChange = { blockShorts = it },
                                blockWebsites = blockWebsites,
                                onBlockWebsitesChange = { blockWebsites = it },
                                isStrict = isStrict,
                                onStrictChange = { isStrict = it },
                                onBack = { currentStep = 2 },
                                onNext = { currentStep = 4 }
                            )
                            4 -> StepReview(
                                routineName = routineName,
                                colorHex = selectedColorHex,
                                iconType = selectedIconType,
                                timeRange = "$startTime – $endTime",
                                durationText = durationText,
                                selectedDays = selectedDays,
                                selectedAppPackages = selectedAppPackages,
                                blockShorts = blockShorts,
                                blockWebsites = blockWebsites,
                                isStrict = isStrict,
                                onBack = { currentStep = 3 },
                                onSave = { saveRoutine() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineStepperBar(currentStep: Int, totalSteps: Int) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 1..totalSteps) {
            val isActive = i <= currentStep
            val isCompleted = i < currentStep
            
            // Circle
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isActive) colors.primary else colors.surfaceElevated)
                    .border(1.dp, if (isActive) colors.primary else colors.border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(text = when(i) { 1 -> "১"; 2 -> "২"; 3 -> "৩"; 4 -> "৪"; else -> i.toString() }, color = if (isActive) Color.White else colors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            // Connecting Line
            if (i < totalSteps) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (isCompleted) colors.primary else colors.border)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeTimePickerDialog(
    initialTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val colors = AppTheme.colors
    
    var initHour = 9
    var initMinute = 0
    try {
        val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.ENGLISH)
        val date = format.parse(initialTime)
        if (date != null) {
            val cal = Calendar.getInstance()
            cal.time = date
            initHour = cal.get(Calendar.HOUR_OF_DAY)
            initMinute = cal.get(Calendar.MINUTE)
        }
    } catch (e: Exception) {}

    val timePickerState = rememberTimePickerState(
        initialHour = initHour,
        initialMinute = initMinute,
        is24Hour = false
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "সময় নির্বাচন করুন",
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                // Material 3 TimePicker inherits colors from MaterialTheme.
                // We wrap it in a MaterialTheme that matches our custom colors.
                MaterialTheme(
                    colorScheme = MaterialTheme.colorScheme.copy(
                        primary = colors.primary,
                        onPrimary = Color.White,
                        surface = colors.surface,
                        onSurface = colors.textPrimary,
                        surfaceVariant = colors.surfaceElevated,
                        onSurfaceVariant = colors.textSecondary,
                        tertiary = colors.primaryBright,
                        outline = colors.border
                    )
                ) {
                    TimePicker(state = timePickerState)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "বাতিল", color = colors.textSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val h = timePickerState.hour
                            val m = timePickerState.minute
                            val amPm = if (h >= 12) "PM" else "AM"
                            val h12 = if (h % 12 == 0) 12 else h % 12
                            val formatted = String.format(java.util.Locale.ENGLISH, "%02d:%02d %s", h12, m, amPm)
                            onConfirm(formatted)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text(text = "সংরক্ষণ করুন", color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepAppSelection(
    viewModel: FocusViewModel,
    selectedAppPackages: MutableList<String>,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val colors = AppTheme.colors
    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps()
    }
    
    var currentTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("সমস্ত অ্যাপ", "সোশ্যাল মিডিয়া", "শর্ট ভিডিও")
    
    val socialMediaKeywords = listOf("facebook", "instagram", "twitter", "snapchat", "linkedin", "pinterest", "threads", "messenger", "whatsapp", "telegram", "discord", "imo", "viber")
    val shortVideoKeywords = listOf("tiktok", "kwai", "likee", "youtube", "vimeo", "mxtech", "bilibili")
    
    val filteredApps = viewModel.installedApps.filter { app ->
        val pkg = app.packageName.lowercase()
        when (currentTab) {
            0 -> true
            1 -> socialMediaKeywords.any { pkg.contains(it) }
            2 -> shortVideoKeywords.any { pkg.contains(it) }
            else -> true
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "ধাপ ১: অ্যাপ নির্বাচন", color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = "যে অ্যাপগুলো ব্লক করতে চান সেগুলো নির্বাচন করুন।", color = colors.textSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        ScrollableTabRow(
            selectedTabIndex = currentTab,
            containerColor = Color.Transparent,
            contentColor = colors.primary,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                    color = colors.primary
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = currentTab == index,
                    onClick = { currentTab = index },
                    text = { Text(text = title, fontSize = 13.sp, fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal) },
                    selectedContentColor = colors.primary,
                    unselectedContentColor = colors.textSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (!viewModel.isAppsLoaded) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.primary)
            }
        } else if (filteredApps.isEmpty()) {
             Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("এই ক্যাটাগরিতে কোনো অ্যাপ নেই", color = colors.textSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    val isChecked = selectedAppPackages.contains(app.packageName)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceElevated)
                            .clickable {
                                if (isChecked) selectedAppPackages.remove(app.packageName)
                                else selectedAppPackages.add(app.packageName)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = app.icon,
                            contentDescription = app.name,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = app.name, color = colors.textPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(checkedColor = colors.primary)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
        ) {
            Text(text = "পরবর্তী ধাপ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun StepTimeAndDays(
    context: Context,
    startTime: String,
    onStartTimeChange: (String) -> Unit,
    endTime: String,
    onEndTimeChange: (String) -> Unit,
    dayLabels: List<String>,
    selectedDays: MutableList<String>,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val colors = AppTheme.colors
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    if (showStartPicker) {
        ThemeTimePickerDialog(
            initialTime = startTime,
            onDismiss = { showStartPicker = false },
            onConfirm = { 
                onStartTimeChange(it)
                showStartPicker = false 
            }
        )
    }

    if (showEndPicker) {
        ThemeTimePickerDialog(
            initialTime = endTime,
            onDismiss = { showEndPicker = false },
            onConfirm = { 
                onEndTimeChange(it)
                showEndPicker = false 
            }
        )
    }

    Column {
        Text(
            text = "ধাপ ২: সময় ও দিন",
            color = colors.textPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "কখন এই রুটিনটি চালু হবে তা সেট করুন।",
            color = colors.textSecondary,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Time Pickers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "শুরু", color = colors.textSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceElevated)
                        .clickable { showStartPicker = true }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = startTime, color = colors.primaryBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "শেষ", color = colors.textSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceElevated)
                        .clickable { showEndPicker = true }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = endTime, color = colors.primaryBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "সক্রিয় দিনসমূহ", color = colors.textSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(10.dp))

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
                        .border(1.dp, if (isSelected) colors.primary else colors.border, CircleShape)
                        .clickable {
                            if (isSelected) selectedDays.remove(day)
                            else selectedDays.add(day)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        color = if (isSelected) Color.White else colors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text(text = "পেছনে", color = colors.textSecondary)
            }
            Button(onClick = onNext, modifier = Modifier.weight(1.5f), colors = ButtonDefaults.buttonColors(containerColor = colors.primary)) {
                Text(text = "পরবর্তী ধাপ", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepSettings(
    routineName: String,
    onNameChange: (String) -> Unit,
    selectedColorHex: String,
    onColorChange: (String) -> Unit,
    selectedIconType: String,
    onIconChange: (String) -> Unit,
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
    val routineColors = listOf("#10B981", "#3B82F6", "#8B5CF6", "#F43F5E", "#F59E0B", "#14B8A6")
    val routineIcons = listOf("book", "briefcase", "laptop", "person", "moon", "shield", "game")

    Column {
        Text(text = "ধাপ ৩: কাস্টমাইজেশন ও রুলস", color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = routineName,
            onValueChange = onNameChange,
            label = { Text("রুটিনের নাম") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                focusedLabelColor = colors.primary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "থিম কালার", color = colors.textSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            routineColors.forEach { hex ->
                val color = parseRoutineColor(hex)
                val isSelected = selectedColorHex == hex
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(if (isSelected) 3.dp else 0.dp, if (isSelected) colors.textPrimary else Color.Transparent, CircleShape)
                        .clickable { onColorChange(hex) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "আইকন", color = colors.textSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            routineIcons.forEach { type ->
                val isSelected = selectedIconType == type
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) colors.primary.copy(alpha = 0.2f) else colors.surfaceElevated)
                        .border(1.dp, if (isSelected) colors.primary else colors.border, RoundedCornerShape(12.dp))
                        .clickable { onIconChange(type) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = getRoutineIconVector(type), contentDescription = null, tint = if (isSelected) colors.primary else colors.textSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = colors.borderLight)
        Spacer(modifier = Modifier.height(16.dp))

        // Switches
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Block YouTube Shorts & Reels", color = colors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Switch(checked = blockShorts, onCheckedChange = onBlockShortsChange, colors = SwitchDefaults.colors(checkedTrackColor = colors.primary))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Block Distracting Websites", color = colors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Switch(checked = blockWebsites, onCheckedChange = onBlockWebsitesChange, colors = SwitchDefaults.colors(checkedTrackColor = colors.primary))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Strict Mode (Hard to bypass)", color = colors.alert, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Switch(checked = isStrict, onCheckedChange = onStrictChange, colors = SwitchDefaults.colors(checkedTrackColor = colors.alert))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text(text = "পেছনে", color = colors.textSecondary)
            }
            Button(onClick = onNext, modifier = Modifier.weight(1.5f), colors = ButtonDefaults.buttonColors(containerColor = colors.primary)) {
                Text(text = "রিভিউ করুন", color = Color.White)
            }
        }
    }
}

@Composable
private fun StepReview(
    routineName: String,
    colorHex: String,
    iconType: String,
    timeRange: String,
    durationText: String,
    selectedDays: List<String>,
    selectedAppPackages: List<String>,
    blockShorts: Boolean,
    blockWebsites: Boolean,
    isStrict: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val colors = AppTheme.colors
    val themeColor = parseRoutineColor(colorHex)
    Column {
        Text(text = "ধাপ ৪: সেভ ও রিভিউ", color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = "রুটিনের সেটিংস যাচাই করুন", color = colors.textSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

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
                        Icon(imageVector = getRoutineIconVector(iconType), contentDescription = null, tint = themeColor)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = routineName, color = colors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "$timeRange • $durationText", color = themeColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                HorizontalDivider(color = colors.border, thickness = 1.dp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Active Days:", color = colors.textSecondary, fontSize = 12.sp)
                    Text(text = selectedDays.joinToString(", "), color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Apps Blocked:", color = colors.textSecondary, fontSize = 12.sp)
                    Text(text = "${selectedAppPackages.size} Apps", color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Shorts & Websites:", color = colors.textSecondary, fontSize = 12.sp)
                    Text(text = if (blockShorts && blockWebsites) "Active" else "Custom", color = colors.primaryBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Protection Mode:", color = colors.textSecondary, fontSize = 12.sp)
                    Text(text = if (isStrict) "Strict" else "Standard", color = if (isStrict) colors.alert else colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(containerColor = themeColor, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "রুটিন তৈরি ও চালু করুন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(onClick = onBack, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().height(44.dp)) {
            Text(text = "এডিট করুন", color = colors.textSecondary, fontSize = 12.sp)
        }
    }
}
