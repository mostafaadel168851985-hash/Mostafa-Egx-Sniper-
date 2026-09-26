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
import kotlin.math.roundToInt

@Composable
fun AveragePriceCalculatorScreen(
    prefilledStock: StockData?,
    modifier: Modifier = Modifier
) {
    var oldPriceText by remember { mutableStateOf(if (prefilledStock != null) String.format(Locale.US, "%.3f", prefilledStock.price * 1.15) else "25.00") }
    var oldQtyText by remember { mutableStateOf("1000") }

    var newPriceText by remember { mutableStateOf(if (prefilledStock != null) String.format(Locale.US, "%.3f", prefilledStock.price) else "20.00") }
    var newQtyText by remember { mutableStateOf("1500") }

    var commissionPctText by remember { mutableStateOf("0.25") }

    // Parse values safely
    val oldPrice = oldPriceText.toDoubleOrNull() ?: 0.0
    val oldQty = oldQtyText.toLongOrNull() ?: 0L

    val newPrice = newPriceText.toDoubleOrNull() ?: 0.0
    val newQty = newQtyText.toLongOrNull() ?: 0L

    val commissionPct = (commissionPctText.toDoubleOrNull() ?: 0.25) / 100.0

    // Calculations
    val oldTotalCost = oldPrice * oldQty
    val newCost = newPrice * newQty
    val totalQty = oldQty + newQty
    val totalCost = oldTotalCost + newCost

    val newAverage = if (totalQty > 0) totalCost / totalQty else 0.0
    val reductionPct = if (oldPrice > 0 && newAverage > 0 && newAverage < oldPrice) {
        ((oldPrice - newAverage) / oldPrice) * 100.0
    } else 0.0

    // Break-even with commission (buy & sell commissions)
    val breakEvenPrice = if (newAverage > 0) newAverage * (1.0 + (commissionPct * 2.0)) else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp)
            .testTag("average_calculator_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Calculate,
                    contentDescription = null,
                    tint = GoldenAmber,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "حاسبة متوسط السعر والتبريد الذكي",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "تعديل التكلفة وحساب نقطة التعادل وأهداف الربح بالجنيه المصري",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        oldPriceText = "25.00"
                        oldQtyText = "1000"
                        newPriceText = "20.00"
                        newQtyText = "1500"
                    }
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "إعادة ضبط", tint = TextMuted)
                }
            }
        }

        // Section 1: Previous Purchase Position
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
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_old_price"),
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
                            label = { Text("عدد الأسهم الحالية", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_old_qty"),
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
                        text = "التكلفة السابقة: ${String.format(Locale.US, "%,.2f", oldTotalCost)} ج.م",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Section 2: New Purchase (Cooling down / Averaging)
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🎯 الشراء الجديد (التبريد / التعزيز)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullishGreen
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
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_new_price"),
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
                            value = newQtyText,
                            onValueChange = { newQtyText = it },
                            label = { Text("عدد الأسهم الجديدة", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_new_qty"),
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

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تكلفة الشراء الجديد: ${String.format(Locale.US, "%,.2f", newCost)} ج.م",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Section 3: Results Hero Card
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
                    Text(
                        text = "🏆 نتيجة الحساب ومتوسط السعر الجديد",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullishGreen
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(text = "متوسط السعر الجديد:", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = "${String.format(Locale.US, "%.3f", newAverage)} ج.م",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = BullishGreen
                            )
                        }

                        if (reductionPct > 0) {
                            Box(
                                modifier = Modifier
                                    .background(BullishGreenBg, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "توفير -${String.format(Locale.US, "%.1f", reductionPct)}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BullishGreen
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = OutlineDark, modifier = Modifier.padding(vertical = 12.dp))

                    // Secondary outputs grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "📦 إجمالي الأسهم", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = String.format(Locale.US, "%,d", totalQty),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "💰 إجمالي الاستثمار", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = "${String.format(Locale.US, "%,.0f", totalCost)} ج",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldenAmber
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "⚖️ نقطة التعادل (شامل العمولة)", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = "${String.format(Locale.US, "%.3f", breakEvenPrice)} ج",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Profit Targets after averaging
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "🏁 مستهدفات الخروج بربح بعد التبريد",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val targets = listOf(
                Triple("المستهدف الأول (+5%)", 1.05, 0.05),
                Triple("المستهدف الثاني (+10%)", 1.10, 0.10),
                Triple("المستهدف الثالث (+15%)", 1.15, 0.15),
                Triple("المستهدف الرابع (+20%)", 1.20, 0.20)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                targets.take(2).forEach { (label, mult, pct) ->
                    val targetPrice = newAverage * mult
                    val profitAmount = totalCost * pct
                    TargetCard(
                        label = label,
                        targetPrice = targetPrice,
                        profitAmount = profitAmount,
                        color = BullishGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                targets.drop(2).forEach { (label, mult, pct) ->
                    val targetPrice = newAverage * mult
                    val profitAmount = totalCost * pct
                    TargetCard(
                        label = label,
                        targetPrice = targetPrice,
                        profitAmount = profitAmount,
                        color = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section 5: Smart Multi-Stage Scaling Plan
        item {
            Spacer(modifier = Modifier.height(16.dp))
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
                            text = "🏹 خطة الدخول والتبريد الذكية (3 مستويات مقترحة)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• المستوى الأول (50% من السيولة): عند الدعم الأول S1.\n• المستوى الثاني (30% من السيولة): عند الدعم الثاني S2 أو كسر كاذب.\n• المستوى الثالث (20% من السيولة): عند تأكيد اختراق نقطة الارتكاز PP واستعادة الزخم.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetCard(
    label: String,
    targetPrice: Double,
    profitAmount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = label, fontSize = 11.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${String.format(Locale.US, "%.3f", targetPrice)} ج",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "صافي ربح: +${String.format(Locale.US, "%,.0f", profitAmount)} ج",
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}
