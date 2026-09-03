package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguri
import com.example.util.LocalAppStrings

@Composable
fun NotificationAlertsDialog(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val strings = LocalAppStrings.current

    if (viewModel.isNotificationAlertVisible) {
        Dialog(onDismissRequest = { viewModel.isNotificationAlertVisible = false }) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Alerts",
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = strings.notificationAlertsTitle,
                                        color = colors.textPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = HindSiliguri
                                    )
                                    if (viewModel.unreadNotificationCount > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(colors.alert)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${viewModel.unreadNotificationCount}",
                                                color = androidx.compose.ui.graphics.Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "সর্বশেষ নিরাপত্তা ও ব্লকিং হিস্টোরি",
                                    color = colors.textMuted,
                                    fontSize = 11.5.sp,
                                    fontFamily = HindSiliguri
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.isNotificationAlertVisible = false },
                            modifier = Modifier.testTag("btn_close_alerts")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Notifications List
                    if (viewModel.notificationAlerts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.surfaceElevated)
                                .border(1.dp, colors.borderSubtle, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = colors.textMuted,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = strings.notificationAlertsEmpty,
                                    color = colors.textSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = HindSiliguri
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(viewModel.notificationAlerts, key = { it.id }) { item ->
                                val itemColor = when (item.type) {
                                    "alert" -> colors.alert
                                    "shield" -> colors.primary
                                    "security" -> colors.secondary
                                    "timer" -> colors.purple
                                    else -> colors.alert
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (!item.isRead) colors.surfaceElevated else colors.surfaceElevated.copy(alpha = 0.5f))
                                        .border(
                                            1.dp,
                                            if (!item.isRead) itemColor.copy(alpha = 0.4f) else colors.borderSubtle,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Indicator icon/dot
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(itemColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (item.type) {
                                                "alert" -> Icons.Default.Shield
                                                "security" -> Icons.Default.Security
                                                "timer" -> Icons.Default.Timer
                                                else -> Icons.Default.Shield
                                            },
                                            contentDescription = null,
                                            tint = itemColor,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            color = colors.textPrimary,
                                            fontSize = 12.5.sp,
                                            fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Medium,
                                            fontFamily = HindSiliguri
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = item.subtitle,
                                                color = colors.textMuted,
                                                fontSize = 11.sp,
                                                fontFamily = HindSiliguri,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = item.timeAgo,
                                                color = colors.textMuted,
                                                fontSize = 10.sp,
                                                fontFamily = HindSiliguri
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = { viewModel.deleteNotification(item.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete alert",
                                            tint = colors.textMuted,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom Action Buttons: Mark all read & Clear all
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.clearAllNotifications() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textSecondary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = strings.notificationClearAll, fontSize = 11.sp, fontFamily = HindSiliguri)
                        }

                        Button(
                            onClick = {
                                viewModel.markAllNotificationsAsRead()
                                viewModel.isNotificationAlertVisible = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary,
                                contentColor = androidx.compose.ui.graphics.Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.notificationMarkAllRead,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                fontFamily = HindSiliguri
                            )
                        }
                    }
                }
            }
        }
    }
}
