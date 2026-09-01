package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Hind Siliguri (Sans-serif: For Headings, UI Elements, Buttons, Metrics, Chips, Badges, Tabs)
val HindSiliguri = FontFamily(
    Font(resId = R.font.hind_siliguri_regular, weight = FontWeight.Normal),
    Font(resId = R.font.hind_siliguri_medium, weight = FontWeight.Medium),
    Font(resId = R.font.hind_siliguri_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.hind_siliguri_bold, weight = FontWeight.Bold)
)

// Noto Serif Bengali (Serif: Exclusively for Rich Descriptions, Quotes, Wisdom & Reflective Reading)
val NotoSerifBengali = FontFamily(
    Font(resId = R.font.noto_serif_bengali_regular, weight = FontWeight.Normal),
    Font(resId = R.font.noto_serif_bengali_medium, weight = FontWeight.Medium),
    Font(resId = R.font.noto_serif_bengali_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.noto_serif_bengali_bold, weight = FontWeight.Bold)
)

// Material 3 Typography system tailored for maximum legibility and crisp UI hierarchy
val Typography = Typography(
    // Display & Headings -> Hind Siliguri Bold (হেডিং ও বড় সংখ্যার জন্য স্পষ্ট ও আত্মবিশ্বাসী)
    displayLarge = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Bold,
        fontSize = 52.sp,
        lineHeight = 60.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        lineHeight = 48.sp
    ),
    displaySmall = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),

    // Section Titles -> Hind Siliguri SemiBold / Bold (বিভাগীয় শিরোনাম)
    titleLarge = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),

    // Body & Descriptions -> Hind Siliguri (Sans-serif for smooth app-wide readability)
    bodyLarge = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.15.sp
    ),
    bodySmall = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    ),

    // Labels, Badges, Tabs, Buttons -> Hind Siliguri Medium / SemiBold (পরিচ্ছন্ন ও প্রফেশনাল)
    labelLarge = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    ),
    labelSmall = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.2.sp
    )
)

// Dedicated Editorial / Quote & Reflection typography using Noto Serif Bengali
object EditorialTypography {
    val quoteHeadline = TextStyle(
        fontFamily = NotoSerifBengali,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.2.sp
    )
    val quoteBody = TextStyle(
        fontFamily = NotoSerifBengali,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.25.sp
    )
    val quoteAuthor = TextStyle(
        fontFamily = HindSiliguri,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.5.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    )
}
