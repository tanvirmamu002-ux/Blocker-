package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.SoftCoral
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.CalmBlue
import com.example.ui.theme.LavenderFocus

enum class NavigationTab(
    val titleBangla: String,
    val titleEnglish: String
) {
    HOME("হোম", "Home"),
    BLOCKER("ব্লকার", "Blocker"),
    SCHEDULE("শিডিউল", "Schedule"),
    ANALYTICS("অ্যানালিটিক্স", "Analytics"),
    SETTINGS("সেটিংস", "Settings")
}

data class CategoryFilter(
    val id: String,
    val titleBangla: String,
    val titleEnglish: String,
    val descBangla: String,
    val descEnglish: String,
    val isEnabled: Boolean = true,
    val iconType: String = "shield"
)

data class BlockedDomain(
    val id: String,
    val domain: String,
    val blockedCount: Int = 0,
    val isCustom: Boolean = true,
    val addedTimeAgo: String = "আজ যুক্ত হয়েছে"
)

data class FocusRoutine(
    val id: String,
    val titleBangla: String,
    val titleEnglish: String,
    val timeRange: String,
    val startTime: String = "09:00 AM",
    val endTime: String = "01:00 PM",
    val durationText: String = "4h",
    val activeDaysBangla: String,
    val activeDaysEnglish: String,
    val targetedAppsBangla: String,
    val targetedAppsEnglish: String,
    val colorHex: String = "#10B981", // Emerald, Purple, Blue, Orange, Pink, Teal
    val iconType: String = "book", // book, briefcase, laptop, person, moon, game, shield
    val blockShorts: Boolean = true,
    val blockWebsites: Boolean = true,
    val isStrict: Boolean = false,
    val isEnabled: Boolean = true,
    val isActiveNow: Boolean = false
)

data class DailyDisciplineStat(
    val dayBangla: String,
    val dayEnglish: String,
    val savedHours: Float,
    val blockedAttempts: Int,
    val isBestDay: Boolean = false
)

data class AddictionTrigger(
    val name: String,
    val blockedCount: Int,
    val percentage: Int,
    val accentColor: Color = SoftCoral
)

data class RecentActivity(
    val id: String,
    val titleBangla: String,
    val titleEnglish: String,
    val timeAgoBangla: String,
    val timeAgoEnglish: String,
    val isSuccess: Boolean = false,
    val iconType: String = "blocked" // "blocked", "session", "shield"
)

data class AccountabilityPartner(
    val name: String = "আরিফ হাসান (ভাই)",
    val email: String = "arif.focus@gmail.com",
    val isConnected: Boolean = true,
    val notifyOnBlockedSite: Boolean = true,
    val notifyOnStrictBypass: Boolean = true,
    val notifyOnUninstall: Boolean = true
)

data class SecurityPermission(
    val id: String,
    val serialNumber: Int,
    val titleBangla: String,
    val titleEnglish: String,
    val descBangla: String,
    val descEnglish: String,
    val isGranted: Boolean,
    val actionLabelBangla: String = "অনুমতি দিন"
)

data class UserAccount(
    val isLoggedIn: Boolean = true,
    val name: String = "John Doe (Boss)",
    val email: String = "focus.guardian@example.com",
    val phone: String = "+৮৮০ ১৭১২-৩৪৫৬৭৮",
    val bio: String = "ফোকাস ও আত্মউন্নয়নের পথে নিয়োজিত",
    val isPremium: Boolean = true,
    val avatarInitials: String = "JD",
    val avatarUri: String? = null,
    val memberSince: String = "জানুয়ারি ২০২৬"
)

data class FocusBadge(
    val id: String,
    val titleBangla: String,
    val titleEnglish: String,
    val descBangla: String,
    val descEnglish: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = true,
    val dateUnlocked: String = "১৫ দিন পূর্বে",
    val dateUnlockedEnglish: String = "15 days ago"
)

enum class AppThemeMode(
    val titleBangla: String,
    val titleEnglish: String,
    val descriptionBangla: String
) {
    DARK("ডার্ক থিম", "Dark Mode", "সাইবারপাঙ্ক ডার্ক ব্যাকগ্রাউন্ড ও নিয়ন গ্লো"),
    LIGHT("লাইট থিম", "Light Mode", "উজ্জ্বল ও আধুনিক ক্লিন মিনিমালিস্ট ইন্টারফেস"),
    SYSTEM("সিস্টেম ডিফল্ট", "System Default", "ডিভাইসের সিস্টেম থিমের সাথে স্বয়ংক্রিয় পরিবর্তন")
}

data class AppScreenTimeLimit(
    val packageName: String,
    val appNameBangla: String,
    val appNameEnglish: String,
    val iconType: String = "generic", // "facebook", "youtube", "instagram", "tiktok", "twitter", "whatsapp", "chrome", "snapchat", "reddit", "netflix", "games", "telegram"
    val limitMinutes: Int = 0, // 0 = no limit, e.g. 30, 60, 120
    val usedMinutesToday: Int = 0, // Minutes used in today's 24-hour cycle (00:00 to 23:59)
    val isEnabled: Boolean = false,
    val isStrict: Boolean = false,
    val category: String = "সোশ্যাল মিডিয়া"
)
