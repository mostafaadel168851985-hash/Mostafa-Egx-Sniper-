package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object TerminalTheme {
    var isDark by mutableStateOf(true)

    val background: Color
        get() = if (isDark) Color(0xFF090D16) else Color(0xFFF8FAFC)

    val surface: Color
        get() = if (isDark) Color(0xFF111827) else Color(0xFFFFFFFF)

    val surfaceVariant: Color
        get() = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)

    val surfaceLightCard: Color
        get() = if (isDark) Color(0xFF162032) else Color(0xFFFFFFFF)

    val outline: Color
        get() = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)

    val bullishGreen: Color
        get() = if (isDark) Color(0xFF10B981) else Color(0xFF059669)

    val bullishGreenLight: Color
        get() = if (isDark) Color(0xFF34D399) else Color(0xFF10B981)

    val bullishGreenBg: Color
        get() = if (isDark) Color(0x2210B981) else Color(0x1F059669)

    val bearishRed: Color
        get() = if (isDark) Color(0xFFEF4444) else Color(0xFFDC2626)

    val bearishRedLight: Color
        get() = if (isDark) Color(0xFFF87171) else Color(0xFFEF4444)

    val bearishRedBg: Color
        get() = if (isDark) Color(0x22EF4444) else Color(0x1FDC2626)

    val goldenAmber: Color
        get() = if (isDark) Color(0xFFF59E0B) else Color(0xFFD97706)

    val goldenAmberLight: Color
        get() = if (isDark) Color(0xFFFBBF24) else Color(0xFFF59E0B)

    val goldenAmberBg: Color
        get() = if (isDark) Color(0x22F59E0B) else Color(0x1FD97706)

    val accentCyan: Color
        get() = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)

    val accentPurple: Color
        get() = if (isDark) Color(0xFFA855F7) else Color(0xFF9333EA)

    val accentIndigo: Color
        get() = if (isDark) Color(0xFF6366F1) else Color(0xFF4F46E5)

    val textPrimary: Color
        get() = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)

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
