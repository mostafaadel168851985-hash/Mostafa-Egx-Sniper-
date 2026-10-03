package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CorporateNews
import com.example.data.model.StockData
import com.example.ui.components.CandlestickChart
import com.example.ui.components.StockFinancialAnalysisCard
import com.example.ui.components.StockRelatedNewsCard
import com.example.ui.components.TraderProfileStrategyView
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BearishRed
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
import java.net.URLEncoder
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    stock: StockData,
    onBack: () -> Unit,
    onCalculateAverage: (StockData) -> Unit,
    onRecordTrade: (StockData) -> Unit,
    modifier: Modifier = Modifier,
    portfolioCapital: Double = 50000.0,
    allNews: List<CorporateNews> = emptyList(),
    onNavigateToPortfolio: () -> Unit = {}
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val initialBudget = (portfolioCapital * 0.20).coerceAtLeast(1000.0)
    var dealBudgetInput by remember(portfolioCapital) { mutableStateOf(String.format(Locale.US, "%.0f", initialBudget)) }
    val dealBudget = dealBudgetInput.toDoubleOrNull() ?: initialBudget

    val isBullish = stock.change >= 0
    val changeColor = if (isBullish) BullishGreen else BearishRed

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${stock.symbol} - ${stock.description}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "${stock.sector} • ${stock.formattedPrice} ج.م (${stock.formattedChange})",
                            fontSize = 12.sp,
                            color = changeColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            com.example.util.ShareHelper.shareStockAnalysisToWhatsApp(context, stock)
                        },
                        modifier = Modifier.testTag("btn_share_whatsapp")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "مشاركة على واتساب", tint = BullishGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BackgroundDark,
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp)
                .testTag("stock_detail_column"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Quick Action Bar: WhatsApp Share, Average Calculator, Trade Recording
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            com.example.util.ShareHelper.shareStockAnalysisToWhatsApp(context, stock)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_detail_share_whatsapp"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مشاركة التحليل عبر واتساب",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = { onCalculateAverage(stock) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldenAmber),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenAmber),
                        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 10.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("متوسط التكلفة", fontSize = 11.sp)
                    }

                    IconButton(
                        onClick = { onRecordTrade(stock) },
                        modifier = Modifier
                            .background(BullishGreenBg, RoundedCornerShape(10.dp))
                            .border(1.dp, BullishGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .size(42.dp)
                    ) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = "تسجيل صفقة", tint = BullishGreen)
                    }
                }
            }

            // Hero Candlestick Chart
            item {
                Spacer(modifier = Modifier.height(10.dp))
                CandlestickChart(stock = stock, heightDp = 220)
            }

            // Ideal Entry / Exit Action Box
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BullishGreen, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🎯 مستويات التداول المثالية المحددة لجلسة الغد",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BullishGreen
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .background(BullishGreenBg, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "R:R ${stock.riskRewardRatio}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BullishGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "🎯 نطاق الدخول المقترح", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = stock.entryRangeFormatted,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🛑 وقف الخسارة الحاسم", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${stock.stopLossFormatted} (-${stock.riskPct}%)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BearishRed
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "🏁 المستهدف الأول R1", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${stock.target1Formatted} (+${stock.targetPct}%)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BullishGreen
                                )
                            }
                        }
                    }
                }
            }

            // 50 & 200 Days Historical Moving Averages Deep-Dive
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🏛️ تحليل الاتجاه التاريخي (50 و 200 يوم)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "متوسط 50 يوم (SMA50)", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%.3f", stock.sma50)} ج",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenAmber
                                )
                                Text(
                                    text = if (stock.distanceSma50 >= 0) "+${stock.distanceSma50}% (أعلى)" else "${stock.distanceSma50}% (أدنى)",
                                    fontSize = 11.sp,
                                    color = if (stock.distanceSma50 >= 0) BullishGreen else BearishRed
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "متوسط 200 يوم (SMA200)", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%.3f", stock.sma200)} ج",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentPurple
                                )
                                Text(
                                    text = if (stock.distanceSma200 >= 0) "+${stock.distanceSma200}% (أعلى)" else "${stock.distanceSma200}% (أدنى)",
                                    fontSize = 11.sp,
                                    color = if (stock.distanceSma200 >= 0) BullishGreen else BearishRed
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "حالة التقاطع", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = if (stock.isGoldenCross) "🌟 تقاطع ذهبي صاعد" else "⚠️ تقاطع سلبي",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (stock.isGoldenCross) GoldenAmber else BearishRed
                                )
                            }
                        }

                        HorizontalDivider(color = OutlineDark, modifier = Modifier.padding(vertical = 10.dp))

                        // 52-week High/Low Channel
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "القمة السنوية (52 أسبوع)", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%.3f", stock.high52)} ج",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "مساحة الصعود المتبقية للقمة", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "+${stock.upsideTo52wHigh}%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (stock.upsideTo52wHigh >= 20.0) BullishGreen else GoldenAmber
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "القاع السنوي (52 أسبوع)", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%.3f", stock.low52)} ج",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 3-Profile Trader Strategy Matrix (Scalper, Swing, Investor)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                TraderProfileStrategyView(stock = stock)
            }

            // Comprehensive Fundamental & Financial Analysis (P/E, ROE, Fair Value, Growth Advice)
            stock.financialMetrics?.let { financial ->
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    StockFinancialAnalysisCard(
                        financial = financial,
                        currentPrice = stock.price
                    )
                }
            }

            // Pivot Support & Resistance Table
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "📐 مستويات الدعم والمقاومة الكلاسيكية (Pivot Points)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        PivotRow(level = "🔴 مقاومة ثانية R2", price = stock.r2, label = "مقاومة قوية ومستهدف أقصى", color = BearishRed)
                        PivotRow(level = "🔴 مقاومة أولى R1", price = stock.r1, label = "المستهدف الأساسي الأول", color = BearishRed.copy(alpha = 0.8f))
                        PivotRow(level = "🟡 نقطة الارتكاز PP", price = stock.pp, label = "المحور الرئيسي لاتجاه الجلسة", color = GoldenAmber)
                        PivotRow(level = "🟢 دعم أول S1", price = stock.s1, label = "منطقة الشراء والتبريد الأولى", color = BullishGreen.copy(alpha = 0.8f))
                        PivotRow(level = "🟢 دعم ثاني S2", price = stock.s2, label = "دعم حاسم وكسره وقف خسارة", color = BullishGreen)
                    }
                }
            }

            // Candlestick Pattern Recognition
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🕯️ تحليل نماذج الشموع اليابانية وجودة الحركة",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (stock.candlePatterns.isNotEmpty()) {
                            stock.candlePatterns.forEach { p ->
                                Text(
                                    text = p,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "لا يوجد نموذج شمعة شاذ اليوم - استقرار نسبي",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        HorizontalDivider(color = OutlineDark, modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "قوة الإغلاق: ${stock.breakoutQuality.closeStrengthPct.toInt()}%", fontSize = 11.sp, color = TextSecondary)
                            Text(text = "نطاق الحركة: ${stock.breakoutQuality.dayRangePct.toInt()}%", fontSize = 11.sp, color = TextSecondary)
                            Text(text = "السيولة: ${stock.turnoverRatingArabic}", fontSize = 11.sp, color = GoldenAmber)
                        }
                    }
                }
            }

            // Real-time Stock Related Disclosures & News
            item {
                Spacer(modifier = Modifier.height(14.dp))
                StockRelatedNewsCard(stock = stock, allNews = allNews)
            }

            // 3-Level Position Sizing Entry Plan
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🏹 خطة الدخول الذكية (3 مستويات) حسب ميزانيتك",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = dealBudgetInput,
                            onValueChange = { dealBudgetInput = it },
                            label = { Text("الميزانية المخصصة للصفقة (ج.م)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldenAmber,
                                unfocusedBorderColor = OutlineDark,
                                focusedContainerColor = SurfaceVariantDark,
                                unfocusedContainerColor = SurfaceVariantDark
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💼 محسوبة تلقائياً: 20% من محفظتك (${String.format(Locale.US, "%,.0f", portfolioCapital)} ج)",
                                fontSize = 10.sp,
                                color = BullishGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "تعديل المحفظة ⚙️",
                                fontSize = 11.sp,
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { onNavigateToPortfolio() }
                                    .padding(vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val entryLvl1 = stock.entryPrice
                        val entryLvl2 = stock.s1
                        val entryLvl3 = stock.entryPrice * 1.02

                        val sharesLvl1 = if (entryLvl1 > 0) ((dealBudget * 0.50) / entryLvl1).toLong() else 0L
                        val sharesLvl2 = if (entryLvl2 > 0) ((dealBudget * 0.30) / entryLvl2).toLong() else 0L
                        val sharesLvl3 = if (entryLvl3 > 0) ((dealBudget * 0.20) / entryLvl3).toLong() else 0L

                        LevelPlanRow(
                            level = "المستوى 1 (50% من السيولة)",
                            price = entryLvl1,
                            shares = sharesLvl1,
                            desc = "دخول أساسي بنطاق ${stock.entryRangeFormatted}",
                            color = BullishGreen
                        )
                        LevelPlanRow(
                            level = "المستوى 2 (30% من السيولة)",
                            price = entryLvl2,
                            shares = sharesLvl2,
                            desc = "تعزيز ودعم إضافي عند ${String.format(Locale.US, "%.3f", entryLvl2)} ج",
                            color = GoldenAmber
                        )
                        LevelPlanRow(
                            level = "المستوى 3 (20% من السيولة)",
                            price = entryLvl3,
                            shares = sharesLvl3,
                            desc = "تأكيد الاختراق وزيادة الكمية عند ${String.format(Locale.US, "%.3f", entryLvl3)} ج",
                            color = AccentCyan
                        )
                    }
                }
            }

            // Action Buttons
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = { com.example.util.ShareHelper.shareStockAnalysisToWhatsApp(context, stock) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_share_whatsapp_full"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مشاركة تحليل السهم على واتساب 📲", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onRecordTrade(stock) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_record_trade_detail"),
                        colors = ButtonDefaults.buttonColors(containerColor = BullishGreen)
                    ) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تسجيل في سجل الصفقات", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { onCalculateAverage(stock) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_calc_avg_detail"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldenAmber)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حساب متوسط السعر", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PivotRow(level: String, price: Double, label: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = level, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 10.sp, color = TextMuted)
        }
        Text(
            text = "${String.format(Locale.US, "%.3f", price)} ج",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun LevelPlanRow(level: String, price: Double, shares: Long, desc: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = level, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                Text(text = desc, fontSize = 10.sp, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${String.format(Locale.US, "%.3f", price)} ج", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "${String.format(Locale.US, "%,d", shares)} سهم", fontSize = 11.sp, color = GoldenAmber)
            }
        }
    }
}
