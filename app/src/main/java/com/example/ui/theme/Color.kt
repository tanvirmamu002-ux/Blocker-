package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// Brand Colors (User Custom Theme: Warm Cream #FFF0D4 & Amber Orange #FF8800 / #F08205)
// =========================================================================
val ThemeOrangePrimary = Color(0xFFFF8800)       // Main Primary Brand Orange
val ThemeOrangeDark = Color(0xFFF08205)          // Deeper Action Amber Orange
val ThemeOrangeLight = Color(0xFFFFA033)         // Soft Highlight Amber
val ThemeOrangeDim = Color(0x24FF8800)           // Subtle 14% Tint

// Calming Accents (Preserved for On/Off toggles, Focus and Metrics)
val EmeraldSuccess = Color(0xFF10B981)           // Active Green for ON states
val EmeraldSuccessDim = Color(0x2610B981)
val CoralAlert = Color(0xFFEF4444)               // Alert Red for OFF / Block states
val CoralAlertDim = Color(0x26EF4444)

val CalmTeal = Color(0xFF0D9488)
val CalmTealDim = Color(0x220D9488)
val LavenderFocus = Color(0xFF8B5CF6)
val LavenderFocusDim = Color(0x228B5CF6)

// Compatibility aliases
val SoftCoral = CoralAlert
val WarmAmber = ThemeOrangeDark
val CalmBlue = ThemeOrangeDark
val SageGreen = EmeraldSuccess

// =========================================================================
// Light Theme Palette (Claude.ai Inspired: Refined Warm Sand/Cream Paper #F7F4EE + White Cards)
// =========================================================================
val ClaudeWarmPaperLightBg = Color(0xFFF7F4EE)    // Subtle, elegant warm paper background (Claude.ai style)
val ClaudeWarmCardBg = Color(0xFFFFFFFF)          // Pure White Card Surface for natural contrast
val ClaudeWarmCardElevated = Color(0xFFEFECE5)    // Soft warm oat elevated container
val ClaudeWarmCardBorder = Color(0xFFE8E2D8)      // Subtle warm sand border
val ClaudeWarmCardBorderLight = Color(0xFFF0EBE2)

// Text Colors (High Contrast Deep Crisp Black/Espresso for maximum readability)
val WarmTextPrimary = Color(0xFF1B1917)          // Crisp Deep Obsidian Black
val WarmTextSecondary = Color(0xFF574F46)        // Warm Charcoal Grey
val WarmTextMuted = Color(0xFF8C8276)            // Muted Warm Grey
val WarmDivider = Color(0xFFE8E2D8)

// =========================================================================
// Dark Theme Palette (Deep Warm Charcoal Slate + Amber Orange Highlights)
// =========================================================================
val WarmDarkBg = Color(0xFF141210)               // Warm Night Charcoal
val WarmDarkCardBg = Color(0xFF1C1916)           // Dark Amber Tint Surface
val WarmDarkCardElevated = Color(0xFF26221E)     // Elevated Dark Surface
val WarmDarkCardBorder = Color(0xFF38312B)       // Subtle Warm Border
val WarmDarkCardBorderLight = Color(0xFF483F37)

val DarkTextPrimary = Color(0xFFF5EFEB)          // Soft Warm White
val DarkTextSecondary = Color(0xFFA89F96)        // Soft Taupe Grey
val DarkTextMuted = Color(0xFF756C64)
val DarkDivider = Color(0xFF332C26)

// Glassmorphic Surface Constants
val GlassDarkSurface = Color(0xDD1C1916)
val GlassDarkBorder = Color(0x24FFFFFF)
val GlassLightSurface = Color(0xF2FFFFFF)
val GlassLightBorder = Color(0x18F08205)         // Subtle Amber Edge Highlight

data class AppColorPalette(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val glassSurface: Color,
    val border: Color,
    val borderLight: Color,
    val borderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val primaryBright: Color,
    val primaryDim: Color,
    val secondary: Color,
    val secondaryDark: Color,
    val secondaryDim: Color,
    val alert: Color,
    val alertDim: Color,
    val warning: Color,
    val warningDim: Color,
    val purple: Color,
    val purpleDim: Color,
    val divider: Color
)

val DarkAppColorPalette = AppColorPalette(
    isDark = true,
    background = WarmDarkBg,
    surface = WarmDarkCardBg,
    surfaceElevated = WarmDarkCardElevated,
    glassSurface = GlassDarkSurface,
    border = WarmDarkCardBorder,
    borderLight = WarmDarkCardBorderLight,
    borderSubtle = GlassDarkBorder,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    primary = ThemeOrangePrimary,
    primaryBright = ThemeOrangeLight,
    primaryDim = ThemeOrangeDim,
    secondary = EmeraldSuccess,                 // Preserved Green for active features
    secondaryDark = Color(0xFF059669),
    secondaryDim = EmeraldSuccessDim,
    alert = CoralAlert,                         // Preserved Red for alert/off
    alertDim = CoralAlertDim,
    warning = ThemeOrangeDark,
    warningDim = ThemeOrangeDim,
    purple = LavenderFocus,
    purpleDim = LavenderFocusDim,
    divider = DarkDivider
)

val LightAppColorPalette = AppColorPalette(
    isDark = false,
    background = ClaudeWarmPaperLightBg,        // #F7F4EE Claude.ai Warm Sand Paper
    surface = ClaudeWarmCardBg,                 // #FFFFFF Crisp White Card Surface
    surfaceElevated = ClaudeWarmCardElevated,   // #EFECE5 Soft Elevated Container
    glassSurface = GlassLightSurface,
    border = ClaudeWarmCardBorder,
    borderLight = ClaudeWarmCardBorderLight,
    borderSubtle = GlassLightBorder,
    textPrimary = WarmTextPrimary,              // Crisp Obsidian Black Text
    textSecondary = WarmTextSecondary,          // Warm Charcoal
    textMuted = WarmTextMuted,                  // Subtle Grey
    primary = ThemeOrangeDark,                  // #F08205 / #FF8800 Primary Action
    primaryBright = ThemeOrangePrimary,         // #FF8800
    primaryDim = ThemeOrangeDim,
    secondary = EmeraldSuccess,                 // Preserved Light Green for ON
    secondaryDark = Color(0xFF059669),
    secondaryDim = EmeraldSuccessDim,
    alert = CoralAlert,                         // Preserved Red for OFF
    alertDim = CoralAlertDim,
    warning = ThemeOrangeDark,
    warningDim = ThemeOrangeDim,
    purple = LavenderFocus,
    purpleDim = LavenderFocusDim,
    divider = WarmDivider
)
