package com.example.data.model

import java.util.Locale

data class StockData(
    val symbol: String,
    val name: String,
    val description: String,
    val price: Double,
    val change: Double,
    val open: Double,
    val high: Double,
    val low: Double,
    val prevClose: Double,
    val volume: Long,
    val avgVolume10d: Long,
    val volumeRatio: Double,
    val dailyTurnover: Double, // in EGP
    val turnoverRatingArabic: String,
    val turnoverScore: Int,

    // Historical Indicators (50 & 200 Days)
    val sma20: Double,
    val sma50: Double,
    val sma200: Double,
    val distanceSma50: Double, // % distance from 50-day SMA
    val distanceSma200: Double, // % distance from 200-day SMA
    val isGoldenCross: Boolean, // SMA50 > SMA200
    val trendShort: String, // "صاعد" or "هابط" (SMA20)
    val trendMedium: String, // "صاعد" or "هابط" (SMA50)
    val trendLong: String, // "صاعد" or "هابط" (SMA200)

    // 52-week High/Low & Long-Term Performance
    val high52: Double,
    val low52: Double,
    val upsideTo52wHigh: Double, // % space to 52-week high
    val perf1m: Double,
    val volatility: Double,

    // Momentum (RSI)
    val rsi: Double,

    // Support and Resistance (Pivot Levels)
    val pp: Double, // Pivot Point
    val s1: Double,
    val s2: Double,
    val r1: Double,
    val r2: Double,

    // Trading Strategy & Ideal Entry / Exit
    val entryMin: Double,
    val entryMax: Double,
    val entryPrice: Double,
    val stopLoss: Double,
    val target1: Double,
    val target2: Double,
    val riskPct: Double,
    val targetPct: Double,
    val riskRewardRatio: Double, // RR
    val smartScore: Int, // 0 - 100
    val confidenceGrade: String, // "A+", "A", "B", "C", "D"
    val confidenceAdvice: String,
    val confidenceColorHex: Long,

    // Candlestick & Breakout Analysis
    val candlePatterns: List<String>,
    val candleStrength: Int,
    val breakoutQuality: BreakoutQuality,

    // Screener Flags
    val isRapidBreakout: Boolean,
    val isCorrectionHunter: Boolean,
    val isSupportBounce: Boolean,
    val isEarlyUptrend: Boolean,
    val isTomorrowPick: Boolean,
    val isShariahCompliant: Boolean = false,
    val indexBelonging: String = "EGX30",
    val screenerReasons: List<String>,
    val sector: String
) {
    val formattedPrice: String
        get() = String.format(Locale.US, "%.3f", price)

    val formattedChange: String
        get() = String.format(Locale.US, "%+.2f%%", change)

    val formattedTurnoverMillion: String
        get() = String.format(Locale.US, "%.1f م.ج", dailyTurnover / 1_000_000.0)

    val entryRangeFormatted: String
        get() = String.format(Locale.US, "%.3f - %.3f", entryMin, entryMax)

    val target1Formatted: String
        get() = String.format(Locale.US, "%.3f", target1)

    val target2Formatted: String
        get() = String.format(Locale.US, "%.3f", target2)

    val stopLossFormatted: String
        get() = String.format(Locale.US, "%.3f", stopLoss)
}
