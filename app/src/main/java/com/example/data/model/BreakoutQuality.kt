package com.example.data.model

data class BreakoutQuality(
    val score: Int = 0,
    val grade: String = "C",
    val gradeArabic: String = "اختراق عادي",
    val gradeColorHex: Long = 0xFF94A3B8,
    val closeStrengthPct: Double = 50.0,
    val nearHighPct: Double = 0.0,
    val dayRangePct: Double = 0.0,
    val reasons: List<String> = emptyList()
)

data class CandlestickInfo(
    val nameArabic: String,
    val isBullish: Boolean,
    val strength: Int,
    val description: String
)
