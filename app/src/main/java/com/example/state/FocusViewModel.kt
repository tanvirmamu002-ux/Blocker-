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
            activities.add(0, RecentActivity(
                id = System.currentTimeMillis().toString(),
                titleBangla = "স্মার্ট প্রোটেকশন চালু করা হয়েছে",
                titleEnglish = "Smart Protection Activated",
                timeAgoBangla = "এইমাত্র",
                timeAgoEnglish = "Just now",
                isSuccess = true,
                iconType = "shield"
            ))
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

        if (!FocusPermissionHelper.areAllRequiredPermissionsGranted(context)) {
            showToast("এই ফিচারটি চালু করার জন্য কিছু পারমিশনের প্রয়োজন। সেটিংসে গিয়ে পারমিশনগুলো চালু করুন।")
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
        activities.add(0, RecentActivity(
            id = System.currentTimeMillis().toString(),
            titleBangla = "Focus Lock সক্রিয় ($durationMinutes মিনিট)",
            titleEnglish = "Focus Lock Active ($durationMinutes mins)",
            timeAgoBangla = "এইমাত্র",
            timeAgoEnglish = "Just now",
            isSuccess = true,
            iconType = "shield"
        ))

        showToast("Focus Lock $durationMinutes মিনিটের জন্য সক্রিয় করা হলো 🔒")
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

                    activities.add(0, RecentActivity(
                        id = System.currentTimeMillis().toString(),
                        titleBangla = "${focusLockConfig.durationMinutes} মিনিটের ফোকাস লক সফল!",
                        titleEnglish = "${focusLockConfig.durationMinutes}m Focus Lock Completed!",
                        timeAgoBangla = "এইমাত্র",
                        timeAgoEnglish = "Just now",
                        isSuccess = true,
                        iconType = "session"
                    ))
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

        activities.add(0, RecentActivity(
            id = System.currentTimeMillis().toString(),
            titleBangla = "ইমার্জেন্সি আনলক: $reason",
            titleEnglish = "Emergency Unlock: $reason",
            timeAgoBangla = "এইমাত্র",
            timeAgoEnglish = "Just now",
            isSuccess = false,
            iconType = "blocked"
        ))

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
        appLanguage = AppLanguageManager.getLanguage(context)
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
    val activities = mutableStateListOf(
        RecentActivity(
            id = "1",
            titleBangla = "YouTube Shorts প্রতিহত করা হয়েছে",
            titleEnglish = "YouTube Shorts Blocked",
            timeAgoBangla = "১০ মিনিট আগে",
            timeAgoEnglish = "10 mins ago",
            isSuccess = false,
            iconType = "blocked"
        ),
        RecentActivity(
            id = "2",
            titleBangla = "Instagram Reels ব্লকড",
            titleEnglish = "Instagram Reels Blocked",
            timeAgoBangla = "২৫ মিনিট আগে",
            timeAgoEnglish = "25 mins ago",
            isSuccess = false,
            iconType = "blocked"
        ),
        RecentActivity(
            id = "3",
            titleBangla = "Gambling ওয়েবসাইট ব্লকড",
            titleEnglish = "Gambling Website Blocked",
            timeAgoBangla = "৪ ঘণ্টা আগে",
            timeAgoEnglish = "4 hours ago",
            isSuccess = false,
            iconType = "blocked"
        ),
        RecentActivity(
            id = "4",
            titleBangla = "৪৫ মিনিটের ফোকাস সেশন সফল!",
            titleEnglish = "45-min Focus Session Completed!",
            timeAgoBangla = "৩ ঘণ্টা আগে",
            timeAgoEnglish = "3 hours ago",
            isSuccess = true,
            iconType = "session"
        ),
        RecentActivity(
            id = "5",
            titleBangla = "TikTok অ্যাপ খোলার চেষ্টা প্রতিহত",
            titleEnglish = "TikTok launch attempt prevented",
            timeAgoBangla = "৫ ঘণ্টা আগে",
            timeAgoEnglish = "5 hours ago",
            isSuccess = false,
            iconType = "blocked"
        )
    )

    fun clearActivities() {
        activities.clear()
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
                activities.add(0, RecentActivity(
                    id = System.currentTimeMillis().toString(),
                    titleBangla = "$timerPresetMinutes মিনিটের $selectedTimerMode সফল!",
                    titleEnglish = "$timerPresetMinutes min $selectedTimerMode Completed!",
                    timeAgoBangla = "এইমাত্র",
                    timeAgoEnglish = "Just now",
                    isSuccess = true,
                    iconType = "session"
                ))
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
            isEnabled = true,
            iconType = "social"
        ),
        CategoryFilter(
            id = "shorts_blocker",
            titleBangla = "Short Video Blocker (শর্টস ও রিলস)",
            titleEnglish = "Short Video Blocker",
            descBangla = "YouTube Shorts, Instagram Reels, FB Reels ও আসক্তিকর ছোট ভিডিও বন্ধ",
            descEnglish = "Blocks YouTube Shorts, Reels, TikTok and addictive short-form video feeds",
            isEnabled = true,
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
            categoryFilters[index] = item.copy(isEnabled = !item.isEnabled)
        }
    }

    // --- Custom Blacklist Domains & Keywords ---
    val customDomains = mutableStateListOf(
        BlockedDomain("1", "youtube.com/shorts", 64, true, "ডিফল্ট"),
        BlockedDomain("2", "instagram.com/reels", 46, true, "ডিফল্ট"),
        BlockedDomain("3", "tiktok.com", 28, true, "ডিফল্ট"),
        BlockedDomain("4", "facebook.com", 22, true, "ডিফল্ট"),
        BlockedDomain("5", "x.com", 15, true, "গতকাল যুক্ত")
    )

    val customKeywords = mutableStateListOf(
        "casino",
        "betting",
        "adult",
        "porn",
        "reels",
        "shorts",
        "viral",
        "dating"
    )

    fun addCustomDomain(domainInput: String) {
        val cleaned = domainInput.trim().lowercase().removePrefix("https://").removePrefix("http://")
        if (cleaned.isNotEmpty() && customDomains.none { it.domain == cleaned }) {
            customDomains.add(0, BlockedDomain(
                id = System.currentTimeMillis().toString(),
                domain = cleaned,
                blockedCount = 0,
                isCustom = true,
                addedTimeAgo = "এইমাত্র যুক্ত"
            ))
            showToast("$cleaned সফলভাবে ব্লকলিস্টে যোগ ও সংরক্ষণ করা হয়েছে")
        }
    }

    fun removeCustomDomain(id: String) {
        val item = customDomains.find { it.id == id }
        customDomains.removeAll { it.id == id }
        if (item != null) {
            showToast("${item.domain} তালিকা থেকে মুছে ফেলা হয়েছে")
        }
    }

    fun addCustomKeyword(keyword: String) {
        val cleaned = keyword.trim().lowercase()
        if (cleaned.isNotEmpty() && !customKeywords.contains(cleaned)) {
            customKeywords.add(0, cleaned)
            showToast("\"$cleaned\" কিওয়ার্ড সফলভাবে ব্লক ও সংরক্ষণ করা হয়েছে")
        }
    }

    fun removeCustomKeyword(keyword: String) {
        customKeywords.remove(keyword)
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
    var userAccount by mutableStateOf(
        UserAccount(
            isLoggedIn = true,
            name = "John Doe (Boss)",
            email = "focus.guardian@example.com",
            phone = "+৮৮০ ১৭১২-৩৪৫৬৭৮",
            bio = "ফোকাস ও আত্মউন্নয়নের পথে নিয়োজিত",
            isPremium = true,
            avatarInitials = "JD",
            avatarUri = null,
            memberSince = "জানুয়ারি ২০২৬"
        )
    )
        private set

    fun setProfileAvatarUri(uri: String?) {
        userAccount = userAccount.copy(avatarUri = uri)
        showToast(if (uri != null) "প্রোফাইল ছবি সফলভাবে আপলোড করা হয়েছে!" else "ছবি সরানো হয়েছে")
    }

    fun updateUserProfile(name: String, email: String, phone: String, bio: String) {
        val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
        userAccount = userAccount.copy(
            name = name,
            email = email,
            phone = phone,
            bio = bio,
            avatarInitials = if (initials.isNotEmpty()) initials else userAccount.avatarInitials
        )
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

    fun setAppTheme(mode: AppThemeMode) {
        appThemeMode = mode
        showToast("${mode.titleBangla} সক্রিয় করা হয়েছে!")
    }

    // --- PIN Management ---
    var currentPin by mutableStateOf("1234")
        private set
    var isPinConfigured by mutableStateOf(true)
        private set
    var enteredPin by mutableStateOf("")
        private set
    var pinErrorMessage by mutableStateOf<String?>(null)
        private set
    var isPinBottomSheetVisible by mutableStateOf(false)
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
        EMERGENCY_UNLOCK
    }

    fun showPinBottomSheet(action: PinAction) {
        currentPinAction = action
        enteredPin = ""
        pinErrorMessage = null
        isPinBottomSheetVisible = true
    }

    fun hidePinBottomSheet() {
        isPinBottomSheetVisible = false
        enteredPin = ""
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
        if (currentPinAction == PinAction.CREATE_PIN) {
            currentPin = enteredPin
            isPinConfigured = true
            hidePinBottomSheet()
            showToast("নতুন পিন সফলভাবে তৈরি করা হয়েছে!")
            return
        }

        if (currentPinAction == PinAction.RESET_PIN || currentPinAction == PinAction.CHANGE_PIN) {
            currentPin = enteredPin
            isPinConfigured = true
            hidePinBottomSheet()
            showToast("পিন সফলভাবে পরিবর্তন / রিসেট করা হয়েছে!")
            return
        }

        if (enteredPin == currentPin) {
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
                else -> {
                    showToast("পিন সঠিক হয়েছে!")
                }
            }
            hidePinBottomSheet()
        } else {
            pinErrorMessage = "ভুল পিন! আবার চেষ্টা করুন।"
            enteredPin = ""
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
                showToast("এই পারমিশনটি ইতোমধ্যেই দেওয়া আছে।")
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
