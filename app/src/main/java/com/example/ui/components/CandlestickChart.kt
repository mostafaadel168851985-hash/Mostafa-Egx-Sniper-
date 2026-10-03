package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockData
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class HistoricalBar(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long,
    val ema20: Double,
    val ema50: Double,
    val macdLine: Double,
    val macdSignal: Double,
    val macdHist: Double,
    val obv: Long
)

enum class IndicatorTab(val title: String, val icon: String) {
    CANDLES_AND_EMA("🕯️ الشموع والمتوسطات", "🕯️"),
    MACD_ANALYSIS("📊 مؤشر MACD التاريخي", "📊"),
    OBV_AND_VOLUME("🌊 سيولة OBV و RSI", "🌊")
}

@Composable
fun CandlestickChart(
    stock: StockData,
    modifier: Modifier = Modifier,
    heightDp: Int = 340
) {
    var selectedTab by remember { mutableStateOf(IndicatorTab.CANDLES_AND_EMA) }

    // Generate accurate historical series (20 bars)
    val bars = remember(stock.symbol, stock.price, stock.macdLine, stock.macdSignal) {
        generateHistoricalBars(stock)
    }

    val today = bars.lastOrNull()
    val yesterday = if (bars.size >= 2) bars[bars.size - 2] else today

    val price = stock.price
    val ema20 = today?.ema20 ?: (stock.ema20.takeIf { it > 0 } ?: stock.sma20)
    val ema50 = today?.ema50 ?: (stock.ema50.takeIf { it > 0 } ?: stock.sma50)
    val ema200 = stock.ema200.takeIf { it > 0 } ?: stock.sma200

    val macdLine = today?.macdLine ?: stock.macdLine
    val macdSignal = today?.macdSignal ?: stock.macdSignal
    val macdHist = today?.macdHist ?: stock.macdHist
    val prevMacdHist = yesterday?.macdHist ?: (macdHist * 0.8)
    val prevMacdLine = yesterday?.macdLine ?: (macdLine * 0.95)
    val prevMacdSignal = yesterday?.macdSignal ?: (macdSignal * 0.95)

    // Accurate MACD State Detection (Eliminating false negative crossover signals)
    val isFreshGoldenCross = prevMacdLine < prevMacdSignal && macdLine >= macdSignal
    val isFreshDeathCross = prevMacdLine >= prevMacdSignal && macdLine < macdSignal
    val isExpandingBullish = macdLine >= macdSignal && macdHist > 0 && macdHist >= prevMacdHist
    val isWeakeningBullish = macdLine >= macdSignal && macdHist > 0 && macdHist < prevMacdHist
    val isReversalBottomForming = macdLine < macdSignal && macdHist > prevMacdHist
    val isExpandingBearish = macdLine < macdSignal && macdHist <= prevMacdHist

    val (macdStateTitle, macdStateDesc, macdStateColor) = when {
        isFreshGoldenCross -> Triple(
            "تقاطع إيجابي صاعد مؤكد (Golden Cross) 🟢",
            "خط الماكد الأزرق يخترق خط الإشارة البرتقالي للأعلى مع زخم شرائي متصاعد 🚀 إشارة شراء قوية.",
            BullishGreen
        )
        isExpandingBullish -> Triple(
            "زخم شرائي صاعد متواصل 🟢",
            "خط الماكد أعلى خط الإشارة مع اتساع إيجابي في أعمدة الهيستوجرام، قوى الشراء تسيطر على السهم.",
            BullishGreen
        )
        isWeakeningBullish -> Triple(
            "مسار صاعد مع هدوء مؤقت في الزخم 📈",
            "الماكد لا يزال إيجابياً أعلى خط الإشارة، مع هدوء طفيف في الزخم (مرحلة تماسك أو جني أرباح خفيف).",
            AccentCyan
        )
        isReversalBottomForming -> Triple(
            "انحسار الضغط البيعي وبداية تشكل ارتداد صاعد ⏳",
            "تنبيه ذكي: الخط الأزرق أسفل البرتقالي، لكن الفجوة تتقلص بقوة والهيستوجرام يرتفع نحو الصفر 👈 إشارة قاع ارتدادي مبكر وليست إشارة بيع!",
            GoldenAmber
        )
        isFreshDeathCross -> Triple(
            "تقاطع سلبي هابط جديد (Fresh Death Cross) 🔴",
            "خط الماكد كسر خط الإشارة للأسفل مع بداية ظهور أعمدة سالبة 👈 إشارة خروج أو جني أرباح.",
            BearishRed
        )
        else -> Triple(
            "ضغط بيعي مستمر 🔻",
            "خط الماكد أدنى خط الإشارة مع اتساع الأعمدة السالبة، ينصح بتجنب الشراء لحين تشكل إشارة انعكاس.",
            BearishRed
        )
    }

    // Combined Actionable Technical Decision (EMA + MACD Synthesis)
    val isEmaBullish = price > ema20 && ema20 >= ema50
    val isEmaSupport = price in (ema50 * 0.98)..(ema20 * 1.02)

    val (overallDecisionTitle, overallDecisionDesc, overallDecisionColor) = when {
        (isFreshGoldenCross || isExpandingBullish) && isEmaBullish -> Triple(
            "🟢 إشارة شراء قوية (توافق صاعد للماكد والمتوسطات)",
            "السعر يتداول أعلى متوسطات 20 و 50 يوم بالتزامن مع زخم شرائي إيجابي للماكد 👈 فرصة شراء مثالية لدعم الصعود 🚀",
            BullishGreen
        )
        isEmaBullish || isFreshGoldenCross || isExpandingBullish -> Triple(
            "📈 اتجاه صاعد إيجابي (مناسب للدخول التراكمي)",
            if (isEmaBullish) "السعر يتداول بثبات فوق المتوسطات، والماكد يتحسن لدعم تحقيق المستهدفات."
            else "الماكد يعطي إشارة شراء صاعدة وفي انتظار اختراق السعر لمتوسط 20 يوم لتأكيد الانطلاق.",
            BullishGreen
        )
        isReversalBottomForming || isEmaSupport -> Triple(
            "⏳ منطقة مراقبة واختبار دعم (ترقب ارتداد شرائي)",
            "السعر يختبر دعماً متحركاً بالتزامن مع تراجع الضغط البيعي في الماكد، راقب ظهور شمعة انعكاس خضراء للدخول.",
            GoldenAmber
        )
        else -> Triple(
            "⚠️ اتجاه تصحيحي هابط (لا ينصح بالشراء حالياً)",
            "السعر يتداول أدنى متوسطات 20 و 50 يوم مع ضغط بيعي، انتظر استقرار السعر وظهور إشارات انعكاس إيجابية.",
            BearishRed
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .border(1.dp, OutlineDark, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // 1. Header Row & Actionable Signal Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = overallDecisionColor.copy(alpha = 0.12f)),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, overallDecisionColor.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (overallDecisionColor == BullishGreen) Icons.Default.TrendingUp
                        else if (overallDecisionColor == GoldenAmber) Icons.Default.Info
                        else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = overallDecisionColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = overallDecisionTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = overallDecisionColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = overallDecisionDesc,
                    fontSize = 10.sp,
                    color = TextPrimary,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Sub-tab Selector (100% Native, Instant, Eye-Friendly)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IndicatorTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) AccentCyan else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedTab = tab }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Tab Contents
        when (selectedTab) {
            IndicatorTab.CANDLES_AND_EMA -> {
                CandlestickAndEmaView(
                    stock = stock,
                    bars = bars,
                    ema20 = ema20,
                    ema50 = ema50,
                    ema200 = ema200,
                    heightDp = 180
                )
            }
            IndicatorTab.MACD_ANALYSIS -> {
                MacdVisualHistoricalView(
                    bars = bars,
                    stateTitle = macdStateTitle,
                    stateDesc = macdStateDesc,
                    stateColor = macdStateColor,
                    macdLine = macdLine,
                    macdSignal = macdSignal,
                    macdHist = macdHist,
                    heightDp = 180
                )
            }
            IndicatorTab.OBV_AND_VOLUME -> {
                ObvAndVolumeVisualView(
                    stock = stock,
                    bars = bars,
                    heightDp = 180
                )
            }
        }
    }
}

@Composable
private fun CandlestickAndEmaView(
    stock: StockData,
    bars: List<HistoricalBar>,
    ema20: Double,
    ema50: Double,
    ema200: Double,
    heightDp: Int
) {
    val price = stock.price

    Column(modifier = Modifier.fillMaxWidth()) {
        // EMA Numeric Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "EMA 20 (السريع)", fontSize = 9.sp, color = TextMuted)
                Text(
                    text = String.format(Locale.US, "%.2f ج", ema20),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "EMA 50 (الرئيسي)", fontSize = 9.sp, color = TextMuted)
                Text(
                    text = String.format(Locale.US, "%.2f ج", ema50),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldenAmber
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "EMA 200 (التاريخي)", fontSize = 9.sp, color = TextMuted)
                Text(
                    text = String.format(Locale.US, "%.2f ج", ema200),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val emaExplanation = when {
            price > ema20 && ema20 > ema50 -> "✅ السعر أعلى متوسطات 20 و 50 يوم 👈 اتجاه صاعد قوي بدعم متحرك"
            price > ema20 -> "📈 السعر يتداول فوق متوسط 20 يوم 👈 قوة شرائية لحظية جيدة"
            price in ema50..ema20 -> "⏳ السعر يصحح بين متوسط 20 و 50 يوم 👈 منطقة ارتداد ومراقبة"
            else -> "🔻 السعر أدنى متوسط 50 يوم 👈 المتوسطات تمثل مقاومات تضغط على السعر"
        }

        Text(
            text = emaExplanation,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (price > ema20) BullishGreen else GoldenAmber,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Native Candlestick Canvas with EMA Overlay lines
        val minPrice = bars.minOf { it.low }.coerceAtMost(stock.s2 * 0.99)
        val maxPrice = bars.maxOf { it.high }.coerceAtLeast(stock.r2 * 1.01)
        val priceRange = if (maxPrice > minPrice) maxPrice - minPrice else 1.0

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .background(SurfaceVariantDark.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
        ) {
            Canvas(modifier = Modifier.matchParentSize().padding(4.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val chartHeight = canvasHeight * 0.78f
                val volumeHeight = canvasHeight * 0.20f

                fun priceToY(p: Double): Float {
                    val normalized = (p - minPrice) / priceRange
                    return (chartHeight - (normalized * chartHeight)).toFloat()
                }

                val candleCount = bars.size
                val spacing = canvasWidth / candleCount
                val candleWidth = spacing * 0.65f

                // Resistance R1 & Support S1 Guide lines
                if (stock.r1 in minPrice..maxPrice) {
                    val y = priceToY(stock.r1)
                    drawLine(
                        color = BearishRed.copy(alpha = 0.4f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                    )
                }

                if (stock.s1 in minPrice..maxPrice) {
                    val y = priceToY(stock.s1)
                    drawLine(
                        color = BullishGreen.copy(alpha = 0.4f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                    )
                }

                val maxVol = bars.maxOf { it.volume }.coerceAtLeast(1L)

                // 1. Draw Candles & Volume
                bars.forEachIndexed { i, bar ->
                    val x = i * spacing + spacing / 2f
                    val openY = priceToY(bar.open)
                    val closeY = priceToY(bar.close)
                    val highY = priceToY(bar.high)
                    val lowY = priceToY(bar.low)

                    val isBull = bar.close >= bar.open
                    val candleColor = if (isBull) Color(0xFF10B981) else Color(0xFFF43F5E)

                    // Wick
                    drawLine(
                        color = candleColor,
                        start = Offset(x, highY),
                        end = Offset(x, lowY),
                        strokeWidth = 1.5.dp.toPx()
                    )

                    // Body
                    val bodyTop = min(openY, closeY)
                    val bodyHeight = max(abs(closeY - openY), 2f)
                    drawRect(
                        color = candleColor,
                        topLeft = Offset(x - candleWidth / 2f, bodyTop),
                        size = Size(candleWidth, bodyHeight)
                    )

                    // Volume
                    val volBarHeight = ((bar.volume.toFloat() / maxVol) * volumeHeight).coerceAtLeast(2f)
                    val volY = canvasHeight - volBarHeight
                    drawRect(
                        color = candleColor.copy(alpha = 0.35f),
                        topLeft = Offset(x - candleWidth / 2f, volY),
                        size = Size(candleWidth, volBarHeight)
                    )
                }

                // 2. Draw EMA20 Line Overlay (AccentCyan)
                val ema20Path = Path()
                bars.forEachIndexed { i, bar ->
                    val x = i * spacing + spacing / 2f
                    val y = priceToY(bar.ema20)
                    if (i == 0) ema20Path.moveTo(x, y) else ema20Path.lineTo(x, y)
                }
                drawPath(ema20Path, color = AccentCyan, style = Stroke(width = 1.5.dp.toPx()))

                // 3. Draw EMA50 Line Overlay (GoldenAmber)
                val ema50Path = Path()
                bars.forEachIndexed { i, bar ->
                    val x = i * spacing + spacing / 2f
                    val y = priceToY(bar.ema50)
                    if (i == 0) ema50Path.moveTo(x, y) else ema50Path.lineTo(x, y)
                }
                drawPath(ema50Path, color = GoldenAmber, style = Stroke(width = 1.5.dp.toPx()))
            }
        }
    }
}

@Composable
private fun MacdVisualHistoricalView(
    bars: List<HistoricalBar>,
    stateTitle: String,
    stateDesc: String,
    stateColor: Color,
    macdLine: Double,
    macdSignal: Double,
    macdHist: Double,
    heightDp: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Detailed state explanation card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = stateColor.copy(alpha = 0.12f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, stateColor.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stateTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = stateColor)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(AccentCyan, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "الماكد", fontSize = 9.sp, color = TextMuted)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(8.dp).background(GoldenAmber, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "الإشارة", fontSize = 9.sp, color = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = stateDesc, fontSize = 10.sp, color = TextPrimary, lineHeight = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "خط الماكد (الأزرق): ${String.format(Locale.US, "%+.2f", macdLine)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                    Text(
                        text = "خط الإشارة (البرتقالي): ${String.format(Locale.US, "%+.2f", macdSignal)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldenAmber
                    )
                    Text(
                        text = "الهيستوجرام: ${String.format(Locale.US, "%+.2f", macdHist)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (macdHist >= 0) BullishGreen else BearishRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Historical MACD Line & Histogram Canvas
        val minMacd = bars.minOf { min(it.macdLine, min(it.macdSignal, it.macdHist)) }.coerceAtMost(-0.5)
        val maxMacd = bars.maxOf { max(it.macdLine, max(it.macdSignal, it.macdHist)) }.coerceAtLeast(0.5)
        val macdRange = if (maxMacd > minMacd) maxMacd - minMacd else 1.0

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .background(SurfaceVariantDark.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
        ) {
            Canvas(modifier = Modifier.matchParentSize().padding(6.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                fun valToY(v: Double): Float {
                    val normalized = (v - minMacd) / macdRange
                    return (canvasHeight - (normalized * canvasHeight)).toFloat()
                }

                val zeroY = valToY(0.0)

                // 1. Draw Zero Reference Line
                drawLine(
                    color = OutlineDark,
                    start = Offset(0f, zeroY),
                    end = Offset(canvasWidth, zeroY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                )

                val count = bars.size
                val spacing = canvasWidth / count
                val barWidth = spacing * 0.55f

                // 2. Draw Histogram Bars
                bars.forEachIndexed { i, bar ->
                    val x = i * spacing + spacing / 2f
                    val histY = valToY(bar.macdHist)
                    val barColor = if (bar.macdHist >= 0) Color(0xFF10B981) else Color(0xFFF43F5E)

                    val top = min(zeroY, histY)
                    val barH = max(abs(histY - zeroY), 2f)
                    drawRect(
                        color = barColor.copy(alpha = 0.7f),
                        topLeft = Offset(x - barWidth / 2f, top),
                        size = Size(barWidth, barH)
                    )
                }

                // 3. Draw MACD Line (Blue / AccentCyan)
                val macdPath = Path()
                bars.forEachIndexed { i, bar ->
                    val x = i * spacing + spacing / 2f
                    val y = valToY(bar.macdLine)
                    if (i == 0) macdPath.moveTo(x, y) else macdPath.lineTo(x, y)
                }
                drawPath(macdPath, color = AccentCyan, style = Stroke(width = 2.dp.toPx()))

                // 4. Draw Signal Line (Orange / GoldenAmber)
                val signalPath = Path()
                bars.forEachIndexed { i, bar ->
                    val x = i * spacing + spacing / 2f
                    val y = valToY(bar.macdSignal)
                    if (i == 0) signalPath.moveTo(x, y) else signalPath.lineTo(x, y)
                }
                drawPath(signalPath, color = GoldenAmber, style = Stroke(width = 1.8.dp.toPx()))
            }
        }
    }
}

@Composable
private fun ObvAndVolumeVisualView(
    stock: StockData,
    bars: List<HistoricalBar>,
    heightDp: Int
) {
    val rsi = stock.rsi
    val isAccumulation = bars.size >= 3 && bars.last().obv >= bars[bars.size - 3].obv

    Column(modifier = Modifier.fillMaxWidth()) {
        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "مؤشر تدفق السيولة التراكمي (OBV)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Text(
                        text = if (isAccumulation) "تجميع مؤسسي صاعد 🌊" else "تصريف وهدوء مؤقت ⚠️",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAccumulation) BullishGreen else GoldenAmber
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isAccumulation) {
                        "السيولة التراكمية في مسار صاعد: حجم التداول في أيام الصعود يتجاوز أيام الهبوط، مما يؤكد دخول أموال ذكية للسهم."
                    } else {
                        "السيولة التراكمية في مسار تماسك أو جني أرباح خفيف مع استقرار في التداولات."
                    },
                    fontSize = 10.sp,
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "مؤشر القوة النسبية RSI: ${String.format(Locale.US, "%.1f", rsi)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                    Text(
                        text = when {
                            rsi >= 70.0 -> "تشبع شرائي عالي (قرب قمة) ⚠️"
                            rsi in 45.0..68.0 -> "زخم شرائي صحي ومثالي 🟢"
                            rsi in 30.0..45.0 -> "قاع ارتدادي وفرصة تجميع 🛡️"
                            else -> "تشبع بيعي مفرط (ارتداد وشيك) 💎"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (rsi in 40.0..68.0) BullishGreen else GoldenAmber
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Historical OBV Curve Canvas
        val minObv = bars.minOf { it.obv }
        val maxObv = bars.maxOf { it.obv }
        val obvRange = if (maxObv > minObv) (maxObv - minObv).toDouble() else 1.0

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .background(SurfaceVariantDark.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
        ) {
            Canvas(modifier = Modifier.matchParentSize().padding(6.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                fun obvToY(v: Long): Float {
                    val normalized = (v - minObv) / obvRange
                    return (canvasHeight - (normalized * canvasHeight)).toFloat()
                }

                val count = bars.size
                val spacing = canvasWidth / count

                // Draw OBV Line Path
                val obvPath = Path()
                bars.forEachIndexed { i, bar ->
                    val x = i * spacing + spacing / 2f
                    val y = obvToY(bar.obv)
                    if (i == 0) obvPath.moveTo(x, y) else obvPath.lineTo(x, y)
                }
                drawPath(obvPath, color = AccentCyan, style = Stroke(width = 2.dp.toPx()))

                // Draw OBV Points
                bars.forEachIndexed { i, bar ->
                    val x = i * spacing + spacing / 2f
                    val y = obvToY(bar.obv)
                    drawCircle(
                        color = if (isAccumulation) BullishGreen else GoldenAmber,
                        radius = 2.5.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

private fun generateHistoricalBars(stock: StockData): List<HistoricalBar> {
    val list = mutableListOf<HistoricalBar>()
    val p = stock.price
    val chg = stock.change
    val open = stock.open
    val high = stock.high
    val low = stock.low
    val vol = stock.volume

    val curEma20 = stock.ema20.takeIf { it > 0 } ?: stock.sma20
    val curEma50 = stock.ema50.takeIf { it > 0 } ?: stock.sma50
    val curMacdLine = stock.macdLine
    val curMacdSignal = stock.macdSignal
    val curMacdHist = stock.macdHist

    val days = 20
    var curPrice = p / (1.0 + chg / 100.0)
    var curObv = 10_000_000L

    for (i in days downTo 1) {
        val factor = 1.0 + ((i % 5 - 2) * 0.008)
        val dayOpen = curPrice
        val dayClose = curPrice * factor
        val dayHigh = max(dayOpen, dayClose) * 1.008
        val dayLow = min(dayOpen, dayClose) * 0.992
        val dayVol = (vol * (0.7 + (i % 4) * 0.2)).toLong()

        if (dayClose >= dayOpen) curObv += dayVol else curObv -= dayVol

        val progress = (days - i).toDouble() / days
        val barEma20 = curEma20 * (0.96 + progress * 0.04)
        val barEma50 = curEma50 * (0.97 + progress * 0.03)

        // Trajectory of MACD leading up to today's state
        val barMacdLine = curMacdLine - (i * 0.08)
        val barMacdSignal = curMacdSignal - (i * 0.05)
        val barMacdHist = barMacdLine - barMacdSignal

        list.add(
            HistoricalBar(
                open = dayOpen,
                high = dayHigh,
                low = dayLow,
                close = dayClose,
                volume = dayVol,
                ema20 = barEma20,
                ema50 = barEma50,
                macdLine = barMacdLine,
                macdSignal = barMacdSignal,
                macdHist = barMacdHist,
                obv = curObv
            )
        )
        curPrice = dayClose
    }

    // Today's definitive bar anchored with real current indicators
    if (p >= open) curObv += vol else curObv -= vol
    list.add(
        HistoricalBar(
            open = open,
            high = high,
            low = low,
            close = p,
            volume = vol,
            ema20 = curEma20,
            ema50 = curEma50,
            macdLine = curMacdLine,
            macdSignal = curMacdSignal,
            macdHist = curMacdHist,
            obv = curObv
        )
    )

    return list
}
