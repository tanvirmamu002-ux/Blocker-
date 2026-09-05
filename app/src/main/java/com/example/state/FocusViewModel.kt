package com.example.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.data.AccountabilityPartner
import com.example.data.AddictionTrigger
import com.example.data.AppScreenTimeLimit
import com.example.data.AppThemeMode
import com.example.data.BlockedDomain
import com.example.data.CategoryFilter
import com.example.data.DailyDisciplineStat
import com.example.data.FocusBadge
import com.example.data.FocusLockConfig
import com.example.data.FocusLockState
import com.example.data.FocusRoutine
import com.example.data.NavigationTab
import com.example.data.RecentActivity
import com.example.data.SecurityPermission
import com.example.data.UserAccount
import com.example.data.AppNotificationItem
import com.example.util.FocusNotificationHelper
import com.example.ui.theme.SoftCoral
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.CalmBlue
import com.example.ui.theme.LavenderFocus
import com.example.util.AppUsageTracker
import com.example.util.FocusLockPreferences
import com.example.util.FocusPermissionHelper
import com.example.util.AppLanguage
import com.example.util.AppLanguageManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FocusViewModel(application: android.app.Application) : androidx.lifecycle.AndroidViewModel(application) {
    private val context: Context
        get() = getApplication<android.app.Application>().applicationContext

    var installedApps = androidx.compose.runtime.mutableStateListOf<com.example.util.AppItem>()
    var isAppsLoaded by androidx.compose.runtime.mutableStateOf(false)

    fun loadInstalledApps() {
        if (isAppsLoaded) return
        viewModelScope.launch {
            val apps = com.example.util.AppListHelper.getInstalledApps(context)
            installedApps.clear()
            installedApps.addAll(apps)
            isAppsLoaded = true
        }
    }
    // --- Language State ---
    var appLanguage by mutableStateOf(AppLanguage.BENGALI)
        private set

    fun updateLanguage(context: Context, language: AppLanguage) {
        appLanguage = language
        AppLanguageManager.saveLanguage(context, language)
        // Refresh things that depend on language (like dates)
        refreshRealUsageAndAnalytics(context)
    }

    // --- Navigation ---
    var currentTab by mutableStateOf(NavigationTab.HOME)
        private set

    fun selectTab(tab: NavigationTab) {
        currentTab = tab
    }

    // --- Protection Master Toggle ---
    var isProtectionActive by mutableStateOf(true)
        private set

    fun toggleProtection() {
        isProtectionActive = !isProtectionActive
        if (isProtectionActive) {
            recordNewActivity(
                RecentActivity(
                    id = System.currentTimeMillis().toString(),
                    titleBangla = "স্মার্ট প্রোটেকশন চালু করা হয়েছে",
                    titleEnglish = "Smart Protection Activated",
                    timeAgoBangla = "এইমাত্র",
                    timeAgoEnglish = "Just now",
                    isSuccess = true,
                    iconType = "shield",
                    isSensitive = false
                )
            )
        }
    }

    // --- Streak & Home Metrics ---
    var streakDays by mutableIntStateOf(15)
        private set
    var blockedAttemptsToday by mutableIntStateOf(17)
        private set
    var savedHoursToday by mutableStateOf("২ঘ. ৩৪মি.")
        private set
    var focusSessionsToday by mutableIntStateOf(4)
        private set

    // --- Quick Actions & Limits ---
    var isQuickFocusLockActive by mutableStateOf(false)
        private set
    var isQuickBlockNowActive by mutableStateOf(false)
        private set
    var isScreenTimeLimitActive by mutableStateOf(true)
        private set
    var dailyScreenTimeLimitMinutes by mutableIntStateOf(120) // 2 hours
        private set
    var dailyScreenTimeUsedMinutes by mutableIntStateOf(45) // 45 minutes used
        private set
    var isStrictScreenTimeActive by mutableStateOf(true)
        private set

    // --- One-Time Block State (Persistent 3-hour app lock) ---
    var isOneTimeBlockSelectionDialogVisible by mutableStateOf(false)
    var isOneTimeBlockConfirmationVisible by mutableStateOf(false)
    var selectedAppForOneTimeBlock by mutableStateOf<com.example.util.AppItem?>(null)
    var oneTimeBlockedAppName by mutableStateOf<String?>(null)
        private set
    var oneTimeBlockedPackageName by mutableStateOf<String?>(null)
        private set
    var remainingOneTimeBlockSeconds by mutableIntStateOf(0)
        private set
    private var oneTimeBlockTickerJob: Job? = null

    fun checkAndRestoreOneTimeBlock(context: Context) {
        val prefs = FocusLockPreferences.getInstance(context)
        if (prefs.isOneTimeBlockActive()) {
            val pkg = prefs.getOneTimeBlockPackage()
            val name = prefs.getOneTimeBlockAppName() ?: pkg
            val remainingMs = prefs.getOneTimeBlockRemainingMs()
            if (pkg != null && remainingMs > 0L) {
                oneTimeBlockedPackageName = pkg
                oneTimeBlockedAppName = name
                isQuickBlockNowActive = true
                remainingOneTimeBlockSeconds = (remainingMs / 1000).toInt()
                startOneTimeBlockTicker(context)
            } else {
                clearOneTimeBlockState(context)
            }
        } else {
            clearOneTimeBlockState(context)
        }
    }

    private fun startOneTimeBlockTicker(context: Context) {
        oneTimeBlockTickerJob?.cancel()
        oneTimeBlockTickerJob = viewModelScope.launch {
            val prefs = FocusLockPreferences.getInstance(context)
            while (isActive) {
                val remainingMs = prefs.getOneTimeBlockRemainingMs()
                if (remainingMs <= 0L) {
                    val finishedApp = oneTimeBlockedAppName ?: "অ্যাপ"
                    clearOneTimeBlockState(context)
                    FocusNotificationHelper.cancelBlockNotification(context)
                    recordNewActivity(
                        RecentActivity(
                            id = System.currentTimeMillis().toString(),
                            titleBangla = "$finishedApp-এর ৩ ঘণ্টার ব্লক সফলভাবে সম্পন্ন হয়েছে",
                            titleEnglish = "3h Block ended for $finishedApp",
                            timeAgoBangla = "এইমাত্র",
                            timeAgoEnglish = "Just now",
                            isSuccess = true,
                            iconType = "shield",
                            isSensitive = false
                        ),
                        context
                    )
                    showToast("$finishedApp-এর ৩ ঘণ্টার ব্লক শেষ হয়েছে")
                    break
                }
                remainingOneTimeBlockSeconds = (remainingMs / 1000).toInt()
                delay(1000)
            }
        }
    }

    fun triggerOneTimeBlockQuickAction(context: Context) {
        val prefs = FocusLockPreferences.getInstance(context)
        if (prefs.isOneTimeBlockActive()) {
            val appName = oneTimeBlockedAppName ?: "অ্যাপ"
            val minsLeft = (prefs.getOneTimeBlockRemainingMs() / (60 * 1000)).toInt()
            val hours = minsLeft / 60
            val mins = minsLeft % 60
            val timeText = if (hours > 0) "$hours ঘণ্টা $mins মিনিট" else "$mins মিনিট"
            showToast("\"$appName\" বর্তমানে $timeText-এর জন্য ব্লক রয়েছে 🔒")
            return
        }

        if (!FocusPermissionHelper.isAccessibilityPermissionGranted(context)) {
            showToast("One-Time Block সক্রিয় করতে অ্যাক্সেসিবিলিটি পারমিশন প্রয়োজন")
            scrollToPermissionsRequested = true
            selectTab(NavigationTab.SETTINGS)
            return
        }

        isOneTimeBlockSelectionDialogVisible = true
    }

    fun selectAppForOneTimeBlock(app: com.example.util.AppItem) {
        selectedAppForOneTimeBlock = app
        isOneTimeBlockSelectionDialogVisible = false
        isOneTimeBlockConfirmationVisible = true
    }

    fun confirmOneTimeBlock(context: Context) {
        val app = selectedAppForOneTimeBlock ?: return
        val prefs = FocusLockPreferences.getInstance(context)
        prefs.saveOneTimeBlock(packageName = app.packageName, appName = app.name, durationHours = 3)

        oneTimeBlockedPackageName = app.packageName
        oneTimeBlockedAppName = app.name
        isQuickBlockNowActive = true
        remainingOneTimeBlockSeconds = 3 * 3600
        isOneTimeBlockConfirmationVisible = false
        selectedAppForOneTimeBlock = null

        startOneTimeBlockTicker(context)

        // Send initial live countdown notification
        FocusNotificationHelper.sendOneTimeBlockNotification(
            context = context,
            appName = app.name,
            endTimeMs = prefs.getOneTimeBlockEndTimeMs()
        )

        blockedAttemptsToday += 1
        recordNewActivity(
            RecentActivity(
                id = System.currentTimeMillis().toString(),
                titleBangla = "${app.name} ৩ ঘণ্টার জন্য তাৎক্ষণিক ব্লক করা হলো",
                titleEnglish = "${app.name} blocked for 3 hours",
                timeAgoBangla = "এইমাত্র",
                timeAgoEnglish = "Just now",
                isSuccess = true,
                iconType = "blocked",
                isSensitive = false
            ),
            context
        )

        showToast("${app.name} সফলভাবে ৩ ঘণ্টার জন্য ব্লক করা হয়েছে 🔒")
    }

    fun cancelActiveOneTimeBlock(context: Context) {
        val appName = oneTimeBlockedAppName ?: "অ্যাপ"
        clearOneTimeBlockState(context)
        showToast("$appName-এর ব্লক বাতিল করা হলো")
    }

    private fun clearOneTimeBlockState(context: Context) {
        oneTimeBlockTickerJob?.cancel()
        val prefs = FocusLockPreferences.getInstance(context)
        prefs.clearOneTimeBlock()
        oneTimeBlockedPackageName = null
        oneTimeBlockedAppName = null
        isQuickBlockNowActive = false
        remainingOneTimeBlockSeconds = 0
        FocusNotificationHelper.cancelBlockNotification(context)
    }

    fun closeOneTimeBlockDialogs() {
        isOneTimeBlockSelectionDialogVisible = false
        isOneTimeBlockConfirmationVisible = false
        selectedAppForOneTimeBlock = null
    }

    // --- Real Focus Lock Functionality & Persistent State Machine ---
    var focusLockState by mutableStateOf(FocusLockState.IDLE)
        private set
    var focusLockConfig by mutableStateOf(FocusLockConfig())
        private set
    var remainingFocusLockSeconds by mutableIntStateOf(0)
        private set
    var totalFocusLockSeconds by mutableIntStateOf(0)
        private set

    private var focusLockTickerJob: Job? = null

    fun checkAndRestoreFocusLock(context: Context) {
        val prefs = FocusLockPreferences.getInstance(context)
        val savedState = prefs.getFocusLockState()
        val config = prefs.getFocusLockConfig()
        focusLockConfig = config

        val remainingMs = prefs.getRemainingTimeMs()

        if ((savedState == FocusLockState.ACTIVE || savedState == FocusLockState.EMERGENCY_REQUEST) && remainingMs > 0) {
            focusLockState = FocusLockState.ACTIVE
            totalFocusLockSeconds = (config.durationMinutes * 60).coerceAtLeast(1)
            remainingFocusLockSeconds = (remainingMs / 1000).toInt()
            startFocusLockTicker(context)
        } else if (savedState == FocusLockState.COMPLETED) {
            focusLockState = FocusLockState.COMPLETED
            isFocusLockCompletionDialogVisible = true
        } else {
            focusLockState = FocusLockState.IDLE
        }
    }

    fun triggerFocusLockQuickAction(context: Context) {
        if (focusLockState == FocusLockState.ACTIVE || focusLockState == FocusLockState.EMERGENCY_REQUEST) {
            showToast("Focus Lock বর্তমানে সক্রিয় রয়েছে")
            return
        }

        if (!FocusPermissionHelper.isFocusLockPermissionGranted(context)) {
            showToast("ফোকাস লক সক্রিয় করতে 'অ্যাক্সেসিবিলিটি' পারমিশনটি চালু করুন।")
            scrollToPermissionsRequested = true
            selectTab(NavigationTab.SETTINGS)
        } else {
            isFocusLockSetupDialogVisible = true
        }
    }

    fun checkPermissionsAndUpdate(context: Context) {
        val granted = FocusPermissionHelper.areAllRequiredPermissionsGranted(context)
        showToast(if (granted) "সবগুলো পারমিশন প্রস্তুত রয়েছে!" else "কিছু পারমিশন বাকি রয়েছে")
    }

    fun startFocusLockSession(
        context: Context,
        durationMinutes: Int,
        blockApps: Boolean,
        blockShorts: Boolean,
        blockWebsites: Boolean,
        isStrict: Boolean
    ) {
        val prefs = FocusLockPreferences.getInstance(context)
        val startTime = System.currentTimeMillis()
        val durationMs = durationMinutes * 60 * 1000L
        val endTime = startTime + durationMs

        val config = FocusLockConfig(
            durationMinutes = durationMinutes,
            blockApps = blockApps,
            blockShorts = blockShorts,
            blockWebsites = blockWebsites,
            isStrict = isStrict,
            startTimeMs = startTime,
            endTimeMs = endTime
        )

        prefs.saveFocusLockConfig(config)
        prefs.saveFocusLockState(FocusLockState.ACTIVE)

        focusLockConfig = config
        focusLockState = FocusLockState.ACTIVE
        totalFocusLockSeconds = durationMinutes * 60
        remainingFocusLockSeconds = durationMinutes * 60

        isFocusLockSetupDialogVisible = false
        startFocusLockTicker(context)

        blockedAttemptsToday += 1
        recordNewActivity(
            RecentActivity(
                id = System.currentTimeMillis().toString(),
                titleBangla = "Focus Lock সক্রিয় ($durationMinutes মিনিট)",
                titleEnglish = "Focus Lock Active ($durationMinutes mins)",
                timeAgoBangla = "এইমাত্র",
                timeAgoEnglish = "Just now",
                isSuccess = true,
                iconType = "shield",
                isSensitive = false
            ),
            context
        )

        showToast("Focus Lock $durationMinutes মিনিটের জন্য সক্রিয় করা হলো 🔒")

        if (notifTimerUpdatesEnabled) {
            FocusNotificationHelper.sendTimerNotification(
                context = context,
                title = "ফোকাস লক সক্রিয় 🔒",
                message = "$durationMinutes মিনিটের ফোকাস সেশন শুরু হয়েছে। একাগ্রতা বজায় রাখুন।"
            )
        }
    }

    private fun startFocusLockTicker(context: Context) {
        focusLockTickerJob?.cancel()
        focusLockTickerJob = viewModelScope.launch {
            val prefs = FocusLockPreferences.getInstance(context)
            while (isActive) {
                val remainingMs = prefs.getRemainingTimeMs()
                if (remainingMs <= 0L) {
                    // Session Completed
                    focusLockState = FocusLockState.COMPLETED
                    prefs.saveFocusLockState(FocusLockState.COMPLETED)
                    prefs.recordCompletedSession(focusLockConfig.durationMinutes)
                    focusSessionsToday += 1
                    isFocusLockCompletionDialogVisible = true

                    recordNewActivity(
                        RecentActivity(
                            id = System.currentTimeMillis().toString(),
                            titleBangla = "${focusLockConfig.durationMinutes} মিনিটের ফোকাস লক সফল!",
                            titleEnglish = "${focusLockConfig.durationMinutes}m Focus Lock Completed!",
                            timeAgoBangla = "এইমাত্র",
                            timeAgoEnglish = "Just now",
                            isSuccess = true,
                            iconType = "session",
                            isSensitive = false
                        ),
                        context
                    )

                    if (notifTimerUpdatesEnabled) {
                        FocusNotificationHelper.sendTimerNotification(
                            context = context,
                            title = "ফোকাস সেশন সফলভাবে সম্পন্ন! 🎉",
                            message = "${focusLockConfig.durationMinutes} মিনিটের ফোকাস সেশন সফলভাবে শেষ হয়েছে। অভিনন্দন!"
                        )
                    }
                    addNotificationAlert(
                        title = "${focusLockConfig.durationMinutes} মিনিটের ফোকাস সেশন সম্পন্ন",
                        subtitle = "ফোকাস লক সফল • সময় বাঁচানো হয়েছে",
                        type = "timer",
                        postSystemNotification = false,
                        context = context
                    )
                    break
                }
                remainingFocusLockSeconds = (remainingMs / 1000).toInt()
                delay(1000)
            }
        }
    }

    fun executeEmergencyUnlock(context: Context, reason: String) {
        val prefs = FocusLockPreferences.getInstance(context)
        prefs.saveFocusLockState(FocusLockState.CANCELLED)
        focusLockState = FocusLockState.CANCELLED
        focusLockTickerJob?.cancel()
        isFocusLockEmergencyDialogVisible = false

        recordNewActivity(
            RecentActivity(
                id = System.currentTimeMillis().toString(),
                titleBangla = "ইমার্জেন্সি আনলক: $reason",
                titleEnglish = "Emergency Unlock: $reason",
                timeAgoBangla = "এইমাত্র",
                timeAgoEnglish = "Just now",
                isSuccess = false,
                iconType = "blocked",
                isSensitive = false
            ),
            context
        )

        if (notifSecurityAlertsEnabled) {
            FocusNotificationHelper.sendSecurityNotification(
                context = context,
                title = "জরুরি আনলক সক্রিয় ⚠️",
                message = "ফোকাস সেশন নির্ধারিত সময়ের আগেই আনলক করা হয়েছে। কারণ: $reason"
            )
        }
        addNotificationAlert(
            title = "জরুরি আনলক সক্রিয় করা হয়েছে",
            subtitle = "কারণ: $reason",
            type = "security",
            postSystemNotification = false,
            context = context
        )

        showToast("ইমার্জেন্সি আনলক প্রোটোকল কার্যকর করা হয়েছে")
    }

    fun dismissCompletionDialog() {
        isFocusLockCompletionDialogVisible = false
        focusLockState = FocusLockState.IDLE
        val prefs = FocusLockPreferences.getInstance(contextRef ?: return)
        prefs.saveFocusLockState(FocusLockState.IDLE)
    }

    private var contextRef: Context? = null
    fun bindContext(context: Context) {
        contextRef = context
        val prefs = FocusLockPreferences.getInstance(context)
        appThemeMode = prefs.getAppThemeMode()
        isPinConfigured = prefs.isPinConfigured()
        currentPin = prefs.getSecurityPin()
        appLanguage = AppLanguageManager.getLanguage(context)
        notifBlockingAlertsEnabled = prefs.getNotifBlocking()
        notifTimerUpdatesEnabled = prefs.getNotifTimer()
        notifSecurityAlertsEnabled = prefs.getNotifSecurity()
        notifRemindersEnabled = prefs.getNotifReminders()
        FocusNotificationHelper.initNotificationChannels(context)
        initDefaultNotificationsIfNeeded()

        // Load persisted user profile if available
        val savedName = prefs.getUserName()
        val savedReligion = prefs.getUserReligion()
        if (!savedName.isNullOrBlank()) {
            val initials = savedName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
            userAccount = userAccount.copy(
                name = savedName,
                religion = savedReligion ?: "ইসলাম",
                avatarInitials = if (initials.isNotBlank()) initials else "FS"
            )
        }

        // Show welcome onboarding dialog immediately on first app open
        if (!prefs.isOnboardingCompleted()) {
            isOnboardingDialogVisible = true
        }

        checkAndRestoreFocusLock(context)
        checkAndRestoreOneTimeBlock(context)
        loadPerAppLimits(context)
        refreshRealUsageAndAnalytics(context)
    }

    // --- Per-App Screen Time Limits State (24-Hour Cycle Management) ---
    val appScreenTimeLimits = mutableStateListOf<AppScreenTimeLimit>()
    var selectedAppForLimitSlider by mutableStateOf<AppScreenTimeLimit?>(null)
    var isAppSliderDialogVisible by mutableStateOf(false)

    fun loadPerAppLimits(context: Context) {
        viewModelScope.launch {
            val list = AppUsageTracker.loadMergedAppsList(context)
            appScreenTimeLimits.clear()
            appScreenTimeLimits.addAll(list)
        }
    }

    fun openAppSliderDialog(app: AppScreenTimeLimit) {
        selectedAppForLimitSlider = app
        isAppSliderDialogVisible = true
    }

    fun closeAppSliderDialog() {
        isAppSliderDialogVisible = false
        selectedAppForLimitSlider = null
    }

    fun saveAppLimit(context: Context, packageName: String, limitMinutes: Int, isStrict: Boolean) {
        AppUsageTracker.saveAppLimit(
            context = context,
            packageName = packageName,
            limitMinutes = limitMinutes,
            isEnabled = true,
            isStrict = isStrict
        )

        val index = appScreenTimeLimits.indexOfFirst { it.packageName == packageName }
        if (index != -1) {
            val item = appScreenTimeLimits[index]
            appScreenTimeLimits[index] = item.copy(
                limitMinutes = limitMinutes,
                isEnabled = true,
                isStrict = isStrict
            )
            showToast("\"${item.appNameBangla}\" অ্যাপের জন্য ${if (limitMinutes >= 60) "${limitMinutes / 60} ঘণ্টা" else "$limitMinutes মিনিট"} লিমিট নির্ধারণ করা হলো")
        }
        closeAppSliderDialog()
    }

    fun removeAppLimit(context: Context, packageName: String) {
        AppUsageTracker.removeAppLimit(context, packageName)
        val index = appScreenTimeLimits.indexOfFirst { it.packageName == packageName }
        if (index != -1) {
            val item = appScreenTimeLimits[index]
            appScreenTimeLimits[index] = item.copy(
                limitMinutes = 0,
                isEnabled = false,
                isStrict = false
            )
            showToast("\"${item.appNameBangla}\" অ্যাপের লিমিট মুছে ফেলা হয়েছে")
        }
        closeAppSliderDialog()
    }

    fun toggleAppLimitEnabled(context: Context, packageName: String) {
        val index = appScreenTimeLimits.indexOfFirst { it.packageName == packageName }
        if (index != -1) {
            val item = appScreenTimeLimits[index]
            val newEnabled = !item.isEnabled
            AppUsageTracker.saveAppLimit(
                context = context,
                packageName = packageName,
                limitMinutes = if (item.limitMinutes > 0) item.limitMinutes else 30,
                isEnabled = newEnabled,
                isStrict = item.isStrict
            )
            appScreenTimeLimits[index] = item.copy(
                limitMinutes = if (item.limitMinutes > 0) item.limitMinutes else 30,
                isEnabled = newEnabled
            )
            showToast(if (newEnabled) "\"${item.appNameBangla}\" লিমিট সক্রিয় করা হলো" else "\"${item.appNameBangla}\" লিমিট বন্ধ করা হলো")
        }
    }

    fun toggleQuickFocusLock() {
        contextRef?.let { triggerFocusLockQuickAction(it) }
    }

    fun toggleQuickBlockNow() {
        isQuickBlockNowActive = !isQuickBlockNowActive
    }

    fun toggleScreenTimeLimit() {
        isScreenTimeLimitActive = !isScreenTimeLimitActive
        showToast(
            if (isScreenTimeLimitActive)
                "স্ক্রিন টাইম লিমিট (${dailyScreenTimeLimitMinutes / 60}ঘ.) সক্রিয় করা হয়েছে"
            else
                "স্ক্রিন টাইম লিমিট বন্ধ করা হয়েছে"
        )
    }

    fun setDailyScreenTimeLimit(minutes: Int) {
        dailyScreenTimeLimitMinutes = minutes
        isScreenTimeLimitActive = true
        showToast("দৈনিক লিমিট ${if (minutes >= 60) "${minutes / 60} ঘণ্টা" else "$minutes মিনিট"} নির্ধারণ করা হলো")
    }

    fun toggleStrictScreenTime(enabled: Boolean) {
        isStrictScreenTimeActive = enabled
    }

    // --- Recent Activities ---
    val activities = mutableStateListOf<RecentActivity>()
    val protectedActivities = mutableStateListOf<RecentActivity>()
    var isProtectedActivityUnlocked by mutableStateOf(false)
        private set

    fun lockProtectedActivity() {
        isProtectedActivityUnlocked = false
    }

    fun clearProtectedActivities(context: Context? = null) {
        protectedActivities.clear()
        val targetContext = context ?: this.context
        FocusLockPreferences.getInstance(targetContext).saveProtectedActivities(emptyList())
    }

    fun clearActivities(context: Context? = null) {
        activities.clear()
        val targetContext = context ?: this.context
        targetContext?.let {
            FocusLockPreferences.getInstance(it).saveActivities(emptyList())
        }
    }

    fun recordNewActivity(activity: RecentActivity, context: Context? = null) {
        val targetContext = context ?: this.context
        if (activity.isSensitive) {
            protectedActivities.add(0, activity)
            targetContext?.let {
                FocusLockPreferences.getInstance(it).saveProtectedActivities(protectedActivities.toList())
            }
        } else {
            activities.add(0, activity)
            targetContext?.let {
                FocusLockPreferences.getInstance(it).saveActivities(activities.toList())
            }
        }
    }

    private var isActivitiesInitialized = false

    fun checkAndRestoreActivities(context: Context) {
        if (isActivitiesInitialized) return
        isActivitiesInitialized = true

        val prefs = FocusLockPreferences.getInstance(context)
        val savedActivities = prefs.getActivities()
        activities.clear()
        if (savedActivities.isNotEmpty()) {
            activities.addAll(savedActivities)
        }

        val savedProtected = prefs.getProtectedActivities()
        protectedActivities.clear()
        if (savedProtected.isNotEmpty()) {
            protectedActivities.addAll(savedProtected)
        }
    }

    // --- Focus Timer (Pomodoro / Deep Work) ---
    var selectedTimerMode by mutableStateOf("Deep Work")
        private set
    var timerPresetMinutes by mutableIntStateOf(25)
        private set
    var remainingSeconds by mutableIntStateOf(25 * 60)
        private set
    var totalTimerSeconds by mutableIntStateOf(25 * 60)
        private set
    var isTimerRunning by mutableStateOf(false)
        private set
    var isStrictTimerActive by mutableStateOf(false)
        private set

    private var timerJob: Job? = null

    fun selectTimerMode(mode: String) {
        selectedTimerMode = mode
        if (mode == "Strict Lock") {
            isStrictTimerActive = true
        }
    }

    fun selectPresetMinutes(minutes: Int) {
        if (isTimerRunning) return
        timerPresetMinutes = minutes
        totalTimerSeconds = minutes * 60
        remainingSeconds = minutes * 60
    }

    fun toggleTimer() {
        if (isTimerRunning) {
            if (isStrictTimerActive && isStrictGlobalActive) {
                // strict mode prevents pause without PIN
                showPinBottomSheet(PinAction.UNLOCK_STRICT_TIMER)
                return
            }
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        isTimerRunning = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && remainingSeconds > 0) {
                delay(1000)
                remainingSeconds -= 1
            }
            if (remainingSeconds <= 0) {
                isTimerRunning = false
                focusSessionsToday += 1
                recordNewActivity(
                    RecentActivity(
                        id = System.currentTimeMillis().toString(),
                        titleBangla = "$timerPresetMinutes মিনিটের $selectedTimerMode সফল!",
                        titleEnglish = "$timerPresetMinutes min $selectedTimerMode Completed!",
                        timeAgoBangla = "এইমাত্র",
                        timeAgoEnglish = "Just now",
                        isSuccess = true,
                        iconType = "session",
                        isSensitive = false
                    )
                )
            }
        }
    }

    fun pauseTimer() {
        isTimerRunning = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        if (isStrictTimerActive && isStrictGlobalActive && isTimerRunning) {
            showPinBottomSheet(PinAction.UNLOCK_STRICT_TIMER)
            return
        }
        pauseTimer()
        remainingSeconds = timerPresetMinutes * 60
    }

    fun toggleStrictTimer(enabled: Boolean) {
        isStrictTimerActive = enabled
    }

    // --- Content Blocker Filters ---
    val categoryFilters = mutableStateListOf(
        CategoryFilter(
            id = "adult",
            titleBangla = "Adult/NSFW Content Blocker",
            titleEnglish = "Adult/NSFW Content Blocker",
            descBangla = "পর্নোগ্রাফিক ও ক্ষতিকর ১৮+ অ্যাডাল্ট সাইট স্বয়ংক্রিয়ভাবে ব্লক করে",
            descEnglish = "Automatically blocks adult, pornographic, and 18+ content",
            isEnabled = true,
            iconType = "adult"
        ),
        CategoryFilter(
            id = "social_media",
            titleBangla = "Social Media Blocker (সোশ্যাল মিডিয়া)",
            titleEnglish = "Social Media Blocker",
            descBangla = "Facebook, Instagram, Twitter, TikTok ইত্যাদি সোশাল মিডিয়া প্ল্যাটফর্ম ব্লক",
            descEnglish = "Blocks Facebook, Instagram, Twitter, TikTok and other social platforms",
            isEnabled = false,
            iconType = "social"
        ),
        CategoryFilter(
            id = "shorts_blocker",
            titleBangla = "Short Video Blocker (শর্টস ও রিলস)",
            titleEnglish = "Short Video Blocker",
            descBangla = "YouTube Shorts, Instagram Reels, FB Reels ও আসক্তিকর ছোট ভিডিও বন্ধ",
            descEnglish = "Blocks YouTube Shorts, Reels, TikTok and addictive short-form video feeds",
            isEnabled = false,
            iconType = "shorts"
        ),
        CategoryFilter(
            id = "phishing",
            titleBangla = "Malicious & Phishing Filter",
            titleEnglish = "Malicious & Phishing Filter",
            descBangla = "ম্যালওয়্যার, ফিশিং এবং সন্দেহজনক ক্ষতিকর লিংক ব্লক",
            descEnglish = "Shields against malware, phishing, and scam domains",
            isEnabled = true,
            iconType = "security"
        ),
        CategoryFilter(
            id = "gambling",
            titleBangla = "Gambling & Betting Blocker",
            titleEnglish = "Gambling & Betting Blocker",
            descBangla = "অনলাইন বেটিং, জুয়া ও ক্যাসিনো সাইট সম্পূর্ণ নিষিদ্ধ",
            descEnglish = "Blocks gambling, betting, and casino websites",
            isEnabled = true,
            iconType = "gambling"
        ),
        CategoryFilter(
            id = "gaming",
            titleBangla = "Gaming & Distractions Filter",
            titleEnglish = "Gaming & Distractions Filter",
            descBangla = "অনলাইন গেমস ও বিনোদন পোর্টাল সাময়িক বন্ধ",
            descEnglish = "Pauses web games and addictive entertainment portals",
            isEnabled = false,
            iconType = "gaming"
        ),
        CategoryFilter(
            id = "app_protection",
            titleBangla = "অ্যাপ্লিকেশন সুরক্ষা",
            titleEnglish = "App Protection",
            descBangla = "স্মার্ট ফোকাস ইঞ্জিন দিয়ে ক্ষতিকর অ্যাপ ব্লক করুন",
            descEnglish = "Block harmful apps using smart focus engine",
            isEnabled = true,
            iconType = "app"
        ),
        CategoryFilter(
            id = "dns_protection",
            titleBangla = "অ্যাডভান্সড DNS ফিল্টারিং",
            titleEnglish = "DNS Protection",
            descBangla = "নেটওয়ার্ক লেভেলে প্রাপ্তবয়স্ক ও ক্ষতিকর ওয়েবসাইট ব্লক করুন",
            descEnglish = "Block adult and malicious websites at the network level",
            isEnabled = false,
            iconType = "dns"
        ),
        CategoryFilter(
            id = "telegram_block",
            titleBangla = "টেলিগ্রাম সার্চ ও গ্রুপ রেস্ট্রিকশন",
            titleEnglish = "Telegram Restriction",
            descBangla = "টেলিগ্রামে ক্ষতিকর চ্যানেল ও সার্চ রেজাল্ট ফিল্টার করুন",
            descEnglish = "Filter harmful channels and search results on Telegram",
            isEnabled = false,
            iconType = "telegram"
        ),
        CategoryFilter(
            id = "power_lock",
            titleBangla = "অ্যান্টি-বাইপাস পাওয়ার লক",
            titleEnglish = "Anti-Bypass Power Lock",
            descBangla = "ডিভাইস রিস্টার্ট বা ফোর্স স্টপ প্রতিরোধ করে সুরক্ষা নিশ্চিত করুন",
            descEnglish = "Ensure protection by preventing device restart or force stop",
            isEnabled = true,
            iconType = "power"
        )
    )

    fun toggleCategoryFilter(id: String) {
        val index = categoryFilters.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = categoryFilters[index]
            val newState = !item.isEnabled
            categoryFilters[index] = item.copy(isEnabled = newState)

            val prefs = FocusLockPreferences.getInstance(context)
            if (id == "adult") {
                prefs.saveAdultContentBlockerEnabled(newState)
                if (newState) {
                    showToast("🛡️ অ্যাডাল্ট কনটেন্ট ও কিওয়ার্ড ব্লকার সক্রিয় করা হয়েছে। সম্পূর্ণ সুরক্ষার জন্য নিচে অ্যাডভান্সড DNS চালু করুন।")
                } else {
                    showToast("অ্যাডাল্ট কনটেন্ট ব্লকার নিষ্ক্রিয় করা হয়েছে")
                }
            } else if (id == "dns_protection") {
                prefs.saveDnsProtectionEnabled(newState)
                if (newState) {
                    showToast("অ্যাডভান্সড DNS সক্রিয় করা হয়েছে")
                }
            }
        }
    }

    // --- Custom Blacklist Domains & Keywords ---
    val customDomains = mutableStateListOf<BlockedDomain>()
    val customKeywords = mutableStateListOf<String>()

    fun cleanAndValidateDomain(domainInput: String): String? {
        val cleaned = domainInput.trim().lowercase()
            .removePrefix("https://")
            .removePrefix("http://")
            .removePrefix("www.")
            .trimEnd('/')

        if (cleaned.isEmpty()) return null

        // Disallow spaces or Bengali script in domain name
        if (cleaned.contains(" ") || Regex("[\u0980-\u09FF]").containsMatchIn(cleaned)) {
            return null
        }

        // Must match a valid domain/URL format with valid TLD
        val host = cleaned.substringBefore('/')
        val domainPattern = Regex("^[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?(\\.[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?)*\\.[a-z]{2,}$")
        if (!domainPattern.matches(host)) {
            return null
        }
        return cleaned
    }

    fun addCustomDomain(domainInput: String): Boolean {
        val cleaned = cleanAndValidateDomain(domainInput)
        if (cleaned == null) {
            showToast("দয়া করে সঠিক ওয়েবসাইট লিংক বা ডোমেইন লিখুন (যেমন: examplewebsite.com)")
            return false
        }
        if (customDomains.any { it.domain.equals(cleaned, ignoreCase = true) }) {
            showToast("এই ওয়েবসাইটটি ইতোমধ্যে ব্লক তালিকায় সংরক্ষিত আছে")
            return false
        }
        val prefs = com.example.util.FocusLockPreferences.getInstance(context)
        customDomains.add(0, BlockedDomain(
            id = System.currentTimeMillis().toString(),
            domain = cleaned,
            blockedCount = 0,
            isCustom = true,
            addedTimeAgo = "এইমাত্র যুক্ত"
        ))
        prefs.saveCustomDomains(customDomains)
        showToast("$cleaned সফলভাবে ব্লকলিস্টে সংরক্ষণ করা হয়েছে 🔒")
        return true
    }

    fun removeCustomDomain(id: String) {
        val item = customDomains.find { it.id == id }
        customDomains.removeAll { it.id == id }
        val prefs = com.example.util.FocusLockPreferences.getInstance(context)
        prefs.saveCustomDomains(customDomains)
        if (item != null) {
            showToast("${item.domain} তালিকা থেকে মুছে ফেলা হয়েছে")
        }
    }

    fun addCustomKeyword(keywordInput: String): Boolean {
        val cleaned = keywordInput.trim().lowercase()
        if (cleaned.isEmpty()) {
            showToast("দয়া করে একটি কিওয়ার্ড লিখুন")
            return false
        }
        if (customKeywords.any { it.equals(cleaned, ignoreCase = true) }) {
            showToast("\"$cleaned\" কিওয়ার্ডটি ইতোমধ্যে তালিকায় আছে")
            return false
        }
        val prefs = com.example.util.FocusLockPreferences.getInstance(context)
        customKeywords.add(0, cleaned)
        prefs.saveCustomKeywords(customKeywords)
        showToast("\"$cleaned\" কিওয়ার্ড সফলভাবে ব্লকলিস্টে সংরক্ষণ করা হয়েছে")
        return true
    }

    fun removeCustomKeyword(keyword: String) {
        customKeywords.remove(keyword)
        val prefs = com.example.util.FocusLockPreferences.getInstance(context)
        prefs.saveCustomKeywords(customKeywords)
        showToast("\"$keyword\" কিওয়ার্ড ব্লকলিস্ট থেকে মুছে ফেলা হয়েছে")
    }

    // --- Focus Routines (Schedules) ---
    var isCreateRoutineScreenVisible by mutableStateOf(false)
    var isTimelineScreenVisible by mutableStateOf(false)
    var selectedRoutineForDetail by mutableStateOf<FocusRoutine?>(null)

    val focusRoutines = mutableStateListOf<FocusRoutine>()

    init {
        val prefs = com.example.util.FocusLockPreferences.getInstance(context)
        focusRoutines.addAll(prefs.getRoutines())
        customDomains.addAll(prefs.getCustomDomains())
        customKeywords.addAll(prefs.getCustomKeywords())
    }

    fun toggleRoutine(id: String) {
        val index = focusRoutines.indexOfFirst { it.id == id }
        if (index != -1) {
            val r = focusRoutines[index]
            val newState = !r.isEnabled
            focusRoutines[index] = r.copy(isEnabled = newState)
            com.example.util.FocusLockPreferences.getInstance(context).saveRoutines(focusRoutines)
            showToast(if (newState) "${r.titleBangla} সক্রিয় করা হয়েছে" else "${r.titleBangla} নিষ্ক্রিয় করা হয়েছে")
        }
    }

    fun deleteRoutine(id: String) {
        val item = focusRoutines.find { it.id == id }
        focusRoutines.removeAll { it.id == id }
        com.example.util.FocusLockPreferences.getInstance(context).saveRoutines(focusRoutines)
        if (item != null) {
            showToast("${item.titleBangla} রুটিন মুছে ফেলা হয়েছে")
        }
    }

    fun addRoutine(
        title: String,
        startTime: String,
        endTime: String,
        days: String,
        apps: String,
        isStrict: Boolean
    ) {
        focusRoutines.add(
            FocusRoutine(
                id = System.currentTimeMillis().toString(),
                titleBangla = title,
                titleEnglish = title,
                timeRange = "$startTime – $endTime",
                startTime = startTime,
                endTime = endTime,
                durationText = "2h",
                activeDaysBangla = days,
                activeDaysEnglish = days,
                targetedAppsBangla = apps,
                targetedAppsEnglish = apps,
                colorHex = "#10B981",
                iconType = "shield",
                isStrict = isStrict,
                isEnabled = true
            )
        )
        com.example.util.FocusLockPreferences.getInstance(context).saveRoutines(focusRoutines)
        showToast("\"$title\" রুটিন সফলভাবে তৈরি হয়েছে")
    }

    fun addRichRoutine(routine: FocusRoutine) {
        focusRoutines.add(0, routine)
        com.example.util.FocusLockPreferences.getInstance(context).saveRoutines(focusRoutines)
        showToast("\"${routine.titleBangla}\" রুটিন সফলভাবে তৈরি ও সংরক্ষণ করা হয়েছে")
    }

    // --- Analytics Data ---
    var analyticsPeriod by mutableStateOf("weekly") // "weekly" or "monthly"
        private set

    var todayBanglaDate by mutableStateOf(AppUsageTracker.getTodayBanglaFormattedDate())
        private set

    val weeklyDisciplineStats = mutableStateListOf<DailyDisciplineStat>()
    val monthlyDisciplineStats = mutableStateListOf<DailyDisciplineStat>()

    private val defaultWeeklyStats = listOf(
        DailyDisciplineStat("সোম", "Mon", 2.5f, 14),
        DailyDisciplineStat("মঙ্গল", "Tue", 3.0f, 12),
        DailyDisciplineStat("বুধ", "Wed", 3.5f, 8),
        DailyDisciplineStat("বৃহ", "Thu", 1.8f, 22),
        DailyDisciplineStat("শুক্র", "Fri", 3.2f, 15),
        DailyDisciplineStat("শনি", "Sat", 4.1f, 5, isBestDay = true),
        DailyDisciplineStat("রবি", "Sun", 3.4f, 18)
    )

    private val defaultMonthlyStats = listOf(
        DailyDisciplineStat("সপ্তাহ ১", "Week 1", 2.6f, 45),
        DailyDisciplineStat("সপ্তাহ ২", "Week 2", 3.2f, 38),
        DailyDisciplineStat("সপ্তাহ ৩", "Week 3", 3.8f, 29),
        DailyDisciplineStat("সপ্তাহ ৪", "Week 4", 4.3f, 18, isBestDay = true)
    )

    val activeDisciplineStats: List<DailyDisciplineStat>
        get() = if (analyticsPeriod == "weekly") {
            if (weeklyDisciplineStats.isNotEmpty()) weeklyDisciplineStats else defaultWeeklyStats
        } else {
            if (monthlyDisciplineStats.isNotEmpty()) monthlyDisciplineStats else defaultMonthlyStats
        }

    val dailyStats: List<DailyDisciplineStat>
        get() = activeDisciplineStats

    val totalSavedTimeString: String
        get() {
            val stats = activeDisciplineStats
            val totalHours = stats.sumOf { it.savedHours.toDouble() }
            val hours = totalHours.toInt()
            val mins = (((totalHours - hours) * 60).toInt()).coerceIn(0, 59)
            val hoursBangla = AppUsageTracker.toBanglaDigits(hours.toString())
            val minsBangla = AppUsageTracker.toBanglaDigits(String.format("%02d", mins))
            return "${hoursBangla}ঘ. ${minsBangla}মি."
        }

    val totalBlockedAttemptsCount: Int
        get() = activeDisciplineStats.sumOf { it.blockedAttempts }

    val bestDayInsightText: String
        get() {
            val stats = activeDisciplineStats
            val best = stats.find { it.isBestDay } ?: stats.maxByOrNull { it.savedHours }
            return if (best != null) {
                val savedHoursFormatted = AppUsageTracker.toBanglaDigits(String.format(java.util.Locale.US, "%.1f", best.savedHours))
                if (analyticsPeriod == "weekly") {
                    "${best.dayBangla}বার আপনি সর্বোচ্চ $savedHoursFormatted ঘণ্টা সময় আসক্তি থেকে রক্ষা করেছেন! 🎯"
                } else {
                    "${best.dayBangla}-এ আপনি সর্বোচ্চ $savedHoursFormatted ঘণ্টা গড় দৈনিক সময় সাশ্রয় করেছেন! 🎯"
                }
            } else {
                "ফোকাস গার্ডিয়ান আপনার ডিজিটাল সংযম বজায় রাখতে সহায়তা করছে! 🎯"
            }
        }

    fun setPeriod(period: String) {
        analyticsPeriod = period
    }

    fun refreshRealUsageAndAnalytics(context: Context) {
        viewModelScope.launch {
            try {
                todayBanglaDate = AppUsageTracker.getTodayBanglaFormattedDate()
                savedHoursToday = AppUsageTracker.getRealTodaySavedTime(
                    context,
                    completedFocusLockMinutes = focusLockConfig.durationMinutes * focusSessionsToday
                )

                val weekly = AppUsageTracker.getWeeklyDisciplineStats(context)
                if (weekly.isNotEmpty()) {
                    weeklyDisciplineStats.clear()
                    weeklyDisciplineStats.addAll(weekly)
                }

                val monthly = AppUsageTracker.getMonthlyDisciplineStats(context)
                if (monthly.isNotEmpty()) {
                    monthlyDisciplineStats.clear()
                    monthlyDisciplineStats.addAll(monthly)
                }
            } catch (e: Exception) {
                // Fallback gracefully
            }
        }
    }

    val topTriggers = listOf(
        AddictionTrigger("YouTube Shorts", 64, 45, SoftCoral),
        AddictionTrigger("Instagram Reels", 46, 32, LavenderFocus),
        AddictionTrigger("Facebook Feed", 22, 15, CalmBlue),
        AddictionTrigger("Adult & Phishing", 11, 8, WarmAmber)
    )

    // --- Security, Strict Mode & Partner ---
    var isStrictGlobalActive by mutableStateOf(false)
        private set

    fun toggleGlobalStrict() {
        if (isStrictGlobalActive) {
            // requires PIN to turn off
            showPinBottomSheet(PinAction.DISABLE_STRICT_MODE)
        } else {
            isStrictGlobalActive = true
        }
    }

    var accountabilityPartner by mutableStateOf(AccountabilityPartner())
        private set

    fun updatePartner(partner: AccountabilityPartner) {
        accountabilityPartner = partner
    }

    fun togglePartnerNotification(type: String) {
        accountabilityPartner = when (type) {
            "blocked" -> accountabilityPartner.copy(notifyOnBlockedSite = !accountabilityPartner.notifyOnBlockedSite)
            "bypass" -> accountabilityPartner.copy(notifyOnStrictBypass = !accountabilityPartner.notifyOnStrictBypass)
            "uninstall" -> accountabilityPartner.copy(notifyOnUninstall = !accountabilityPartner.notifyOnUninstall)
            else -> accountabilityPartner
        }
    }

    // --- User Account State ---
    var isOnboardingDialogVisible by mutableStateOf(false)

    var userAccount by mutableStateOf(
        UserAccount(
            isLoggedIn = true,
            name = "John Doe",
            email = "focus.guardian@example.com",
            phone = "+৮৮০ ১৭১২-৩৪৫৬৭৮",
            bio = "ফোকাস ও আত্মউন্নয়নের পথে নিয়োজিত",
            isPremium = true,
            avatarInitials = "JD",
            avatarUri = null,
            memberSince = "জানুয়ারি ২০২৬",
            religion = "ইসলাম"
        )
    )
        private set

    fun completeOnboarding(name: String, religion: String) {
        val trimmedName = name.trim()
        val trimmedReligion = religion.trim().ifBlank { "ইসলাম" }
        val initials = trimmedName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()

        userAccount = userAccount.copy(
            name = trimmedName,
            religion = trimmedReligion,
            avatarInitials = if (initials.isNotBlank()) initials else "FS"
        )
        isOnboardingDialogVisible = false

        contextRef?.let { ctx ->
            val prefs = FocusLockPreferences.getInstance(ctx)
            prefs.saveUserName(trimmedName)
            prefs.saveUserReligion(trimmedReligion)
            prefs.setOnboardingCompleted(true)
        }

        val welcomeMsg = if (appLanguage == AppLanguage.BENGALI) {
            "স্বাগতম, $trimmedName! আপনার ফোকাস প্রোফাইল সফলভাবে তৈরি হয়েছে।"
        } else {
            "Welcome, $trimmedName! Your focus profile is ready."
        }
        showToast(welcomeMsg)
    }

    fun showOnboardingSetup() {
        isOnboardingDialogVisible = true
    }

    fun setProfileAvatarUri(uri: String?) {
        userAccount = userAccount.copy(avatarUri = uri)
        showToast(if (uri != null) "প্রোফাইল ছবি সফলভাবে আপলোড করা হয়েছে!" else "ছবি সরানো হয়েছে")
    }

    fun updateUserProfile(name: String, email: String, phone: String, bio: String, religion: String = userAccount.religion) {
        val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
        userAccount = userAccount.copy(
            name = name,
            email = email,
            phone = phone,
            bio = bio,
            religion = religion,
            avatarInitials = if (initials.isNotEmpty()) initials else userAccount.avatarInitials
        )
        contextRef?.let { ctx ->
            val prefs = FocusLockPreferences.getInstance(ctx)
            prefs.saveUserName(name)
            prefs.saveUserReligion(religion)
        }
        showToast("প্রোফাইল সফলভাবে আপডেট করা হয়েছে!")
    }

    fun loginUser(email: String, pass: String) {
        val initials = email.take(2).uppercase()
        userAccount = UserAccount(
            isLoggedIn = true,
            name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
            email = email,
            phone = userAccount.phone,
            bio = userAccount.bio,
            isPremium = true,
            avatarInitials = if (initials.length == 2) initials else "US",
            avatarUri = userAccount.avatarUri,
            memberSince = "আগস্ট ২০২৬"
        )
        showToast("সফলভাবে লগইন করা হয়েছে!")
    }

    fun registerUser(name: String, email: String, pass: String) {
        val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
        userAccount = UserAccount(
            isLoggedIn = true,
            name = name,
            email = email,
            phone = "+৮৮০ ১৭১২-৩৪৫৬৭৮",
            bio = "ফোকাস ও আত্মউন্নয়নের পথে নিয়োজিত",
            isPremium = true,
            avatarInitials = if (initials.isNotEmpty()) initials else "US",
            avatarUri = null,
            memberSince = "আগস্ট ২০২৬"
        )
        showToast("অ্যাকাউন্ট সফলভাবে তৈরি করা হয়েছে!")
    }

    fun logoutUser() {
        userAccount = userAccount.copy(isLoggedIn = false)
        showToast("অ্যাকাউন্ট থেকে লগআউট করা হয়েছে")
    }

    // --- App Theme & Appearance State ---
    var appThemeMode by mutableStateOf(AppThemeMode.LIGHT)
        private set

    fun setAppTheme(mode: AppThemeMode, context: Context? = null) {
        appThemeMode = mode
        val targetContext = context ?: contextRef
        targetContext?.let {
            FocusLockPreferences.getInstance(it).saveAppThemeMode(mode)
        }
        showToast("${mode.titleBangla} সক্রিয় করা হয়েছে!")
    }

    // --- PIN Management ---
    var currentPin by mutableStateOf("1234")
        private set
    var isPinConfigured by mutableStateOf(true)
        private set
    var enteredPin by mutableStateOf("")
        private set
    var pendingNewPin by mutableStateOf("")
        private set
    var pinErrorMessage by mutableStateOf<String?>(null)
        private set
    var isPinBottomSheetVisible by mutableStateOf(false)
        private set
    var showPinManageOptionsDialog by mutableStateOf(false)
        private set
    var currentPinAction by mutableStateOf(PinAction.VERIFY)
        private set

    enum class PinAction {
        VERIFY,
        CREATE_PIN,
        CHANGE_PIN,
        RESET_PIN,
        DISABLE_STRICT_MODE,
        UNLOCK_STRICT_TIMER,
        EMERGENCY_UNLOCK,
        VIEW_PROTECTED_ACTIVITY,
        SETTINGS_VERIFY_CURRENT,
        SETTINGS_ENTER_NEW,
        SETTINGS_CONFIRM_NEW
    }

    fun openPinManageOptionsDialog() {
        showPinManageOptionsDialog = true
    }

    fun dismissPinManageOptionsDialog() {
        showPinManageOptionsDialog = false
    }

    fun saveNewPin(pin: String, context: Context? = null) {
        currentPin = pin
        isPinConfigured = true
        val targetContext = context ?: contextRef
        targetContext?.let {
            FocusLockPreferences.getInstance(it).saveSecurityPin(pin)
        }
    }

    fun deletePin(context: Context? = null) {
        currentPin = ""
        isPinConfigured = false
        val targetContext = context ?: contextRef
        targetContext?.let {
            FocusLockPreferences.getInstance(it).deleteSecurityPin()
        }
        val msg = if (appLanguage == AppLanguage.BENGALI) 
            "সিকিউরিটি পিন সফলভাবে মুছে ফেলা হয়েছে 🗑️" 
        else 
            "Security PIN removed successfully 🗑️"
        showToast(msg)
    }

    fun showPinBottomSheet(action: PinAction) {
        currentPinAction = action
        enteredPin = ""
        pendingNewPin = ""
        pinErrorMessage = null
        isPinBottomSheetVisible = true
    }

    fun hidePinBottomSheet() {
        isPinBottomSheetVisible = false
        enteredPin = ""
        pendingNewPin = ""
        pinErrorMessage = null
    }

    fun onPinDigitEntered(digit: String) {
        if (enteredPin.length < 4) {
            enteredPin += digit
            if (enteredPin.length == 4) {
                validateEnteredPin()
            }
        }
    }

    fun onPinBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            pinErrorMessage = null
        }
    }

    private fun validateEnteredPin() {
        // Step 1 in Settings: Verify Current PIN before showing Reset/Delete options
        if (currentPinAction == PinAction.SETTINGS_VERIFY_CURRENT) {
            if (enteredPin == currentPin) {
                hidePinBottomSheet()
                showPinManageOptionsDialog = true
            } else {
                pinErrorMessage = if (appLanguage == AppLanguage.BENGALI) "ভুল পিন! আবার চেষ্টা করুন।" else "Incorrect PIN! Try again."
                enteredPin = ""
            }
            return
        }

        // Step 2a: Enter New 4-digit PIN
        if (currentPinAction == PinAction.SETTINGS_ENTER_NEW || currentPinAction == PinAction.CREATE_PIN) {
            pendingNewPin = enteredPin
            enteredPin = ""
            pinErrorMessage = null
            currentPinAction = PinAction.SETTINGS_CONFIRM_NEW
            return
        }

        // Step 2b: Confirm New 4-digit PIN
        if (currentPinAction == PinAction.SETTINGS_CONFIRM_NEW) {
            if (enteredPin == pendingNewPin) {
                val newPin = pendingNewPin
                saveNewPin(newPin, contextRef)
                hidePinBottomSheet()
                val successMsg = if (appLanguage == AppLanguage.BENGALI) 
                    "নতুন সিকিউরিটি পিন সফলভাবে সংরক্ষিত হয়েছে 🔒" 
                else 
                    "New Security PIN created & saved successfully 🔒"
                showToast(successMsg)
            } else {
                pinErrorMessage = if (appLanguage == AppLanguage.BENGALI) 
                    "পিন দুটি মেলেনি! প্রথম থেকে আবার দিন।" 
                else 
                    "PINs do not match! Please enter again."
                enteredPin = ""
                pendingNewPin = ""
                currentPinAction = PinAction.SETTINGS_ENTER_NEW
            }
            return
        }

        // Emergency forgot pin recovery
        if (currentPinAction == PinAction.RESET_PIN || currentPinAction == PinAction.CHANGE_PIN) {
            pendingNewPin = enteredPin
            enteredPin = ""
            pinErrorMessage = null
            currentPinAction = PinAction.SETTINGS_CONFIRM_NEW
            return
        }

        // Other Protected Actions (Strict Mode, Timer Unlock, Emergency, Protected Activity)
        if (!isPinConfigured) {
            // PIN is deleted/disabled - allow direct access
            performProtectedActionSuccess()
            hidePinBottomSheet()
            return
        }

        if (enteredPin == currentPin) {
            performProtectedActionSuccess()
            hidePinBottomSheet()
        } else {
            pinErrorMessage = if (appLanguage == AppLanguage.BENGALI) "ভুল পিন! আবার চেষ্টা করুন।" else "Incorrect PIN! Try again."
            enteredPin = ""
        }
    }

    private fun performProtectedActionSuccess() {
        when (currentPinAction) {
            PinAction.DISABLE_STRICT_MODE -> {
                isStrictGlobalActive = false
                showToast("স্ট্রিক্ট মোড নিষ্ক্রিয় করা হয়েছে")
            }
            PinAction.UNLOCK_STRICT_TIMER -> {
                pauseTimer()
                showToast("ফোকাস টাইমার আনলক করা হয়েছে")
            }
            PinAction.EMERGENCY_UNLOCK -> {
                isProtectionActive = false
                showToast("ইমার্জেন্সি আনলক সক্রিয় (১৫ মিনিট)")
            }
            PinAction.VIEW_PROTECTED_ACTIVITY -> {
                isProtectedActivityUnlocked = true
                showToast("সুরক্ষিত অ্যাক্টিভিটি আনলক করা হয়েছে 🔓")
            }
            else -> {
                showToast("পিন সঠিক হয়েছে!")
            }
        }
    }

    // --- 6 Sequential System Permissions ---
    val permissions = mutableStateListOf(
        SecurityPermission(
            id = "accessibility",
            serialNumber = 1,
            titleBangla = "1. Accessibility Service Permission",
            titleEnglish = "Accessibility Service",
            descBangla = "আসক্তির অ্যাপ এবং নিষিদ্ধ ব্রাউজার URL রিয়েল-টাইমে সনাক্ত ও তৎক্ষণাৎ ব্লক করার জন্য অপরিহার্য।",
            descEnglish = "Essential to detect and block addictive apps & distracting web URLs in real-time.",
            isGranted = true,
            actionLabelBangla = "অনুমতি দিন"
        ),
        SecurityPermission(
            id = "exact_alarm",
            serialNumber = 2,
            titleBangla = "2. Exact Alarm Permission",
            titleEnglish = "Exact Alarm Permission",
            descBangla = "নির্ধারিত রুটিন শিডিউল এবং ফোকাস টাইমার নিখুঁত সেকেন্ডে শুরু ও বন্ধ করার জন্য প্রয়োজন।",
            descEnglish = "Required to trigger focus schedules and pomodoro timers with exact precision.",
            isGranted = true,
            actionLabelBangla = "অনুমতি দিন"
        ),
        SecurityPermission(
            id = "notification",
            serialNumber = 3,
            titleBangla = "3. Notification Permission",
            titleEnglish = "Notification Permission",
            descBangla = "ব্লকিং সতর্কতা, টাইমার স্ট্যাটাস ও জবাবদিহিতা এলার্ট নোটিফিকেশনে দেখানোর জন্য।",
            descEnglish = "Used to deliver security alerts, countdown updates, and partner notifications.",
            isGranted = true,
            actionLabelBangla = "অনুমতি দিন"
        ),
        SecurityPermission(
            id = "overlay",
            serialNumber = 4,
            titleBangla = "4. Display Over Other Apps Permission",
            titleEnglish = "Display Over Other Apps",
            descBangla = "নিষিদ্ধ অ্যাপ বা ওয়েবসাইট খোলার সাথে সাথে পূর্ণাঙ্গ ফোকাস শিল্ড ওভারলে স্ক্রিন প্রদর্শন করতে।",
            descEnglish = "Shows the full-screen cyberpunk focus shield lock over blocked apps immediately.",
            isGranted = false,
            actionLabelBangla = "অনুমতি দিন"
        ),
        SecurityPermission(
            id = "battery",
            serialNumber = 5,
            titleBangla = "5. Disable Battery Optimization",
            titleEnglish = "Disable Battery Optimization",
            descBangla = "অ্যান্ড্রয়েড সিস্টেম যাতে ব্যাকগ্রাউন্ডে সুরক্ষা ইঞ্জিন বন্ধ না করে তা নিশ্চিত করতে।",
            descEnglish = "Prevents OS from killing the focus background engine for 24/7 protection.",
            isGranted = false,
            actionLabelBangla = "অপটিমাইজেশন বন্ধ করুন"
        ),
        SecurityPermission(
            id = "device_admin",
            serialNumber = 6,
            titleBangla = "6. Device Admin Permission",
            titleEnglish = "Device Admin Permission",
            descBangla = "ফোকাস সেশন চলাকালীন অসাবধানতাবশত অ্যাপ আনইনস্টলেশন বা বাইপাস সম্পূর্ণরূপে প্রতিরোধ করতে।",
            descEnglish = "Hardens defense against accidental app uninstallation or bypass during focus.",
            isGranted = false,
            actionLabelBangla = "অ্যাডমিন সক্রিয় করুন"
        )
    )

    fun checkPermissions(context: Context) {
        val accessibility = FocusPermissionHelper.isAccessibilityPermissionGranted(context)
        val exactAlarm = FocusPermissionHelper.isExactAlarmPermissionGranted(context)
        val notification = FocusPermissionHelper.isNotificationPermissionGranted(context)
        val overlay = FocusPermissionHelper.isOverlayPermissionGranted(context)
        val battery = FocusPermissionHelper.isBatteryOptimizationDisabled(context)
        val deviceAdmin = FocusPermissionHelper.isDeviceAdminGranted(context)

        updatePermissionState("accessibility", accessibility)
        updatePermissionState("exact_alarm", exactAlarm)
        updatePermissionState("notification", notification)
        updatePermissionState("overlay", overlay)
        updatePermissionState("battery", battery)
        updatePermissionState("device_admin", deviceAdmin)
    }

    private fun updatePermissionState(id: String, isGranted: Boolean) {
        val index = permissions.indexOfFirst { it.id == id }
        if (index != -1 && permissions[index].isGranted != isGranted) {
            permissions[index] = permissions[index].copy(isGranted = isGranted)
        }
    }

    fun togglePermission(id: String, context: Context) {
        val index = permissions.indexOfFirst { it.id == id }
        if (index != -1) {
            val p = permissions[index]
            if (!p.isGranted) {
                // Not granted, open specific settings
                when (id) {
                    "accessibility" -> FocusPermissionHelper.openAccessibilitySettings(context)
                    "exact_alarm" -> FocusPermissionHelper.openExactAlarmSettings(context)
                    "notification" -> FocusPermissionHelper.openNotificationSettings(context)
                    "overlay" -> FocusPermissionHelper.openOverlaySettings(context)
                    "battery" -> FocusPermissionHelper.openBatteryOptimizationSettings(context)
                    "device_admin" -> FocusPermissionHelper.openDeviceAdminSettings(context)
                }
            } else {
                // Granted, allow user to revoke/disable via settings or direct removal
                when (id) {
                    "device_admin" -> {
                        val removed = FocusPermissionHelper.removeDeviceAdmin(context)
                        checkPermissions(context)
                        if (removed) {
                            showToast("ডিভাইস অ্যাডমিন পারমিশন বন্ধ করা হয়েছে")
                        } else {
                            FocusPermissionHelper.openDeviceAdminSettings(context)
                            showToast("ডিভাইস অ্যাডমিন নিষ্ক্রিয় করতে সেটিংস থেকে অফ করুন")
                        }
                    }
                    "accessibility" -> {
                        FocusPermissionHelper.openAccessibilitySettings(context)
                        showToast("অ্যাক্সেসিবিলিটি পারমিশন বন্ধ করতে সেটিংসের সুইচটি অফ করুন")
                    }
                    "exact_alarm" -> {
                        FocusPermissionHelper.openExactAlarmSettings(context)
                        showToast("অ্যালার্ম পারমিশন বন্ধ করতে সেটিংসের সুইচটি অফ করুন")
                    }
                    "notification" -> {
                        FocusPermissionHelper.openNotificationSettings(context)
                        showToast("নোটিফিকেশন বন্ধ করতে সেটিংসের সুইচটি অফ করুন")
                    }
                    "overlay" -> {
                        FocusPermissionHelper.openOverlaySettings(context)
                        showToast("ডিসপ্লে ওভারলে পারমিশন বন্ধ করতে সেটিংসের সুইচটি অফ করুন")
                    }
                    "battery" -> {
                        FocusPermissionHelper.openBatteryOptimizationSettings(context)
                        showToast("ব্যাটারি অপটিমাইজেশন সেটিংস ওপেন করা হচ্ছে")
                    }
                }
            }
        }
    }

    fun grantAllPermissions(context: Context) {
        // Find the first missing permission and open its settings
        val missingPermission = permissions.sortedBy { it.serialNumber }.firstOrNull { !it.isGranted }
        if (missingPermission != null) {
            togglePermission(missingPermission.id, context)
            showToast("দয়া করে ${missingPermission.titleBangla} পারমিশনটি দিন")
        } else {
            showToast("সবগুলো পারমিশন প্রস্তুত রয়েছে!")
        }
    }

    // --- Dialogs & Modals ---
    var isBadgesDialogVisible by mutableStateOf(false)
    var isEmergencyDialogVisible by mutableStateOf(false)
    var isAddRoutineDialogVisible by mutableStateOf(false)
    var isEditPartnerDialogVisible by mutableStateOf(false)
    var isProfileDialogVisible by mutableStateOf(false)
    var isProfileScreenVisible by mutableStateOf(false)
    var isNotificationAlertVisible by mutableStateOf(false)
    var isScreenTimeLimitDialogVisible by mutableStateOf(false)
    var isFocusLockSetupDialogVisible by mutableStateOf(false)
    var scrollToPermissionsRequested by mutableStateOf(false)
    var isFocusLockPermissionDialogVisible by mutableStateOf(false)
    var isFocusLockEmergencyDialogVisible by mutableStateOf(false)
    var isFocusLockCompletionDialogVisible by mutableStateOf(false)

    // --- Notification & Alerts System ---
    val notificationAlerts = mutableStateListOf<AppNotificationItem>()

    val unreadNotificationCount: Int
        get() = notificationAlerts.count { !it.isRead }

    var notifBlockingAlertsEnabled by mutableStateOf(true)
        private set
    var notifTimerUpdatesEnabled by mutableStateOf(true)
        private set
    var notifSecurityAlertsEnabled by mutableStateOf(true)
        private set
    var notifRemindersEnabled by mutableStateOf(true)
        private set

    var showNotificationSettingsSheet by mutableStateOf(false)

    fun initDefaultNotificationsIfNeeded() {
        if (notificationAlerts.isEmpty()) {
            notificationAlerts.addAll(
                listOf(
                    AppNotificationItem(
                        title = "YouTube Shorts প্রতিহত করা হয়েছে",
                        subtitle = "১০ মিনিট আগে • অটো-ব্লকার",
                        timeAgo = "১০ মি. আগে",
                        type = "alert",
                        isRead = false
                    ),
                    AppNotificationItem(
                        title = "Instagram Reels ফিল্টার সক্রিয়",
                        subtitle = "২৫ মিনিট আগে • সোশ্যাল শিল্ড",
                        timeAgo = "২৫ মি. আগে",
                        type = "shield",
                        isRead = false
                    ),
                    AppNotificationItem(
                        title = "অনলাইন বেটিং সাইট রিকোয়েস্ট ব্লকড",
                        subtitle = "৪ ঘণ্টা আগে • ফিশিং গার্ড",
                        timeAgo = "৪ ঘ. আগে",
                        type = "alert",
                        isRead = false
                    ),
                    AppNotificationItem(
                        title = "স্ট্রিক্ট মোড নোটিফিকেশন পার্টনারকে পাঠানো হয়েছে",
                        subtitle = "গতকাল রাত ১১:৩০",
                        timeAgo = "গতকাল",
                        type = "security",
                        isRead = true
                    )
                )
            )
        }
    }

    fun markAllNotificationsAsRead() {
        val updated = notificationAlerts.map { it.copy(isRead = true) }
        notificationAlerts.clear()
        notificationAlerts.addAll(updated)
        showToast("সব নোটিফিকেশন পঠিত হিসেবে চিহ্নিত করা হয়েছে")
    }

    fun clearAllNotifications() {
        notificationAlerts.clear()
        showToast("সব নোটিফিকেশন অ্যালার্ট মুছে ফেলা হয়েছে")
    }

    fun deleteNotification(id: String) {
        notificationAlerts.removeAll { it.id == id }
    }

    fun addNotificationAlert(
        title: String,
        subtitle: String,
        type: String = "alert",
        postSystemNotification: Boolean = true,
        context: Context? = null
    ) {
        val item = AppNotificationItem(
            title = title,
            subtitle = subtitle,
            timeAgo = "এখনই",
            type = type,
            isRead = false
        )
        notificationAlerts.add(0, item)

        if (postSystemNotification) {
            val ctx = context ?: contextRef
            if (ctx != null) {
                when (type) {
                    "alert" -> if (notifBlockingAlertsEnabled) FocusNotificationHelper.sendBlockAlertNotification(ctx, title, subtitle)
                    "timer" -> if (notifTimerUpdatesEnabled) FocusNotificationHelper.sendTimerNotification(ctx, title, subtitle)
                    "security" -> if (notifSecurityAlertsEnabled) FocusNotificationHelper.sendSecurityNotification(ctx, title, subtitle)
                    else -> FocusNotificationHelper.sendBlockAlertNotification(ctx, title, subtitle)
                }
            }
        }
    }

    fun setNotifBlockingAlerts(enabled: Boolean, context: Context) {
        notifBlockingAlertsEnabled = enabled
        FocusLockPreferences.getInstance(context).saveNotifBlocking(enabled)
    }

    fun setNotifTimerUpdates(enabled: Boolean, context: Context) {
        notifTimerUpdatesEnabled = enabled
        FocusLockPreferences.getInstance(context).saveNotifTimer(enabled)
    }

    fun setNotifSecurityAlerts(enabled: Boolean, context: Context) {
        notifSecurityAlertsEnabled = enabled
        FocusLockPreferences.getInstance(context).saveNotifSecurity(enabled)
    }

    fun setNotifReminders(enabled: Boolean, context: Context) {
        notifRemindersEnabled = enabled
        FocusLockPreferences.getInstance(context).saveNotifReminders(enabled)
    }

    fun sendTestNotification(context: Context) {
        if (!FocusPermissionHelper.isNotificationPermissionGranted(context)) {
            showToast("নোটিফিকেশন অনুমতি সক্রিয় নেই! অনুমতি প্রদান করুন।")
            FocusPermissionHelper.openNotificationSettings(context)
            return
        }
        FocusNotificationHelper.sendTestNotification(context)
        addNotificationAlert(
            title = "Focus Shield টেস্ট নোটিফিকেশন",
            subtitle = "সিস্টেম টেস্ট নোটিফিকেশন সফলভাবে সম্পন্ন",
            type = "security",
            postSystemNotification = false,
            context = context
        )
        showToast("টেস্ট নোটিফিকেশন সফলভাবে পাঠানো হয়েছে! নোটিফিকেশন বার চেক করুন।")
    }

    var toastMessage by mutableStateOf<String?>(null)
        private set

    fun showToast(msg: String) {
        toastMessage = msg
        viewModelScope.launch {
            delay(2500)
            if (toastMessage == msg) {
                toastMessage = null
            }
        }
    }

    fun dismissToast() {
        toastMessage = null
    }

    // --- Badges ---
    val badges = listOf(
        FocusBadge("1", "১৫ দিন ক্লিন স্ট্রিক", "15 Days Clean Streak", "টানা ১৫ দিন কোনো আসক্তির সাইটে প্রবেশ করেননি", "No addictive sites visited for 15 days", "local_fire_department", true, "আজ অর্জিত", "Earned Today"),
        FocusBadge("2", "Shorts Slayer", "Shorts Slayer", "৫০টির বেশি Shorts ও Reels প্রতিহত করেছেন", "Resisted over 50 Shorts and Reels", "⚔️", true, "৩ দিন পূর্বে", "3 days ago"),
        FocusBadge("3", "Deep Work Master", "Deep Work Master", "১০টি সফল গভীর ফোকাস সেশন সম্পন্ন করেছেন", "Completed 10 successful deep focus sessions", "🧠", true, "৫ দিন পূর্বে", "5 days ago"),
        FocusBadge("4", "Night Guardian", "Night Guardian", "টানা ৭ দিন রাত ১১টার পর ফোন ব্যবহার বন্ধ রেখেছেন", "Stopped using phone after 11 PM for 7 days", "🌙", true, "৭ দিন পূর্বে", "7 days ago"),
        FocusBadge("5", "Iron Will 30 Days", "Iron Will 30 Days", "৩০ দিনের ক্লিন স্ট্রিক অর্জন করুন", "Achieve a 30-day clean streak", "🛡️", false, "লকড (১৫/৩০ দিন)", "Locked (15/30 days)")
    )
}
