package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AlertDao
import com.example.data.local.AlertEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertNotificationManager(
    private val context: Context,
    private val alertDao: AlertDao
) {
    companion object {
        const val CHANNEL_ID = "egx_market_alerts"
        const val CHANNEL_NAME = "تنبيهات قناص البورصة المصرية الفورية"
        const val CHANNEL_DESC = "إخطارات لحظية لتغير اتجاه مؤشر EGX30 واختراقات الأسهم والدعوم"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setSound(soundUri, null)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun notifyIndexDirectionChange(
        oldTrend: String,
        newTrend: String,
        egx30Price: Double,
        change: Double
    ) {
        val title = "🚨 تغير اتجاه مؤشر EGX30!"
        val message = "تحول السوق من ($oldTrend) إلى ($newTrend) عند النقطة ${String.format(java.util.Locale.US, "%.1f", egx30Price)} بنسبة ${String.format(java.util.Locale.US, "%+.2f%%", change)}"

        saveAlertToDb(
            title = title,
            message = message,
            symbol = "EGX30",
            alertType = "INDEX_SHIFT"
        )

        showNotification(
            notificationId = 1001,
            title = title,
            message = message
        )
    }

    fun notifyBreakout(symbol: String, price: Double, r1: Double, volumeRatio: Double) {
        val title = "⚡ انفجار سعري واختراق R1: $symbol"
        val message = "السعر الحالي ${String.format(java.util.Locale.US, "%.3f", price)} ج تجاوز المقاومة ${String.format(java.util.Locale.US, "%.3f", r1)} بسيولة ${String.format(java.util.Locale.US, "%.1f", volumeRatio)}x ضعف المتوسط!"

        saveAlertToDb(
            title = title,
            message = message,
            symbol = symbol,
            alertType = "BREAKOUT"
        )

        showNotification(
            notificationId = 2000 + symbol.hashCode() % 1000,
            title = title,
            message = message
        )
    }

    fun notifyGoldenCross(symbol: String, price: Double, sma50: Double, sma200: Double) {
        val title = "🌟 تقاطع ذهبي تاريخي 50/200: $symbol"
        val message = "متوسط 50 يوم اخترق متوسط 200 يوم لأعلى عند سعر ${String.format(java.util.Locale.US, "%.3f", price)} ج - إشارة اتجاه صاعد رئيسي!"

        saveAlertToDb(
            title = title,
            message = message,
            symbol = symbol,
            alertType = "GOLDEN_CROSS"
        )

        showNotification(
            notificationId = 3000 + symbol.hashCode() % 1000,
            title = title,
            message = message
        )
    }

    fun notifySupportBounce(symbol: String, price: Double, s1: Double) {
        val title = "🎯 ارتداد من الدعم: $symbol"
        val message = "ارتداد إيجابي قوي من مستوى الدعم ${String.format(java.util.Locale.US, "%.3f", s1)} ج، السعر الآن ${String.format(java.util.Locale.US, "%.3f", price)} ج"

        saveAlertToDb(
            title = title,
            message = message,
            symbol = symbol,
            alertType = "SUPPORT_BOUNCE"
        )

        showNotification(
            notificationId = 4000 + symbol.hashCode() % 1000,
            title = title,
            message = message
        )
    }

    fun notifyGeneralAlert(title: String, message: String, symbol: String? = null, type: String = "GENERAL") {
        saveAlertToDb(title, message, symbol, type)
        showNotification(
            notificationId = System.currentTimeMillis().toInt() % 100000,
            title = title,
            message = message
        )
    }

    private fun showNotification(notificationId: Int, title: String, message: String) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 250, 150, 250))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission might not be granted yet on Android 13+
        }
    }

    private fun saveAlertToDb(title: String, message: String, symbol: String?, alertType: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                alertDao.insertAlert(
                    AlertEntity(
                        title = title,
                        message = message,
                        symbol = symbol,
                        alertType = alertType,
                        timestamp = System.currentTimeMillis(),
                        isRead = false,
                        importance = "HIGH"
                    )
                )
            } catch (_: Exception) {
            }
        }
    }
}
