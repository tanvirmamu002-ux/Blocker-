package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.ui.theme.HindSiliguri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppThemeMode
import com.example.data.SecurityPermission
import com.example.data.UserAccount
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme

@Composable
fun SecurityScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var showForgotPinDialog by remember { mutableStateOf(false) }
    var showThemeSelectorSheet by remember { mutableStateOf(false) }
    var showLanguageSelectorSheet by remember { mutableStateOf(false) }
    var showDeletePinConfirmDialog by remember { mutableStateOf(false) }
    var showNotificationSheet by remember { mutableStateOf(false) }
    val strings = com.example.util.LocalAppStrings.current
    val scrollState = rememberScrollState()

    LaunchedEffect(viewModel.scrollToPermissionsRequested) {
        if (viewModel.scrollToPermissionsRequested) {
            // Wait briefly to allow layout to settle, then scroll to the bottom/permissions section
            kotlinx.coroutines.delay(300)
            scrollState.animateScrollTo(scrollState.maxValue)
            viewModel.scrollToPermissionsRequested = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Page Title Header (Clean, gear icon removed)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = strings.securityTitle,
                color = colors.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = strings.securitySubtitle,
                color = colors.textSecondary,
                fontSize = 12.5.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        val context = androidx.compose.ui.platform.LocalContext.current

        // ==========================================
        // 1. UNIFIED COMPACT QUICK SETTINGS (Theme, Language, PIN in 3 Lines)
        // ==========================================
        UnifiedQuickSettingsCard(
            viewModel = viewModel,
            currentTheme = viewModel.appThemeMode,
            onOpenThemeSheet = { showThemeSelectorSheet = true },
            currentLanguage = viewModel.appLanguage,
            onOpenLanguageSheet = { showLanguageSelectorSheet = true },
            onPinAction = {
                if (viewModel.isPinConfigured) {
                    viewModel.showPinBottomSheet(FocusViewModel.PinAction.SETTINGS_VERIFY_CURRENT)
                } else {
                    viewModel.showPinBottomSheet(FocusViewModel.PinAction.SETTINGS_ENTER_NEW)
                }
            },
            onOpenNotificationSheet = { showNotificationSheet = true }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 3. 6 SEQUENTIAL SYSTEM PERMISSIONS (1 to 6)
        // ==========================================
        SequentialPermissionsCard(
            permissions = viewModel.permissions,
            onTogglePermission = { id -> viewModel.togglePermission(id, context) },
            onGrantAll = { viewModel.grantAllPermissions(context) }
        )

        Spacer(modifier = Modifier.height(28.dp))
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    // 1. Theme Selection Bottom Sheet
    if (showThemeSelectorSheet) {
        ThemeSelectionBottomSheet(
            currentTheme = viewModel.appThemeMode,
            onThemeSelected = { newTheme ->
                viewModel.setAppTheme(newTheme, context)
            },
            onDismiss = { showThemeSelectorSheet = false }
        )
    }

    // 2. Language Selection Bottom Sheet
    if (showLanguageSelectorSheet) {
        LanguageSelectionBottomSheet(
            currentLanguage = viewModel.appLanguage,
            onLanguageSelected = { newLang ->
                viewModel.updateLanguage(context, newLang)
            },
            onDismiss = { showLanguageSelectorSheet = false }
        )
    }

    // 3. PIN Management Options (Reset / Delete PIN) after verifying current PIN
    if (viewModel.showPinManageOptionsDialog) {
        PinManagementOptionsBottomSheet(
            onResetPinClicked = {
                viewModel.dismissPinManageOptionsDialog()
                viewModel.showPinBottomSheet(FocusViewModel.PinAction.SETTINGS_ENTER_NEW)
            },
            onDeletePinClicked = {
                viewModel.dismissPinManageOptionsDialog()
                showDeletePinConfirmDialog = true
            },
            onDismiss = { viewModel.dismissPinManageOptionsDialog() }
        )
    }

    // 4. Confirm Delete PIN Dialog
    if (showDeletePinConfirmDialog) {
        DeletePinConfirmDialog(
            onConfirmDelete = {
                viewModel.deletePin(context)
                showDeletePinConfirmDialog = false
            },
            onDismiss = { showDeletePinConfirmDialog = false }
        )
    }

    // Emergency PIN Reset / Recovery Dialog
    if (showForgotPinDialog) {
        ForgotPinDialog(
            userEmail = viewModel.userAccount.email,
            onDismiss = { showForgotPinDialog = false },
            onConfirmReset = {
                showForgotPinDialog = false
                viewModel.showPinBottomSheet(FocusViewModel.PinAction.RESET_PIN)
            }
        )
    }

    // 5. Notification & Alerts Control Bottom Sheet
    if (showNotificationSheet || viewModel.showNotificationSettingsSheet) {
        NotificationSettingsBottomSheet(
            viewModel = viewModel,
            onDismiss = {
                showNotificationSheet = false
                viewModel.showNotificationSettingsSheet = false
            }
        )
    }
}

// -----------------------------------------------------------------------------------
// 1. Unified Compact Quick Settings Card (Theme, Language, PIN in 3 Lines)
// -----------------------------------------------------------------------------------
@Composable
private fun UnifiedQuickSettingsCard(
    viewModel: FocusViewModel,
    currentTheme: AppThemeMode,
    onOpenThemeSheet: () -> Unit,
    currentLanguage: com.example.util.AppLanguage,
    onOpenLanguageSheet: () -> Unit,
    onPinAction: () -> Unit,
    onOpenNotificationSheet: () -> Unit
) {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("unified_quick_settings_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Line 1: Theme
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenThemeSheet() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = when (currentTheme) {
                            AppThemeMode.DARK -> Icons.Default.DarkMode
                            AppThemeMode.LIGHT -> Icons.Default.LightMode
                            AppThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                        },
                        contentDescription = "Theme",
                        tint = colors.primaryBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.themeCardTitle,
                            color = colors.textPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (currentTheme) {
                                AppThemeMode.DARK -> strings.themeDark
                                AppThemeMode.LIGHT -> strings.themeLight
                                AppThemeMode.SYSTEM -> strings.themeSystemActive
                            },
                            color = colors.textSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = onOpenThemeSheet,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primaryBright),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp).testTag("btn_select_theme")
                ) {
                    Text(
                        text = when (currentTheme) {
                            AppThemeMode.DARK -> strings.themeDarkActive
                            AppThemeMode.LIGHT -> strings.themeLightActive
                            AppThemeMode.SYSTEM -> strings.themeSystemActive
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            HorizontalDivider(color = colors.borderSubtle, thickness = 0.5.dp)

            // Line 2: Language
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenLanguageSheet() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = colors.primaryBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.languageCardTitle,
                            color = colors.textPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentLanguage == com.example.util.AppLanguage.BENGALI) "বাংলা" else "English [ইংরেজি]",
                            color = colors.textSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = onOpenLanguageSheet,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primaryBright),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp).testTag("btn_select_language")
                ) {
                    Text(
                        text = currentLanguage.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            HorizontalDivider(color = colors.borderSubtle, thickness = 0.5.dp)

            // Line 3: PIN Security / Reset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onPinAction() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "PIN",
                        tint = if (viewModel.isPinConfigured) colors.primary else colors.warning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.pinCardTitle,
                            color = colors.textPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (viewModel.isPinConfigured) strings.pinConfigured else strings.pinNotConfigured,
                            color = if (viewModel.isPinConfigured) colors.secondary else colors.textMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Button(
                    onClick = onPinAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isPinConfigured) colors.primary.copy(alpha = 0.15f) else colors.warning.copy(alpha = 0.2f),
                        contentColor = if (viewModel.isPinConfigured) colors.primary else colors.warning
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp).testTag("btn_manage_pin")
                ) {
                    Icon(
                        imageVector = if (viewModel.isPinConfigured) Icons.Default.LockReset else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (viewModel.isPinConfigured) strings.pinChangeButton else strings.pinCreateButton,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = colors.borderSubtle, thickness = 0.5.dp)

            // Line 4: Notifications & Alerts
            val context = androidx.compose.ui.platform.LocalContext.current
            val isNotifGranted = com.example.util.FocusPermissionHelper.isNotificationPermissionGranted(context)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenNotificationSheet() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = if (isNotifGranted) colors.primary else colors.warning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.notificationQuickTitle,
                            color = colors.textPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isNotifGranted) strings.notificationGranted else strings.notificationNotGranted,
                            color = if (isNotifGranted) colors.secondary else colors.warning,
                            fontSize = 11.5.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = onOpenNotificationSheet,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primaryBright),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp).testTag("btn_manage_notifications")
                ) {
                    Text(
                        text = if (isNotifGranted) "কন্ট্রোল" else "অনুমতি",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// Theme Selection Bottom Sheet (System, Light, Dark)
// -----------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelectionBottomSheet(
    currentTheme: AppThemeMode,
    onThemeSelected: (AppThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.themeSheetTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = HindSiliguri
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = strings.themeSheetSubtitle,
                        fontSize = 12.5.sp,
                        color = colors.textSecondary,
                        fontFamily = HindSiliguri
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val themeOptions = listOf(
                Triple(AppThemeMode.SYSTEM, strings.themeSystemActive, strings.themeSystemDesc),
                Triple(AppThemeMode.LIGHT, strings.themeLight, strings.themeLightDesc),
                Triple(AppThemeMode.DARK, strings.themeDark, strings.themeDarkDesc)
            )

            themeOptions.forEach { (mode, title, desc) ->
                val isSelected = currentTheme == mode
                val cardBorderColor = if (isSelected) colors.primary else colors.border
                val cardBg = if (isSelected) colors.primary.copy(alpha = 0.08f) else colors.surfaceElevated

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(cardBg)
                        .border(if (isSelected) 1.5.dp else 1.dp, cardBorderColor, RoundedCornerShape(14.dp))
                        .clickable {
                            onThemeSelected(mode)
                            onDismiss()
                        }
                        .padding(14.dp)
                        .testTag("theme_option_${mode.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) colors.primary.copy(alpha = 0.15f) else colors.surface)
                                    .border(1.dp, if (isSelected) colors.primary else colors.border, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (mode) {
                                        AppThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                                        AppThemeMode.LIGHT -> Icons.Default.LightMode
                                        AppThemeMode.DARK -> Icons.Default.DarkMode
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) colors.primary else colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = title,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) colors.primary else colors.textPrimary,
                                    fontFamily = HindSiliguri
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary,
                                    fontFamily = HindSiliguri
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, colors.border, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// Language Selection Bottom Sheet (Bangla, English)
// -----------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelectionBottomSheet(
    currentLanguage: com.example.util.AppLanguage,
    onLanguageSelected: (com.example.util.AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.languageSheetTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = HindSiliguri
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = strings.languageSheetSubtitle,
                        fontSize = 12.5.sp,
                        color = colors.textSecondary,
                        fontFamily = HindSiliguri
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val languageOptions = listOf(
                Pair(com.example.util.AppLanguage.BENGALI, Pair("বাংলা", "অ্যাপের সম্পূর্ণ ইন্টারফেস বাংলায়")),
                Pair(com.example.util.AppLanguage.ENGLISH, Pair("English [ইংরেজি]", "App interface and controls in English"))
            )

            languageOptions.forEach { (lang, info) ->
                val isSelected = currentLanguage == lang
                val cardBorderColor = if (isSelected) colors.primary else colors.border
                val cardBg = if (isSelected) colors.primary.copy(alpha = 0.08f) else colors.surfaceElevated

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(cardBg)
                        .border(if (isSelected) 1.5.dp else 1.dp, cardBorderColor, RoundedCornerShape(14.dp))
                        .clickable {
                            onLanguageSelected(lang)
                            onDismiss()
                        }
                        .padding(14.dp)
                        .testTag("language_option_${lang.code}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) colors.primary.copy(alpha = 0.15f) else colors.surface)
                                    .border(1.dp, if (isSelected) colors.primary else colors.border, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (lang == com.example.util.AppLanguage.BENGALI) "বাং" else "EN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) colors.primary else colors.textSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = info.first,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) colors.primary else colors.textPrimary,
                                    fontFamily = HindSiliguri
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = info.second,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary,
                                    fontFamily = HindSiliguri
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, colors.border, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// PIN Management Options Bottom Sheet (Reset / Delete PIN)
// -----------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PinManagementOptionsBottomSheet(
    onResetPinClicked: () -> Unit,
    onDeletePinClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .padding(bottom = 26.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.pinManageOptionsTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = HindSiliguri
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "পিন যাচাই সফল হয়েছে। পরবর্তী পদক্ষেপ বেছে নিন",
                        fontSize = 12.5.sp,
                        color = colors.textSecondary,
                        fontFamily = HindSiliguri
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Option 1: Reset / Change PIN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                    .clickable {
                        onResetPinClicked()
                    }
                    .padding(16.dp)
                    .testTag("option_change_pin")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colors.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.pinOptionChangeTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontFamily = HindSiliguri
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = strings.pinOptionChangeDesc,
                            fontSize = 12.5.sp,
                            color = colors.textSecondary,
                            fontFamily = HindSiliguri
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = colors.textMuted
                    )
                }
            }

            // Option 2: Delete PIN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                    .clickable {
                        onDeletePinClicked()
                    }
                    .padding(16.dp)
                    .testTag("option_delete_pin")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colors.alert.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = colors.alert,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.pinOptionDeleteTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.alert,
                            fontFamily = HindSiliguri
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = strings.pinOptionDeleteDesc,
                            fontSize = 12.5.sp,
                            color = colors.textSecondary,
                            fontFamily = HindSiliguri
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = colors.textMuted
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// Confirm Delete PIN Dialog
// -----------------------------------------------------------------------------------
@Composable
private fun DeletePinConfirmDialog(
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.pinDeleteConfirmTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                fontFamily = HindSiliguri
            )
        },
        text = {
            Text(
                text = strings.pinDeleteConfirmMessage,
                fontSize = 13.5.sp,
                color = colors.textSecondary,
                fontFamily = HindSiliguri
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.alert,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = strings.pinDeleteConfirmButton,
                    fontWeight = FontWeight.Bold,
                    fontFamily = HindSiliguri
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = strings.forgotPinCancel,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Medium,
                    fontFamily = HindSiliguri
                )
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

// -----------------------------------------------------------------------------------
// 3. 6 Sequential System Permissions (Dual-state glowing items)
// -----------------------------------------------------------------------------------
@Composable
private fun SequentialPermissionsCard(
    permissions: List<SecurityPermission>,
    onTogglePermission: (String) -> Unit,
    onGrantAll: () -> Unit
) {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current
    val grantedCount = permissions.count { it.isGranted }
    val totalCount = permissions.size
    val progress = if (totalCount > 0) grantedCount.toFloat() / totalCount else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.permCardTitle,
                        color = colors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = strings.permCardSubtitle,
                        color = colors.textSecondary,
                        fontSize = 11.5.sp
                    )
                }

                if (grantedCount < totalCount) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.primary.copy(alpha = 0.15f))
                            .border(1.dp, colors.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable(onClick = onGrantAll)
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("btn_grant_all_permissions")
                    ) {
                        Text(
                            text = strings.permGrantAll,
                            color = colors.primaryBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (grantedCount == totalCount) Color(0xFF10B981) else colors.primary,
                trackColor = colors.surfaceElevated
            )

            Spacer(modifier = Modifier.height(14.dp))

            // List 6 Permissions sequentially with Dual-State Glow
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                permissions.sortedBy { it.serialNumber }.forEach { perm ->
                    SinglePermissionRow(
                        permission = perm,
                        onToggle = { onTogglePermission(perm.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SinglePermissionRow(
    permission: SecurityPermission,
    onToggle: () -> Unit
) {
    val colors = AppTheme.colors
    val isGranted = permission.isGranted
    val currentLang = com.example.util.LocalAppStrings.current // Just to trigger recomposition or check language
    val isBengali = currentLang === com.example.util.BengaliStrings

    // Dynamic Dual-State Colors & Glow Brushes (Matching BlockerScreen style)
    val cardBackgroundBrush = if (isGranted) {
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

    val cardBorderBrush = if (isGranted) {
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBackgroundBrush)
            .border(
                width = 1.2.dp,
                brush = cardBorderBrush,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 11.dp)
            .testTag("permission_item_${permission.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Serial Number / Status badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isGranted) Color(0xFF10B981).copy(alpha = 0.18f) else Color(0xFFEF4444).copy(alpha = 0.14f)
                        )
                        .border(
                            1.dp,
                            if (isGranted) Color(0xFF10B981).copy(alpha = 0.35f) else Color(0xFFEF4444).copy(alpha = 0.3f),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGranted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Granted",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(17.dp)
                        )
                    } else {
                        Text(
                            text = "${permission.serialNumber}",
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (isBengali) permission.titleBangla else permission.titleEnglish,
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (isBengali) permission.descBangla else permission.descEnglish,
                        color = if (isGranted) colors.textSecondary else if (colors.isDark) Color(0xFFFCA5A5) else Color(0xFFDC2626),
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Toggle Switch
            Switch(
                checked = isGranted,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF10B981),
                    uncheckedThumbColor = if (colors.isDark) Color(0xFFEF4444) else Color(0xFFDC2626),
                    uncheckedTrackColor = if (colors.isDark) Color(0xFF3B181D) else Color(0xFFFEE2E2),
                    uncheckedBorderColor = Color(0xFFEF4444).copy(alpha = 0.4f)
                ),
                modifier = Modifier.testTag("switch_perm_${permission.id}")
            )
        }
    }
}

// -----------------------------------------------------------------------------------
// Emergency Forgot PIN Dialog
// -----------------------------------------------------------------------------------
@Composable
private fun ForgotPinDialog(
    userEmail: String,
    onDismiss: () -> Unit,
    onConfirmReset: () -> Unit
) {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .border(1.dp, colors.warning.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.warning.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = colors.warning,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.forgotPinDialogTitle,
                        color = colors.textPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.forgotPinDialogDesc(userEmail),
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = strings.forgotPinCancel, color = colors.textSecondary)
                    }

                    Button(
                        onClick = onConfirmReset,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.warning,
                            contentColor = if (colors.isDark) Color(0xFF0D1117) else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = strings.forgotPinConfirm, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// 5. Notification & Alerts Control Bottom Sheet
// -----------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationSettingsBottomSheet(
    viewModel: FocusViewModel,
    onDismiss: () -> Unit
) {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val isGranted = com.example.util.FocusPermissionHelper.isNotificationPermissionGranted(context)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        scrimColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(colors.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.notificationSheetTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = HindSiliguri
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = strings.notificationSheetSubtitle,
                        fontSize = 12.5.sp,
                        color = colors.textSecondary,
                        fontFamily = HindSiliguri
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Permission Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isGranted) colors.primary.copy(alpha = 0.08f) else colors.warning.copy(alpha = 0.1f))
                    .border(
                        1.dp,
                        if (isGranted) colors.primary.copy(alpha = 0.3f) else colors.warning.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isGranted) colors.primary.copy(alpha = 0.15f) else colors.warning.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isGranted) Icons.Default.Check else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isGranted) colors.primary else colors.warning,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.notificationPermCardTitle,
                                color = colors.textPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = HindSiliguri
                            )
                            Text(
                                text = if (isGranted) strings.notificationGranted else strings.notificationPermCardDesc,
                                color = colors.textSecondary,
                                fontSize = 11.5.sp,
                                fontFamily = HindSiliguri
                            )
                        }
                    }

                    if (!isGranted) {
                        Button(
                            onClick = { com.example.util.FocusPermissionHelper.openNotificationSettings(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.warning,
                                contentColor = androidx.compose.ui.graphics.Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = strings.notificationGrantButton,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = HindSiliguri
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = strings.notificationGrantedChip,
                                color = colors.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = HindSiliguri
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Toggles List
            val toggles = listOf(
                NotificationToggleItem(
                    title = strings.notificationToggleBlocking,
                    desc = strings.notificationToggleBlockingDesc,
                    checked = viewModel.notifBlockingAlertsEnabled,
                    onCheckedChange = { viewModel.setNotifBlockingAlerts(it, context) }
                ),
                NotificationToggleItem(
                    title = strings.notificationToggleTimer,
                    desc = strings.notificationToggleTimerDesc,
                    checked = viewModel.notifTimerUpdatesEnabled,
                    onCheckedChange = { viewModel.setNotifTimerUpdates(it, context) }
                ),
                NotificationToggleItem(
                    title = strings.notificationToggleSecurity,
                    desc = strings.notificationToggleSecurityDesc,
                    checked = viewModel.notifSecurityAlertsEnabled,
                    onCheckedChange = { viewModel.setNotifSecurityAlerts(it, context) }
                ),
                NotificationToggleItem(
                    title = strings.notificationToggleReminders,
                    desc = strings.notificationToggleRemindersDesc,
                    checked = viewModel.notifRemindersEnabled,
                    onCheckedChange = { viewModel.setNotifReminders(it, context) }
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(16.dp))
                    .padding(vertical = 4.dp)
            ) {
                toggles.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                color = colors.textPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = HindSiliguri
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.desc,
                                color = colors.textMuted,
                                fontSize = 11.5.sp,
                                fontFamily = HindSiliguri
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = item.checked,
                            onCheckedChange = item.onCheckedChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                                checkedTrackColor = colors.primary,
                                uncheckedThumbColor = colors.textMuted,
                                uncheckedTrackColor = colors.surface
                            )
                        )
                    }

                    if (index < toggles.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            color = colors.borderSubtle,
                            thickness = 0.5.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Test Notification Button
            Button(
                onClick = { viewModel.sendTestNotification(context) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = androidx.compose.ui.graphics.Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_send_test_notification")
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.notificationTestButton,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = HindSiliguri
                )
            }
        }
    }
}

private data class NotificationToggleItem(
    val title: String,
    val desc: String,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)

