package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.model.CorporateNews
import com.example.data.model.StockData
import java.net.URLEncoder

object ShareHelper {

    fun shareCorporateNewsToWhatsApp(context: Context, news: CorporateNews) {
        val message = buildString {
            append("📢 *إفصاح / خبر بورصة هام: ${news.title}*\n")
            append("━━━━━━━━━━━━━━━━━━━\n")
            append("🏢 *الشركة:* ${news.companyName} (${news.symbol})\n")
            append("📁 *التصنيف:* ${news.category.arabicLabel}\n")
            append("📅 *التاريخ / الموعد:* ${news.date}\n")
            append("📌 *الحالة:* ${news.status}\n")
            append("🏛️ *المصدر:* ${news.source}\n")
            append("━━━━━━━━━━━━━━━━━━━\n")
            append("📝 *الملخص:* ${news.summary}\n\n")
            append("🔍 *التفاصيل:* ${news.fullDetails}\n")
            append("━━━━━━━━━━━━━━━━━━━\n")
            append("📱 *تابع أحدث الاكتتابات والإفصاحات عبر تطبيق: قناص البورصة المصرية EGX* 🇪🇬")
        }

        try {
            val encoded = URLEncoder.encode(message, "UTF-8")
            val directUri = Uri.parse("whatsapp://send?text=$encoded")
            val directIntent = Intent(Intent.ACTION_VIEW, directUri)
            context.startActivity(directIntent)
        } catch (_: Exception) {
            try {
                val encoded = URLEncoder.encode(message, "UTF-8")
                val webUri = Uri.parse("https://api.whatsapp.com/send?text=$encoded")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                context.startActivity(webIntent)
            } catch (_: Exception) {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                }
                val chooserIntent = Intent.createChooser(sendIntent, "مشاركة خبر ${news.title} عبر:")
                context.startActivity(chooserIntent)
            }
        }
    }

    fun shareStockAnalysisToWhatsApp(context: Context, stock: StockData) {
        val message = buildString {
            append("🎯 *تقرير تحليل فني - سهم ${stock.name} (${stock.symbol})*\n")
            append("━━━━━━━━━━━━━━━━━━━\n")
            append("🏢 *القطاع:* ${stock.sector}\n")
            append("💰 *السعر الحالي:* ${stock.formattedPrice} ج.م (${stock.formattedChange})\n")
            append("🎯 *نطاق الدخول المقترح:* ${stock.entryRangeFormatted}\n")
            append("🛑 *وقف الخسارة الحاسم:* ${stock.stopLossFormatted} (-${stock.riskPct}%)\n")
            append("🏁 *المستهدف الأول:* ${stock.target1Formatted} (+${stock.targetPct}%)\n")
            append("🏁 *المستهدف الثاني:* ${stock.target2Formatted}\n")
            append("⚖️ *نسبة العائد إلى المخاطرة (R:R):* 1 : ${stock.riskRewardRatio}\n")
            append("⭐ *التقييم الفني الذكي:* ${stock.confidenceGrade} (النقاط: ${stock.smartScore}/100)\n")
            
            if (stock.candlePatterns.isNotEmpty()) {
                append("🕯️ *نماذج الشموع اليابانية:* ${stock.candlePatterns.joinToString("، ")}\n")
            }
            if (stock.confidenceAdvice.isNotBlank()) {
                append("💡 *الخطة الفنية المقترحة:* ${stock.confidenceAdvice}\n")
            }
            append("━━━━━━━━━━━━━━━━━━━\n")
            append("📱 *تم استخراج التحليل بواسطة: قناص البورصة المصرية EGX* 🇪🇬")
        }

        // Try direct WhatsApp scheme first
        try {
            val encoded = URLEncoder.encode(message, "UTF-8")
            val directUri = Uri.parse("whatsapp://send?text=$encoded")
            val directIntent = Intent(Intent.ACTION_VIEW, directUri)
            context.startActivity(directIntent)
        } catch (_: Exception) {
            // Fallback 1: try web wa.me url
            try {
                val encoded = URLEncoder.encode(message, "UTF-8")
                val webUri = Uri.parse("https://api.whatsapp.com/send?text=$encoded")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                context.startActivity(webIntent)
            } catch (_: Exception) {
                // Fallback 2: General Android share chooser
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                }
                val chooserIntent = Intent.createChooser(sendIntent, "مشاركة تحليل سهم ${stock.symbol} عبر:")
                context.startActivity(chooserIntent)
            }
        }
    }
}
