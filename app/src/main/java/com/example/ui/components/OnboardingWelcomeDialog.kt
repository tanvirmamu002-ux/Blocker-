package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.rounded.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguri
import com.example.util.LocalAppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingWelcomeDialog(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val strings = LocalAppStrings.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (viewModel.isOnboardingDialogVisible) {
        var inputName by remember { mutableStateOf("") }
        var selectedReligion by remember { mutableStateOf("ইসলাম") }
        var customReligion by remember { mutableStateOf("") }
        var isCustomReligionActive by remember { mutableStateOf(false) }
        var nameError by remember { mutableStateOf<String?>(null) }

        val religionOptions = listOf(
            "☪️ ইসলাম" to "ইসলাম",
            "🕉️ সনাতন" to "সনাতন",
            "☸️ বৌদ্ধ" to "বৌদ্ধ",
            "✝️ খ্রিস্টান" to "খ্রিস্টান",
            "🌟 অন্যান্য" to "অন্যান্য"
        )

        Dialog(
            onDismissRequest = {
                // Keep open on first launch to ensure user gets their custom greeting
                if (inputName.isNotBlank()) {
                    val finalReligion = if (isCustomReligionActive && customReligion.isNotBlank()) customReligion else selectedReligion
                    viewModel.completeOnboarding(inputName, finalReligion)
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surface)
                    .border(1.5.dp, colors.primary.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Logo Badge
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(colors.primary.copy(alpha = 0.15f))
                            .border(1.5.dp, colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Focus Shield",
                            tint = colors.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = strings.onboardingWelcomeTitle,
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = strings.onboardingWelcomeSubtitle,
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        fontFamily = HindSiliguri,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 1. Input: Name
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.onboardingNameLabel,
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = " *",
                                color = Color(0xFFEF4444),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = inputName,
                            onValueChange = {
                                inputName = it
                                if (it.isNotBlank()) nameError = null
                            },
                            placeholder = {
                                Text(
                                    text = strings.onboardingNamePlaceholder,
                                    color = colors.textMuted,
                                    fontSize = 13.sp,
                                    fontFamily = HindSiliguri
                                )
                            },
                            isError = nameError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { keyboardController?.hide() }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border,
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary,
                                focusedContainerColor = colors.surfaceElevated,
                                unfocusedContainerColor = colors.surfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_onboarding_name")
                        )

                        if (nameError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = nameError ?: "",
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                fontFamily = HindSiliguri
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. Input: Religion
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = colors.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.onboardingReligionLabel,
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                fontFamily = HindSiliguri,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            religionOptions.forEach { (label, value) ->
                                val isSelected = selectedReligion == value
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) colors.primary.copy(alpha = 0.2f)
                                            else colors.surfaceElevated
                                        )
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) colors.primary else colors.border,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            selectedReligion = value
                                            isCustomReligionActive = (value == "অন্যান্য")
                                        }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                        .testTag("chip_religion_$value")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) colors.primary else colors.textSecondary,
                                            fontSize = 13.sp,
                                            fontFamily = HindSiliguri,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = colors.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = isCustomReligionActive) {
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                OutlinedTextField(
                                    value = customReligion,
                                    onValueChange = { customReligion = it },
                                    placeholder = {
                                        Text(
                                            text = "আপনার ধর্মীয় বিশ্বাস লিখুন...",
                                            color = colors.textMuted,
                                            fontSize = 12.sp,
                                            fontFamily = HindSiliguri
                                        )
                                    },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.border,
                                        focusedTextColor = colors.textPrimary,
                                        unfocusedTextColor = colors.textPrimary,
                                        focusedContainerColor = colors.surfaceElevated,
                                        unfocusedContainerColor = colors.surfaceElevated
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("input_custom_religion")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3. Dynamic Greeting Live Preview Card
                    val previewGreeting = strings.getDynamicGreeting(
                        if (inputName.isNotBlank()) inputName.trim() else "..."
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.surfaceElevated)
                            .border(1.dp, colors.borderSubtle, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.WavingHand,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "হোম স্ক্রিনে যেভাবে দেখাবে:",
                                    color = colors.textMuted,
                                    fontSize = 11.sp,
                                    fontFamily = HindSiliguri
                                )
                                Text(
                                    text = previewGreeting,
                                    color = colors.primaryBright,
                                    fontSize = 15.sp,
                                    fontFamily = HindSiliguri,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // 4. Submit CTA Button
                    Button(
                        onClick = {
                            if (inputName.trim().isBlank()) {
                                nameError = strings.onboardingNameRequiredError
                            } else {
                                val finalReligion = if (isCustomReligionActive && customReligion.isNotBlank()) {
                                    customReligion.trim()
                                } else {
                                    selectedReligion
                                }
                                viewModel.completeOnboarding(inputName, finalReligion)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = if (colors.isDark) Color(0xFF0D1117) else Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_onboarding_submit")
                    ) {
                        Text(
                            text = strings.onboardingSubmitButton,
                            fontSize = 16.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
