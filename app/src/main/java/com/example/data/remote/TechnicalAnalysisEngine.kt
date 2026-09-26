package com.example.data.remote

import com.example.data.model.BreakoutQuality
import com.example.data.model.StockData
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object TechnicalAnalysisEngine {

    private val SECTOR_MAP = mapOf(
        "🏦 البنوك" to listOf("CIEB", "COMI", "AAIB", "QNBA", "ALEX", "BID", "CAE", "NBK", "ADIB", "FAIT", "EGBX"),
        "🏗️ العقارات" to listOf("TMGH", "OCDI", "PHDC", "HELI", "DEGC", "MNHD", "DSC", "EMIR", "ORAS", "CCAP", "RORE"),
        "🍔 الأغذية والمشروبات" to listOf("BFR", "EFID", "JUFO", "ORWE", "EDFO", "BIF", "OLFI", "DOMT", "ISMA", "AJWA"),
        "📡 الاتصالات والتكنولوجيا" to listOf("ETEL", "OTMT", "TE", "EMOB", "EGS", "FWRY", "RAYA", "BTFH", "MTIE"),
        "🏭 البتروكيماويات والصناعات" to listOf("ESRS", "MFPC", "SKPC", "ABUK", "EFIC", "EGCH", "MICH", "AMOC", "EKHO", "SWDY", "SIDPEC"),
        "💊 الرعاية الصحية والأدوية" to listOf("PHAR", "ISPH", "RMDA", "MCRO", "SPMD", "AXPH"),
        "🛒 التجارة والخدمات" to listOf("AUTO", "ELSE", "MENA", "CAPI", "PRDC", "MOIL")
    )

    fun detectSector(symbol: String): String {
        val sym = symbol.uppercase()
        for ((sector, symbols) in SECTOR_MAP) {
            if (symbols.any { sym.contains(it) || it.contains(sym) }) {
                return sector
            }
        }
        return "📌 قطاعات أخرى"
    }

    fun calculateVolatility(high: Double, low: Double, close: Double): Double {
        return if (high > 0 && low > 0 && close > 0 && high > low) {
            val vol = ((high - low) / close) * 100.0
            (vol * 100.0).roundToInt() / 100.0
        } else {
            1.5
        }
    }

    fun analyzeTurnover(price: Double, volume: Long, avgVolume: Long): Triple<String, Int, Double> {
        val dailyTurnover = price * volume
        val avgTurnover = if (avgVolume > 0) price * avgVolume else 1.0
        val ratio = dailyTurnover / avgTurnover

        val (rating, score) = when {
            dailyTurnover >= 100_000_000 -> "🔥🔥 سيولة خرافية" to 4
            dailyTurnover >= 50_000_000 -> "🔥 سيولة استثنائية" to 3
            dailyTurnover >= 20_000_000 -> "✅ سيولة ممتازة" to 2
            dailyTurnover >= 5_000_000 -> "👍 سيولة جيدة" to 1
            dailyTurnover >= 1_000_000 -> "⚠️ سيولة متوسطة" to 0
            else -> "❄️ سيولة ضعيفة" to -1
        }
        return Triple(rating, score, ratio)
    }

    fun analyzeBreakoutQuality(
        price: Double,
        high: Double,
        low: Double,
        volumeRatio: Double
    ): BreakoutQuality {
        if (price <= 0) return BreakoutQuality()

        val nearHigh = if (price > 0) (high - price) / price * 100.0 else 0.0
        val dayRange = if (low > 0) (high - low) / low * 100.0 else 0.0
        val closeStrength = if (high > low) (price - low) / (high - low) * 100.0 else 50.0

        var score = 0
        val reasons = mutableListOf<String>()

        if (nearHigh < 0.5) {
            score += 35
            reasons.add("🎯 إغلاق عند قمة اليوم تماماً")
        } else if (nearHigh < 1.0) {
            score += 25
            reasons.add("📍 قريب جداً من قمة اليوم")
        } else if (nearHigh < 1.5) {
            score += 15
            reasons.add("📍 قريب من القمة اليومية")
        }

        if (closeStrength >= 80) {
            score += 35
            reasons.add("💪 إغلاق شرائي فائق القوة ($closeStrength%)")
        } else if (closeStrength >= 70) {
            score += 25
            reasons.add("💪 إغلاق شرائي قوي ($closeStrength%)")
        } else if (closeStrength >= 60) {
            score += 15
            reasons.add("📊 إغلاق إيجابي أعلى المنتصف")
        }

        if (dayRange > 5.0) {
            score += 20
            reasons.add("📊 نطاق حركة وتوسع كبير ($dayRange%)")
        } else if (dayRange > 3.0) {
            score += 15
            reasons.add("📈 نطاق حركة جيد ($dayRange%)")
        } else if (dayRange > 1.5) {
            score += 10
            reasons.add("📈 نطاق حركة مقبول")
        }

        if (volumeRatio > 2.5) {
            score += 10
            reasons.add("💥 سيولة تفجيرية (${String.format(java.util.Locale.US, "%.1f", volumeRatio)}x)")
        } else if (volumeRatio > 1.8) {
            score += 5
            reasons.add("🚀 سيولة قوية أعلى من المتوسط")
        }

        val (grade, gradeArabic, colorHex) = when {
            score >= 75 -> Triple("A+", "اختراق حقيقي فائق القوة", 0xFF10B981)
            score >= 60 -> Triple("A", "اختراق حقيقي ممتاز", 0xFF34D399)
            score >= 45 -> Triple("B", "اختراق متوسط للمتابعة", 0xFFF59E0B)
            score >= 30 -> Triple("C", "اختراق ضعيف - يفضل الحذر", 0xFFF97316)
            else -> Triple("D", "اختراق وهمي (Fake Breakout)", 0xFFEF4444)
        }

        return BreakoutQuality(
            score = score,
            grade = grade,
            gradeArabic = gradeArabic,
            gradeColorHex = colorHex,
            closeStrengthPct = closeStrength,
            nearHighPct = nearHigh,
            dayRangePct = dayRange,
            reasons = reasons
        )
    }

    fun analyzeCandlestickPatterns(
        price: Double,
        open: Double,
        high: Double,
        low: Double,
        prevClose: Double,
        changePct: Double
    ): Pair<List<String>, Int> {
        val patterns = mutableListOf<String>()
        var strengthScore = 0

        val candleBody = abs(price - open)
        val candleRange = if (high > low) high - low else 0.001

        // 1. Hammer (مطرقة - انعكاس صاعد بعد هبوط)
        val lowerShadow = min(price, open) - low
        if (lowerShadow > candleBody * 2 && changePct > -3.0) {
            patterns.add("🔨 شمعة مطرقة (Hammer) - إشارة ارتداد وانعكاس صاعد")
            strengthScore += 3
        }

        // 2. Shooting Star (شهاب - انعكاس هابط محتمل)
        val upperShadow = high - max(price, open)
        if (upperShadow > candleBody * 2 && changePct > 1.0) {
            patterns.add("⭐ شمعة شهاب (Shooting Star) - مقاومة بيعية علوية")
            strengthScore -= 2
        }

        // 3. Bullish Engulfing (ابتلاع صاعد)
        if (price > prevClose && prevClose > open && price > prevClose * 1.02) {
            patterns.add("🟢 نموذج ابتلاع صاعد (Bullish Engulfing) - قوة شرائية كاسحة")
            strengthScore += 4
        }

        // 4. Bearish Engulfing (ابتلاع هابط)
        if (price < prevClose && open > prevClose && price < prevClose * 0.98) {
            patterns.add("🔴 نموذج ابتلاع هابط (Bearish Engulfing) - ضغط بيعي")
            strengthScore -= 3
        }

        // 5. Doji (دوجي)
        if (candleBody < candleRange * 0.1) {
            patterns.add("✚ شمعة دوجي (Doji) - تعادل بين المشترين والبائعين")
        }

        // 6. Marubozu (ماروبوزو)
        if (upperShadow < candleRange * 0.1 && lowerShadow < candleRange * 0.1) {
            if (changePct > 0) {
                patterns.add("📈 شمعة ماروبوزو صاعدة - سيطرة شرائية كاملة طوال الجلسة")
                strengthScore += 3
            } else {
                patterns.add("📉 شمعة ماروبوزو هابطة - سيطرة بيعية")
                strengthScore -= 2
            }
        }

        // 7. Morning Star (نجم الصباح)
        if (changePct > 0 && price > open * 1.03 && prevClose < open) {
            patterns.add("⭐ نموذج نجم الصباح (Morning Star) - ارتداد صاعد ثلاثي مميز")
            strengthScore += 3
        }

        return patterns to strengthScore
    }

    fun calculateSmartScore(
        trendLong: String,
        trendMedium: String,
        trendShort: String,
        volumeRatio: Double,
        dailyTurnover: Double,
        rsi: Double,
        rr: Double,
        change: Double,
        candleStrength: Int
    ): Int {
        var score = 0.0

        // 1. TREND (30 pts)
        var trendPts = 0.0
        if (trendLong == "صاعد") trendPts += 40.0
        if (trendMedium == "صاعد") trendPts += 35.0
        if (trendShort == "صاعد") trendPts += 25.0
        score += (trendPts / 100.0) * 30.0

        // 2. VOLUME (25 pts)
        val volPts = when {
            volumeRatio > 2.5 -> 100.0
            volumeRatio > 1.8 -> 80.0
            volumeRatio > 1.2 -> 60.0
            volumeRatio > 0.8 -> 40.0
            else -> 20.0
        }
        score += (volPts / 100.0) * 25.0

        if (dailyTurnover >= 50_000_000) score += 3.0
        else if (dailyTurnover >= 20_000_000) score += 2.0
        else if (dailyTurnover >= 5_000_000) score += 1.0

        // 3. RSI (15 pts)
        val rsiPts = when {
            rsi in 45.0..60.0 -> 100.0
            rsi in 40.0..65.0 -> 80.0
            rsi in 35.0..70.0 -> 60.0
            else -> 40.0
        }
        score += (rsiPts / 100.0) * 15.0

        // 4. RR RATIO (15 pts)
        val rrPts = when {
            rr >= 2.5 -> 100.0
            rr >= 2.0 -> 80.0
            rr >= 1.5 -> 60.0
            rr >= 1.2 -> 40.0
            else -> 20.0
        }
        score += (rrPts / 100.0) * 15.0

        // 5. PRICE ACTION (15 pts)
        val paPts = when {
            change > 2.0 -> 100.0
            change > 1.0 -> 80.0
            change > 0.5 -> 60.0
            change > 0.0 -> 40.0
            else -> 20.0
        }
        score += (paPts / 100.0) * 15.0

        // Candlestick pattern bonus/penalty
        if (candleStrength >= 3) score += 6.0
        else if (candleStrength >= 1) score += 3.0
        else if (candleStrength <= -2) score -= 4.0

        return min(100.0, max(0.0, score)).roundToInt()
    }

    fun calculateConfidence(
        price: Double,
        rsi: Double,
        volumeRatio: Double,
        change: Double,
        trendShort: String,
        trendMedium: String,
        trendLong: String,
        sma200: Double,
        r1: Double,
        dailyTurnover: Double,
        candleStrength: Int
    ): Triple<String, String, Long> {
        var score = 0.0
        val total = 8.0

        if (trendLong == "صاعد" || (sma200 > 0 && price > sma200)) score += 1.0
        if (trendMedium == "صاعد" && trendShort == "صاعد") score += 1.0
        if (rsi in 45.0..65.0) score += 1.0 else if (rsi in 35.0..70.0) score += 0.5
        if (volumeRatio > 1.8) score += 1.0 else if (volumeRatio > 1.2) score += 0.5
        if (r1 > price) {
            val dist = (r1 - price) / price * 100.0
            if (dist < 2.0) score += 1.0 else if (dist < 3.0) score += 0.5
        }
        if (change > 0.3) score += 1.0 else if (change > 0.0) score += 0.5
        if (dailyTurnover >= 20_000_000) score += 1.0 else if (dailyTurnover >= 5_000_000) score += 0.5
        if (candleStrength >= 2) score += 1.0 else if (candleStrength >= 1) score += 0.5

        val pct = (score / total) * 100.0

        return when {
            pct >= 80.0 -> Triple("A+", "فرصة عالية الجودة - تطابق تام في المؤشرات", 0xFF10B981)
            pct >= 65.0 -> Triple("A", "فرصة قوية وممتازة للتداول", 0xFF34D399)
            pct >= 50.0 -> Triple("B", "فرصة متوسطة - تحتاج متابعة حذرة", 0xFFF59E0B)
            pct >= 35.0 -> Triple("C", "فرصة ضعيفة - مؤشرات متضاربة", 0xFFF97316)
            else -> Triple("D", "تجنب - اتجاهات سلبية", 0xFFEF4444)
        }
    }
}
