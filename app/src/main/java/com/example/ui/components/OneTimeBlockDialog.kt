package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguri
import com.example.util.AppItem
import com.example.util.AppListHelper

/**
 * Helper to safely convert Android Drawable into Compose ImageBitmap
 */
fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    if (drawable == null) return null
    return try {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            drawable.bitmap
        } else {
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * App Icon Composable: renders real Android app icon with rounded corners and subtle shadow/border
 */
@Composable
fun RealAppIcon(
    drawable: Drawable?,
    modifier: Modifier = Modifier,
    sizeDp: Int = 44
) {
    val colors = AppTheme.colors
    val bitmap = remember(drawable) { drawableToBitmap(drawable) }

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "App Icon",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            Icon(
                imageVector = Icons.Default.Block,
                contentDescription = "App Icon",
                tint = colors.primary,
                modifier = Modifier.size((sizeDp * 0.55).dp)
            )
        }
    }
}

/**
 * 1. App Selection Dialog for One-Time Block
 * Displays real list of installed apps on the user's phone.
 */
@Composable
fun OneTimeBlockAppSelectionDialog(
    viewModel: FocusViewModel
) {
    if (!viewModel.isOneTimeBlockSelectionDialogVisible) return

    val colors = AppTheme.colors
    val context = LocalContext.current

    var installedApps by remember { mutableStateOf<List<AppItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        installedApps = AppListHelper.getInstalledApps(context)
        isLoading = false
    }

    Dialog(
        onDismissRequest = { viewModel.closeOneTimeBlockDialogs() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(26.dp))
                .background(colors.background)
                .border(1.dp, colors.borderSubtle, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                // Header
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
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.alert.copy(alpha = 0.14f))
                                .border(1.dp, colors.alert.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = "One-Time Block",
                                tint = colors.alert,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "One-Time Block",
                                color = colors.textPrimary,
                                fontSize = 18.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "কয়েক ঘণ্টার জন্য কোনো অ্যাপ লক করতে অ্যাপ নির্বাচন করুন",
                                color = colors.textSecondary,
                                fontSize = 12.sp,
                                fontFamily = HindSiliguri,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.closeOneTimeBlockDialogs() },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "অ্যাপ খুঁজুন...",
                            color = colors.textMuted,
                            fontFamily = HindSiliguri,
                            fontSize = 13.5.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = colors.textMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = colors.textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.borderSubtle,
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_apps_onetime")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // App List or Loading State
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = colors.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "ইনস্টল থাকা অ্যাপগুলো লোড করা হচ্ছে...",
                                color = colors.textSecondary,
                                fontSize = 13.sp,
                                fontFamily = HindSiliguri
                            )
                        }
                    }
                } else {
                    val filteredList = installedApps.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                                it.packageName.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "কোনো অ্যাপ পাওয়া যায়নি",
                                color = colors.textMuted,
                                fontSize = 13.5.sp,
                                fontFamily = HindSiliguri
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredList, key = { it.packageName }) { appItem ->
                                AppSelectionRow(
                                    app = appItem,
                                    onClick = {
                                        viewModel.selectAppForOneTimeBlock(appItem)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppSelectionRow(
    app: AppItem,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceElevated.copy(alpha = 0.7f))
            .border(1.dp, colors.borderSubtle, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            RealAppIcon(
                drawable = app.icon,
                sizeDp = 42
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = app.name,
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    color = colors.textMuted,
                    fontSize = 11.sp,
                    fontFamily = HindSiliguri,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colors.alert.copy(alpha = 0.12f))
                .border(1.dp, colors.alert.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "ব্লক করুন",
                color = colors.alert,
                fontSize = 11.5.sp,
                fontFamily = HindSiliguri,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 2. Premium Confirmation Popup for 3-Hour Block
 * Shows selected app's real icon, Bengali confirmation text, and two buttons.
 */
@Composable
fun OneTimeBlockConfirmationDialog(
    viewModel: FocusViewModel
) {
    val app = viewModel.selectedAppForOneTimeBlock ?: return
    if (!viewModel.isOneTimeBlockConfirmationVisible) return

    val colors = AppTheme.colors
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_halo")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Dialog(
        onDismissRequest = { viewModel.closeOneTimeBlockDialogs() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            colors.surfaceElevated,
                            colors.surface,
                            colors.background
                        )
                    )
                )
                .border(1.2.dp, colors.alert.copy(alpha = 0.45f), RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top Glowing Halo with Real App Icon
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(colors.alert.copy(alpha = 0.12f * pulseScale))
                        .border(1.5.dp, colors.alert.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    RealAppIcon(
                        drawable = app.icon,
                        sizeDp = 58
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Name Label
                Text(
                    text = app.name,
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Duration Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.alert.copy(alpha = 0.15f))
                        .border(1.dp, colors.alert.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = null,
                            tint = colors.alert,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "৩ ঘণ্টার তাৎক্ষণিক ব্লক",
                            color = colors.alert,
                            fontSize = 12.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Question Text as strictly requested
                Text(
                    text = "আপনি কি এখনই ৩ ঘণ্টার জন্য এই অ্যাপটি ব্লক করতে চান?",
                    color = colors.textPrimary,
                    fontSize = 15.5.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "ব্লক চলাকালীন এই অ্যাপটি ওপেন করলে Focus Shield প্রটেকশন স্ক্রিন প্রদর্শিত হবে।",
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    fontFamily = HindSiliguri,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons: "হ্যাঁ, ব্লক করুন" and "এখন নয়"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.confirmOneTimeBlock(context)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.alert,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_confirm_one_time_block")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "হ্যাঁ, ব্লক করুন",
                                fontSize = 14.5.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.closeOneTimeBlockDialogs() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.textSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_cancel_one_time_block")
                    ) {
                        Text(
                            text = "এখন নয়",
                            fontSize = 14.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
