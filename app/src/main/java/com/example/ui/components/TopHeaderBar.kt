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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme

@Composable
fun TopHeaderBar(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Logo + App Name
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceElevated.copy(alpha = 0.8f))
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "App Shield Logo",
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Focus Shield",
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontFamily = com.example.ui.theme.HindSiliguri,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
        }

        // Right Actions: Notification, Profile JD
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Notification Bell with red badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceElevated.copy(alpha = 0.8f))
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(14.dp))
                    .clickable { viewModel.isNotificationAlertVisible = true }
                    .testTag("btn_notifications"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
                // Red badge dot if unread
                if (viewModel.unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(colors.alert)
                    )
                }
            }

            // User Profile Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceElevated)
                    .border(1.5.dp, colors.primary.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .clickable { viewModel.isProfileScreenVisible = true }
                    .testTag("btn_user_profile"),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.userAccount.avatarUri != null) {
                    AsyncImage(
                        model = viewModel.userAccount.avatarUri,
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = colors.primaryBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
