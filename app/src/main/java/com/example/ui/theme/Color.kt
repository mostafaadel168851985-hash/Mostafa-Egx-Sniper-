package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object TerminalTheme {
    var isDark by mutableStateOf(true)

    // Background: Soothing deep obsidian slate in dark mode, soft off-white in light mode
    val background: Color
        get() = if (isDark) Color(0xFF0B101B) else Color(0xFFF8FAFC)

    // Primary Card Surface: Rich modern midnight navy in dark mode, crisp clean white in light mode
    val surface: Color
        get() = if (isDark) Color(0xFF131B2E) else Color(0xFFFFFFFF)

    // Secondary Surface & Inputs
    val surfaceVariant: Color
        get() = if (isDark) Color(0xFF19233A) else Color(0xFFF1F5F9)

    // Elevated Cards
    val surfaceLightCard: Color
        get() = if (isDark) Color(0xFF1E2B47) else Color(0xFFFFFFFF)

    // Outlines & Dividers: Subtle soft slate
    val outline: Color
        get() = if (isDark) Color(0xFF263554) else Color(0xFFE2E8F0)

    // Vibrant & Eye-friendly Emerald Green (Gains & Buy signals)
    val bullishGreen: Color
        get() = if (isDark) Color(0xFF10B981) else Color(0xFF059669)

    val bullishGreenLight: Color
        get() = if (isDark) Color(0xFF34D399) else Color(0xFF10B981)

    val bullishGreenBg: Color
        get() = if (isDark) Color(0x2410B981) else Color(0x1F059669)

    // Vibrant & Eye-friendly Rose Coral (Losses & Warnings - not harsh on eyes)
    val bearishRed: Color
        get() = if (isDark) Color(0xFFF43F5E) else Color(0xFFE11D48)

    val bearishRedLight: Color
        get() = if (isDark) Color(0xFFFB7185) else Color(0xFFF43F5E)

    val bearishRedBg: Color
        get() = if (isDark) Color(0x24F43F5E) else Color(0x1FE11D48)

    // Vibrant Golden Amber (Caution, Reversals, Targets)
    val goldenAmber: Color
        get() = if (isDark) Color(0xFFF59E0B) else Color(0xFFD97706)

    val goldenAmberLight: Color
        get() = if (isDark) Color(0xFFFBBF24) else Color(0xFFF59E0B)

    val goldenAmberBg: Color
        get() = if (isDark) Color(0x24F59E0B) else Color(0x1FD97706)

    // Electric Cyan & Sky Blue (Accents, Symbols, Indicators)
    val accentCyan: Color
        get() = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)

    val accentPurple: Color
        get() = if (isDark) Color(0xFFA855F7) else Color(0xFF9333EA)

    val accentIndigo: Color
        get() = if (isDark) Color(0xFF6366F1) else Color(0xFF4F46E5)

    // High Legibility Arabic Typography
    val textPrimary: Color
        get() = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)

    val textSecondary: Color
        get() = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

    val textMuted: Color
        get() = if (isDark) Color(0xFF64748B) else Color(0xFF64748B)
}

val BackgroundDark: Color get() = TerminalTheme.background
val SurfaceDark: Color get() = TerminalTheme.surface
val SurfaceVariantDark: Color get() = TerminalTheme.surfaceVariant
val SurfaceLightCard: Color get() = TerminalTheme.surfaceLightCard
val OutlineDark: Color get() = TerminalTheme.outline

val BullishGreen: Color get() = TerminalTheme.bullishGreen
val BullishGreenLight: Color get() = TerminalTheme.bullishGreenLight
val BullishGreenBg: Color get() = TerminalTheme.bullishGreenBg

val BearishRed: Color get() = TerminalTheme.bearishRed
val BearishRedLight: Color get() = TerminalTheme.bearishRedLight
val BearishRedBg: Color get() = TerminalTheme.bearishRedBg

val GoldenAmber: Color get() = TerminalTheme.goldenAmber
val GoldenAmberLight: Color get() = TerminalTheme.goldenAmberLight
val GoldenAmberBg: Color get() = TerminalTheme.goldenAmberBg

val AccentCyan: Color get() = TerminalTheme.accentCyan
val AccentPurple: Color get() = TerminalTheme.accentPurple
val AccentIndigo: Color get() = TerminalTheme.accentIndigo

val TextPrimary: Color get() = TerminalTheme.textPrimary
val TextSecondary: Color get() = TerminalTheme.textSecondary
val TextMuted: Color get() = TerminalTheme.textMuted
