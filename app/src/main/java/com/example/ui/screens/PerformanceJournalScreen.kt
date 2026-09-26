package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TradeEntity
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BackgroundDark
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

@Composable
fun PerformanceJournalScreen(
    trades: List<TradeEntity>,
    onUpdateStatus: (Long, String, Double?) -> Unit,
    onDeleteTrade: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalTrades = trades.size
    val hitTargetCount = trades.count { it.status == "hit_target" }
    val stoppedOutCount = trades.count { it.status == "stopped_out" }
    val pendingCount = trades.count { it.status == "pending" }

    val closedCount = hitTargetCount + stoppedOutCount
    val winRatePct = if (closedCount > 0) (hitTargetCount.toDouble() / closedCount) * 100.0 else 0.0
    val avgRR = if (totalTrades > 0) trades.map { it.rr }.average() else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp)
            .testTag("performance_journal_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.QueryStats,
                    contentDescription = null,
                    tint = BullishGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "سجل التداول وتقييم أداء الصفقات",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "تتبع دقة التوصيات ونسب النجاح ومعدل العائد إلى المخاطرة",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Stats Hero Card
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BullishGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
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
                            Text(text = "معدل النجاح (Win Rate)", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = "${String.format(Locale.US, "%.1f", winRatePct)}%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = if (winRatePct >= 50.0) BullishGreen else GoldenAmber
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "متوسط العائد / المخاطرة", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = "1 : ${String.format(Locale.US, "%.2f", avgRR)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                        }
                    }

                    HorizontalDivider(color = OutlineDark, modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatColumn(title = "إجمالي الصفقات", value = "$totalTrades", color = TextPrimary)
                        StatColumn(title = "حققت الهدف 🟢", value = "$hitTargetCount", color = BullishGreen)
                        StatColumn(title = "ضربت الوقف 🔴", value = "$stoppedOutCount", color = BearishRed)
                        StatColumn(title = "قيد الانتظار ⏳", value = "$pendingCount", color = GoldenAmber)
                    }
                }
            }
        }

        // List Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "📋 الصفقات المسجلة ($totalTrades)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (trades.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "لا توجد صفقات مسجلة بعد",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "اضغط على أيقونة الحفظ 🔖 في بطاقة أي سهم لتسجيله هنا",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(trades, key = { it.id }) { trade ->
                TradeItemCard(
                    trade = trade,
                    onUpdateStatus = { status, profit -> onUpdateStatus(trade.id, status, profit) },
                    onDelete = { onDeleteTrade(trade.id) }
                )
            }
        }
    }
}

@Composable
private fun StatColumn(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 10.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun TradeItemCard(
    trade: TradeEntity,
    onUpdateStatus: (String, Double?) -> Unit,
    onDelete: () -> Unit
) {
    val (statusLabel, statusColor, statusBg) = when (trade.status) {
        "hit_target" -> Triple("🟢 حققت الهدف", BullishGreen, BullishGreenBg)
        "stopped_out" -> Triple("🔴 ضربت الوقف", BearishRed, BearishRedBg)
        else -> Triple("🟡 قيد المتابعة", GoldenAmber, GoldenAmberBg)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, OutlineDark, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${trade.symbol} - ${trade.name}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                    Text(
                        text = "${trade.tradeType} • تاريخ التسجيل: ${trade.dateRecorded}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trade parameters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "سعر الدخول", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.3f", trade.entryPrice)} ج",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "المستهدف", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.3f", trade.target)} ج",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullishGreen
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "وقف الخسارة", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.3f", trade.stopLoss)} ج",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BearishRed
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "العائد/المخاطرة", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${trade.rr}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldenAmber
                    )
                }
            }

            // Status Update Buttons if pending
            if (trade.status == "pending") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onUpdateStatus("hit_target", trade.targetPct) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BullishGreen)
                    ) {
                        Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "حققت الهدف", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onUpdateStatus("stopped_out", -trade.riskPct) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BearishRed)
                    ) {
                        Icon(Icons.Default.ThumbDown, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "ضربت الوقف", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
