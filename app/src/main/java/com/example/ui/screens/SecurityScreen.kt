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
            onThemeSelected = { viewModel.setAppTheme(it) },
            currentLanguage = viewModel.appLanguage,
            onLanguageSelected = { viewModel.updateLanguage(context, it) },
            onPinAction = {
                val action = if (viewModel.isPinConfigured) FocusViewModel.PinAction.CHANGE_PIN else FocusViewModel.PinAction.CREATE_PIN
                viewModel.showPinBottomSheet(action)
            }
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
// 1. Unified Compact Quick Settings Card (Theme, Language, PIN in 3 Lines)
// -----------------------------------------------------------------------------------
@Composable
private fun UnifiedQuickSettingsCard(
    viewModel: FocusViewModel,
    currentTheme: AppThemeMode,
    onThemeSelected: (AppThemeMode) -> Unit,
    currentLanguage: com.example.util.AppLanguage,
    onLanguageSelected: (com.example.util.AppLanguage) -> Unit,
    onPinAction: () -> Unit
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Text(
                        text = strings.themeCardTitle,
                        color = colors.textPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        val nextTheme = when (currentTheme) {
                            AppThemeMode.DARK -> AppThemeMode.LIGHT
                            AppThemeMode.LIGHT -> AppThemeMode.SYSTEM
                            AppThemeMode.SYSTEM -> AppThemeMode.DARK
                        }
                        onThemeSelected(nextTheme)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primaryBright),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
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
                }
            }

            HorizontalDivider(color = colors.borderSubtle, thickness = 0.5.dp)

            // Line 2: Language
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = colors.primaryBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.languageCardTitle,
                        color = colors.textPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        val nextLang = if (currentLanguage == com.example.util.AppLanguage.BENGALI) 
                            com.example.util.AppLanguage.ENGLISH 
                        else 
                            com.example.util.AppLanguage.BENGALI
                        onLanguageSelected(nextLang)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primaryBright),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = currentLanguage.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = colors.borderSubtle, thickness = 0.5.dp)

            // Line 3: PIN Security / Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "PIN",
                        tint = colors.warning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.pinCardTitle,
                        color = colors.textPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onPinAction,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.warning.copy(alpha = 0.2f), contentColor = colors.warning),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LockReset,
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
