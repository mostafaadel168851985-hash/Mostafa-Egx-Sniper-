package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioAllocationScreen(
    currentCapital: Double,
    currentRiskPct: Double,
    onSavePortfolio: (Double, Double) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var capitalInput by remember { mutableStateOf(String.format(Locale.US, "%.0f", currentCapital)) }
    var riskInput by remember { mutableStateOf(String.format(Locale.US, "%.1f", currentRiskPct)) }

    val capital = capitalInput.toDoubleOrNull() ?: currentCapital
    val riskPct = riskInput.toDoubleOrNull() ?: currentRiskPct

    // Optimal allocations
    val fastTradingPool = capital * 0.40 // 40% for fast momentum & breakouts
    val swingTradingPool = capital * 0.40 // 40% for uptrends & corrections
    val cashEmergencyBuffer = capital * 0.20 // 20% untouched cash buffer

    val dealBudget = capital * 0.20 // 20% max per stock
    val maxRiskPerTradeEgp = capital * (riskPct / 100.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = GoldenAmber)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إدارة وتوزيع رأس مال المحفظة 💼",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_portfolio")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BackgroundDark,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp)
                .testTag("portfolio_allocation_column"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Capital Input Card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldenAmber.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💰 إجمالي رأس مال محفظتك الاستثمارية",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                        Text(
                            text = "بناءً على هذا الرقم، سيحسب التطبيق ميزانية كل صفقة وعدد الأسهم الآمن تلقائياً",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = capitalInput,
                                onValueChange = { capitalInput = it },
                                label = { Text("رأس المال (بالجنيه المصري)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("input_portfolio_capital"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldenAmber,
                                    unfocusedBorderColor = OutlineDark,
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark
                                )
                            )

                            OutlinedTextField(
                                value = riskInput,
                                onValueChange = { riskInput = it },
                                label = { Text("المخاطرة %", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier
                                    .weight(0.7f)
                                    .testTag("input_risk_pct"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldenAmber,
                                    unfocusedBorderColor = OutlineDark,
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark
                                )
                            )
                        }

                        // Quick presets chips
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "اختر مبالغ سريعة:", fontSize = 10.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("30000", "50000", "100000", "250000").forEach { amount ->
                                val isSelected = capitalInput == amount
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) GoldenAmber else SurfaceVariantDark,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { capitalInput = amount }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${amount.toInt() / 1000} ألف",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onSavePortfolio(capital, riskPct)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_save_portfolio"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("حفظ وتحديث حسابات التطبيق 💾", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Automatic Deal Budget Result Banner
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, BullishGreen, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261C))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = BullishGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "💼 ميزانية الصفقة التلقائية المطبقة لكل سهم",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "تظهر تلقائياً الآن داخل كارت كل سهم وشاشة تحليله",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        HorizontalDivider(color = OutlineDark, modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "ميزانية الدخول لكل سهم (20% من المحفظة)", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%,.0f", dealBudget)} ج.م",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BullishGreen
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "أقصى مخاطرة مادية للصفقة (${riskPct}%)", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%,.0f", maxRiskPerTradeEgp)} ج.م",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenAmber
                                )
                            }
                        }
                    }
                }
            }

            // Optimal Capital Breakdown (3 Buckets)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PieChart, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📊 التوزيع الأمثل لرأس مال المحفظة (${String.format(Locale.US, "%,.0f", capital)} ج)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bucket 1: Fast Momentum & Breakout (40%)
            item {
                AllocationCard(
                    title = "1. صفقات سريعة ومضاربة (40% من المحفظة)",
                    subtitle = "مرشحات الغد 🎯 • فرص الاختراق السريع ⚡",
                    amount = fastTradingPool,
                    pct = "40%",
                    color = AccentCyan,
                    desc = "موزعة على صفقتين كحد أقصى (كل صفقة بـ ${String.format(Locale.US, "%,.0f", dealBudget)} ج). هدف سريع ووقف صارم."
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bucket 2: Swing & Uptrend (40%)
            item {
                AllocationCard(
                    title = "2. صفقات سوينغ ومتوسطة (40% من المحفظة)",
                    subtitle = "بداية الصعود 🚀 • صيد التصحيحات 🌊",
                    amount = swingTradingPool,
                    pct = "40%",
                    color = BullishGreen,
                    desc = "موزعة على مركزين استثماريين لاقتناص موجات صاعدة ممتدة تصل لأهداف ثانية كبيرة."
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bucket 3: Emergency Cash Buffer (20%)
            item {
                AllocationCard(
                    title = "3. كاش أمان واقتناص القيعان (20% من المحفظة)",
                    subtitle = "سيولة حرة 🛡️ • صيد القيعان والارتداد 💎",
                    amount = cashEmergencyBuffer,
                    pct = "20%",
                    color = GoldenAmber,
                    desc = "كاش محفوظ في المحفظة لا يتم فتحه إلا للتعزيز عند الدعم أو لاقتناص هبوط مفاجئ في السوق."
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Golden Capital Rules
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldenAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "📜 القواعد الذهبية لحماية وتنمية رأس المال",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldenAmber
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "1. لا تستثمر أكثر من 20% في أي سهم واحد مهما كانت الإشارة مغرية.\n2. لا تفتح أكثر من 3 إلى 4 أسهم في نفس الوقت لتتمكن من متابعتها بدقة.\n3. عند تحقيق الهدف الأول (Target 1)، بادر ببيع 50% من الكمية وارفع الوقف لسعر الدخول فوراً.\n4. التزم دائماً بوقف الخسارة الموضح؛ فهو صمام الأمان الذي يحمي 98% من محفظتك.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AllocationCard(
    title: String,
    subtitle: String,
    amount: Double,
    pct: String,
    color: Color,
    desc: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
                    Text(text = subtitle, fontSize = 10.sp, color = TextMuted)
                }
                Box(
                    modifier = Modifier
                        .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = pct, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${String.format(Locale.US, "%,.0f", amount)} ج.م",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, fontSize = 10.sp, color = TextSecondary, lineHeight = 15.sp)
        }
    }
}
