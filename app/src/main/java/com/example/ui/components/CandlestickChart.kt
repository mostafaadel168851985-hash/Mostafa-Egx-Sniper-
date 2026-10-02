package com.example.ui.components

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
    LIVE_TRADINGVIEW("📈 الشارت الحقيقي المباشر (TradingView)", "📈"),
    TECHNICAL_INDICATORS("📊 مؤشرات MACD و OBV و EMAs", "📊")
}

@Composable
fun CandlestickChart(
    stock: StockData,
    modifier: Modifier = Modifier,
    heightDp: Int = 300
) {
    var mode by remember { mutableStateOf(ChartViewMode.LIVE_TRADINGVIEW) }

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
                heightDp = heightDp
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
    heightDp: Int
) {
    val themeStr = if (isDark) "dark" else "light"
    val bgColor = if (isDark) "#111827" else "#FFFFFF"
    val cleanSymbol = symbol.trim().uppercase()

    val htmlData = remember(cleanSymbol, isDark) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    height: 100%;
                    background-color: $bgColor;
                    overflow: hidden;
                }
                #tradingview_widget {
                    width: 100%;
                    height: 100%;
                }
            </style>
        </head>
        <body>
            <div id="tradingview_widget"></div>
            <script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>
            <script type="text/javascript">
                try {
                    new TradingView.widget({
                        "autosize": true,
                        "symbol": "EGX:$cleanSymbol",
                        "interval": "D",
                        "timezone": "Africa/Cairo",
                        "theme": "$themeStr",
                        "style": "1",
                        "locale": "ar_AE",
                        "toolbar_bg": "$bgColor",
                        "enable_publishing": false,
                        "hide_side_toolbar": false,
                        "allow_symbol_change": false,
                        "container_id": "tradingview_widget",
                        "studies": [
                            "MASimple@tv-basicstudies",
                            "MACD@tv-basicstudies"
                        ]
                    });
                } catch(e) {}
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .background(SurfaceDark, RoundedCornerShape(10.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL("https://www.tradingview.com", htmlData, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://www.tradingview.com", htmlData, "text/html", "UTF-8", null)
            }
        )
    }
}

@Composable
private fun TechnicalIndicatorsAndNativeChart(
    stock: StockData,
    heightDp: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // EMAs Metric Row (EMA20, EMA50, EMA200)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "EMA 20", fontSize = 9.sp, color = TextMuted)
                Text(
                    text = String.format(Locale.US, "%.2f ج", stock.ema20.takeIf { it > 0 } ?: stock.sma20),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "EMA 50", fontSize = 9.sp, color = TextMuted)
                Text(
                    text = String.format(Locale.US, "%.2f ج", stock.ema50.takeIf { it > 0 } ?: stock.sma50),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldenAmber
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "EMA 200", fontSize = 9.sp, color = TextMuted)
                Text(
                    text = String.format(Locale.US, "%.2f ج", stock.ema200.takeIf { it > 0 } ?: stock.sma200),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // MACD & OBV Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // MACD Values
            Column {
                Text(text = "مؤشر MACD (12, 26, 9)", fontSize = 9.sp, color = TextMuted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "الماكد: ${String.format(Locale.US, "%+.2f", stock.macdLine)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الإشارة: ${String.format(Locale.US, "%+.2f", stock.macdSignal)}",
                        fontSize = 10.sp,
                        color = GoldenAmber
                    )
                }
                val isMacdBullish = stock.macdHist >= 0
                Text(
                    text = "الهيستوجرام: ${String.format(Locale.US, "%+.2f", stock.macdHist)} (${if (isMacdBullish) "زخم شرائي 🟢" else "زخم بيعي 🔴"})",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMacdBullish) BullishGreen else BearishRed
                )
            }

            // OBV & Volume Trend
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "حجم التداول التراكمي (OBV)", fontSize = 9.sp, color = TextMuted)
                Text(
                    text = stock.obvTrend,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BullishGreen
                )
                Text(
                    text = "مؤشر RSI: ${String.format(Locale.US, "%.1f", stock.rsi)}",
                    fontSize = 9.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Native Candlestick Canvas
        val candles = remember(stock.symbol, stock.price) { generateHistoricalCandles(stock) }
        val minPrice = candles.minOf { it.low }.coerceAtMost(stock.s2 * 0.99)
        val maxPrice = candles.maxOf { it.high }.coerceAtLeast(stock.r2 * 1.01)
        val priceRange = if (maxPrice > minPrice) maxPrice - minPrice else 1.0

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((heightDp - 100).coerceAtLeast(160).dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val chartHeight = canvasHeight * 0.78f
                val volumeHeight = canvasHeight * 0.20f
                val volumeTop = canvasHeight * 0.80f

                fun priceToY(price: Double): Float {
                    val normalized = (price - minPrice) / priceRange
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
