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
}
