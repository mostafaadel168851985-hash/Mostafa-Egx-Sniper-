package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProfileStrategyPlan
import com.example.data.model.StockData
import com.example.data.model.TraderProfileEngine
import com.example.data.model.TraderProfileType
import com.example.ui.theme.AccentCyan
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

@Composable
fun TraderProfileStrategyView(
    stock: StockData,
    modifier: Modifier = Modifier
) {
    val plans = remember(stock) { TraderProfileEngine.generatePlans(stock) }
    var selectedType by remember { mutableStateOf(TraderProfileType.SWING) }
    val currentPlan = plans[selectedType] ?: return

    val themeColor = when (selectedType) {
        TraderProfileType.SCALPER -> GoldenAmber
        TraderProfileType.SWING -> AccentCyan
        TraderProfileType.INVESTOR -> BullishGreen
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.2.dp, themeColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯 خطة الدخول والخروج حسب أسلوب تداولك",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .background(themeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = selectedType.shortBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3-Profile Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TraderProfileType.values().forEach { profile ->
                    val isSelected = selectedType == profile
                    val activeColor = when (profile) {
                        TraderProfileType.SCALPER -> GoldenAmber
                        TraderProfileType.SWING -> AccentCyan
                        TraderProfileType.INVESTOR -> BullishGreen
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) activeColor else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedType = profile }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${profile.icon} ${profile.shortBadge}",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Profile Description & Timeframe
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = themeColor, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "المدة المتوقعة: ${selectedType.timeframe}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Levels Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "نطاق الدخول", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = currentPlan.entryRange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "وقف الخسارة", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.2f", currentPlan.stopLossPrice)} ج (-${currentPlan.stopLossPct}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BearishRed
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "المستهدف 1", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.2f", currentPlan.target1Price)} ج (+${currentPlan.target1Pct}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullishGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "المستهدف 2 (الامتداد)", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.2f", currentPlan.target2Price)} ج (+${currentPlan.target2Pct}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullishGreen
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "نسبة العائد للمخاطرة R:R", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "1 : ${String.format(Locale.US, "%.2f", currentPlan.riskRewardRatio)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Golden execution rule
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentPlan.goldenRule,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
