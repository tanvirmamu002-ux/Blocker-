package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinLockBottomSheet(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (viewModel.isPinBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.hidePinBottomSheet() },
            sheetState = sheetState,
            containerColor = colors.surface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
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
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.surfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.hidePinBottomSheet() },
                        modifier = Modifier.testTag("btn_close_pin_sheet")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title & Subtitle based on Action
                val title = when (viewModel.currentPinAction) {
                    FocusViewModel.PinAction.CREATE_PIN -> "নতুন ৪-ডিজিট পিন তৈরি করুন"
                    FocusViewModel.PinAction.RESET_PIN -> "পিন রিসেট করুন"
                    FocusViewModel.PinAction.CHANGE_PIN -> "নতুন ৪-ডিজিট পিন দিন"
                    FocusViewModel.PinAction.DISABLE_STRICT_MODE -> "স্ট্রিক্ট মোড নিষ্ক্রিয় করতে পিন দিন"
                    FocusViewModel.PinAction.UNLOCK_STRICT_TIMER -> "টাইমার আনলক করতে পিন দিন"
                    FocusViewModel.PinAction.EMERGENCY_UNLOCK -> "ইমার্জেন্সি আনলক পিন দিন"
                    else -> "সিকিউরিটি পিন ভেরিফিকেশন"
                }

                val subtitle = when (viewModel.currentPinAction) {
                    FocusViewModel.PinAction.CREATE_PIN -> "লক সুরক্ষার জন্য ৪ সংখ্যার পিন নির্ধারণ করুন"
                    FocusViewModel.PinAction.RESET_PIN -> "নতুন ৪-ডিজিট পিন টাইপ করে রিসেট নিশ্চিত করুন"
                    FocusViewModel.PinAction.CHANGE_PIN -> "নতুন পিন সেট করতে ৪টি সংখ্যা টাইপ করুন (ডিফল্ট: 1234)"
                    FocusViewModel.PinAction.DISABLE_STRICT_MODE -> "স্ট্রিক্ট সুরক্ষা বন্ধের জন্য আপনার পিন দিন"
                    else -> "আপনার গোপন ৪-ডিজিট পিন প্রবেশ করান (ডিফল্ট: 1234)"
                }

                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4 PIN Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < viewModel.enteredPin.length
                        val isError = viewModel.pinErrorMessage != null

                        val dotColor = when {
                            isError -> colors.alert
                            isFilled -> colors.primary
                            else -> colors.surfaceElevated
                        }

                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                                .border(
                                    1.5.dp,
                                    if (isError) colors.alert else if (isFilled) colors.primary else colors.border,
                                    CircleShape
                                )
                        )
                    }
                }

                // Error message display
                AnimatedVisibility(visible = viewModel.pinErrorMessage != null) {
                    Text(
                        text = viewModel.pinErrorMessage ?: "",
                        color = colors.alert,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Numeric Keypad Grid (1-9, Clear/Empty, 0, Backspace)
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "DEL")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    keypadRows.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            row.forEach { key ->
                                KeypadButton(
                                    key = key,
                                    onClick = {
                                        when (key) {
                                            "DEL" -> viewModel.onPinBackspace()
                                            "C" -> {
                                                while (viewModel.enteredPin.isNotEmpty()) {
                                                    viewModel.onPinBackspace()
                                                }
                                            }
                                            else -> viewModel.onPinDigitEntered(key)
                                        }
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
private fun KeypadButton(
    key: String,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                if (key == "DEL" || key == "C") colors.surfaceElevated else colors.surface
            )
            .border(1.dp, colors.border, CircleShape)
            .clickable(onClick = onClick)
            .testTag("pin_key_$key"),
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            "DEL" -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            "C" -> {
                Text(
                    text = "C",
                    color = colors.textMuted,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            else -> {
                Text(
                    text = key,
                    color = colors.textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
