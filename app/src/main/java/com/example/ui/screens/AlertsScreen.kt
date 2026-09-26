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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AlertEntity
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertsScreen(
    alerts: List<AlertEntity>,
    onTriggerTestAlert: () -> Unit,
    onMarkAllRead: () -> Unit,
    onClearAll: () -> Unit,
    onMarkRead: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var notifyIndexChanges by remember { mutableStateOf(true) }
    var notifyBreakouts by remember { mutableStateOf(true) }
    var notifyGoldenCross by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp)
            .testTag("alerts_screen_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = GoldenAmber,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "نظام التنبيهات الفورية اللحظية",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "إخطارات لحظية عالية الدقة لتغير اتجاه السوق والاختراقات",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Notification Settings & High-Precision System integration
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BullishGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "منصة الإخطارات الفورية (Android High Priority)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullishGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AlertToggleRow(
                        title = "تغير اتجاه مؤشر EGX30",
                        subtitle = "تنبيه صوتي فوري عند تحول السوق (صاعد / متذبذب / هابط)",
                        checked = notifyIndexChanges,
                        onCheckedChange = { notifyIndexChanges = it }
                    )

                    AlertToggleRow(
                        title = "قناص الاختراق السريع R1",
                        subtitle = "إشعار عند تجاوز المقاومة الأولى بسيولة غير معتادة",
                        checked = notifyBreakouts,
                        onCheckedChange = { notifyBreakouts = it }
                    )

                    AlertToggleRow(
                        title = "التقاطعات الذهبية (50 / 200 يوم)",
                        subtitle = "إشعار بالتحول الصاعد التاريخي طويل المدى للأسهم",
                        checked = notifyGoldenCross,
                        onCheckedChange = { notifyGoldenCross = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onTriggerTestAlert,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_trigger_test_alert"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "اختبار إرسال إشعار فوري وتجربة النظام",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Alerts History Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 سجل الإخطارات والتنبيهات الواردة (${alerts.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))

                if (alerts.isNotEmpty()) {
                    IconButton(onClick = onMarkAllRead, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DoneAll, contentDescription = "قراءة الكل", tint = AccentCyan)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onClearAll, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "مسح الكل", tint = BearishRed)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (alerts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد تنبيهات حتى الآن في السجل",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "اضغط على زر الاختبار بالأعلى لتجربة التنبيهات الفورية",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(alerts, key = { it.id }) { alert ->
                AlertItemCard(
                    alert = alert,
                    onMarkRead = { onMarkRead(alert.id) }
                )
            }
        }
    }
}

@Composable
private fun AlertToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = subtitle, fontSize = 10.sp, color = TextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = BullishGreen,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SurfaceVariantDark
            )
        )
    }
}

@Composable
private fun AlertItemCard(
    alert: AlertEntity,
    onMarkRead: () -> Unit
) {
    val dateFormatted = remember(alert.timestamp) {
        SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(alert.timestamp))
    }

    val iconColor = when (alert.alertType) {
        "INDEX_SHIFT" -> GoldenAmber
        "BREAKOUT" -> AccentCyan
        "GOLDEN_CROSS" -> GoldenAmber
        else -> BullishGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onMarkRead() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isRead) SurfaceDark else SurfaceVariantDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = alert.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = dateFormatted,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alert.message,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
