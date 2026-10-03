package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CorporateNews
import com.example.data.model.StockData
import com.example.ui.components.StockFinancialAnalysisCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BearishRedBg
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.BullishGreenBg
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.GoldenAmberBg
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StockCard(
    stock: StockData,
    onSelectStock: (StockData) -> Unit,
    onRecordTrade: (StockData) -> Unit,
    onCalculateAverage: (StockData) -> Unit,
    onToggleAlert: (StockData) -> Unit,
    modifier: Modifier = Modifier,
    portfolioCapital: Double = 50000.0,
    allNews: List<CorporateNews> = emptyList()
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    val dealBudget = portfolioCapital * 0.20 // 20% of portfolio
    val recommendedShares = if (stock.entryPrice > 0) (dealBudget / stock.entryPrice).toLong() else 0L
    val maxRiskEgp = portfolioCapital * 0.02 // 2% risk

    val isBullish = stock.change >= 0
    val changeColor = if (isBullish) BullishGreen else BearishRed
    val borderAccent = Color(stock.confidenceColorHex).copy(alpha = 0.6f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, borderAccent, RoundedCornerShape(16.dp))
            .clickable { onSelectStock(stock) }
            .testTag("stock_card_${stock.symbol}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Symbol, Name, Score, Confidence Grade
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stock.symbol,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Sector Tag
                        Text(
                            text = stock.sector,
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier
                                .background(SurfaceVariantDark, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = stock.description,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }

                // Smart Score badge
                Box(
                    modifier = Modifier
                        .background(BullishGreenBg, RoundedCornerShape(8.dp))
                        .border(1.dp, BullishGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${stock.smartScore}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BullishGreen
                        )
                        Text(
                            text = "Smart",
                            fontSize = 9.sp,
                            color = BullishGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Confidence Grade Pill
                Box(
                    modifier = Modifier
                        .background(Color(stock.confidenceColorHex).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(stock.confidenceColorHex), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stock.confidenceGrade,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(stock.confidenceColorHex)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main metrics row: Price, Change %, RR, Turnover
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price & Change
                Column {
                    Text(
                        text = "${stock.formattedPrice} ج.م",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = stock.formattedChange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = changeColor
                    )
                }

                // R:R Ratio
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "العائد / المخاطرة", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = "${stock.riskRewardRatio}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (stock.riskRewardRatio >= 2.0) BullishGreen else GoldenAmber
                    )
                }

                // RSI
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "RSI الزخم", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = String.format(Locale.US, "%.0f", stock.rsi),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            stock.rsi in 45.0..62.0 -> BullishGreen
                            stock.rsi < 35.0 -> AccentCyan
                            stock.rsi > 70.0 -> BearishRed
                            else -> TextPrimary
                        }
                    )
                }

                // Turnover
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "السيولة اليومية", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = stock.formattedTurnoverMillion,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldenAmber
                    )
                }
            }

            // Daily High/Low Visual Range Bar
            if (stock.high > stock.low && stock.high > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val rangeProgress = ((stock.price - stock.low) / (stock.high - stock.low)).toFloat().coerceIn(0f, 1f)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceVariantDark.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "أدنى: ${stock.formattedLow} ج",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "نطاق الجلسة (الحالي: ${stock.formattedPrice} ج)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = changeColor
                        )
                        Text(
                            text = "أعلى: ${stock.formattedHigh} ج",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .background(SurfaceDark, RoundedCornerShape(3.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(rangeProgress)
                                .height(5.dp)
                                .background(
                                    if (isBullish) BullishGreen else GoldenAmber,
                                    RoundedCornerShape(3.dp)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Indicator tags: 50 & 200 Days, Candlesticks, Upside
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 50/200 Day Status
                if (stock.isGoldenCross) {
                    IndicatorTag(
                        text = "🌟 تقاطع ذهبي 50/200",
                        bgColor = GoldenAmberBg,
                        textColor = GoldenAmber
                    )
                }

                if (stock.distanceSma50 > 0) {
                    IndicatorTag(
                        text = "📈 أعلى SMA50 (+${stock.distanceSma50}%)",
                        bgColor = BullishGreenBg,
                        textColor = BullishGreen
                    )
                } else {
                    IndicatorTag(
                        text = "📉 أدنى SMA50 (${stock.distanceSma50}%)",
                        bgColor = BearishRedBg,
                        textColor = BearishRed
                    )
                }

                if (stock.distanceSma200 > 0) {
                    IndicatorTag(
                        text = "🏛️ اتجاه صاعد رئيسي 200 يوم",
                        bgColor = AccentPurple.copy(alpha = 0.2f),
                        textColor = AccentPurple
                    )
                }

                if (stock.upsideTo52wHigh >= 20.0) {
                    IndicatorTag(
                        text = "🚀 مساحة صعود ${stock.upsideTo52wHigh}%",
                        bgColor = BullishGreenBg,
                        textColor = BullishGreen
                    )
                }

                // Candlestick Pattern tags
                stock.candlePatterns.take(1).forEach { pattern ->
                    IndicatorTag(
                        text = pattern,
                        bgColor = SurfaceVariantDark,
                        textColor = AccentCyan
                    )
                }
            }

            // Ideal Entry & Exit Box with Dual Target & Trailing Stop Guidance
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .border(1.dp, OutlineDark, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "🎯 نطاق الدخول", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = stock.entryRangeFormatted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🛑 وقف الخسارة", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = "${stock.stopLossFormatted} (-${stock.riskPct}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BearishRed
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "🏁 المستهدف 1", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = "${stock.target1Formatted} (+${stock.targetPct}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullishGreen
                        )
                    }
                }

                HorizontalDivider(color = OutlineDark.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🚀 المستهدف 2: ${stock.target2Formatted} (+${String.format(java.util.Locale.US, "%.1f", stock.targetPct * 1.8)}%)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentCyan
                    )
                    Text(
                        text = "🛡️ عند هدف 1: احجز 50% وارفع الوقف للدخول",
                        fontSize = 9.sp,
                        color = GoldenAmber
                    )
                }
            }

            // Calculated Deal Budget Box (20% of Portfolio)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C2417), RoundedCornerShape(8.dp))
                    .border(1.dp, BullishGreen.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "💼 ميزانية الصفقة: ${String.format(Locale.US, "%,.0f", dealBudget)} ج (20% من المحفظة)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullishGreen
                    )
                    Text(
                        text = "🔢 الكمية المقترحة: ${String.format(Locale.US, "%,d", recommendedShares)} سهم • أقصى مخاطرة: -${String.format(Locale.US, "%,.0f", maxRiskEgp)} ج",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            // Always Visible Action Buttons Row (Direct Full Analysis & Direct WhatsApp Share)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onSelectStock(stock) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_detail_${stock.symbol}"),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("التحليل الكامل والشارت 🔍", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                // Direct WhatsApp Share Button
                IconButton(
                    onClick = { com.example.util.ShareHelper.shareStockAnalysisToWhatsApp(context, stock) },
                    modifier = Modifier
                        .background(Color(0xFF25D366).copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                        .size(38.dp)
                        .testTag("btn_share_wa_${stock.symbol}")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "مشاركة على واتساب", tint = Color(0xFF25D366), modifier = Modifier.size(18.dp))
                }

                // Average Price Calculator
                IconButton(
                    onClick = { onCalculateAverage(stock) },
                    modifier = Modifier
                        .background(GoldenAmberBg, RoundedCornerShape(8.dp))
                        .size(38.dp)
                        .testTag("btn_avg_${stock.symbol}")
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = "متوسط السعر", tint = GoldenAmber, modifier = Modifier.size(18.dp))
                }

                // Toggle Alert
                IconButton(
                    onClick = { onToggleAlert(stock) },
                    modifier = Modifier
                        .background(AccentPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .size(38.dp)
                        .testTag("btn_alert_${stock.symbol}")
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = "تنبيه", tint = AccentPurple, modifier = Modifier.size(18.dp))
                }

                // Record Trade
                IconButton(
                    onClick = { onRecordTrade(stock) },
                    modifier = Modifier
                        .background(BullishGreenBg, RoundedCornerShape(8.dp))
                        .size(38.dp)
                        .testTag("btn_record_${stock.symbol}")
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = "تسجيل صفقة", tint = BullishGreen, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun IndicatorTag(
    text: String,
    bgColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
