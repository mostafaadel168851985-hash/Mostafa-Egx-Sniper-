package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TradeEntity
import com.example.data.model.StockData
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

enum class JournalFilter(val label: String) {
    TOMORROW("مرشح الغد 🎯"),
    ALL("الكل 🌐"),
    BREAKOUTS("فرص الاختراق ⚡"),
    EARLY_UPTREND("بداية الصعود 🚀"),
    SUPPORT_BOUNCE("صيد القيعان 💎"),
    CORRECTIONS("صيد التصحيح 🌊")
}

@Composable
fun PerformanceJournalScreen(
    trades: List<TradeEntity>,
    currentStocks: List<StockData>,
    onSnapshotTomorrowOnly: () -> Unit,
    onSnapshotAllScreeners: () -> Unit,
    onAutoAudit: () -> Unit,
    onClearAllTrades: () -> Unit,
    onUpdateStatus: (Long, String, Double?) -> Unit,
    onDeleteTrade: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(JournalFilter.TOMORROW) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    // Filter trades based on selected strategy
    val filteredTrades = remember(trades, selectedFilter) {
        if (selectedFilter == JournalFilter.ALL) {
            trades
        } else {
            trades.filter {
                it.tradeType.contains(selectedFilter.label.replace(" ", "").take(5)) ||
                it.tradeType.contains(selectedFilter.name)
            }
        }
    }

    val totalTrades = filteredTrades.size
    val hitTargetCount = filteredTrades.count { it.status == "hit_target" }
    val stoppedOutCount = filteredTrades.count { it.status == "stopped_out" }
    val pendingCount = filteredTrades.count { it.status == "pending" }

    val closedCount = hitTargetCount + stoppedOutCount
    val winRatePct = if (closedCount > 0) (hitTargetCount.toDouble() / closedCount) * 100.0 else 0.0
    val avgRR = if (totalTrades > 0) filteredTrades.map { it.rr }.average() else 0.0

    // Average gain of winning trades
    val winningTrades = filteredTrades.filter { it.status == "hit_target" }
    val avgWinGainPct = if (winningTrades.isNotEmpty()) {
        winningTrades.mapNotNull { it.profitPct }.filter { it > 0 }.let { if (it.isNotEmpty()) it.average() else 0.0 }
    } else 0.0

    val stockMap = remember(currentStocks) { currentStocks.associateBy { it.symbol } }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = BearishRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مسح سجل التدقيق بالكامل 🗑️", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Text(
                    text = "هل أنت متأكد من مسح جميع صفقات وسجلات التدقيق المحفوظة للبدء من جديد وبسجل نظيف؟\n\nلن يمكن استعادة السجلات المحذوفة.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmDialog = false
                        onClearAllTrades()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BearishRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("نعم، مسح الكل 🗑️", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("إلغاء", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(14.dp)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp)
            .testTag("performance_journal_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.QueryStats,
                    contentDescription = null,
                    tint = GoldenAmber,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "تدقيق وقياس دقة التوصيات 🎯",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "قياس نجاح مرشحات اليوم القادم وفلاتر البورصة بدقة واحترافية",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Clear All Button in top corner
                IconButton(
                    onClick = { showClearConfirmDialog = true },
                    modifier = Modifier
                        .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                        .size(36.dp)
                        .testTag("btn_clear_audit_journal")
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = "مسح السجل", tint = BearishRed, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Primary Hero Action: Snapshot Tomorrow Picks ONLY
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSnapshotTomorrowOnly,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_snapshot_tomorrow_only"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F291E)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BullishGreen)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = GoldenAmber, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🎯 حفظ مرشحات الغد فقط (أفضل 3-5 أسهم لليوم القادم)",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Secondary Actions Row: Live Auto-Audit & Snapshot All
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAutoAudit,
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("btn_auto_audit"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تدقيق آلي بالأسعار الحية 🔄", color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSnapshotAllScreeners,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_snapshot_all_screeners"),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineDark)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TextMuted, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حفظ كل الفلاتر 📸", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Filter Categories Bar
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                JournalFilter.values().forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) AccentCyan else SurfaceVariantDark,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) AccentCyan else OutlineDark,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else TextPrimary
                        )
                    }
                }
            }
        }

        // Stats Hero Card
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (selectedFilter == JournalFilter.TOMORROW) GoldenAmber else BullishGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
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
                            Text(
                                text = "دقة قسم: ${selectedFilter.label}",
                                fontSize = 11.sp,
                                color = GoldenAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.1f", winRatePct)}%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = if (winRatePct >= 60.0) BullishGreen else if (winRatePct >= 40.0) GoldenAmber else BearishRed
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "متوسط ربح الصفقات الرابحة", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = "+${String.format(Locale.US, "%.1f", avgWinGainPct)}%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BullishGreen
                            )
                            Text(
                                text = "معدل العائد/المخاطرة: 1 : ${String.format(Locale.US, "%.2f", avgRR)}",
                                fontSize = 10.sp,
                                color = AccentCyan
                            )
                        }
                    }

                    HorizontalDivider(color = OutlineDark, modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatColumn(title = "إجمالي الأسهم", value = "$totalTrades", color = TextPrimary)
                        StatColumn(title = "حققت الهدف 🟢", value = "$hitTargetCount", color = BullishGreen)
                        StatColumn(title = "كسرت الوقف 🔴", value = "$stoppedOutCount", color = BearishRed)
                        StatColumn(title = "قيد الحركة ⏳", value = "$pendingCount", color = GoldenAmber)
                    }
                }
            }
        }

        // Explanatory Note on Audit Calculation
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "القياس الذكي: تُحسب الصفقة ناجحة فور ملامسة أعلى سعر اليوم (High) للمستهدف، وخاسرة إذا كسر أدنى سعر (Low) نقطة الوقف.",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        // List Header
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 صفقات ${selectedFilter.label} ($totalTrades)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "متابعة دقيقة لحظة بلحظة ⚡",
                    fontSize = 10.sp,
                    color = AccentCyan
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (filteredTrades.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 35.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد أسهم مسجلة لـ ${selectedFilter.label}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اضغط على زر (حفظ مرشحات الغد فقط 🎯) لحفظ وتقييم أسهم الغد بدقة",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(filteredTrades, key = { it.id }) { trade ->
                val currentStock = stockMap[trade.symbol]
                TradeAuditorItemCard(
                    trade = trade,
                    currentStock = currentStock,
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
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun TradeAuditorItemCard(
    trade: TradeEntity,
    currentStock: StockData?,
    onUpdateStatus: (String, Double?) -> Unit,
    onDelete: () -> Unit
) {
    val currentLivePrice = currentStock?.price ?: trade.entryPrice

    val currentProfitPct = if (trade.status == "pending") {
        if (trade.entryPrice > 0.0) {
            ((currentLivePrice - trade.entryPrice) / trade.entryPrice) * 100.0
        } else 0.0
    } else {
        trade.profitPct ?: 0.0
    }

    val (statusLabel, statusColor, statusBg) = when (trade.status) {
        "hit_target" -> Triple("🟢 حققت الهدف", BullishGreen, BullishGreenBg)
        "stopped_out" -> Triple("🔴 ضربت الوقف", BearishRed, BearishRedBg)
        else -> Triple("⚡ قيد الحركة", GoldenAmber, GoldenAmberBg)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .border(1.dp, OutlineDark, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = trade.symbol,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = AccentCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = trade.name,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "${trade.tradeType} • إشارة: ${trade.dateRecorded}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                // Return Badge (+% or -%)
                Box(
                    modifier = Modifier
                        .background(
                            if (currentProfitPct >= 0) BullishGreenBg else BearishRedBg,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${if (currentProfitPct >= 0) "+" else ""}${String.format(Locale.US, "%.2f", currentProfitPct)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (currentProfitPct >= 0) BullishGreen else BearishRed
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pricing Matrix
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "سعر الإشارة", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.2f", trade.entryPrice)} ج",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "السعر اللحظي ⚡", fontSize = 9.sp, color = AccentCyan)
                    Text(
                        text = "${String.format(Locale.US, "%.2f", currentLivePrice)} ج",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (currentLivePrice >= trade.entryPrice) BullishGreen else BearishRed
                    )
                    if (currentStock != null) {
                        Text(
                            text = "أعلى: ${String.format(Locale.US, "%.2f", currentStock.high)}",
                            fontSize = 8.sp,
                            color = TextMuted
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "المستهدف (+${String.format(Locale.US, "%.1f", trade.targetPct)}%)", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.2f", trade.target)} ج",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullishGreen
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "الوقف (-${String.format(Locale.US, "%.1f", trade.riskPct)}%)", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.2f", trade.stopLoss)} ج",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BearishRed
                    )
                }
            }

            // Progress Bar towards Target
            if (trade.status == "pending" && trade.target > trade.entryPrice) {
                Spacer(modifier = Modifier.height(6.dp))
                val totalDistance = trade.target - trade.entryPrice
                val currentDistance = (currentLivePrice - trade.entryPrice).coerceIn(0.0, totalDistance)
                val progress = (currentDistance / totalDistance).toFloat().coerceIn(0f, 1f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "التقدم للهدف:", fontSize = 9.sp, color = TextMuted)
                    Spacer(modifier = Modifier.width(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp),
                        color = if (progress >= 0.8f) BullishGreen else AccentCyan,
                        trackColor = OutlineDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${(progress * 100).toInt()}%", fontSize = 9.sp, color = AccentCyan)
                }
            }

            // Status Update Buttons if pending
            if (trade.status == "pending") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onUpdateStatus("hit_target", trade.targetPct) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BullishGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "حققت الهدف", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onUpdateStatus("stopped_out", -trade.riskPct) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BearishRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ThumbDown, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "ضربت الوقف", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
