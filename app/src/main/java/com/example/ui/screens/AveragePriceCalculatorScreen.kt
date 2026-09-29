package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.data.model.StockData
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

enum class CalculatorTab(val title: String) {
    POSITION_SIZING("حجم الصفقة وإدارة المخاطر 🛡️"),
    DCA_AVERAGING("تعديل المتوسط والتبريد ⚖️")
}

@Composable
fun AveragePriceCalculatorScreen(
    prefilledStock: StockData?,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(CalculatorTab.POSITION_SIZING) }

    // --- State for Position Sizing Calculator ---
    var portfolioText by remember { mutableStateOf("100000") }
    var riskPctText by remember { mutableStateOf("2.0") }
    var entryPriceText by remember {
        mutableStateOf(if (prefilledStock != null) String.format(Locale.US, "%.3f", prefilledStock.entryPrice) else "25.00")
    }
    var stopLossText by remember {
        mutableStateOf(if (prefilledStock != null) String.format(Locale.US, "%.3f", prefilledStock.stopLoss) else "24.00")
    }
    var target1Text by remember {
        mutableStateOf(if (prefilledStock != null) String.format(Locale.US, "%.3f", prefilledStock.target1) else "27.00")
    }
    var target2Text by remember {
        mutableStateOf(if (prefilledStock != null) String.format(Locale.US, "%.3f", prefilledStock.target2) else "28.80")
    }

    // --- State for DCA / Average Price Calculator ---
    var oldPriceText by remember { mutableStateOf(if (prefilledStock != null) String.format(Locale.US, "%.3f", prefilledStock.price * 1.15) else "25.00") }
    var oldQtyText by remember { mutableStateOf("1000") }
    var newPriceText by remember { mutableStateOf(if (prefilledStock != null) String.format(Locale.US, "%.3f", prefilledStock.price) else "20.00") }
    var newQtyText by remember { mutableStateOf("1500") }
    var commissionPctText by remember { mutableStateOf("0.25") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp)
            .testTag("average_calculator_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (activeTab == CalculatorTab.POSITION_SIZING) Icons.Default.Shield else Icons.Default.Calculate,
                    contentDescription = null,
                    tint = GoldenAmber,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (activeTab == CalculatorTab.POSITION_SIZING) "حاسبة إدارة المخاطر وتحديد حجم الصفقة 🛡️" else "حاسبة متوسط السعر والتبريد الذكي ⚖️",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (activeTab == CalculatorTab.POSITION_SIZING) "حساب الكمية الآمنة للشراء لحماية رأس المال من الخسائر" else "تعديل التكلفة وحساب نقطة التعادل بالجنيه المصري",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = {
                        if (activeTab == CalculatorTab.POSITION_SIZING) {
                            portfolioText = "100000"
                            riskPctText = "2.0"
                            entryPriceText = if (prefilledStock != null) String.format(Locale.US, "%.3f", prefilledStock.entryPrice) else "25.00"
                            stopLossText = if (prefilledStock != null) String.format(Locale.US, "%.3f", prefilledStock.stopLoss) else "24.00"
                        } else {
                            oldPriceText = "25.00"
                            oldQtyText = "1000"
                            newPriceText = "20.00"
                            newQtyText = "1500"
                        }
                    }
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "إعادة ضبط", tint = TextMuted)
                }
            }
        }

        // Tab Switcher
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CalculatorTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) AccentCyan else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { activeTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else TextPrimary
                        )
                    }
                }
            }
        }

        // Prefilled Stock Banner if available
        if (prefilledStock != null) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                        .border(1.dp, OutlineDark, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "سهم مطبق: ", fontSize = 11.sp, color = TextMuted)
                    Text(text = "${prefilledStock.symbol} (${prefilledStock.name})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "السعر الحالي: ${String.format(Locale.US, "%.2f", prefilledStock.price)} ج", fontSize = 11.sp, color = GoldenAmber)
                }
            }
        }

        // RENDER ACTIVE TAB
        if (activeTab == CalculatorTab.POSITION_SIZING) {
            // ==========================================
            // TAB 1: POSITION SIZING & RISK MANAGEMENT
            // ==========================================
            val portfolio = portfolioText.toDoubleOrNull() ?: 100000.0
            val riskPct = (riskPctText.toDoubleOrNull() ?: 2.0) / 100.0
            val entry = entryPriceText.toDoubleOrNull() ?: 25.0
            val stopLoss = stopLossText.toDoubleOrNull() ?: 24.0
            val target1 = target1Text.toDoubleOrNull() ?: (entry * 1.06)
            val target2 = target2Text.toDoubleOrNull() ?: (entry * 1.12)

            val maxRiskEgp = portfolio * riskPct
            val perShareRisk = (entry - stopLoss).coerceAtLeast(0.001)
            val recommendedShares = if (perShareRisk > 0) (maxRiskEgp / perShareRisk).toLong() else 0L
            val positionCapital = recommendedShares * entry
            val portfolioAllocPct = if (portfolio > 0) (positionCapital / portfolio) * 100.0 else 0.0

            val gainT1 = recommendedShares * 0.5 * (target1 - entry)
            val gainT2 = recommendedShares * 0.5 * (target2 - entry)
            val totalProfit = (gainT1 + gainT2).coerceAtLeast(0.0)
            val netRR = if (maxRiskEgp > 0) totalProfit / maxRiskEgp else 0.0

            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💼 1. مدخلات رأس المال والمخاطرة",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = portfolioText,
                                onValueChange = { portfolioText = it },
                                label = { Text("رأس مال المحفظة (ج)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.2f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = OutlineDark,
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark
                                )
                            )

                            OutlinedTextField(
                                value = riskPctText,
                                onValueChange = { riskPctText = it },
                                label = { Text("نسبة المخاطرة %", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(0.8f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = OutlineDark,
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark
                                )
                            )
                        }

                        // Quick Risk selector chips
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("1.0", "1.5", "2.0", "3.0").forEach { r ->
                                val isSelected = riskPctText == r
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) GoldenAmber else SurfaceVariantDark,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { riskPctText = r }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$r%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Entry, Stop Loss & Targets Inputs
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🎯 2. مستويات الصفقة (الدخول والوقف والأهداف)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = entryPriceText,
                                onValueChange = { entryPriceText = it },
                                label = { Text("سعر الدخول (ج)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = OutlineDark,
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark
                                )
                            )

                            OutlinedTextField(
                                value = stopLossText,
                                onValueChange = { stopLossText = it },
                                label = { Text("وقف الخسارة (ج)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BearishRed,
                                    unfocusedBorderColor = OutlineDark,
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = target1Text,
                                onValueChange = { target1Text = it },
                                label = { Text("المستهدف الأول (ج)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BullishGreen,
                                    unfocusedBorderColor = OutlineDark,
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark
                                )
                            )

                            OutlinedTextField(
                                value = target2Text,
                                onValueChange = { target2Text = it },
                                label = { Text("المستهدف الثاني (ج)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BullishGreen,
                                    unfocusedBorderColor = OutlineDark,
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark
                                )
                            )
                        }
                    }
                }
            }

            // Results: Position Sizing Recommendation Card
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, BullishGreen, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F231B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "الكمية الموصى بشرائها (حجم الصفقة الآمن)", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%,d", recommendedShares)} سهم",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BullishGreen
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "أقصى خسارة لو كُسر الوقف", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = "-${String.format(Locale.US, "%,.0f", maxRiskEgp)} ج",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BearishRed
                                )
                            }
                        }

                        HorizontalDivider(color = OutlineDark, modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "قيمة الصفقة الإجمالية", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%,.0f", positionCapital)} ج",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "حصة الصفقة من المحفظة", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", portfolioAllocPct)}%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "معدل العائد / المخاطرة", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "1 : ${String.format(Locale.US, "%.2f", netRR)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenAmber
                                )
                            }
                        }
                    }
                }
            }

            // Smart Profit Taking & Trailing Stop Plan
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = GoldenAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🛡️ خطة جني الأرباح وتأمين الصفقة (استراتيجية المحترفين)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldenAmber
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Target 1 Plan
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "المستهدف الأول: ${String.format(Locale.US, "%.2f", target1)} ج", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BullishGreen)
                                Text(text = "• قم ببيع 50% من الأسهم لحجز ربح: +${String.format(Locale.US, "%,.0f", gainT1)} ج\n• ارفع وقف الخسارة للنصف المتبقي إلى سعر الدخول (${entry} ج) لحماية رأس المال فوراً!", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Target 2 Plan
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "المستهدف الثاني: ${String.format(Locale.US, "%.2f", target2)} ج", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                                Text(text = "• بيع النصف الثاني بربح إضافي: +${String.format(Locale.US, "%,.0f", gainT2)} ج\n• إجمالي الربح الكلي المحقق: +${String.format(Locale.US, "%,.0f", totalProfit)} ج", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

        } else {
            // ==========================================
            // TAB 2: DCA / AVERAGE PRICE CALCULATOR
            // ==========================================
            val oldPrice = oldPriceText.toDoubleOrNull() ?: 0.0
            val oldQty = oldQtyText.toLongOrNull() ?: 0L
            val newPrice = newPriceText.toDoubleOrNull() ?: 0.0
            val newQty = newQtyText.toLongOrNull() ?: 0L
            val commissionPct = (commissionPctText.toDoubleOrNull() ?: 0.25) / 100.0

            val oldTotalCost = oldPrice * oldQty
            val newCost = newPrice * newQty
            val totalQty = oldQty + newQty
            val totalCost = oldTotalCost + newCost

            val newAverage = if (totalQty > 0) totalCost / totalQty else 0.0
            val reductionPct = if (oldPrice > 0 && newAverage > 0 && newAverage < oldPrice) {
                ((oldPrice - newAverage) / oldPrice) * 100.0
            } else 0.0

            val breakEvenPrice = if (newAverage > 0) newAverage * (1.0 + (commissionPct * 2.0)) else 0.0

            // Section 1: Previous Position
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "📌 المركز الشرائي السابق (السعر والكمية الحالية)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = oldPriceText,
                                onValueChange = { oldPriceText = it },
                                label = { Text("سعر الشراء السابق (ج)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = OutlineDark,
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark
                                )
                            )

                            OutlinedTextField(
                                value = oldQtyText,
                                onValueChange = { oldQtyText = it },
                                label = { Text("الكمية الحالية (سهم)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = OutlineDark,
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "إجمالي التكلفة السابقة: ${String.format(Locale.US, "%,.0f", oldTotalCost)} ج",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // Section 2: New Position
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🛒 الشراء الإضافي المقترح (التبريد)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = newPriceText,
                                onValueChange = { newPriceText = it },
                                label = { Text("سعر الشراء الجديد (ج)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
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
                                value = newQtyText,
                                onValueChange = { newQtyText = it },
                                label = { Text("الكمية الجديدة (سهم)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
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

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "السيولة المطلوبة للشراء الإضافي: ${String.format(Locale.US, "%,.0f", newCost)} ج",
                            fontSize = 11.sp,
                            color = GoldenAmber
                        )
                    }
                }
            }

            // Section 3: Summary Results Card
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BullishGreen, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "متوسط السعر الجديد بعد التبريد", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%.3f", newAverage)} ج",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BullishGreen
                                )
                            }
                            if (reductionPct > 0) {
                                Box(
                                    modifier = Modifier
                                        .background(BullishGreenBg, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "تخفيض -${String.format(Locale.US, "%.1f", reductionPct)}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BullishGreen
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = OutlineDark, modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "إجمالي الأسهم", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%,d", totalQty)} سهم",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "إجمالي التكلفة الكلية", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%,.0f", totalCost)} ج",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "سعر التعادل (مع العمولة)", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%.3f", breakEvenPrice)} ج",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
