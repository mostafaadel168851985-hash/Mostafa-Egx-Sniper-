package com.example.data.model

import java.util.Locale
import kotlin.math.roundToInt

enum class TraderProfileType(
    val arabicTitle: String,
    val shortBadge: String,
    val icon: String,
    val timeframe: String,
    val description: String
) {
    SCALPER(
        arabicTitle = "المضارب السريع اللحظي",
        shortBadge = "مضاربة ⚡",
        icon = "⚡",
        timeframe = "جلسة إلى 3 جلسات",
        description = "استغلال زخم الصعود اللحظي واختراق المقاومات بأهداف سريعة ووقف خسارة ضيق لحماية السيولة."
    ),
    SWING(
        arabicTitle = "متداول السوينغ (موجة متوسطة)",
        shortBadge = "سوينغ 🌊",
        icon = "🌊",
        timeframe = "أسبوع إلى شهر",
        description = "الشراء عند الارتداد من الدعوم القوية ومناطق التصحيح وركوب موجة صاعدة كاملة نحو أهداف عليا."
    ),
    INVESTOR(
        arabicTitle = "المستثمر طويل الأجل والنمو",
        shortBadge = "استثمار 🏛️",
        icon = "🏛️",
        timeframe = "3 أشهر إلى سنة فأكثر",
        description = "بناء مراكز استثمارية تراكمية في الشركات القوية عند اقتراب السعر من متوسط 200 يوم والقمم السنوية."
    )
}

data class ProfileStrategyPlan(
    val type: TraderProfileType,
    val entryPrice: Double,
    val entryRange: String,
    val stopLossPrice: Double,
    val stopLossPct: Double,
    val target1Price: Double,
    val target1Pct: Double,
    val target2Price: Double,
    val target2Pct: Double,
    val riskRewardRatio: Double,
    val goldenRule: String,
    val keyIndicators: String
)

object TraderProfileEngine {

    fun generatePlans(stock: StockData): Map<TraderProfileType, ProfileStrategyPlan> {
        val p = stock.price
        val entry = if (stock.entryPrice > 0) stock.entryPrice else p

        // 1. Scalper (Tight stop -2.5%, Quick target +4.5% to +6%)
        val scalperStop = (entry * 0.975).coerceAtLeast(0.01)
        val scalperStopPct = 2.5
        val scalperT1 = entry * 1.045
        val scalperT1Pct = 4.5
        val scalperT2 = entry * 1.075
        val scalperT2Pct = 7.5
        val scalperRR = if (scalperStopPct > 0) scalperT1Pct / scalperStopPct else 1.8
        val scalperPlan = ProfileStrategyPlan(
            type = TraderProfileType.SCALPER,
            entryPrice = entry,
            entryRange = "${String.format(Locale.US, "%.2f", entry * 0.995)} - ${String.format(Locale.US, "%.2f", entry * 1.015)} ج",
            stopLossPrice = scalperStop,
            stopLossPct = scalperStopPct,
            target1Price = scalperT1,
            target1Pct = scalperT1Pct,
            target2Price = scalperT2,
            target2Pct = scalperT2Pct,
            riskRewardRatio = scalperRR,
            goldenRule = "⚡ اجنِ نصف أرباحك عند الهدف الأول (+4.5%)، وارفع الوقف لسعر الدخول فوراً ولا تبيت بالسهم عند كسر الوقف.",
            keyIndicators = "حجم التداول اللحظي • RSI (45-65) • اختراق المقاومة R1"
        )

        // 2. Swing Trader (Support bounce, S1 entry, target R1/R2)
        val swingEntry = if (stock.s1 > 0 && stock.s1 < p) stock.s1 else entry * 0.98
        val swingStop = if (stock.stopLoss > 0) stock.stopLoss else entry * 0.95
        val swingStopPct = if (entry > 0) ((entry - swingStop) / entry) * 100.0 else 5.0
        val swingT1 = if (stock.target1 > 0) stock.target1 else entry * 1.09
        val swingT1Pct = if (entry > 0) ((swingT1 - entry) / entry) * 100.0 else 9.0
        val swingT2 = if (stock.target2 > 0) stock.target2 else entry * 1.16
        val swingT2Pct = if (entry > 0) ((swingT2 - entry) / entry) * 100.0 else 16.0
        val swingRR = if (swingStopPct > 0) swingT1Pct / swingStopPct else 2.0
        val swingPlan = ProfileStrategyPlan(
            type = TraderProfileType.SWING,
            entryPrice = swingEntry,
            entryRange = "${String.format(Locale.US, "%.2f", swingEntry * 0.99)} - ${String.format(Locale.US, "%.2f", swingEntry * 1.02)} ج",
            stopLossPrice = swingStop,
            stopLossPct = (swingStopPct * 10).roundToInt() / 10.0,
            target1Price = swingT1,
            target1Pct = (swingT1Pct * 10).roundToInt() / 10.0,
            target2Price = swingT2,
            target2Pct = (swingT2Pct * 10).roundToInt() / 10.0,
            riskRewardRatio = (swingRR * 100).roundToInt() / 100.0,
            goldenRule = "🌊 اشترِ عند التهدئة قرب الدعم الأول S1، وأغلق نصف الكمية عند R1 واترك الباقي للموجة الكاملة.",
            keyIndicators = "الدعم S1 • متوسط متحرك 20 و 50 يوم • نموذج شمعة الارتداد"
        )

        // 3. Long-term Investor (SMA200, 52W High upside, value growth)
        val investEntry = if (stock.sma200 > 0 && p > stock.sma200) p else entry
        val investStop = if (stock.sma200 > 0) stock.sma200 * 0.92 else entry * 0.90
        val investStopPct = if (investEntry > 0) ((investEntry - investStop) / investEntry) * 100.0 else 9.0
        val investT1 = if (stock.high52 > 0 && stock.high52 > investEntry) stock.high52 else investEntry * 1.25
        val investT1Pct = if (investEntry > 0) ((investT1 - investEntry) / investEntry) * 100.0 else 25.0
        val investT2 = investT1 * 1.20
        val investT2Pct = investT1Pct + 20.0
        val investRR = if (investStopPct > 0) investT1Pct / investStopPct else 2.8
        val investPlan = ProfileStrategyPlan(
            type = TraderProfileType.INVESTOR,
            entryPrice = investEntry,
            entryRange = "${String.format(Locale.US, "%.2f", investEntry * 0.98)} - ${String.format(Locale.US, "%.2f", investEntry * 1.03)} ج",
            stopLossPrice = investStop,
            stopLossPct = (investStopPct * 10).roundToInt() / 10.0,
            target1Price = investT1,
            target1Pct = (investT1Pct * 10).roundToInt() / 10.0,
            target2Price = investT2,
            target2Pct = (investT2Pct * 10).roundToInt() / 10.0,
            riskRewardRatio = (investRR * 100).roundToInt() / 100.0,
            goldenRule = "🏛️ قسّم سيولتك على 3 دفعات عند كل تصحيح شهري طالما السهم أعلى من متوسط 200 يوم الصاعد.",
            keyIndicators = "متوسط 200 يوم (الاتجاه الرئيسي) • القمة السنوية 52W • توافق الشريعة"
        )

        return mapOf(
            TraderProfileType.SCALPER to scalperPlan,
            TraderProfileType.SWING to swingPlan,
            TraderProfileType.INVESTOR to investPlan
        )
    }
}
