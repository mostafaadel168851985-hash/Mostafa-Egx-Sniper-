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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketIndexStatus
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
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import java.util.Locale

@Composable
fun IndexStatusBanner(
    status: MarketIndexStatus,
    onRefresh: () -> Unit,
    onTestAlert: () -> Unit,
    lastUpdatedTime: String = "",
    isAutoRefreshActive: Boolean = true,
    onToggleAutoRefresh: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val statusColor = Color(status.statusColorHex)
    val isPositive = status.change >= 0
    val changeColor = if (isPositive) BullishGreen else BearishRed

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .testTag("index_status_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Title, Index Price, Change, Refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glowing status dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(statusColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "مؤشر البورصة المصرية EGX30",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (lastUpdatedTime.isNotBlank()) {
                        Text(
                            text = "آخر تحديث: $lastUpdatedTime",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Auto refresh state indicator pill
                if (onToggleAutoRefresh != null) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (isAutoRefreshActive) BullishGreen.copy(alpha = 0.15f) else TextMuted.copy(alpha = 0.15f),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                if (isAutoRefreshActive) BullishGreen.copy(alpha = 0.4f) else OutlineDark,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onToggleAutoRefresh() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("btn_toggle_auto_refresh")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(if (isAutoRefreshActive) BullishGreen else TextMuted, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAutoRefreshActive) "تلقائي كل 15د" else "التلقائي متوقف",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAutoRefreshActive) BullishGreen else TextMuted
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(34.dp)
                        .background(SurfaceVariantDark, CircleShape)
                        .testTag("btn_refresh_market")
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "تحديث يدوي فوري",
                        tint = AccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Value and Status Description
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format(Locale.US, "%,.1f", status.price),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format(Locale.US, "%+.2f%%", status.change),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = changeColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "RSI الزخم: ${String.format(Locale.US, "%.1f", status.rsi)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentCyan
                    )
                    if (status.high > 0 && status.low > 0) {
                        Text(
                            text = "نطاق اليوم: ${String.format(Locale.US, "%,.0f", status.low)} - ${String.format(Locale.US, "%,.0f", status.high)}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Other indices mini-strip: EGX70 EWI and EGX100 EWI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "مؤشر EGX70: ", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%,.0f", status.egx70Price)} (${String.format(Locale.US, "%+.2f%%", status.egx70Change)})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (status.egx70Change >= 0) BullishGreen else BearishRed
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "مؤشر EGX100: ", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%,.0f", status.egx100Price)} (${String.format(Locale.US, "%+.2f%%", status.egx100Change)})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (status.egx100Change >= 0) BullishGreen else BearishRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status message box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                    .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = status.statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 50 & 200 Day moving average index health check
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "المتوسطات: SMA50 (${String.format(Locale.US, "%,.0f", status.sma50)}) | SMA200 (${String.format(Locale.US, "%,.0f", status.sma200)})",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = if (status.isPriceAboveSma50 && status.isPriceAboveSma200)
                            "✅ المؤشر أعلى متوسط 50 و 200 يوم (صاعد)"
                        else if (status.price > status.sma200)
                            "⚠️ أعلى متوسط 200 يوم وفي تصحيح دون 50 يوم"
                        else
                            "❌ أدنى المتوسطات الرئيسية (هابط)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (status.isPriceAboveSma50 && status.isPriceAboveSma200) BullishGreen else GoldenAmber
                    )
                }

                // Check alert / Test alert button
                FilledTonalButton(
                    onClick = onTestAlert,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = GoldenAmber.copy(alpha = 0.2f),
                        contentColor = GoldenAmber
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "فحص الإشارات", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
