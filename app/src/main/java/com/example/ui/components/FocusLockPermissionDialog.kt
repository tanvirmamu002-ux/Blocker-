package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme
import com.example.util.FocusPermissionHelper

@Composable
fun FocusLockPermissionDialog(
    viewModel: FocusViewModel
) {
    if (!viewModel.isFocusLockPermissionDialogVisible) return

    val colors = AppTheme.colors
    val context = LocalContext.current

    val hasAccessibility = FocusPermissionHelper.isAccessibilityPermissionGranted(context)
    val hasUsageStats = FocusPermissionHelper.isUsageStatsPermissionGranted(context)
    val hasOverlay = FocusPermissionHelper.isOverlayPermissionGranted(context)
    val hasNotifications = FocusPermissionHelper.isNotificationPermissionGranted(context)

    Dialog(
        onDismissRequest = { viewModel.isFocusLockPermissionDialogVisible = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.warning.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Permissions Required",
                                tint = colors.warning,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = com.example.util.LocalAppStrings.current.permDialogTitle,
                                color = colors.textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = com.example.util.LocalAppStrings.current.permDialogDesc,
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.isFocusLockPermissionDialogVisible = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Permission Items
                PermissionSetupItem(
                    title = "Accessibility Service Permission",
                    description = com.example.util.LocalAppStrings.current.permAccDesc,
                    isGranted = hasAccessibility,
                    icon = Icons.Default.Accessibility,
                    onClick = { FocusPermissionHelper.openAccessibilitySettings(context) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                PermissionSetupItem(
                    title = "Usage Access Permission",
                    description = com.example.util.LocalAppStrings.current.permUsageDesc,
                    isGranted = hasUsageStats,
                    icon = Icons.Default.DataUsage,
                    onClick = { FocusPermissionHelper.openUsageStatsSettings(context) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                PermissionSetupItem(
                    title = "Display Over Other Apps (Overlay)",
                    description = com.example.util.LocalAppStrings.current.permOverlayDesc,
                    isGranted = hasOverlay,
                    icon = Icons.Default.Layers,
                    onClick = { FocusPermissionHelper.openOverlaySettings(context) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                PermissionSetupItem(
                    title = "Notification Permission",
                    description = com.example.util.LocalAppStrings.current.permNotifDesc,
                    isGranted = hasNotifications,
                    icon = Icons.Default.Notifications,
                    onClick = { FocusPermissionHelper.openNotificationSettings(context) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.checkPermissionsAndUpdate(context)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_recheck_permissions"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(com.example.util.LocalAppStrings.current.permRecheck, color = colors.textPrimary, fontSize = 12.sp)
                    }

                    val allGranted = hasAccessibility && hasUsageStats && hasOverlay && hasNotifications

                    Button(
                        onClick = {
                            viewModel.checkPermissionsAndUpdate(context)
                            if (allGranted) {
                                viewModel.isFocusLockPermissionDialogVisible = false
                                viewModel.isFocusLockSetupDialogVisible = true
                            }
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("btn_continue_lock_setup"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (allGranted) colors.primary else colors.surfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (allGranted) com.example.util.LocalAppStrings.current.permContinue else com.example.util.LocalAppStrings.current.permGrant,
                            color = if (allGranted) Color.White else colors.textMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionSetupItem(
    title: String,
    description: String,
    isGranted: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceElevated)
            .border(
                1.dp,
                if (isGranted) colors.secondary.copy(alpha = 0.4f) else colors.border,
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isGranted) colors.secondary.copy(alpha = 0.15f) else colors.warning.copy(alpha = 0.15f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isGranted) colors.secondary else colors.warning,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = if (isGranted) "Granted" else "Pending",
                    tint = if (isGranted) colors.secondary else colors.warning,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = colors.textSecondary,
                fontSize = 10.5.sp,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (!isGranted) {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = colors.warning),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(com.example.util.LocalAppStrings.current.permGrant, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
