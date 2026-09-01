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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.delay

@Composable
fun FocusLockEmergencyDialog(
    viewModel: FocusViewModel
) {
    if (!viewModel.isFocusLockEmergencyDialogVisible) return

    val colors = AppTheme.colors
    val context = LocalContext.current

    var coolingSeconds by remember { mutableIntStateOf(30) }
    var reasonText by remember { mutableStateOf("") }
    val presetReasons = listOf("জরুরি কল বা মেসেজ পাঠাতে", "গুরুত্বপূর্ণ ব্যাংকিং/অফিসিয়াল কাজ", "অন্যান্য জরুরি প্রয়োজন")
    var selectedPresetIndex by remember { mutableIntStateOf(0) }

    // Cooling off 30s timer
    LaunchedEffect(Unit) {
        while (coolingSeconds > 0) {
            delay(1000)
            coolingSeconds -= 1
        }
    }

    Dialog(
        onDismissRequest = {
            if (coolingSeconds <= 0) viewModel.isFocusLockEmergencyDialogVisible = false
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface)
                .border(1.dp, colors.alert, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                                .background(colors.alert.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Emergency Protocol",
                                tint = colors.alert,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "জরুরি এক্সেস প্রোটোকল",
                                color = colors.textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Focus Lock বাইপাস নিরাপত্তামূলক নিশ্চিতকরণ",
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.isFocusLockEmergencyDialogVisible = false },
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

                // Cooling Off Timer Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.alert.copy(alpha = 0.1f))
                        .border(1.dp, colors.alert.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Cooling timer",
                            tint = colors.alert,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        if (coolingSeconds > 0) {
                            Text(
                                text = "Cooling-Off Period: $coolingSeconds সেকেন্ড অপেক্ষা করুন",
                                color = colors.alert,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Cooling-off সম্পন্ন! এখন আনলক করতে পারবেন",
                                color = colors.secondary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reason Selection
                Text(
                    text = "আনলকের কারণ নির্বাচন করুন (ইভেন্ট লগ তৈরি হবে)",
                    color = colors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                presetReasons.forEachIndexed { index, reason ->
                    val isSelected = selectedPresetIndex == index
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.primary.copy(alpha = 0.15f) else colors.surfaceElevated)
                            .border(1.dp, if (isSelected) colors.primary else colors.border, RoundedCornerShape(10.dp))
                            .clickable { selectedPresetIndex = index }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = reason,
                            color = if (isSelected) colors.primary else colors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                if (selectedPresetIndex == 2) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("কারণ সংক্ষেপে লিখুন") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.alert,
                            unfocusedBorderColor = colors.border
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                val finalReason = if (selectedPresetIndex == 2 && reasonText.isNotBlank()) {
                    reasonText
                } else {
                    presetReasons[selectedPresetIndex]
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.isFocusLockEmergencyDialogVisible = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("বাতিল", color = colors.textSecondary)
                    }

                    Button(
                        onClick = {
                            viewModel.executeEmergencyUnlock(context, finalReason)
                        },
                        enabled = coolingSeconds <= 0,
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("btn_confirm_emergency_unlock"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.alert,
                            disabledContainerColor = colors.surfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Unlock",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (coolingSeconds > 0) "অপেক্ষা করুন ($coolingSeconds)" else "ইমার্জেন্সি আনলক",
                            color = if (coolingSeconds > 0) colors.textMuted else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
