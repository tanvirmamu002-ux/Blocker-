package com.example.util

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(val code: String, val displayName: String) {
    BENGALI("bn", "বাংলা"),
    ENGLISH("en", "English")
}

object AppLanguageManager {
    private const val PREF_NAME = "app_language_prefs"
    private const val KEY_LANGUAGE = "app_language"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLanguage(context: Context, language: AppLanguage) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    fun getLanguage(context: Context): AppLanguage {
        val code = getPrefs(context).getString(KEY_LANGUAGE, AppLanguage.BENGALI.code)
        return AppLanguage.values().find { it.code == code } ?: AppLanguage.BENGALI
    }
}

// Global strings mapping
val LocalAppStrings = compositionLocalOf<AppStrings> { BengaliStrings }

interface AppStrings {
    // Settings / Security Screen
    val securityTitle: String
    val securitySubtitle: String
    
    val themeCardTitle: String
    val themeDarkActive: String
    val themeLightActive: String
    val themeSystemActive: String
    val themeDark: String
    val themeLight: String
    val themeSystem: String
    val themeSheetTitle: String
    val themeSheetSubtitle: String
    val themeLightDesc: String
    val themeDarkDesc: String
    val themeSystemDesc: String

    val languageCardTitle: String
    val languageSheetTitle: String
    val languageSheetSubtitle: String
    
    val pinCardTitle: String
    val pinCardSubtitle: String
    val pinCreateNew: String
    val pinChange: String
    val pinReset: String
    val pinForgot: String
    
    val pinConfigured: String
    val pinNotConfigured: String
    val pinChangeButton: String
    val pinCreateButton: String
    val pinResetButton: String
    val pinForgotHint: String
    val pinVerifyCurrentTitle: String
    val pinVerifyCurrentSubtitle: String
    val pinManageOptionsTitle: String
    val pinOptionChangeTitle: String
    val pinOptionChangeDesc: String
    val pinOptionDeleteTitle: String
    val pinOptionDeleteDesc: String
    val pinDeleteConfirmTitle: String
    val pinDeleteConfirmMessage: String
    val pinDeleteConfirmButton: String
    val pinEnterNewTitle: String
    val pinEnterNewSubtitle: String
    val pinConfirmNewTitle: String
    val pinConfirmNewSubtitle: String
    val pinMismatchError: String
    val pinChangedSuccess: String
    val pinDeletedSuccess: String
    val pinCreatedSuccess: String
    
    val permCardTitle: String
    val permCardSubtitle: String
    val permGrantAll: String
    val permGranted: String
    val permGrant: String
    
    val forgotPinDialogTitle: String
    fun forgotPinDialogDesc(email: String): String
    val forgotPinCancel: String
    val forgotPinConfirm: String
    
    // Home Screen
    val homeGreeting: String
    fun getDynamicGreeting(userName: String, hour: Int = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)): String
    val onboardingWelcomeTitle: String
    val onboardingWelcomeSubtitle: String
    val onboardingNameLabel: String
    val onboardingNamePlaceholder: String
    val onboardingReligionLabel: String
    val onboardingSubmitButton: String
    val onboardingNameRequiredError: String
    val profileLabelReligion: String
    val homeSavedTimePrefix: String
    val homeMetricsTitle: String
    val homeMetricBlocked: String
    val homeMetricSaved: String
    val homeMetricSessions: String
    val homeQuickActionsTitle: String
    
    val quickActionFocusLock: String
    val quickActionFocusLockActive: String
    val quickActionFocusLockSetup: String
    val quickActionScreenLimit: String
    val quickActionScreenLimitDesc: String
    val quickActionSchedule: String
    val quickActionScheduleDesc: String
    val quickActionOneTime: String
    val quickActionOneTimeActive: String
    val quickActionOneTimeDesc: String
    val toastInstantBlockApplied: String
    val toastBlockRemoved: String
    
    val homeRecentActivity: String
    val homeRecentClear: String
    val homeRecentEmpty: String
    
    val quoteBody: String
    val quoteAuthor: String
    
    val streakClean: String
    val streakMsg: String
    val streakBadges: String
    
    // Radar Shield Card
    val radarProtected: String
    val radarPaused: String
    val radarShieldActive: String
    val radarShieldPaused: String
    val radarShieldActiveDesc: String
    val radarShieldPausedDesc: String
    
    // Analytics Screen
    val analyticsTitle: String
    val analyticsSubtitle: String
    val analyticsWeekly: String
    val analyticsMonthly: String
    val analyticsWeeklySaved: String
    val analyticsMonthlySaved: String
    val analyticsWeeklyTrend: String
    val analyticsMonthlyTrend: String
    val analyticsTotalBlocked: String
    val analyticsMostBlocked: String
    val analyticsTopTriggers: String
    fun analyticsReportToast(type: String): String
    val analyticsDownloadWeekly: String
    val analyticsDownloadMonthly: String
    
    // Charts
    val chartWeeklyTrend: String
    val chartMonthlyTrend: String
    val chartWeeklyDesc: String
    val chartMonthlyDesc: String
    fun chartSavedHours(day: String, hours: String, isWeekly: Boolean): String
    fun chartBlockedAttempts(attempts: Int): String
    val chartCategoryRatioTitle: String
    val chartCategoryRatioDesc: String
    val chartBlockedPrefix: String
    fun chartCategoryDetails(count: Int, percentage: Int): String
    val chartDisciplineScoreTitle: String
    val chartDisciplineScoreDesc: String
    
    val chartInsightWeeklyFallback: String
    val chartInsightMonthlyFallback: String
    
    // Profile Screen
    val profileTitle: String
    val profileSubtitle: String
    val profileEdit: String
    val profileSave: String
    val profileUploadPhoto: String
    val profileChangePhoto: String
    val profileRemovePhoto: String
    val profileVerifiedProtection: String
    val profileProMember: String
    val profilePersonalDetails: String
    val profileMemberSincePrefix: String
    val profileNameInput: String
    val profileEmailInput: String
    val profilePhoneInput: String
    val profileBioInput: String
    val profileSaveChanges: String
    val profileLabelName: String
    val profileLabelEmail: String
    val profileLabelPhone: String
    val profileLabelBio: String
    val profilePerformanceSummary: String
    val profileCleanStreak: String
    val profileTodayBlocking: String
    val profileSavedTime: String
    val profileSettingsNav: String
    val profileAccountStatus: String
    val profileCloudSyncActive: String
    val profileLogout: String
    val profileAuthTitle: String
    val profileLoginTab: String
    val profileRegisterTab: String
    val profilePasswordInput: String
    val profileLoginSubmit: String
    val profileRegisterSubmit: String
    
    // Protected Activity & Security Section
    val profileSecuritySectionTitle: String
    val profileProtectedActivityTitle: String
    val profileProtectedActivityDesc: String
    val profileProtectedActivityUnlock: String
    val profileProtectedActivityLocked: String
    val profileProtectedActivityEmpty: String
    val profileProtectedActivityLockAgain: String
    val profileProtectedActivityClear: String
    
    // Create Routine Screen
    val routineCreateTitle: String
    val routineCreateSubtitle: String
    val routineNameLabel: String
    val routineNameHint: String
    val routineColorLabel: String
    val routineTimeLabel: String
    val routineTimeDesc: String
    val routineStartTime: String
    val routineEndTime: String
    val routineProtectedTime: String
    val routineProtectedTimeDesc: String
    val routineQuickPreset: String
    val routineRepeatDays: String
    val routineRepeatDaysDesc: String
    val routineAllDays: String
    val routineWeekdays: String
    val routineBlockingFilter: String
    val routineBlockingFilterDesc: String
    val routineTargetCategory: String
    val routineShortsBlocking: String
    val routineShortsBlockingDesc: String
    val routineWebBlocking: String
    val routineWebBlockingDesc: String
    val routineStrictMode: String
    val routineStrictModeDesc: String
    val routineSummaryTitle: String
    val routineSummaryDesc: String
    val routineActiveDays: String
    val routineBlockedApps: String
    val routineCategoryCount: String
    val routineCustomFilter: String
    val routineProtectionMode: String
    val routineStrictLabel: String
    val routineStandardLabel: String
    val routineEditSave: String
    
    // RoutineDetailSheet
    val routineDetailTimePeriod: String
    val routineDetailRepeatDays: String
    val routineDetailTotalDuration: String
    val routineDetailBlockRules: String
    val routineDetailTargetApps: String
    val routineDetailShortsBlock: String
    val routineDetailWebBlock: String
    val routineDetailStrictMode: String
    val routineDetailDelete: String
    val routineDetailPause: String
    val routineDetailActivate: String
    
    // BadgesDialog
    val badgesTitle: String
    val badgesOkButton: String
    
    // FocusLockPermissionDialog
    val permDialogTitle: String
    val permDialogDesc: String
    val permAccDesc: String
    val permUsageDesc: String
    val permOverlayDesc: String
    val permNotifDesc: String
    val permRecheck: String
    val permContinue: String
    
    // Time Formats
    fun formatMinutesLong(minutes: Int): String
    fun formatMinutesShort(minutes: Int): String
    val timeSliderDailyBudget: String
    val timeSliderTodayUsage: String
    val timeSliderStrictLock: String
    val timeSliderQuickPresets: String
    val timeSliderStrictLimitTitle: String
    val timeSliderStrictLimitDesc: String
    val timeSliderDelete: String
    val timeSliderCancel: String
    val timeSliderSave: String

    // Notification & Alerts
    val notificationQuickTitle: String
    val notificationGranted: String
    val notificationNotGranted: String
    val notificationSheetTitle: String
    val notificationSheetSubtitle: String
    val notificationPermCardTitle: String
    val notificationPermCardDesc: String
    val notificationGrantButton: String
    val notificationGrantedChip: String
    val notificationToggleBlocking: String
    val notificationToggleBlockingDesc: String
    val notificationToggleTimer: String
    val notificationToggleTimerDesc: String
    val notificationToggleSecurity: String
    val notificationToggleSecurityDesc: String
    val notificationToggleReminders: String
    val notificationToggleRemindersDesc: String
    val notificationTestButton: String
    val notificationTestSent: String
    val notificationAlertsTitle: String
    val notificationAlertsEmpty: String
    val notificationMarkAllRead: String
    val notificationClearAll: String
}

object BengaliStrings : AppStrings {
    override val securityTitle = "সেটিংস ও কন্ট্রোল"
    override val securitySubtitle = "অ্যাপ থিম, সিকিউরিটি পিন ও সিস্টেম পারমিশন সেটিংস"
    
    override val themeCardTitle = "অ্যাপের থিম"
    override val themeDarkActive = "ডার্ক মোড সক্রিয়"
    override val themeLightActive = "লাইট মোড সক্রিয়"
    override val themeSystemActive = "সিস্টেম অনুযায়ী"
    override val themeDark = "ডার্ক"
    override val themeLight = "লাইট"
    override val themeSystem = "সিস্টেম অনুযায়ী"
    override val themeSheetTitle = "অ্যাপের থিম নির্বাচন করুন"
    override val themeSheetSubtitle = "আপনার পছন্দের ইন্টারফেস মোড বেছে নিন"
    override val themeLightDesc = "উজ্জ্বল ও আধুনিক ক্লিন মিনিমালিস্ট ইন্টারফেস"
    override val themeDarkDesc = "ডার্ক ব্যাকগ্রাউন্ড ও ব্যাটারি সাশ্রয়ী ইন্টারফেস"
    override val themeSystemDesc = "ডিভাইসের সিস্টেম সেটিংস স্বয়ংক্রিয়ভাবে অনুসরণ করবে"

    override val languageCardTitle = "অ্যাপের ভাষা"
    override val languageSheetTitle = "অ্যাপের ভাষা নির্বাচন করুন"
    override val languageSheetSubtitle = "সম্পূর্ণ অ্যাপের ইন্টারফেসের ভাষা পরিবর্তন করুন"
    
    override val pinCardTitle = "সিকিউরিটি পিন (PIN) সেটআপ"
    override val pinCardSubtitle = "অ্যান্টি-আনইনস্টল ও ফোর্স ক্লোজ প্রোটেকশন"
    override val pinCreateNew = "নতুন পিন তৈরি করুন"
    override val pinChange = "পিন পরিবর্তন করুন"
    override val pinReset = "পিন রিসেট করুন"
    override val pinForgot = "পিন ভুলে গেছেন?"

    override val pinConfigured = "•••• (সুরক্ষিত ৪-ডিজিট সক্রিয়)"
    override val pinNotConfigured = "পিন সেট করা হয়নি"
    override val pinChangeButton = "পিন পরিবর্তন"
    override val pinCreateButton = "নতুন পিন তৈরি"
    override val pinResetButton = "রিসেট করুন"
    override val pinForgotHint = "পিন ভুলে গেছেন? ইমেইল দিয়ে উদ্ধার করুন"
    override val pinVerifyCurrentTitle = "বর্তমান পিন যাচাই করুন"
    override val pinVerifyCurrentSubtitle = "নিরাপত্তা নিশ্চিত করতে অনুগ্রহ করে বর্তমান ৪-ডিজিটের পিন প্রবেশ করান"
    override val pinManageOptionsTitle = "সিকিউরিটি পিন ব্যবস্থাপনা"
    override val pinOptionChangeTitle = "পিন পরিবর্তন করুন (Reset / Change PIN)"
    override val pinOptionChangeDesc = "আপনার গোপন ৪-সংখ্যার নতুন পিন নির্ধারণ করুন"
    override val pinOptionDeleteTitle = "পিন মুছে ফেলুন (Delete PIN)"
    override val pinOptionDeleteDesc = "সিকিউরিটি পিন বন্ধ ও মুছে ফেলুন"
    override val pinDeleteConfirmTitle = "সিকিউরিটি পিন মুছে ফেলতে চান?"
    override val pinDeleteConfirmMessage = "আপনি কি নিশ্চিতভাবে সিকিউরিটি পিন মুছে ফেলতে চান? এটি মুছে ফেললে অ্যাপের সেটিংসে কোনো পিনের প্রয়োজন হবে না।"
    override val pinDeleteConfirmButton = "মুছে ফেলুন"
    override val pinEnterNewTitle = "নতুন ৪-ডিজিট পিন দিন"
    override val pinEnterNewSubtitle = "গোপন ও সুরক্ষিত ৪ সংখ্যার পিন নির্ধারণ করুন"
    override val pinConfirmNewTitle = "নতুন পিন নিশ্চিত করুন"
    override val pinConfirmNewSubtitle = "নিশ্চিত করতে একই ৪-ডিজিটের পিন পুনরায় প্রবেশ করান"
    override val pinMismatchError = "পিন দুটি মেলেনি! প্রথম থেকে আবার চেষ্টা করুন।"
    override val pinChangedSuccess = "সিকিউরিটি পিন সফলভাবে পরিবর্তন ও সংরক্ষণ করা হয়েছে 🔒"
    override val pinDeletedSuccess = "সিকিউরিটি পিন সফলভাবে মুছে ফেলা হয়েছে 🗑️"
    override val pinCreatedSuccess = "নতুন সিকিউরিটি পিন সফলভাবে তৈরি ও সংরক্ষণ করা হয়েছে 🔒"

    override val permCardTitle = "সিস্টেম পারমিশন প্রোটেকশন"
    override val permCardSubtitle = "অ্যাপটি সঠিকভাবে কাজ করার জন্য নিচের সব পারমিশন প্রয়োজন"
    override val permGrantAll = "সব পারমিশন দিন"
    override val permGranted = "পারমিশন দেওয়া হয়েছে"
    override val permGrant = "পারমিশন দিন"
    
    override val forgotPinDialogTitle = "জরুরি পিন রিকভারি"
    override fun forgotPinDialogDesc(email: String) = "আপনার রেজিস্টার্ড ইমেইল ($email)-এ ভেরিফিকেশন কোড পাঠানো হয়েছে। আপনি এখনই সরাসরি নতুন পিন তৈরি ও রিসেট করতে পারেন।"
    override val forgotPinCancel = "বাতিল"
    override val forgotPinConfirm = "পিন রিসেট করুন"
    
    override fun getDynamicGreeting(userName: String, hour: Int): String {
        val cleanName = userName
            .replace("(Boss)", "")
            .replace("Boss", "")
            .replace("John Doe", "")
            .replace("(বস)", "")
            .replace("বস", "")
            .replace("(", "")
            .replace(")", "")
            .trim()

        val periodGreeting = when (hour) {
            in 5..11 -> "শুভ সকাল"
            in 12..15 -> "শুভ দুপুর"
            in 16..17 -> "শুভ বিকাল"
            in 18..20 -> "শুভ সন্ধ্যা"
            else -> "শুভ রাত্রি"
        }

        return if (cleanName.isNotBlank()) "$periodGreeting, $cleanName" else periodGreeting
    }

    override val homeGreeting: String
        get() = getDynamicGreeting("")

    override val onboardingWelcomeTitle = "Focus Shield-এ স্বাগতম! 🛡️"
    override val onboardingWelcomeSubtitle = "আপনার ডিজিটাল ওয়েলবিয়িং ও ফোকাস যাত্রা শুরু করতে নিচের তথ্যগুলো পূরণ করুন:"
    override val onboardingNameLabel = "আপনার নাম"
    override val onboardingNamePlaceholder = "যেমন: তানভীর আহমেদ"
    override val onboardingReligionLabel = "আপনার ধর্ম"
    override val onboardingSubmitButton = "শুরু করুন 🚀"
    override val onboardingNameRequiredError = "অনুগ্রহ করে আপনার নাম লিখুন"
    override val profileLabelReligion = "ধর্ম"
    override val homeSavedTimePrefix = "আজ আপনার সময় বাঁচিয়েছি "
    override val homeMetricsTitle = "আজকের মেট্রিক্স"
    override val homeMetricBlocked = "ব্লকড অ্যাটেম্পট"
    override val homeMetricSaved = "বাঁচানো সময়"
    override val homeMetricSessions = "ফোকাস সেশন"
    override val homeQuickActionsTitle = "কুইক অ্যাকশনস"
    
    override val quickActionFocusLock = "Focus Lock"
    override val quickActionFocusLockActive = "ফোকাস লক সক্রিয় 🔒"
    override val quickActionFocusLockSetup = "বাস্তব ফোকাস লক সেটআপ"
    override val quickActionScreenLimit = "Screen Time Limit"
    override val quickActionScreenLimitDesc = "দৈনিক সীমা নির্ধারণ"
    override val quickActionSchedule = "Schedule"
    override val quickActionScheduleDesc = "রুটিন অনুযায়ী অটো ব্লক"
    override val quickActionOneTime = "One-time Block"
    override val quickActionOneTimeActive = "তাৎক্ষণিক ব্লক সক্রিয়"
    override val quickActionOneTimeDesc = "অ্যাপ ও সাইট বন্ধ করুন"
    override val toastInstantBlockApplied = "তাৎক্ষণিক ব্লক করা হলো"
    override val toastBlockRemoved = "ব্লক প্রত্যাহার করা হয়েছে"
    
    override val homeRecentActivity = "সাম্প্রতিক অ্যাক্টিভিটি"
    override val homeRecentClear = "ক্লিয়ার"
    override val homeRecentEmpty = "কোনো সাম্প্রতিক অ্যাক্টিভিটি নেই"
    
    override val quoteBody = "“আজকের এক মুহূর্তের আত্মনিয়ন্ত্রণ, আগামী দিনের সফলতার ভিত গড়ে দেয়।”"
    override val quoteAuthor = "— আত্মউন্নয়ন ও ফোকাস চিন্তা"
    
    override val streakClean = "ক্লিন স্ট্রিক"
    override val streakMsg = "দারুণ ডিসিপ্লিন! আপনি সঠিক ট্র্যাকে আছেন।"
    override val streakBadges = "ব্যাজসমূহ"
    
    override val radarProtected = "PROTECTED"
    override val radarPaused = "PAUSED"
    override val radarShieldActive = "Shield Active"
    override val radarShieldPaused = "Shield Paused"
    override val radarShieldActiveDesc = "সবগুলো আসক্তির অ্যাপ ও শর্টস রিয়েল-টাইমে ব্লক করা হচ্ছে।"
    override val radarShieldPausedDesc = "সুরক্ষা সাময়িকভাবে স্থগিত করা হয়েছে।"
    
    override val analyticsTitle = "ডিসিপ্লিন অ্যানালিটিক্স"
    override val analyticsSubtitle = "আপনার সময় ও ডিজিটাল সংযমের বিস্তারিত পরিসংখ্যান"
    override val analyticsWeekly = "সাপ্তাহিক"
    override val analyticsMonthly = "মাসিক"
    override val analyticsWeeklySaved = "সাপ্তাহিক মোট সাশ্রয়"
    override val analyticsMonthlySaved = "মাসিক মোট সাশ্রয়"
    override val analyticsWeeklyTrend = "+২৩% গত সপ্তাহের চেয়ে"
    override val analyticsMonthlyTrend = "+৩১% গত মাসের চেয়ে"
    override val analyticsTotalBlocked = "মোট ব্লকড অ্যাটেম্পট"
    override val analyticsMostBlocked = "সবচেয়ে বেশি: রাতে ১০টা-১২টা"
    override val analyticsTopTriggers = "টপ আসক্তি ট্রিগারসমূহ (বিস্তারিত)"
    override fun analyticsReportToast(type: String) = "$type রিপোর্ট সফলভাবে জেনারেট হয়েছে!"
    override val analyticsDownloadWeekly = "সাপ্তাহিক রিক্যাপ ডাউনলোড করুন"
    override val analyticsDownloadMonthly = "মাসিক রিক্যাপ ডাউনলোড করুন"
    
    override val chartWeeklyTrend = "সাপ্তাহিক টাইম সেভিং ট্রেন্ড"
    override val chartMonthlyTrend = "মাসিক ধারাবাহিকতা ট্রেন্ড"
    override val chartWeeklyDesc = "গত ৭ দিনের রিয়েল ডিসিপ্লিন ট্র্যাকিং"
    override val chartMonthlyDesc = "৪ সপ্তাহের অগ্রগতি ও সাশ্রয় বিশ্লেষণ"
    override fun chartSavedHours(day: String, hours: String, isWeekly: Boolean) = if (isWeekly) "${day}বার: $hours ঘণ্টা সাশ্রয়" else "${day}: $hours ঘণ্টা সাশ্রয়"
    override fun chartBlockedAttempts(attempts: Int) = "${attempts}টি বাধা"
    override val chartCategoryRatioTitle = "আসক্তি প্রতিরোধ অনুপাত"
    override val chartCategoryRatioDesc = "ক্যাটাগরি ভিত্তিক ব্লকড ডিস্ট্রিবিউশন"
    override val chartBlockedPrefix = "ব্লকড"
    override fun chartCategoryDetails(count: Int, percentage: Int) = "$count বার ($percentage%)"
    override val chartDisciplineScoreTitle = "ডিসিপ্লিন স্কোর: ৯২% (উৎকৃষ্ট)"
    override val chartDisciplineScoreDesc = "আপনি গত ৭ দিনের লক্ষ্যের অধিকাংশ পূরণ করেছেন!"
    
    override val chartInsightWeeklyFallback = "শনিবার আপনি সর্বোচ্চ ৪.১ ঘণ্টা সময় আসক্তি থেকে রক্ষা করেছেন! 🎯"
    override val chartInsightMonthlyFallback = "সপ্তাহ ৪-এ আপনি সর্বোচ্চ ৪.৩ ঘণ্টা গড় দৈনিক সময় সাশ্রয় করেছেন! 🚀"
    
    override val profileTitle = "আমার প্রোফাইল"
    override val profileSubtitle = "অ্যাকাউন্ট বিবরণ ও পার্সোনালাইজেশন"
    override val profileEdit = "এডিট"
    override val profileSave = "সংরক্ষণ"
    override val profileUploadPhoto = "ছবি আপলোড করুন"
    override val profileChangePhoto = "ছবি পরিবর্তন করুন"
    override val profileRemovePhoto = "ছবি মুছুন"
    override val profileVerifiedProtection = "ভেরিফাইড প্রোটেকশন"
    override val profileProMember = "PRO সদস্য"
    override val profilePersonalDetails = "ব্যক্তিগত তথ্য"
    override val profileMemberSincePrefix = "সদস্য: "
    override val profileNameInput = "পূর্ণ নাম (Name)"
    override val profileEmailInput = "ইমেইল অ্যাড্রেস (Email)"
    override val profilePhoneInput = "মোবাইল নম্বর (Phone)"
    override val profileBioInput = "ফোকাস লক্ষ্য / বায়ো (Bio)"
    override val profileSaveChanges = "পরিবর্তন সংরক্ষণ করুন"
    override val profileLabelName = "নাম"
    override val profileLabelEmail = "ইমেইল"
    override val profileLabelPhone = "মোবাইল"
    override val profileLabelBio = "ফোকাস লক্ষ্য"
    override val profilePerformanceSummary = "ফোকাস পারফরম্যান্স সামারি"
    override val profileCleanStreak = "ক্লিন স্ট্রিক"
    override val profileTodayBlocking = "আজকের ব্লকিং"
    override val profileSavedTime = "বাঁচানো সময়"
    override val profileSettingsNav = "সেটিংস ও পিন কনফিগারেশন"
    override val profileAccountStatus = "অ্যাকাউন্ট স্ট্যাটাস"
    override val profileCloudSyncActive = "ক্লাউড সিঙ্ক ও ব্যাকআপ সক্রিয় রয়েছে"
    override val profileLogout = "লগআউট"
    override val profileAuthTitle = "অ্যাকাউন্টে প্রবেশ করুন"
    override val profileLoginTab = "লগইন"
    override val profileRegisterTab = "নতুন অ্যাকাউন্ট"
    override val profilePasswordInput = "পাসওয়ার্ড (Password)"
    override val profileLoginSubmit = "লগইন সম্পন্ন করুন"
    override val profileRegisterSubmit = "অ্যাকাউন্ট তৈরি করুন"
    
    override val profileSecuritySectionTitle = "সিকিউরিটি ও প্রাইভেসি"
    override val profileProtectedActivityTitle = "সুরক্ষিত অ্যাক্টিভিটি (Protected Activity)"
    override val profileProtectedActivityDesc = "সংবেদনশীল ও রেস্ট্রিক্টেড অ্যাক্টিভিটি হিস্ট্রি দেখার জন্য পিন ভেরিফিকেশন প্রয়োজন"
    override val profileProtectedActivityUnlock = "পিন দিয়ে আনলক করুন"
    override val profileProtectedActivityLocked = "লক করা রয়েছে • দেখার জন্য পিন দিয়ে আনলক করুন"
    override val profileProtectedActivityEmpty = "কোনো সংবেদনশীল অ্যাক্টিভিটি হিস্ট্রি নেই"
    override val profileProtectedActivityLockAgain = "পুনরায় লক করুন"
    override val profileProtectedActivityClear = "সব মুছে ফেলুন"
    
    override val routineCreateTitle = "নতুন রুটিন"
    override val routineCreateSubtitle = "একটি নতুন ফোকাস রুটিন তৈরি করুন"
    override val routineNameLabel = "রুটিনের নাম"
    override val routineNameHint = "যেমন: Morning Focus"
    override val routineColorLabel = "রঙ নির্বাচন করুন"
    override val routineTimeLabel = "সময় নির্ধারণ করুন"
    override val routineTimeDesc = "রুটিনটি কখন শুরু এবং শেষ হবে তা নির্বাচন করুন"
    override val routineStartTime = "শুরুর সময় (Start Time)"
    override val routineEndTime = "শেষ সময় (End Time)"
    override val routineProtectedTime = "মোট সুরক্ষিত সময়:"
    override val routineProtectedTimeDesc = "(ফোকাসড সময়)"
    override val routineQuickPreset = "দ্রুত প্রিসেট নির্বাচন:"
    override val routineRepeatDays = "রিপিট দিনসমূহ নির্বাচন করুন"
    override val routineRepeatDaysDesc = "সপ্তাহের কোন কোন দিন এই রুটিনটি স্বয়ংক্রিয়ভাবে কার্যকর হবে"
    override val routineAllDays = "সব দিন (7 Days)"
    override val routineWeekdays = "সোম–শুক্র (Weekdays)"
    override val routineBlockingFilter = "ব্লকিং ও ডিস্ট্র্যাকশন ফিল্টার"
    override val routineBlockingFilterDesc = "এই রুটিন চলার সময় কোন কোন অ্যাপ ও কন্টেন্ট ব্লক থাকবে"
    override val routineTargetCategory = "টার্গেট অ্যাপ ক্যাটেগরি:"
    override val routineShortsBlocking = "Shorts & Reels ব্লকিং"
    override val routineShortsBlockingDesc = "ইউটিউব ও ইন্সটাগ্রামের শর্ট ভিডিও স্বয়ংক্রিয়ভাবে ব্লক থাকবে"
    override val routineWebBlocking = "ওয়েবসাইট ও ব্রাউজার ব্লকার"
    override val routineWebBlockingDesc = "ব্লকলিস্টে থাকা সমস্ত ক্ষতিকর ও সোশ্যাল ওয়েবসাইট ফিল্টার হবে"
    override val routineStrictMode = "কঠোর মোড (Strict Mode)"
    override val routineStrictModeDesc = "রুটিন চলাকালীন কোনো বাইপাস বা বন্ধ করার সুযোগ থাকবে না"
    override val routineSummaryTitle = "রুটিন সারসংক্ষেপ ও চূড়ান্ত পর্যালোচনা"
    override val routineSummaryDesc = "নিচের তথ্যগুলো নিশ্চিত করে রুটিনটি সংরক্ষণ করুন"
    override val routineActiveDays = "সক্রিয় দিন:"
    override val routineBlockedApps = "ব্লকড অ্যাপস:"
    override val routineCategoryCount = "টি ক্যাটাগরি"
    override val routineCustomFilter = "উভয়ই ব্লকড/কাস্টম ফিল্টার"
    override val routineProtectionMode = "সুরক্ষা মোড:"
    override val routineStrictLabel = "🔒 Strict Mode (বাইপাস নিষিদ্ধ)"
    override val routineStandardLabel = "স্ট্যান্ডার্ড"
    override val routineEditSave = "পরিবর্তন করুন (Edit)"
    
    override val routineDetailTimePeriod = "নির্ধারিত সময়কাল"
    override val routineDetailRepeatDays = "রিপিট দিনসমূহ:"
    override val routineDetailTotalDuration = "মোট ব্যাপ্তি:"
    override val routineDetailBlockRules = "ব্লকিং ও সুরক্ষা নিয়মাবলী"
    override val routineDetailTargetApps = "টার্গেট অ্যাপস:"
    override val routineDetailShortsBlock = "YouTube Shorts ও Instagram Reels ব্লক থাকবে"
    override val routineDetailWebBlock = "কাস্টম ব্রাউজার ওয়েবসাইট ফিল্টার সক্রিয়"
    override val routineDetailStrictMode = "কঠোর মোড (Strict Mode) — বাইপাস প্রতিরোধ চালু"
    override val routineDetailDelete = "মুছুন"
    override val routineDetailPause = "পজ করুন"
    override val routineDetailActivate = "সক্রিয় করুন"
    
    override val badgesTitle = "ডিসিপ্লিন ব্যাজসমূহ"
    override val badgesOkButton = "ঠিক আছে"
    
    override val permDialogTitle = "প্রয়োজনীয় পারমিশন সেটআপ"
    override val permDialogDesc = "Focus Lock সক্রিয় করতে এই পারমিশনগুলো প্রয়োজন"
    override val permAccDesc = "আসক্তির অ্যাপস, রিলস/শর্টস এবং ক্ষতিকর ওয়েবসাইট রিয়েল-টাইমে সনাক্ত ও তৎক্ষণাৎ ব্লক করার জন্য অপরিহার্য।"
    override val permUsageDesc = "অ্যান্ড্রয়েড ব্যাকগ্রাউন্ডে কোন ডিসট্র্যাক্টিং অ্যাপ খোলা হচ্ছে তা পর্যবেক্ষণ করতে।"
    override val permOverlayDesc = "ব্লকড অ্যাপ বা সাইট খোলার সাথে সাথে Focus Shield-এর ফুল-স্ক্রিন ব্লক ওভারলে দেখাতে।"
    override val permNotifDesc = "লক চলাকালীন রিয়েল-টাইম টাইম কাউন্টডাউন এবং নিরাপত্তা বার্তা দেখতে।"
    override val permRecheck = "পুনরায় যাচাই করুন"
    override val permContinue = "সামনে এগিয়ে যান"
    
    override fun formatMinutesLong(minutes: Int): String {
        if (minutes <= 0) return "সীমা নেই (০ মি.)"
        val hours = minutes / 60
        val remMins = minutes % 60
        return when {
            hours > 0 && remMins > 0 -> "$hours ঘণ্টা $remMins মিনিট"
            hours > 0 -> "$hours ঘণ্টা"
            else -> "$minutes মিনিট"
        }
    }
    
    override fun formatMinutesShort(minutes: Int): String {
        if (minutes <= 0) return "০মি."
        val hours = minutes / 60
        val remMins = minutes % 60
        return when {
            hours > 0 && remMins > 0 -> "${hours}ঘ. ${remMins}মি."
            hours > 0 -> "${hours} ঘণ্টা"
            else -> "${minutes} মি."
        }
    }
    
    override val timeSliderDailyBudget = "২৪ ঘণ্টার দৈনিক বাজেট"
    override val timeSliderTodayUsage = "আজকের ব্যবহার"
    override val timeSliderStrictLock = "কঠোর লক"
    override val timeSliderQuickPresets = "কুইক প্রিসেট বাটনস"
    override val timeSliderStrictLimitTitle = "কঠোর লক (Strict Limit)"
    override val timeSliderStrictLimitDesc = "২৪ ঘণ্টার মধ্যে সীমা শেষ হলে পিন ছাড়া আনলক হবে না"
    override val timeSliderDelete = "মুছুন"
    override val timeSliderCancel = "বাতিল"
    override val timeSliderSave = "লিমিট সংরক্ষণ করুন"

    // Notification & Alerts
    override val notificationQuickTitle = "নোটিফিকেশন ও অ্যালার্ট"
    override val notificationGranted = "অনুমতি সক্রিয় • অ্যালার্ট চালু"
    override val notificationNotGranted = "অনুমতি দেওয়া হয়নি"
    override val notificationSheetTitle = "নোটিফিকেশন ও অ্যালার্ট কন্ট্রোল"
    override val notificationSheetSubtitle = "কোন ধরণের সতর্কতা ও আপডেট পেতে চান তা নিয়ন্ত্রণ করুন"
    override val notificationPermCardTitle = "সিস্টেম নোটিফিকেশন অনুমতি"
    override val notificationPermCardDesc = "অ্যাপ ও ওয়েবসাইট ব্লক করার পর তৎক্ষণাৎ সতর্কতা পাঠাতে প্রয়োজন"
    override val notificationGrantButton = "অনুমতি দিন"
    override val notificationGrantedChip = "সক্রিয় (Active)"
    override val notificationToggleBlocking = "ব্লকিং সতর্কতা অ্যালার্ট"
    override val notificationToggleBlockingDesc = "নিষিদ্ধ অ্যাপ বা শর্টস প্রতিহত করার সাথে সাথে নোটিফিকেশন পাবেন"
    override val notificationToggleTimer = "ফোকাস টাইমার ও রুটিন আপডেট"
    override val notificationToggleTimerDesc = "টাইমার চলাকালীন রিয়েল-টাইম অবশিষ্ট সময় ও সেশন সমাপ্তি বার্তা"
    override val notificationToggleSecurity = "নিরাপত্তা ও পিন সতর্কতা"
    override val notificationToggleSecurityDesc = "ভুল পিন চেষ্টা, জরুরি আনলক ও সেটিংস পরিবর্তনের অ্যালার্ট"
    override val notificationToggleReminders = "দৈনিক স্ট্রিক ও রিমাইন্ডার"
    override val notificationToggleRemindersDesc = "প্রতিদিনের ফোকাস গোল ও নিয়মানুবর্তিতার উৎসাহব্যঞ্জক বার্তা"
    override val notificationTestButton = "টেস্ট নোটিফিকেশন পাঠান"
    override val notificationTestSent = "টেস্ট নোটিফিকেশন সফলভাবে পাঠানো হয়েছে! নোটিফিকেশন বার চেক করুন।"
    override val notificationAlertsTitle = "নিরাপত্তা ও ব্লকিং অ্যালার্ট"
    override val notificationAlertsEmpty = "কোনো নতুন নোটিফিকেশন নেই"
    override val notificationMarkAllRead = "সব পঠিত হিসেবে চিহ্নিত করুন"
    override val notificationClearAll = "সব অ্যালার্ট মুছুন"
}

object EnglishStrings : AppStrings {
    override val securityTitle = "Settings & Controls"
    override val securitySubtitle = "App theme, security PIN & system permissions"
    
    override val themeCardTitle = "App Theme"
    override val themeDarkActive = "Dark mode active"
    override val themeLightActive = "Light mode active"
    override val themeSystemActive = "System default"
    override val themeDark = "Dark"
    override val themeLight = "Light"
    override val themeSystem = "System default"
    override val themeSheetTitle = "Select App Theme"
    override val themeSheetSubtitle = "Choose your preferred display theme"
    override val themeLightDesc = "Bright and modern minimalist clean interface"
    override val themeDarkDesc = "Dark theme background and battery-friendly interface"
    override val themeSystemDesc = "Automatically follows system display settings"

    override val languageCardTitle = "App Language"
    override val languageSheetTitle = "Select App Language"
    override val languageSheetSubtitle = "Change complete app interface language"
    
    override val pinCardTitle = "Security PIN Setup"
    override val pinCardSubtitle = "Anti-uninstall & force close protection"
    override val pinCreateNew = "Create New PIN"
    override val pinChange = "Change PIN"
    override val pinReset = "Reset PIN"
    override val pinForgot = "Forgot PIN?"

    override val pinConfigured = "•••• (Secure 4-digit active)"
    override val pinNotConfigured = "PIN not set"
    override val pinChangeButton = "Change PIN"
    override val pinCreateButton = "Create PIN"
    override val pinResetButton = "Reset"
    override val pinForgotHint = "Forgot PIN? Recover via email"
    override val pinVerifyCurrentTitle = "Verify Current PIN"
    override val pinVerifyCurrentSubtitle = "Please enter your current 4-digit PIN to proceed"
    override val pinManageOptionsTitle = "Security PIN Management"
    override val pinOptionChangeTitle = "Change PIN (Reset / Change PIN)"
    override val pinOptionChangeDesc = "Set a new secret 4-digit security PIN"
    override val pinOptionDeleteTitle = "Delete PIN (Remove Protection)"
    override val pinOptionDeleteDesc = "Remove security PIN protection from settings"
    override val pinDeleteConfirmTitle = "Delete Security PIN?"
    override val pinDeleteConfirmMessage = "Are you sure you want to delete your security PIN? PIN verification will no longer be required."
    override val pinDeleteConfirmButton = "Delete"
    override val pinEnterNewTitle = "Enter New 4-digit PIN"
    override val pinEnterNewSubtitle = "Choose a secure 4-digit PIN"
    override val pinConfirmNewTitle = "Confirm New PIN"
    override val pinConfirmNewSubtitle = "Re-enter the same 4-digit PIN to confirm"
    override val pinMismatchError = "PINs do not match! Please try again from the beginning."
    override val pinChangedSuccess = "Security PIN changed and saved successfully 🔒"
    override val pinDeletedSuccess = "Security PIN removed successfully 🗑️"
    override val pinCreatedSuccess = "New Security PIN created and saved successfully 🔒"

    override val permCardTitle = "System Permissions"
    override val permCardSubtitle = "The following permissions are required for the app to function properly"
    override val permGrantAll = "Grant All"
    override val permGranted = "Granted"
    override val permGrant = "Grant"

    override val forgotPinDialogTitle = "Emergency PIN Recovery"
    override fun forgotPinDialogDesc(email: String) = "A verification code has been sent to your registered email ($email). You can create and reset your PIN directly now."
    override val forgotPinCancel = "Cancel"
    override val forgotPinConfirm = "Reset PIN"
    
    override fun getDynamicGreeting(userName: String, hour: Int): String {
        val cleanName = userName
            .replace("(Boss)", "")
            .replace("Boss", "")
            .replace("(বস)", "")
            .replace("বস", "")
            .replace("(", "")
            .replace(")", "")
            .trim()

        val periodGreeting = when (hour) {
            in 5..11 -> "Good morning"
            in 12..15 -> "Good noon"
            in 16..17 -> "Good afternoon"
            in 18..20 -> "Good evening"
            else -> "Good night"
        }

        return if (cleanName.isNotBlank()) "$periodGreeting, $cleanName" else periodGreeting
    }

    override val homeGreeting: String
        get() = getDynamicGreeting("")

    override val onboardingWelcomeTitle = "Welcome to Focus Shield! 🛡️"
    override val onboardingWelcomeSubtitle = "To personalize your focus journey, please provide your name and religion:"
    override val onboardingNameLabel = "Your Name"
    override val onboardingNamePlaceholder = "e.g. Tanvir Ahmed"
    override val onboardingReligionLabel = "Your Religion"
    override val onboardingSubmitButton = "Get Started 🚀"
    override val onboardingNameRequiredError = "Please enter your name"
    override val profileLabelReligion = "Religion"
    override val homeSavedTimePrefix = "Today I saved you "
    override val homeMetricsTitle = "Today's Metrics"
    override val homeMetricBlocked = "Blocked Attempts"
    override val homeMetricSaved = "Saved Time"
    override val homeMetricSessions = "Focus Sessions"
    override val homeQuickActionsTitle = "Quick Actions"
    
    override val quickActionFocusLock = "Focus Lock"
    override val quickActionFocusLockActive = "Focus Lock Active 🔒"
    override val quickActionFocusLockSetup = "Setup Real Focus Lock"
    override val quickActionScreenLimit = "Screen Time Limit"
    override val quickActionScreenLimitDesc = "Set daily limit"
    override val quickActionSchedule = "Schedule"
    override val quickActionScheduleDesc = "Auto block by routine"
    override val quickActionOneTime = "One-time Block"
    override val quickActionOneTimeActive = "Instant block active"
    override val quickActionOneTimeDesc = "Block apps & sites"
    override val toastInstantBlockApplied = "Instant block applied"
    override val toastBlockRemoved = "Block removed"
    
    override val homeRecentActivity = "Recent Activity"
    override val homeRecentClear = "Clear"
    override val homeRecentEmpty = "No recent activity"
    
    override val quoteBody = "“A moment of self-control today builds the foundation of success for tomorrow.”"
    override val quoteAuthor = "— Self-improvement & Focus Thought"
    
    override val streakClean = "Clean Streak"
    override val streakMsg = "Great discipline! You are on the right track."
    override val streakBadges = "Badges"
    
    override val radarProtected = "PROTECTED"
    override val radarPaused = "PAUSED"
    override val radarShieldActive = "Shield Active"
    override val radarShieldPaused = "Shield Paused"
    override val radarShieldActiveDesc = "All addictive apps and shorts are blocked in real-time."
    override val radarShieldPausedDesc = "Protection is temporarily paused."
    
    override val analyticsTitle = "Discipline Analytics"
    override val analyticsSubtitle = "Detailed statistics of your time and digital restraint"
    override val analyticsWeekly = "Weekly"
    override val analyticsMonthly = "Monthly"
    override val analyticsWeeklySaved = "Weekly Total Savings"
    override val analyticsMonthlySaved = "Monthly Total Savings"
    override val analyticsWeeklyTrend = "+23% from last week"
    override val analyticsMonthlyTrend = "+31% from last month"
    override val analyticsTotalBlocked = "Total Blocked Attempts"
    override val analyticsMostBlocked = "Peak: 10 PM - 12 AM"
    override val analyticsTopTriggers = "Top Addiction Triggers (Details)"
    override fun analyticsReportToast(type: String) = "$type report generated successfully!"
    override val analyticsDownloadWeekly = "Download Weekly Recap"
    override val analyticsDownloadMonthly = "Download Monthly Recap"
    
    override val chartWeeklyTrend = "Weekly Time Saving Trend"
    override val chartMonthlyTrend = "Monthly Consistency Trend"
    override val chartWeeklyDesc = "Real discipline tracking over the last 7 days"
    override val chartMonthlyDesc = "4 weeks progress and savings analysis"
    override fun chartSavedHours(day: String, hours: String, isWeekly: Boolean) = "$day: $hours hours saved"
    override fun chartBlockedAttempts(attempts: Int) = "$attempts blocks"
    override val chartCategoryRatioTitle = "Addiction Prevention Ratio"
    override val chartCategoryRatioDesc = "Category based blocked distribution"
    override val chartBlockedPrefix = "Blocked"
    override fun chartCategoryDetails(count: Int, percentage: Int) = "$count times ($percentage%)"
    override val chartDisciplineScoreTitle = "Discipline Score: 92% (Excellent)"
    override val chartDisciplineScoreDesc = "You have met most of your goals for the last 7 days!"
    
    override val chartInsightWeeklyFallback = "You saved a maximum of 4.1 hours from addiction on Saturday! 🎯"
    override val chartInsightMonthlyFallback = "In Week 4, you saved an average of 4.3 hours daily! 🚀"
    
    override val profileTitle = "My Profile"
    override val profileSubtitle = "Account details & personalization"
    override val profileEdit = "Edit"
    override val profileSave = "Save"
    override val profileUploadPhoto = "Upload Photo"
    override val profileChangePhoto = "Change Photo"
    override val profileRemovePhoto = "Remove Photo"
    override val profileVerifiedProtection = "Verified Protection"
    override val profileProMember = "PRO Member"
    override val profilePersonalDetails = "Personal Details"
    override val profileMemberSincePrefix = "Member since: "
    override val profileNameInput = "Full Name"
    override val profileEmailInput = "Email Address"
    override val profilePhoneInput = "Phone Number"
    override val profileBioInput = "Focus Goal / Bio"
    override val profileSaveChanges = "Save Changes"
    override val profileLabelName = "Name"
    override val profileLabelEmail = "Email"
    override val profileLabelPhone = "Phone"
    override val profileLabelBio = "Focus Goal"
    override val profilePerformanceSummary = "Focus Performance Summary"
    override val profileCleanStreak = "Clean Streak"
    override val profileTodayBlocking = "Today's Blocking"
    override val profileSavedTime = "Saved Time"
    override val profileSettingsNav = "Settings & PIN Config"
    override val profileAccountStatus = "Account Status"
    override val profileCloudSyncActive = "Cloud Sync & Backup is active"
    override val profileLogout = "Logout"
    override val profileAuthTitle = "Login to Account"
    override val profileLoginTab = "Login"
    override val profileRegisterTab = "New Account"
    override val profilePasswordInput = "Password"
    override val profileLoginSubmit = "Login Now"
    override val profileRegisterSubmit = "Create Account"
    
    override val profileSecuritySectionTitle = "Security & Privacy"
    override val profileProtectedActivityTitle = "Protected Activity"
    override val profileProtectedActivityDesc = "PIN verification required to view sensitive and restricted activity history"
    override val profileProtectedActivityUnlock = "Unlock with PIN"
    override val profileProtectedActivityLocked = "Locked • Authenticate with PIN to view details"
    override val profileProtectedActivityEmpty = "No sensitive activity recorded"
    override val profileProtectedActivityLockAgain = "Lock Again"
    override val profileProtectedActivityClear = "Clear All"
    
    override val routineCreateTitle = "New Routine"
    override val routineCreateSubtitle = "Create a new focus routine"
    override val routineNameLabel = "Routine Name"
    override val routineNameHint = "e.g. Morning Focus"
    override val routineColorLabel = "Select Color"
    override val routineTimeLabel = "Set Time"
    override val routineTimeDesc = "Select when the routine starts and ends"
    override val routineStartTime = "Start Time"
    override val routineEndTime = "End Time"
    override val routineProtectedTime = "Total Protected Time:"
    override val routineProtectedTimeDesc = "(Focused time)"
    override val routineQuickPreset = "Quick Preset Selection:"
    override val routineRepeatDays = "Select Repeat Days"
    override val routineRepeatDaysDesc = "Select the days this routine will automatically activate"
    override val routineAllDays = "All Days (7 Days)"
    override val routineWeekdays = "Mon-Fri (Weekdays)"
    override val routineBlockingFilter = "Blocking & Distraction Filter"
    override val routineBlockingFilterDesc = "Apps and content to block during this routine"
    override val routineTargetCategory = "Target App Category:"
    override val routineShortsBlocking = "Shorts & Reels Blocking"
    override val routineShortsBlockingDesc = "YouTube and Instagram shorts will be automatically blocked"
    override val routineWebBlocking = "Website & Browser Blocker"
    override val routineWebBlockingDesc = "Harmful and social websites on the blocklist will be filtered"
    override val routineStrictMode = "Strict Mode"
    override val routineStrictModeDesc = "No bypass or stopping allowed during the routine"
    override val routineSummaryTitle = "Routine Summary & Final Review"
    override val routineSummaryDesc = "Confirm the information below to save the routine"
    override val routineActiveDays = "Active Days:"
    override val routineBlockedApps = "Blocked Apps:"
    override val routineCategoryCount = "Categories"
    override val routineCustomFilter = "Both Blocked / Custom Filter"
    override val routineProtectionMode = "Protection Mode:"
    override val routineStrictLabel = "🔒 Strict Mode (No Bypass)"
    override val routineStandardLabel = "Standard"
    override val routineEditSave = "Edit"
    
    override val routineDetailTimePeriod = "Scheduled Time"
    override val routineDetailRepeatDays = "Repeat Days:"
    override val routineDetailTotalDuration = "Total Duration:"
    override val routineDetailBlockRules = "Blocking & Protection Rules"
    override val routineDetailTargetApps = "Target Apps:"
    override val routineDetailShortsBlock = "YouTube Shorts & Instagram Reels are blocked"
    override val routineDetailWebBlock = "Custom Browser Website Filter is active"
    override val routineDetailStrictMode = "Strict Mode — Bypass protection enabled"
    override val routineDetailDelete = "Delete"
    override val routineDetailPause = "Pause"
    override val routineDetailActivate = "Activate"
    
    override val badgesTitle = "Discipline Badges"
    override val badgesOkButton = "OK"
    
    override val permDialogTitle = "Required Permission Setup"
    override val permDialogDesc = "These permissions are required to activate Focus Lock"
    override val permAccDesc = "Essential for real-time detection and immediate blocking of addictive apps, reels/shorts, and harmful websites."
    override val permUsageDesc = "To monitor which distracting apps are opened in the Android background."
    override val permOverlayDesc = "To show the Focus Shield full-screen block overlay immediately when opening a blocked app or site."
    override val permNotifDesc = "To view real-time countdown and security messages during the lock."
    override val permRecheck = "Recheck"
    override val permContinue = "Continue"
    
    override fun formatMinutesLong(minutes: Int): String {
        if (minutes <= 0) return "No Limit (0 min)"
        val hours = minutes / 60
        val remMins = minutes % 60
        return when {
            hours > 0 && remMins > 0 -> "$hours hr $remMins min"
            hours > 0 -> "$hours hr"
            else -> "$minutes min"
        }
    }
    
    override fun formatMinutesShort(minutes: Int): String {
        if (minutes <= 0) return "0m"
        val hours = minutes / 60
        val remMins = minutes % 60
        return when {
            hours > 0 && remMins > 0 -> "${hours}h ${remMins}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }
    
    override val timeSliderDailyBudget = "24-Hour Daily Budget"
    override val timeSliderTodayUsage = "Today's Usage"
    override val timeSliderStrictLock = "Strict Lock"
    override val timeSliderQuickPresets = "Quick Presets"
    override val timeSliderStrictLimitTitle = "Strict Lock (Strict Limit)"
    override val timeSliderStrictLimitDesc = "Cannot be unlocked without PIN if the limit is reached within 24 hours"
    override val timeSliderDelete = "Delete"
    override val timeSliderCancel = "Cancel"
    override val timeSliderSave = "Save Limit"

    // Notification & Alerts
    override val notificationQuickTitle = "Notifications & Alerts"
    override val notificationGranted = "Permission active • Alerts enabled"
    override val notificationNotGranted = "Permission not granted"
    override val notificationSheetTitle = "Notification & Alert Controls"
    override val notificationSheetSubtitle = "Control what alerts and updates you receive"
    override val notificationPermCardTitle = "System Notification Permission"
    override val notificationPermCardDesc = "Required to send instant alerts when apps or sites are blocked"
    override val notificationGrantButton = "Grant Permission"
    override val notificationGrantedChip = "Active"
    override val notificationToggleBlocking = "Blocking Alert Notifications"
    override val notificationToggleBlockingDesc = "Receive notifications immediately when restricted apps or shorts are blocked"
    override val notificationToggleTimer = "Focus Timer & Routine Updates"
    override val notificationToggleTimerDesc = "Real-time remaining time and session completion updates"
    override val notificationToggleSecurity = "Security & PIN Alerts"
    override val notificationToggleSecurityDesc = "Alerts on failed PIN attempts, emergency unlocks, and settings changes"
    override val notificationToggleReminders = "Daily Streak & Reminders"
    override val notificationToggleRemindersDesc = "Daily focus goals and discipline encouragement messages"
    override val notificationTestButton = "Send Test Notification"
    override val notificationTestSent = "Test notification sent successfully! Check your notification bar."
    override val notificationAlertsTitle = "Security & Blocking Alerts"
    override val notificationAlertsEmpty = "No new notifications"
    override val notificationMarkAllRead = "Mark all as read"
    override val notificationClearAll = "Clear all alerts"
}

fun getStrings(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.BENGALI -> BengaliStrings
        AppLanguage.ENGLISH -> EnglishStrings
    }
}
