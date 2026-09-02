package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppThemeMode
import com.example.data.FocusLockState
import com.example.data.NavigationTab
import com.example.state.FocusViewModel
import com.example.ui.components.AddRoutineDialog
import com.example.ui.components.BadgesDialog
import com.example.ui.components.BottomNavBar
import com.example.ui.components.EmergencyDialog
import com.example.ui.components.FocusLockActiveScreen
import com.example.ui.components.FocusLockCompletionDialog
import com.example.ui.components.FocusLockEmergencyDialog
import com.example.ui.screens.FocusLockScreen
import com.example.ui.components.FocusLockSetupDialog
import com.example.ui.components.NotificationAlertsDialog
import com.example.ui.components.PinLockBottomSheet
import com.example.ui.components.ProfileDialog
import com.example.ui.components.ScreenTimeLimitDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BlockerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.CalmBlue
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.runtime.CompositionLocalProvider
import com.example.util.LocalAppStrings
import com.example.util.getStrings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: FocusViewModel = viewModel()
            val isSystemDark = isSystemInDarkTheme()
            val isDark = when (viewModel.appThemeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> isSystemDark
            }
            
            val appStrings = getStrings(viewModel.appLanguage)

            MyApplicationTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalAppStrings provides appStrings) {
                    FocusShieldApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun FocusShieldApp(
    viewModel: FocusViewModel = viewModel()
) {
    val colors = AppTheme.colors
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissions(context)
                viewModel.checkAndRestoreFocusLock(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.bindContext(context)
        viewModel.checkAndRestoreFocusLock(context)
    }

    if (viewModel.focusLockState == FocusLockState.ACTIVE || viewModel.focusLockState == FocusLockState.EMERGENCY_REQUEST) {
        FocusLockActiveScreen(viewModel = viewModel)
    } else if (viewModel.isFocusLockSetupDialogVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            FocusLockScreen(
                viewModel = viewModel,
                onBack = { viewModel.isFocusLockSetupDialogVisible = false }
            )

            // Custom in-app floating banner / toast
            AnimatedVisibility(
                visible = viewModel.toastMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceElevated)
                        .border(1.dp, colors.primary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("app_toast_banner")
                ) {
                    Text(
                        text = viewModel.toastMessage ?: "",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    } else if (viewModel.isProfileScreenVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { viewModel.isProfileScreenVisible = false }
            )

            // Custom in-app floating banner / toast
            AnimatedVisibility(
                visible = viewModel.toastMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceElevated)
                        .border(1.dp, colors.primary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("app_toast_banner")
                ) {
                    Text(
                        text = viewModel.toastMessage ?: "",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            containerColor = colors.background,
            contentColor = colors.textPrimary,
            topBar = {
                TopHeaderBar(
                    viewModel = viewModel,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )
            },
            bottomBar = {
                BottomNavBar(
                    selectedTab = viewModel.currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = viewModel.currentTab,
                    transitionSpec = {
                        (fadeIn(androidx.compose.animation.core.tween(240)) + 
                         slideInVertically(
                            animationSpec = androidx.compose.animation.core.spring(
                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
                                stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
                            ),
                            initialOffsetY = { 28 }
                         )) togetherWith fadeOut(androidx.compose.animation.core.tween(180))
                    },
                    label = "screen_transition"
                ) { tab ->
                    when (tab) {
                        NavigationTab.HOME -> HomeScreen(viewModel = viewModel)
                        NavigationTab.BLOCKER -> BlockerScreen(viewModel = viewModel)
                        NavigationTab.SCHEDULE -> ScheduleScreen(viewModel = viewModel)
                        NavigationTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                        NavigationTab.SETTINGS -> SecurityScreen(viewModel = viewModel)
                    }
                }

                // Custom in-app floating banner / toast
                AnimatedVisibility(
                    visible = viewModel.toastMessage != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceElevated)
                            .border(1.dp, colors.primary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("app_toast_banner")
                    ) {
                        Text(
                            text = viewModel.toastMessage ?: "",
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Interactive Modals & Dialogs
    PinLockBottomSheet(viewModel = viewModel)
    BadgesDialog(viewModel = viewModel)
    AddRoutineDialog(viewModel = viewModel)
    EmergencyDialog(viewModel = viewModel)
    ProfileDialog(viewModel = viewModel)
    NotificationAlertsDialog(viewModel = viewModel)
    ScreenTimeLimitDialog(viewModel = viewModel)
    FocusLockEmergencyDialog(viewModel = viewModel)
    FocusLockCompletionDialog(viewModel = viewModel)
}

