package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.NavigationTab
import com.example.state.FocusViewModel
import com.example.ui.theme.AppTheme

@Composable
fun ProfileScreen(
    viewModel: FocusViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val user = viewModel.userAccount

    // Gallery Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setProfileAvatarUri(uri.toString())
        }
    }

    var isEditMode by remember { mutableStateOf(false) }
    var editName by remember(user.name) { mutableStateOf(user.name) }
    var editEmail by remember(user.email) { mutableStateOf(user.email) }
    var editPhone by remember(user.phone) { mutableStateOf(user.phone) }
    var editBio by remember(user.bio) { mutableStateOf(user.bio) }
    var editReligion by remember(user.religion) { mutableStateOf(user.religion) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // --- Top Bar with Back Button ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                        .testTag("btn_profile_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = com.example.util.LocalAppStrings.current.profileTitle,
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = com.example.util.LocalAppStrings.current.profileSubtitle,
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Edit Profile Toggle Button
            OutlinedButton(
                onClick = {
                    if (isEditMode) {
                        viewModel.updateUserProfile(editName, editEmail, editPhone, editBio, editReligion)
                        isEditMode = false
                    } else {
                        isEditMode = true
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isEditMode) colors.secondary else colors.primaryBright
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        if (isEditMode) listOf(colors.secondary, colors.secondary)
                        else listOf(colors.primary, colors.primaryBright)
                    )
                ),
                modifier = Modifier.testTag("btn_toggle_edit_profile")
            ) {
                Icon(
                    imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (isEditMode) com.example.util.LocalAppStrings.current.profileSave else com.example.util.LocalAppStrings.current.profileEdit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Avatar & Upload Card ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(colors.surfaceElevated, colors.surface)
                    )
                )
                .border(1.dp, colors.borderLight, RoundedCornerShape(20.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar with Camera Overlay
                Box(
                    modifier = Modifier.size(108.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(colors.primary.copy(alpha = 0.25f), colors.surface)
                                )
                            )
                            .border(3.dp, colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (user.avatarUri != null) {
                            AsyncImage(
                                model = user.avatarUri,
                                contentDescription = "Uploaded User Photo",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Profile",
                                    tint = colors.primaryBright,
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = user.avatarInitials,
                                    color = colors.textSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Floating Upload Action Button on avatar
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colors.primary)
                            .border(2.dp, colors.background, CircleShape)
                            .clickable { photoPickerLauncher.launch("image/*") }
                            .testTag("btn_upload_photo"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Upload Profile Photo",
                            tint = if (colors.isDark) Color(0xFF0D1117) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = user.name,
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user.email,
                    color = colors.textSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pro Membership & Verification Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(colors.secondary.copy(alpha = 0.15f))
                            .border(1.dp, colors.secondary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = colors.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = com.example.util.LocalAppStrings.current.profileVerifiedProtection,
                                color = colors.secondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(colors.primary.copy(alpha = 0.15f))
                            .border(1.dp, colors.primary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = com.example.util.LocalAppStrings.current.profileProMember,
                            color = colors.primaryBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Photo action buttons (Upload from Gallery / Remove Photo)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = if (colors.isDark) Color(0xFF0D1117) else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("btn_select_gallery_photo")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (user.avatarUri == null) com.example.util.LocalAppStrings.current.profileUploadPhoto else com.example.util.LocalAppStrings.current.profileChangePhoto,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (user.avatarUri != null) {
                        OutlinedButton(
                            onClick = { viewModel.setProfileAvatarUri(null) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colors.alert
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(
                                    listOf(colors.alert.copy(alpha = 0.5f), colors.alert.copy(alpha = 0.3f))
                                )
                            ),
                            modifier = Modifier
                                .height(42.dp)
                                .testTag("btn_remove_photo")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Remove photo",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = com.example.util.LocalAppStrings.current.profileRemovePhoto, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Personal Details & Bio Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = com.example.util.LocalAppStrings.current.profilePersonalDetails,
                        color = colors.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${com.example.util.LocalAppStrings.current.profileMemberSincePrefix}${user.memberSince}",
                        color = colors.textMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isEditMode) {
                    // Editable Text Fields
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(com.example.util.LocalAppStrings.current.profileNameInput) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = colors.textSecondary) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedContainerColor = colors.surfaceElevated,
                            unfocusedContainerColor = colors.surfaceElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text(com.example.util.LocalAppStrings.current.profileEmailInput) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = colors.textSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedContainerColor = colors.surfaceElevated,
                            unfocusedContainerColor = colors.surfaceElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_email")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text(com.example.util.LocalAppStrings.current.profilePhoneInput) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = colors.textSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedContainerColor = colors.surfaceElevated,
                            unfocusedContainerColor = colors.surfaceElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_phone")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text(com.example.util.LocalAppStrings.current.profileBioInput) },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = colors.textSecondary) },
                        singleLine = false,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedContainerColor = colors.surfaceElevated,
                            unfocusedContainerColor = colors.surfaceElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_bio")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Religion edit
                    Text(
                        text = com.example.util.LocalAppStrings.current.profileLabelReligion,
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editReligion,
                        onValueChange = { editReligion = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedContainerColor = colors.surfaceElevated,
                            unfocusedContainerColor = colors.surfaceElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_religion")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.updateUserProfile(editName, editEmail, editPhone, editBio, editReligion)
                            isEditMode = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = if (colors.isDark) Color(0xFF0D1117) else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_save_profile")
                    ) {
                        Text(text = com.example.util.LocalAppStrings.current.profileSaveChanges, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Read-only Information Rows
                    ProfileInfoRow(icon = Icons.Default.Person, label = com.example.util.LocalAppStrings.current.profileLabelName, value = user.name)
                    HorizontalDivider(color = colors.border, modifier = Modifier.padding(vertical = 10.dp))

                    ProfileInfoRow(icon = Icons.Default.Email, label = com.example.util.LocalAppStrings.current.profileLabelEmail, value = user.email)
                    HorizontalDivider(color = colors.border, modifier = Modifier.padding(vertical = 10.dp))

                    ProfileInfoRow(icon = Icons.Default.Phone, label = com.example.util.LocalAppStrings.current.profileLabelPhone, value = user.phone)
                    HorizontalDivider(color = colors.border, modifier = Modifier.padding(vertical = 10.dp))

                    ProfileInfoRow(icon = Icons.Default.Badge, label = com.example.util.LocalAppStrings.current.profileLabelBio, value = user.bio)
                    HorizontalDivider(color = colors.border, modifier = Modifier.padding(vertical = 10.dp))

                    ProfileInfoRow(icon = Icons.Default.AutoAwesome, label = com.example.util.LocalAppStrings.current.profileLabelReligion, value = user.religion)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Security & Privacy: Protected Activity Section (Located above Logout / Auth) ---
        ProfileSecurityAndPrivacyCard(
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Account Login / Registration / Logout Section ---
        ProfileAccountAuthCard(
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Activity & Discipline Summary Card ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = com.example.util.LocalAppStrings.current.profilePerformanceSummary,
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val daysUnit = if (viewModel.streakDays > 1) "days" else "day"
                    val timesUnit = if (viewModel.blockedAttemptsToday > 1) "times" else "time"
                    // Formatting them keeping similar pattern but we need to support both strings
                    val strDays = if (com.example.util.LocalAppStrings.current.profileLabelName == "Name") " ${daysUnit}" else " দিন"
                    val strTimes = if (com.example.util.LocalAppStrings.current.profileLabelName == "Name") " ${timesUnit}" else " বার"
                    
                    PerformanceCard(
                        title = "${viewModel.streakDays}$strDays",
                        subtitle = com.example.util.LocalAppStrings.current.profileCleanStreak,
                        color = colors.primaryBright,
                        modifier = Modifier.weight(1f)
                    )
                    PerformanceCard(
                        title = "${viewModel.blockedAttemptsToday}$strTimes",
                        subtitle = com.example.util.LocalAppStrings.current.profileTodayBlocking,
                        color = colors.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    PerformanceCard(
                        title = viewModel.savedHoursToday,
                        subtitle = com.example.util.LocalAppStrings.current.profileSavedTime,
                        color = colors.warning,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onBack()
                        viewModel.selectTab(NavigationTab.SETTINGS)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.surfaceElevated,
                        contentColor = colors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                        .testTag("btn_go_to_settings")
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = colors.primaryBright, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = com.example.util.LocalAppStrings.current.profileSettingsNav, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileSecurityAndPrivacyCard(
    viewModel: FocusViewModel
) {
    val colors = AppTheme.colors
    val strings = com.example.util.LocalAppStrings.current
    val isUnlocked = viewModel.isProtectedActivityUnlocked
    val protectedList = viewModel.protectedActivities

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Icon + Section Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security",
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = strings.profileSecuritySectionTitle,
                            color = colors.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.profileProtectedActivityTitle,
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                if (isUnlocked) {
                    IconButton(
                        onClick = { viewModel.lockProtectedActivity() },
                        modifier = Modifier.testTag("btn_relock_protected_activity")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = strings.profileProtectedActivityLockAgain,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isUnlocked) {
                // Locked state: Do NOT reveal any activity name, domain, or details
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surfaceElevated)
                        .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(colors.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = colors.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = strings.profileProtectedActivityLocked,
                            color = colors.textMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = strings.profileProtectedActivityDesc,
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.showPinBottomSheet(FocusViewModel.PinAction.VIEW_PROTECTED_ACTIVITY)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_unlock_protected_activity")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.profileProtectedActivityUnlock,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            } else {
                // Unlocked state: Show real sensitive activities
                if (protectedList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceElevated)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.profileProtectedActivityEmpty,
                            color = colors.textMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        protectedList.forEach { activity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surfaceElevated)
                                    .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(colors.alert.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = colors.alert,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = activity.titleBangla,
                                        color = colors.textPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Text(
                                    text = activity.timeAgoBangla,
                                    color = colors.textMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.clearProtectedActivities() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.alert),
                                modifier = Modifier.testTag("btn_clear_protected_activity")
                            ) {
                                Text(text = strings.profileProtectedActivityClear, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { viewModel.lockProtectedActivity() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.surfaceElevated,
                                    contentColor = colors.textPrimary
                                ),
                                modifier = Modifier.testTag("btn_lock_protected_done")
                            ) {
                                Text(text = strings.profileProtectedActivityLockAgain, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    val colors = AppTheme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primaryBright,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = colors.textMuted, fontSize = 11.sp)
            Text(text = value, color = colors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun PerformanceCard(
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = colors.textMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ProfileAccountAuthCard(
    viewModel: FocusViewModel
) {
    val colors = AppTheme.colors
    val user = viewModel.userAccount
    var authMode by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (user.isLoggedIn) {
                // Logged In Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = com.example.util.LocalAppStrings.current.profileAccountStatus,
                            color = colors.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = com.example.util.LocalAppStrings.current.profileCloudSyncActive,
                            color = colors.secondary,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.logoutUser() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.alert
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(
                                listOf(colors.alert.copy(alpha = 0.5f), colors.alert.copy(alpha = 0.3f))
                            )
                        ),
                        modifier = Modifier.testTag("btn_logout_profile")
                    ) {
                        Text(text = com.example.util.LocalAppStrings.current.profileLogout, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                // Logged Out / Form State (Login or Register)
                Text(
                    text = com.example.util.LocalAppStrings.current.profileAuthTitle,
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = authMode,
                    containerColor = colors.surfaceElevated,
                    contentColor = colors.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[authMode]),
                            color = colors.primary
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = authMode == 0,
                        onClick = { authMode = 0 },
                        text = {
                            Text(
                                text = com.example.util.LocalAppStrings.current.profileLoginTab,
                                color = if (authMode == 0) colors.primaryBright else colors.textSecondary,
                                fontWeight = if (authMode == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = authMode == 1,
                        onClick = { authMode = 1 },
                        text = {
                            Text(
                                text = com.example.util.LocalAppStrings.current.profileRegisterTab,
                                color = if (authMode == 1) colors.primaryBright else colors.textSecondary,
                                fontWeight = if (authMode == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (authMode == 1) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("পূর্ণ নাম (Full Name)") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = colors.textSecondary) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedContainerColor = colors.surfaceElevated,
                            unfocusedContainerColor = colors.surfaceElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_reg_name")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("ইমেইল অ্যাড্রেস (Email)") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = colors.textSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.border,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedContainerColor = colors.surfaceElevated,
                        unfocusedContainerColor = colors.surfaceElevated
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_auth_email")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text(com.example.util.LocalAppStrings.current.profilePasswordInput) },
                    leadingIcon = { Icon(Icons.Default.Password, contentDescription = null, tint = colors.textSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.border,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedContainerColor = colors.surfaceElevated,
                        unfocusedContainerColor = colors.surfaceElevated
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_auth_password")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val finalEmail = if (emailInput.isNotBlank()) emailInput else "user@focusguard.app"
                        val finalPass = if (passwordInput.isNotBlank()) passwordInput else "123456"
                        if (authMode == 0) {
                            viewModel.loginUser(finalEmail, finalPass)
                        } else {
                            val finalName = if (nameInput.isNotBlank()) nameInput else "New Focus User"
                            viewModel.registerUser(finalName, finalEmail, finalPass)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = if (colors.isDark) Color(0xFF0D1117) else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_auth_submit")
                ) {
                    Text(
                        text = if (authMode == 0) com.example.util.LocalAppStrings.current.profileLoginSubmit else com.example.util.LocalAppStrings.current.profileRegisterSubmit,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
