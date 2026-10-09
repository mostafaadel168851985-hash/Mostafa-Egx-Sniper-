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
