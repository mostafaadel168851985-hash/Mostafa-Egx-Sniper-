package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.StockData
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TerminalTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class CandlePoint(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)

enum class ChartViewMode(val title: String, val icon: String) {
    TECHNICAL_INDICATORS("🎯 مؤشرات الشراء والقرار الفني (MACD & EMA)", "🎯"),
    LIVE_TRADINGVIEW("📈 الشارت المباشر (TradingView)", "📈")
}

@Composable
fun CandlestickChart(
    stock: StockData,
    modifier: Modifier = Modifier,
    heightDp: Int = 320
) {
    var mode by remember { mutableStateOf(ChartViewMode.TECHNICAL_INDICATORS) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(14.dp))
            .border(1.dp, OutlineDark, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        // Tab switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ChartViewMode.values().forEach { m ->
                val isSelected = mode == m
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) AccentCyan else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { mode = m }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = m.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (mode == ChartViewMode.LIVE_TRADINGVIEW) {
            TradingViewLiveChartWidget(
                symbol = stock.symbol,
                isDark = TerminalTheme.isDark,
                heightDp = heightDp,
                onSwitchToIndicators = { mode = ChartViewMode.TECHNICAL_INDICATORS }
            )
        } else {
            TechnicalIndicatorsAndNativeChart(
                stock = stock,
                heightDp = heightDp
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun TradingViewLiveChartWidget(
    symbol: String,
    isDark: Boolean,
    heightDp: Int,
    onSwitchToIndicators: () -> Unit
) {
    val themeStr = if (isDark) "dark" else "light"
    val cleanSymbol = symbol.trim().uppercase()
    val embedUrl = remember(cleanSymbol, isDark) {
        "https://s.tradingview.com/widgetembed/?symbol=EGX%3A$cleanSymbol&interval=D&symboledit=1&saveimage=0&toolbarbg=${if (isDark) "111827" else "ffffff"}&theme=$themeStr&style=1&timezone=Africa%2FCairo&locale=ar_AE"
    }

    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var reloadTrigger by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .background(SurfaceVariantDark, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (!hasError) {
            key(reloadTrigger) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        settings.userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
                        
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }

                            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                if (request?.isForMainFrame == true) {
                                    hasError = true
                                    isLoading = false
                                }
                            }
                        }
                        loadUrl(embedUrl)
                    }
                }
            )
            }
        }

        if (isLoading && !hasError) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = AccentCyan,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "جاري تحميل شارت TradingView اللحظي...",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        if (hasError) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = GoldenAmber,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "تعذر تحميل شارت TradingView المباشر",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "تأكد من الاتصال بالإنترنت أو استخدم الرسم البياني الفني المدمج",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            hasError = false
                            isLoading = true
                            reloadTrigger++
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إعادة المحاولة", color = Color.Black, fontSize = 11.sp)
                    }
                    Button(
                        onClick = onSwitchToIndicators,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("الرسم الفني المدمج", color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TechnicalIndicatorsAndNativeChart(
    stock: StockData,
    heightDp: Int
) {
    val price = stock.price
    val ema20 = stock.ema20.takeIf { it > 0 } ?: stock.sma20
    val ema50 = stock.ema50.takeIf { it > 0 } ?: stock.sma50
    val ema200 = stock.ema200.takeIf { it > 0 } ?: stock.sma200
    val macdLine = stock.macdLine
    val macdSignal = stock.macdSignal
    val macdHist = stock.macdHist

    // Technical Evaluation
    val isEmaBullish = price > ema20 && ema20 >= ema50
    val isEmaPullback = price in (ema50 * 0.98)..(ema20 * 1.02)
    val isMacdGoldenCross = macdLine >= macdSignal
    val isMacdBullishHist = macdHist >= 0

    // Clear Actionable Decision
    val (decisionTitle, decisionDesc, decisionColor, decisionIcon) = when {
        isEmaBullish && isMacdGoldenCross && isMacdBullishHist -> Quadruple(
            "🟢 إشارة شراء قوية وتأكيد فني صاعد",
            "المؤشرات في أفضل وضعية: السعر يتداول أعلى متوسطات 20 و 50 يوم مع تقاطع إيجابي للماكد وزخم شرائي قوي يدعم استمرار الصعود 🚀",
            BullishGreen,
            Icons.Default.TrendingUp
        )
        isEmaBullish && isMacdGoldenCross -> Quadruple(
            "📈 ميل صاعد إيجابي (مناسب للشراء والدخول)",
            "السعر فوق المتوسطات المتحركة والماكد في مسار صاعد، الاتجاه العام إيجابي ويدعم تحقيق المستهدفات.",
            BullishGreen,
            Icons.Default.TrendingUp
        )
        isEmaPullback || (isMacdGoldenCross && !isEmaBullish) -> Quadruple(
            "⏳ منطقة مراقبة واختبار دعم (ترقب تأكيد الارتداد)",
            "السعر يختبر متوسط الدعم 20/50 يوم مع بداية تشكل تقاطع إيجابي في الماكد؛ يفضل انتظار شمعة ارتداد خضراء قبل الشراء.",
            GoldenAmber,
            Icons.Default.Info
        )
        else -> Quadruple(
            "⚠️ إشارة تصحيح وهبوط (لا ينصح بالشراء حالياً)",
            "السعر يتداول أدنى المتوسطات المتحركة مع تقاطع بيعي لمؤشر MACD؛ تجنب الشراء اللحظي وانتظر استقرار السعر.",
            BearishRed,
            Icons.Default.TrendingDown
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // 1. Actionable Decision Card (بطاقة القرار الفني المباشر)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = decisionColor.copy(alpha = 0.12f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, decisionColor.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(decisionIcon, contentDescription = null, tint = decisionColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = decisionTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = decisionColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = decisionDesc,
                    fontSize = 10.sp,
                    color = TextPrimary,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. EMA Metric & Explanation Row
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "EMA 20 (المتوسط السريع)", fontSize = 9.sp, color = TextMuted)
                        Text(
                            text = String.format(Locale.US, "%.2f ج", ema20),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "EMA 50 (المتوسط الرئيسي)", fontSize = 9.sp, color = TextMuted)
                        Text(
                            text = String.format(Locale.US, "%.2f ج", ema50),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "EMA 200 (الاتجاه التاريخي)", fontSize = 9.sp, color = TextMuted)
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
                    price > ema20 && ema20 > ema50 -> "✅ السعر يتداول أعلى كافة المتوسطات (دعم صاعد متتابع)"
                    price > ema20 -> "📈 السعر يخترق متوسط 20 يوم للأعلى (إشارة دخول لحظية)"
                    price in ema50..ema20 -> "⏳ السعر يصحح بين متوسط 20 و 50 يوم (منطقة ارتداد مرتقبة)"
                    else -> "🔻 السعر أدنى متوسط 50 يوم (المتوسطات تمثل مقاومات تضغط على السعر)"
                }
                Text(
                    text = emaExplanation,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (price > ema20) BullishGreen else GoldenAmber
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. MACD Metric & Explanation Row
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "مؤشر MACD (12, 26, 9)", fontSize = 9.sp, color = TextMuted)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "الماكد: ${String.format(Locale.US, "%+.2f", macdLine)}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الإشارة: ${String.format(Locale.US, "%+.2f", macdSignal)}",
                                fontSize = 10.sp,
                                color = GoldenAmber
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isMacdGoldenCross) "تقاطع إيجابي صاعد 🟢" else "تقاطع سلبي هابط 🔴",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMacdGoldenCross) BullishGreen else BearishRed
                        )
                        Text(
                            text = "الهيستوجرام: ${String.format(Locale.US, "%+.2f", macdHist)} (${if (isMacdBullishHist) "عزم شراء" else "عزم بيع"})",
                            fontSize = 9.sp,
                            color = if (isMacdBullishHist) BullishGreen else BearishRed
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                val macdExplanation = if (isMacdGoldenCross && isMacdBullishHist) {
                    "✅ مؤشر الماكد يؤكد تفوق قوى الشراء وتوسع الزخم الصاعد 🚀"
                } else if (isMacdGoldenCross) {
                    "⏳ تقاطع إيجابي جديد في طور تشكل الزخم الشرائي"
                } else {
                    "🔻 الماكد في المنطقة السلبية ويشير إلى ضغط بيعي مؤقت"
                }
                Text(
                    text = macdExplanation,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isMacdGoldenCross) BullishGreen else TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Native Candlestick Canvas
        val candles = remember(stock.symbol, stock.price) { generateHistoricalCandles(stock) }
        val minPrice = candles.minOf { it.low }.coerceAtMost(stock.s2 * 0.99)
        val maxPrice = candles.maxOf { it.high }.coerceAtLeast(stock.r2 * 1.01)
        val priceRange = if (maxPrice > minPrice) maxPrice - minPrice else 1.0

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val chartHeight = canvasHeight * 0.78f
                val volumeHeight = canvasHeight * 0.20f

                fun priceToY(p: Double): Float {
                    val normalized = (p - minPrice) / priceRange
                    return (chartHeight - (normalized * chartHeight)).toFloat()
                }

                val candleCount = candles.size
                val spacing = canvasWidth / candleCount
                val candleWidth = spacing * 0.65f

                // Draw S1/R1 Guide lines
                if (stock.r1 in minPrice..maxPrice) {
                    val y = priceToY(stock.r1)
                    drawLine(
                        color = BearishRed.copy(alpha = 0.4f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                    )
                }

                if (stock.s1 in minPrice..maxPrice) {
                    val y = priceToY(stock.s1)
                    drawLine(
                        color = BullishGreen.copy(alpha = 0.4f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                    )
                }

                // Draw Candles & Volume
                val maxVol = candles.maxOf { it.volume }.coerceAtLeast(1L)

                candles.forEachIndexed { i, candle ->
                    val x = i * spacing + spacing / 2f
                    val openY = priceToY(candle.open)
                    val closeY = priceToY(candle.close)
                    val highY = priceToY(candle.high)
                    val lowY = priceToY(candle.low)

                    val isBull = candle.close >= candle.open
                    val candleColor = if (isBull) Color(0xFF10B981) else Color(0xFFEF4444)

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

                    // Volume Bar
                    val volBarHeight = ((candle.volume.toFloat() / maxVol) * volumeHeight).coerceAtLeast(2f)
                    val volY = canvasHeight - volBarHeight
                    drawRect(
                        color = candleColor.copy(alpha = 0.35f),
                        topLeft = Offset(x - candleWidth / 2f, volY),
                        size = Size(candleWidth, volBarHeight)
                    )
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

private fun generateHistoricalCandles(stock: StockData): List<CandlePoint> {
    val list = mutableListOf<CandlePoint>()
    val p = stock.price
    val chg = stock.change
    val open = stock.open
    val high = stock.high
    val low = stock.low
    val vol = stock.volume

    // Anchor past 14 days realistically
    var curPrice = p / (1.0 + chg / 100.0)
    for (i in 14 downTo 1) {
        val factor = 1.0 + ((i % 5 - 2) * 0.008)
        val dayOpen = curPrice
        val dayClose = curPrice * factor
        val dayHigh = max(dayOpen, dayClose) * 1.008
        val dayLow = min(dayOpen, dayClose) * 0.992
        val dayVol = (vol * (0.7 + (i % 4) * 0.2)).toLong()
        list.add(CandlePoint(dayOpen, dayHigh, dayLow, dayClose, dayVol))
        curPrice = dayClose
    }

    // Today's candle
    list.add(CandlePoint(open, high, low, p, vol))
    return list
}
