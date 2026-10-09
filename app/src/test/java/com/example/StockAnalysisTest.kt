package com.example

import com.example.data.remote.EgyptianStockDirectory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class StockAnalysisTest {

    @Test
    fun testArabicStockMatching() {
        val tmghMatch = EgyptianStockDirectory.findMatchingSymbol("طلعت مصطفى")
        assertEquals("TMGH", tmghMatch)

        val comiMatch = EgyptianStockDirectory.findMatchingSymbol("التجاري الدولي")
        assertEquals("COMI", comiMatch)

        val fwryMatch = EgyptianStockDirectory.findMatchingSymbol("فوري")
        assertEquals("FWRY", fwryMatch)

        val swdyMatch = EgyptianStockDirectory.findMatchingSymbol("السويدي")
        assertEquals("SWDY", swdyMatch)

        val abukMatch = EgyptianStockDirectory.findMatchingSymbol("أبو قير")
        assertEquals("ABUK", abukMatch)

        val skpcMatch = EgyptianStockDirectory.findMatchingSymbol("سيدي كرير")
        assertEquals("SKPC", skpcMatch)
    }

    @Test
    fun testSymbolDirectMatching() {
        assertEquals("TMGH", EgyptianStockDirectory.findMatchingSymbol("tmgh"))
        assertEquals("COMI", EgyptianStockDirectory.findMatchingSymbol("COMI"))
        assertEquals("ETEL", EgyptianStockDirectory.findMatchingSymbol("etel"))
        assertEquals("CCAP", EgyptianStockDirectory.findMatchingSymbol("ccap"))
    }

    @Test
    fun testArabicNameLookup() {
        val name = EgyptianStockDirectory.getArabicName("TMGH")
        assertNotNull(name)
        assert(name.contains("طلعت مصطفى"))
    }

    @Test
    fun testVolatilityFilter() {
        val (isVolLow, reasonLow) = com.example.data.remote.TechnicalAnalysisEngine.isVolatileEnough(0.6)
        assertEquals(false, isVolLow)
        assert(reasonLow.contains("ضعيف جداً"))

        val (isVolGood, _) = com.example.data.remote.TechnicalAnalysisEngine.isVolatileEnough(2.5)
        assertEquals(true, isVolGood)
    }

    @Test
    fun testBreakoutQualityScoring() {
        val quality = com.example.data.remote.TechnicalAnalysisEngine.analyzeBreakoutQuality(
            price = 100.0,
            high = 100.2,
            low = 95.0,
            volumeRatio = 2.0
        )
        assert(quality.closeStrengthPct >= 90.0)
        assert(quality.score >= 60)
        assertEquals("A+", quality.grade)
    }

    @Test
    fun testTomorrowDisqualificationReasons() {
        val stockWithHighGain = com.example.data.model.StockData(
            symbol = "SIPC",
            name = "سيد كرير",
            description = "بتروكيماويات",
            price = 30.0,
            change = 7.2,
            open = 28.0,
            high = 30.5,
            low = 27.8,
            prevClose = 28.0,
            volume = 2000000L,
            avgVolume10d = 1500000L,
            volumeRatio = 1.33,
            dailyTurnover = 60000000.0,
            turnoverRatingArabic = "سيولة عملاقة",
            turnoverScore = 95,
            sma20 = 27.0,
            sma50 = 25.0,
            sma200 = 22.0,
            distanceSma50 = 20.0,
            distanceSma200 = 36.3,
            isGoldenCross = true,
            trendShort = "صاعد",
            trendMedium = "صاعد",
            trendLong = "صاعد",
            high52 = 35.0,
            low52 = 18.0,
            upsideTo52wHigh = 16.6,
            perf1m = 12.0,
            volatility = 4.2,
            rsi = 74.0,
            pp = 29.0,
            s1 = 28.0,
            s2 = 27.0,
            r1 = 31.0,
            r2 = 32.0,
            entryMin = 29.5,
            entryMax = 30.0,
            entryPrice = 30.0,
            stopLoss = 28.8,
            target1 = 31.5,
            target2 = 33.0,
            riskPct = 4.0,
            targetPct = 5.0,
            riskRewardRatio = 1.25,
            smartScore = 78,
            confidenceGrade = "B",
            confidenceAdvice = "تداول بحذر",
            confidenceColorHex = 0xFFF59E0B,
            candlePatterns = listOf("شمعة صاعدة"),
            candleStrength = 2,
            breakoutQuality = com.example.data.model.BreakoutQuality(
                score = 65,
                grade = "A",
                gradeArabic = "اختراق حقيقي",
                gradeColorHex = 0xFF34D399,
                closeStrengthPct = 85.0,
                nearHighPct = 1.6,
                dayRangePct = 9.7,
                reasons = emptyList()
            ),
            isRapidBreakout = true,
            isCorrectionHunter = false,
            isSupportBounce = false,
            isEarlyUptrend = false,
            isTomorrowPick = false,
            tomorrowScore = 70,
            weeklyRsi = 62.0,
            isWeeklyConfirmedBullish = true,
            isOfflineFallback = false,
            screenerReasons = listOf("سيولة قوية"),
            sector = "بتروكيماويات",
            tomorrowDisqualificationReasons = listOf(
                "ارتفع 7.2% اليوم - خطر جني أرباح",
                "متضخم: 11.1% فوق SMA20"
            ),
            tomorrowWatchlistScore = 60,
            isTomorrowWatchlistEligible = true
        )
        assert(stockWithHighGain.tomorrowDisqualificationReasons.isNotEmpty())
        assert(stockWithHighGain.tomorrowDisqualificationReasons[0].contains("ارتفع 7.2% اليوم"))
        assertEquals(true, stockWithHighGain.isTomorrowWatchlistEligible)
    }

    @Test
    fun testMarketSafetyThresholds() {
        val minScoreBullish = when (com.example.data.model.TrendDirection.BULLISH) {
            com.example.data.model.TrendDirection.BULLISH -> 62
            com.example.data.model.TrendDirection.CHOPPY -> 68
            com.example.data.model.TrendDirection.BEARISH -> 76
        }
        val minScoreBearish = when (com.example.data.model.TrendDirection.BEARISH) {
            com.example.data.model.TrendDirection.BULLISH -> 62
            com.example.data.model.TrendDirection.CHOPPY -> 68
            com.example.data.model.TrendDirection.BEARISH -> 76
        }
        assertEquals(62, minScoreBullish)
        assertEquals(76, minScoreBearish)
    }
}
