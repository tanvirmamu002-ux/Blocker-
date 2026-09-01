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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val strings = com.example.util.LocalAppStrings.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
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
        // 1. COMPACT & MODERN THEME SELECTOR
        // ==========================================
        ThemeSelectionCard(
            currentTheme = viewModel.appThemeMode,
            onThemeSelected = { viewModel.setAppTheme(it) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 1.5 LANGUAGE SELECTOR
        // ==========================================
        LanguageSelectionCard(
            currentLanguage = viewModel.appLanguage,
            onLanguageSelected = { viewModel.updateLanguage(context, it) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 2. PIN CREATION & RESET SECTION
        // ==========================================
        PinManagementCard(
            viewModel = viewModel,
            onCreatePin = { viewModel.showPinBottomSheet(FocusViewModel.PinAction.CREATE_PIN) },
            onChangePin = { viewModel.showPinBottomSheet(FocusViewModel.PinAction.CHANGE_PIN) },
            onResetPin = { viewModel.showPinBottomSheet(FocusViewModel.PinAction.RESET_PIN) },
            onForgotPin = { showForgotPinDialog = true }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 3. 6 SEQUENTIAL SYSTEM PERMISSIONS (1 to 6)
        // ==========================================
        SequentialPermissionsCard(
            permissions = viewModel.permissions,
            onTogglePermission = { id -> viewModel.togglePermission(id) },
            onGrantAll = { viewModel.grantAllPermissions() }
        )

        Spacer(modifier = Modifier.height(28.dp))
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
}

// -----------------------------------------------------------------------------------
// 1. Compact & Modern Segmented Theme Selection Card
// -----------------------------------------------------------------------------------
@Composable
private fun ThemeSelectionCard(
    currentTheme: AppThemeMode,
    onThemeSelected: (AppThemeMode) -> Unit
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
            .testTag("theme_selection_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (currentTheme) {
                                AppThemeMode.DARK -> Icons.Default.DarkMode
                                AppThemeMode.LIGHT -> Icons.Default.LightMode
                                AppThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                            },
                            contentDescription = "Theme Icon",
                            tint = colors.primaryBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = strings.themeCardTitle,
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = when (currentTheme) {
                        AppThemeMode.DARK -> strings.themeDarkActive
                        AppThemeMode.LIGHT -> strings.themeLightActive
                        AppThemeMode.SYSTEM -> strings.themeSystemActive
                    },
                    color = colors.primaryBright,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Modern Horizontal Segmented Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.borderLight, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CompactThemeSegment(
                    label = strings.themeDark,
                    icon = Icons.Default.DarkMode,
                    isSelected = currentTheme == AppThemeMode.DARK,
                    modifier = Modifier.weight(1f),
                    onClick = { onThemeSelected(AppThemeMode.DARK) }
                )

                CompactThemeSegment(
                    label = strings.themeLight,
                    icon = Icons.Default.LightMode,
                    isSelected = currentTheme == AppThemeMode.LIGHT,
                    modifier = Modifier.weight(1f),
                    onClick = { onThemeSelected(AppThemeMode.LIGHT) }
                )

                CompactThemeSegment(
                    label = strings.themeSystem,
                    icon = Icons.Default.SettingsBrightness,
                    isSelected = currentTheme == AppThemeMode.SYSTEM,
                    modifier = Modifier.weight(1f),
                    onClick = { onThemeSelected(AppThemeMode.SYSTEM) }
                )
            }
        }
    }
}

@Composable
private fun CompactThemeSegment(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val animBgColor by animateColorAsState(
        targetValue = if (isSelected) colors.primary.copy(alpha = 0.22f) else Color.Transparent,
        animationSpec = tween(200),
        label = "segment_bg"
    )
    val animBorderColor by animateColorAsState(
        targetValue = if (isSelected) colors.primary.copy(alpha = 0.6f) else Color.Transparent,
        animationSpec = tween(200),
        label = "segment_border"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(animBgColor)
            .border(1.dp, animBorderColor, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) colors.primaryBright else colors.textMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (isSelected) colors.textPrimary else colors.textSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

// -----------------------------------------------------------------------------------
// 1.5. Compact Language Selection Card
// -----------------------------------------------------------------------------------
@Composable
private fun LanguageSelectionCard(
    currentLanguage: com.example.util.AppLanguage,
    onLanguageSelected: (com.example.util.AppLanguage) -> Unit
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
            .testTag("language_selection_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Language,
                            contentDescription = "Language Icon",
                            tint = colors.primaryBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = strings.languageCardTitle,
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Modern Horizontal Segmented Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.borderLight, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CompactLanguageSegment(
                    label = "বাংলা",
                    isSelected = currentLanguage == com.example.util.AppLanguage.BENGALI,
                    modifier = Modifier.weight(1f),
                    onClick = { onLanguageSelected(com.example.util.AppLanguage.BENGALI) }
                )

                CompactLanguageSegment(
                    label = "English",
                    isSelected = currentLanguage == com.example.util.AppLanguage.ENGLISH,
                    modifier = Modifier.weight(1f),
                    onClick = { onLanguageSelected(com.example.util.AppLanguage.ENGLISH) }
                )
            }
        }
    }
}

@Composable
private fun CompactLanguageSegment(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val animBgColor by animateColorAsState(
        targetValue = if (isSelected) colors.primary.copy(alpha = 0.22f) else Color.Transparent,
        animationSpec = tween(200),
        label = "segment_bg"
    )
    val animBorderColor by animateColorAsState(
        targetValue = if (isSelected) colors.primary.copy(alpha = 0.6f) else Color.Transparent,
        animationSpec = tween(200),
        label = "segment_border"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(animBgColor)
            .border(1.dp, animBorderColor, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) colors.textPrimary else colors.textSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// -----------------------------------------------------------------------------------
// 2. PIN Management Card (Clean & Modern)
// -----------------------------------------------------------------------------------
@Composable
private fun PinManagementCard(
    viewModel: FocusViewModel,
    onCreatePin: () -> Unit,
    onChangePin: () -> Unit,
    onResetPin: () -> Unit,
    onForgotPin: () -> Unit
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
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.warning.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "PIN Lock",
                            tint = colors.warning,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = strings.pinCardTitle,
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (viewModel.isPinConfigured) strings.pinConfigured else strings.pinNotConfigured,
                            color = if (viewModel.isPinConfigured) colors.secondary else colors.alert,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons for PIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = if (viewModel.isPinConfigured) onChangePin else onCreatePin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = if (colors.isDark) Color(0xFF0D1117) else Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_pin_create_change")
                ) {
                    Icon(
                        imageVector = if (viewModel.isPinConfigured) Icons.Default.LockReset else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (viewModel.isPinConfigured) strings.pinChangeButton else strings.pinCreateButton,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onResetPin,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.textPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(colors.border, colors.border))
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_pin_reset")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = colors.warning,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = strings.pinResetButton,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Forgot PIN hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onForgotPin)
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.pinForgotHint,
                    color = colors.textMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
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
