package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinancialMetrics
import com.example.ui.theme.AccentCyan
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

@Composable
fun StockFinancialAnalysisCard(
    financial: FinancialMetrics,
    currentPrice: Double,
    modifier: Modifier = Modifier
) {
    val healthColor = when {
        financial.financialHealthScore >= 75 -> BullishGreen
        financial.financialHealthScore >= 55 -> GoldenAmber
        else -> BearishRed
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.2.dp, healthColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Title & Health Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = GoldenAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🏛️ التحليل المالي ونمو الشركة والاستثمار",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .background(healthColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "المتانة: ${financial.financialHealthScore}/100",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = healthColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Verdict & Growth Stage Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (financial.isWorthInvesting) Icons.Default.WorkspacePremium else Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = healthColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = financial.investmentVerdict,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "مرحلة الشركة: ${financial.growthStageArabic}",
                        fontSize = 10.sp,
                        color = AccentCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Metrics Grid (P/E, P/B, EPS, ROE, Dividend Yield, Fair Value)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FinancialMetricBox(
                    label = "مكرر الربحية P/E",
                    value = financial.peRatio?.let { String.format(Locale.US, "%.1fx", it) } ?: "N/A",
                    sub = if ((financial.peRatio ?: 20.0) < 10.0) "جاذب جداً" else "معتدل",
                    color = if ((financial.peRatio ?: 20.0) < 12.0) BullishGreen else GoldenAmber,
                    modifier = Modifier.weight(1f)
                )

                FinancialMetricBox(
                    label = "مضاعف الدفترية P/B",
                    value = financial.pbRatio?.let { String.format(Locale.US, "%.2f", it) } ?: "N/A",
                    sub = "نسبة القيمة",
                    color = AccentCyan,
                    modifier = Modifier.weight(1f)
                )

                FinancialMetricBox(
                    label = "العائد ROE",
                    value = financial.roePct?.let { String.format(Locale.US, "%.1f%%", it) } ?: "N/A",
                    sub = "كفاءة رأس المال",
                    color = if ((financial.roePct ?: 0.0) >= 20.0) BullishGreen else GoldenAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FinancialMetricBox(
                    label = "ربحية السهم EPS",
                    value = financial.eps?.let { String.format(Locale.US, "%.2f ج", it) } ?: "N/A",
                    sub = "صافي سنوي",
                    color = GoldenAmber,
                    modifier = Modifier.weight(1f)
                )

                FinancialMetricBox(
                    label = "عائد التوزيعات",
                    value = financial.dividendYieldPct?.let { String.format(Locale.US, "%.1f%%", it) } ?: "0.0%",
                    sub = "توزيعات نقدية",
                    color = if ((financial.dividendYieldPct ?: 0.0) >= 5.0) BullishGreen else TextSecondary,
                    modifier = Modifier.weight(1f)
                )

                val isUndervalued = financial.marginOfSafetyPct > 0
                FinancialMetricBox(
                    label = "القيمة العادلة المقدرة",
                    value = "${String.format(Locale.US, "%.2f", financial.estimatedFairValue)} ج",
                    sub = if (isUndervalued) "+${financial.marginOfSafetyPct}% أمان" else "${financial.marginOfSafetyPct}% تضخم",
                    color = if (isUndervalued) BullishGreen else BearishRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Plain-English Investment Advice Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(healthColor.copy(alpha = 0.10f), RoundedCornerShape(10.dp))
                    .border(1.dp, healthColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = healthColor,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "💡 نصيحة المستثمر الصريحة:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = healthColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = financial.investmentAdviceArabic,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FinancialMetricBox(
    label: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 7.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = label, fontSize = 9.sp, color = TextMuted, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
            Spacer(modifier = Modifier.height(1.dp))
            Text(text = sub, fontSize = 8.sp, color = TextSecondary, maxLines = 1)
        }
    }
}
